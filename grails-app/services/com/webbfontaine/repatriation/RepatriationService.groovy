package com.webbfontaine.repatriation

import com.ibm.icu.text.DecimalFormat
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.hibernate.sql.JoinType
import org.hibernate.transform.AliasToBeanResultTransformer
import org.joda.time.LocalDate
import org.springframework.context.i18n.LocaleContextHolder
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALIDATED


/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
@Transactional
class RepatriationService {

    SessionStore sessionStoreService
    def repatBusinessLogicService
    def repatriationWorkflowService
    def messageSource
    def loggerService

    def addToSessionStore(Repatriation repatriation, conversationId = null) {
        if (conversationId) {
            sessionStoreService.put(conversationId, repatriation)
        } else {
            sessionStoreService.put(repatriation)
        }
    }

    def findFromSessionStore(def conversationId) {
        sessionStoreService.get(conversationId) as Repatriation
    }

    def loadRepatriation(id) {
        Repatriation.findById(id, [fetch: [attachedDocs: 'join', logs: 'join']])
    }

    def doPersist(Repatriation repatriation, Operation commitOperation) {
        OperationHandlerService opHandler = repatBusinessLogicService.resolveOperation(commitOperation)
        Repatriation.withTransaction { transactionStatus ->
            repatriation = opHandler.execute(repatriation, transactionStatus, commitOperation)
        }

        repatriation
    }

    def generateRequestNumber(Repatriation domainInstance) {
        String nbr = new DecimalFormat("000000").format(domainInstance?.requestNumberSequence)
        return "${domainInstance?.requestDate.getYear()}${nbr}"

    }

    def checkCommitOperation(params) {
        def commitOperation = repatriationWorkflowService.getCommitOperation(params)
        if (commitOperation) {
            params.commitOperation = Operation.valueOf(commitOperation.id)
            params.commitOperationName = repatriationWorkflowService.getOperationName(params?.commitOperation)
        }

        if (repatriationWorkflowService.validationNotRequired(params?.commitOperation)) {
            params.validationNotRequired = true
        } else {
            params.validationRequired = true
        }
    }

    def retrieveExchangeData(ecReference, exporterCode = null) {
        def exchange = findECDocument(ecReference)
        def responseChecking
        if (exchange) {
            responseChecking = checkECDocument(exchange, exporterCode)
        } else {
            responseChecking = getErrorMessage("correctEc")
        }
        return responseChecking

    }

    def findECDocument(ecReference) {
        def criteria = Exchange.createCriteria()
        applyCriteriaOnEcReference(ecReference, criteria)
    }

    def static applyCriteriaOnEcReference(String ecReference, criteria) {
        criteria.get() {
            eq("requestNo", ecReference)
            eq("requestType", RepatriationConstants.REQUEST_TYPE_EC )
        }
    }

    def checkExchangeExporterCode(Exchange doc, exporterCode) {
        return doc?.exporterCode?.equalsIgnoreCase(exporterCode)
    }

    def validateExporterCode(exchange, exporterCode) {
        def docChecking
        if (checkExchangeExporterCode(exchange, exporterCode)) {
            docChecking = validateStatus(exchange)
        } else {
            docChecking = getErrorMessage("exporterCode")
        }
        return docChecking
    }

    def checkExchangeStatus(Exchange doc) {
        return (doc?.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED])
    }

    def validateStatus(exchange) {
        def docChecking
        if (checkExchangeStatus(exchange)) {
            docChecking = validateCurrencyCode(exchange)
        } else {
            docChecking = getErrorMessage("status")
        }
        return docChecking
    }

    def checkExchangeCurrencyCode(Exchange doc) {
        return (doc?.currencyCode in RepatriationConstants.ALLOWED_CURRENCYCODE ? null : doc)
    }

    def validateCurrencyCode(Exchange doc) {
        def docChecking

        def criteria = checkExchangeCurrencyCode(doc)
        if (criteria) {
            docChecking = checkExchangeGeoArea(doc, criteria)
        } else {
            docChecking = getErrorMessage("currency")
        }
        return docChecking
    }

    def checkExchangeGeoArea(Exchange doc, criteria) {
        def docChecking
        boolean result = (doc?.areaPartyCode in RepatriationConstants.ALLOWED_AREA)
        if (result) {
            docChecking = validateDeclarationInformations(doc, criteria)
        } else {
            docChecking = getErrorMessage("AreaGeo")
        }
        return docChecking
    }

    def validateDeclarationInformations(Exchange exchange, criteria){
        def docChecking
        if (checkDeclarantionExistance(exchange)) {
            docChecking = ["error": false, "criteria": criteria]
        } else {
            docChecking = getErrorMessage("declaration")
        }
        return docChecking
    }

    boolean checkDeclarantionExistance(Exchange exchange){
        exchange?.declarationNumber && exchange?.declarationSerial && exchange?.declarationDate && exchange?.clearanceOfficeCode
    }

    def checkingCurrencyOfExchangeData(ecReference) {
        return Exchange.findByRequestNoAndRequestTypeAndCurrencyCodeNotInList(ecReference, RepatriationConstants.REQUEST_TYPE_EC, RepatriationConstants.ALLOWED_CURRENCYCODE)
    }

    def checkBalanceOfExchange(ecReference) {
        return Exchange.findByRequestNo(ecReference)?.balanceAs
    }

    def doAddClearanceChecking(repatriationInstance, ClearanceOfDom clearanceOfDom) {
        repatriationInstance?.addClearanceOfDom(clearanceOfDom)
        BigDecimal balance = checkBalanceOfExchange(clearanceOfDom.ecReference)
        def allPrevAmount = repatriationInstance?.clearances.findAll {
            it?.ecReference == clearanceOfDom?.ecReference && !it?.id && it?.status
        }?.repatriatedAmtInCurr?.sum()
        def total_sum_rep = allPrevAmount == null ? BigDecimal.ZERO : allPrevAmount
        def total = total_sum_rep
        def result = total.compareTo(balance)
        if (result == 1) {
            repatriationInstance?.removeClearanceOfDom(clearanceOfDom)
            if (repatriationInstance?.clearances?.findAll{it.status == true}?.size() > 0) {
                clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.sum', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Domiciliated Amount in Currency')
            } else {
                clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.Great', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Final Invoice Amount in Currency')
            }

        }
        return clearanceOfDom
    }

    def checkECDocument(exchange, exporterCode) {
        def docChecking = validateDoc(exchange, exporterCode)
        return docChecking
    }

    def validateDoc(exchange, exporterCode) {
        def docChecking
        if (exchange) {
            docChecking = validateExporterCode(exchange, exporterCode)
        } else {
            docChecking = getErrorMessage("correctEc")
        }
        return docChecking
    }

    def getErrorMessage(String fieldError) {
        def error
        Locale locale = LocaleContextHolder.getLocale()
        if (fieldError.equalsIgnoreCase("exporterCode")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.exporterCode.error", null, locale)]
        } else if (fieldError.equalsIgnoreCase("status")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.status.error", null, locale)]
        } else if (fieldError.equalsIgnoreCase("currency")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.currency.error", null, locale)]
        } else if (fieldError.equalsIgnoreCase("correctEc")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.correctEc.error", null, locale)]
        } else if (fieldError.equalsIgnoreCase("AreaGeo")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.AreaGeo.error", null, locale)]
        }else if (fieldError.equalsIgnoreCase("declaration")) {
            error = ["error": true, "messageError": messageSource.getMessage("repatriation.exchangeDeclaration.error", null, locale)]
        }
        return error
    }

    def updateEcBalanceAndStatus(Repatriation repatriation, def operation){
        if(operation in [Operation.DECLARE_QUERIED, Operation.CONFIRM_DECLARED, Operation.UPDATE_CONFIRMED, Operation.UPDATE_CLEARANCE, Operation.UPDATE_QUERIED]){
            def listNewClearances = repatriation?.clearances.findAll {!it.id}
            listNewClearances?.each { ClearanceOfDom newClearance  ->
                updateEcBalance(newClearance)
            }
        }else if(operation in [Operation.DECLARE, Operation.DECLARE_STORED]){
            repatriation?.clearances?.each {ClearanceOfDom newClearance ->
                updateEcBalance(newClearance)
            }
        }
    }

    def updateEcBalance(ClearanceOfDom clearance){
        if(clearance.status){
            Exchange exchangeEC = Exchange.findByRequestNo(clearance.ecReference)
            setNewBalanceAndStatus(clearance, exchangeEC)
            exchangeEC.merge(flush: true, deepValidate: false, validate: false)
        }
    }

    def setNewBalanceAndStatus(ClearanceOfDom newClearances, Exchange exchange){
        def currentBalance = exchange.balanceAs ?: BigDecimal.ZERO
        LOGGER.debug("updateEcBalanceAndStatus method balance    for declare from new and for stored : ${currentBalance}")
        def newBalance = currentBalance - newClearances.repatriatedAmtInCurr
        LOGGER.debug("updateEcBalanceAndStatus method newBalance for declare from new and for stored : ${newBalance}")
        exchange?.balanceAs = newBalance
        if(exchange?.status == Statuses.ST_APPROVED){
            exchange?.status = Statuses.ST_EXECUTED
        }
    }

    CurrencyTransfer checkCurrencyTransfer(repatriationNo){
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        List<String> statuses = [Statuses.ST_TRANSFERRED, Statuses.ST_STORED]
        int res = CurrencyTransfer.findAllByRepatriationNoAndStatusInList(repatriationNo, statuses).size()
        if(res>0){
            currencyTransfer.errors.reject("currencyTransfer.repatriation.used","This repatriation has already linked to a currency Transfer")
        }
        currencyTransfer
    }

    def loadRepatriationFromParams(params){
        CurrencyTransfer currencyTransfer  = new CurrencyTransfer()
        Repatriation repatriation = findRepatriationByParams(params)

        if(repatriation){
            currencyTransfer = checkCurrencyTransfer(repatriation.requestNo)
            if(currencyTransfer.hasErrors()){
                return currencyTransfer
            }
            currencyTransfer = defineCurrencyFromRepat(repatriation,currencyTransfer)
        }else{
            currencyTransfer.errors.reject("currencyTransfer.repatriation.inexistent", "The Repatriation doesn\'t exist")
        }
        currencyTransfer
    }

    CurrencyTransfer defineCurrencyFromRepat(Repatriation repatriation, CurrencyTransfer currencyTransfer){
        if((repatriation?.status.toString().equalsIgnoreCase(Statuses.ST_CONFIRMED)) || (repatriation.natureOfFund == NatureOfFund.NOF_PRE && repatriation.status.equalsIgnoreCase(Statuses.ST_CEDED))){
            currencyTransfer?.repatriationNo = repatriation?.requestNo
            currencyTransfer?.repatriationDate = repatriation.requestDate
            currencyTransfer?.bankCode = repatriation?.repatriationBankCode
            currencyTransfer?.bankName = repatriation?.repatriationBankName
            currencyTransfer?.currencyCode = repatriation?.currencyCode
            currencyTransfer?.currencyName = repatriation?.currencyName
            currencyTransfer?.currencyRate = repatriation?.currencyRate
            currencyTransfer?.amountTransferred = BigDecimal.ZERO
            currencyTransfer?.amountTransferredNat = BigDecimal.ZERO
            currencyTransfer?.amountRepatriated = repatriation?.receivedAmount
            currencyTransfer?.amountRepatriatedNat = repatriation?.receivedAmountNat
            currencyTransfer?.currencyTransferDate = new LocalDate()
            if(repatriation.clearances.size() > 0){
                repatriation.clearances?.each { ClearanceOfDom clearanceOfDom ->
                    if(clearanceOfDom.status){
                        ClearanceDomiciliation itemClearances = new ClearanceDomiciliation(
                                rank: clearanceOfDom?.rank,
                                ecReference: clearanceOfDom?.ecReference,
                                ecDate: clearanceOfDom?.ecDate,
                                ecExporterName: clearanceOfDom?.repats?.nameAndAddress,
                                domiciliationCodeBank: clearanceOfDom?.bankCode?:checkingCurrencyOfExchangeData(clearanceOfDom.ecReference)?.bankCode,
                                domiciliationNo: clearanceOfDom?.domiciliationNo,
                                domiciliationDate: clearanceOfDom?.domiciliationDate,
                                domiciliatedAmounttInCurr: clearanceOfDom?.domAmtInCurr,
                                invoiceFinalAmountInCurr: clearanceOfDom?.invFinalAmtInCurr,
                                repatriatedAmountToBank: clearanceOfDom?.repatriatedAmtInCurr,
                                amountTransferredInCurr: BigDecimal.ZERO,
                        )
                        currencyTransfer?.addToClearanceDomiciliations(itemClearances)
                    }
                }
            }else{
                currencyTransfer.isPrefinancingWithoutEC = true
            }
        }else{
            currencyTransfer.errors.reject("currencyTransfer.repatriation.status.invalid", "The Repatriation doesn't have a valid status")
        }
        currencyTransfer
    }

    def setCurrencyFromRepatriation(Repatriation repatriation){
        CurrencyTransfer currencyTransfer = CurrencyTransfer.findByRepatriationNoAndStatusNotEqual(repatriation.requestNo, Statuses.ST_CANCELLED)
//            CurrencyTransfer currencyTransfer = CurrencyTransfer.findByRepatriationNo(repatriation.requestNo)
            repatriation.clearances?.each { ClearanceOfDom clearanceOfDom ->
                ClearanceDomiciliation clearance = currencyTransfer.getClearanceDomiciliation(clearanceOfDom?.rank,clearanceOfDom?.ecReference)
                if(clearance){
                    if(clearanceOfDom.status){ // Metre à jour
                        clearance.rank= clearanceOfDom?.rank
                        clearance.ecReference= clearanceOfDom?.ecReference
                        clearance.ecDate= clearanceOfDom?.ecDate
                        clearance.ecExporterName= clearanceOfDom?.repats?.nameAndAddress
                        clearance.domiciliationCodeBank= clearanceOfDom?.bankCode?:checkingCurrencyOfExchangeData(clearanceOfDom.ecReference)?.bankCode
                        clearance.domiciliationNo= clearanceOfDom?.domiciliationNo
                        clearance.domiciliationDate= clearanceOfDom?.domiciliationDate
                        clearance.domiciliatedAmounttInCurr= clearanceOfDom?.domAmtInCurr
                        clearance.invoiceFinalAmountInCurr= clearanceOfDom?.invFinalAmtInCurr
                        clearance.repatriatedAmountToBank= clearanceOfDom?.repatriatedAmtInCurr
                        clearance.repatClearance = clearanceOfDom.id
                        clearance.save(flush: true, deepValidate: false, validate: false)
                    }else{ // Supprimer
                        currencyTransfer.removeFromClearanceDomiciliations(clearance)
                    }
                }else{ // Créer
                    if(clearanceOfDom.status){
                        ClearanceDomiciliation itemClearances = new ClearanceDomiciliation(
                                rank: clearanceOfDom?.rank,
                                ecReference: clearanceOfDom?.ecReference,
                                ecDate: clearanceOfDom?.ecDate,
                                ecExporterName: clearanceOfDom?.repats?.nameAndAddress,
                                domiciliationCodeBank: clearanceOfDom?.bankCode?:checkingCurrencyOfExchangeData(clearanceOfDom.ecReference)?.bankCode,
                                domiciliationNo: clearanceOfDom?.domiciliationNo,
                                domiciliationDate: clearanceOfDom?.domiciliationDate,
                                domiciliatedAmounttInCurr: clearanceOfDom?.domAmtInCurr,
                                invoiceFinalAmountInCurr: clearanceOfDom?.invFinalAmtInCurr,
                                repatriatedAmountToBank: clearanceOfDom?.repatriatedAmtInCurr,
                                amountTransferredInCurr: BigDecimal.ZERO,
                                repatClearance: clearanceOfDom.id
                        )
                        currencyTransfer?.addToClearanceDomiciliations(itemClearances)
                    }
                }
            }
            currencyTransfer.merge(flush: true, deepValidate: false, validate: false)
    }

    Repatriation findRepatriationByParams(def params){
        if(params?.repatriationNo && params?.repatriationDate){
            Repatriation.findByRequestNoAndRequestDate(params?.repatriationNo, TypeCastUtils.toLocalDate(params?.repatriationDate))
        } else if(params?.id){
            Repatriation.findById(params?.id)
        }
    }

    def findRepatriationByRequestNoAndDate(repatriationNo, repatriationDate){
        if(repatriationDate && repatriationNo){
            Repatriation.findByRequestNoAndRequestDate(repatriationNo, repatriationDate)
        }
    }

    def handleSetCancelRepatriationClearanceOfDom(Repatriation repatriation) {
        List<ClearanceOfDom> clearanceOfDoms = repatriation?.clearances
        clearanceOfDoms?.each {ClearanceOfDom clearance ->
            Exchange exchange = Exchange.findByRequestNo(clearance.ecReference)
            if (exchange) {
                def activeRepatriation = getActiveRepatriationByEcReference(clearance.ecReference, repatriation.id)
                exchange.status = activeRepatriation?.size() == 0 ? Statuses.ST_APPROVED : exchange.status
                if(exchange.status == Statuses.ST_APPROVED){
                    exchange.isFinalAmount = exchange.isFinalAmount?:false
                }
                BigDecimal amount = exchange.balanceAs?: BigDecimal.ZERO
                BigDecimal balance = amount + clearance.repatriatedAmtInCurr
                exchange.balanceAs = balance
                exchange.merge(flush: true, deepValidate: false, validate: false)

                Locale locale = LocaleContextHolder.getLocale()
                Object[] exParams = [messageSource.getMessage("natureOfFund.${repatriation.natureOfFund}", null, locale), repatriation.requestNo, messageSource.getMessage("status.${exchange.status}", null, locale) ]
                loggerService.addMessage(exchange, messageSource.getMessage("efem.cancel.ec.fromRepats", exParams, locale))

                Object[] repatParams = [ clearance.ecReference, messageSource.getMessage("natureOfFund.${repatriation.natureOfFund}", null, locale), messageSource.getMessage("status.${repatriation.status}", null, locale) ]
                loggerService.addMessage(repatriation, messageSource.getMessage("repats.cancel.ec", repatParams, locale))

            }
        }
    }

    def logConcernedEC(Repatriation repatriation){
        repatriation.clearances.findAll {
            if(it.status){
                Exchange exchangeAuthorization = Exchange.findByRequestNo(it.ecReference)
                loggerService.saveDocumentHistory(exchangeAuthorization, repatriation.status, Operations.OP_UPDATE)
            }
        }
    }

    def logConcernedEcDeleted(Repatriation repatriation, List<String> ecRefToBeDeleted){
        Locale locale = LocaleContextHolder.getLocale()
        ecRefToBeDeleted?.each {ecReference ->
            Exchange exchange = Exchange.findByRequestNo(ecReference)
            Object[] repatParams = [ ecReference, messageSource.getMessage("natureOfFund.${repatriation.natureOfFund}", null, locale), messageSource.getMessage("status.${repatriation.status}", null, locale) ]
            Object[] exParams = [messageSource.getMessage("natureOfFund.${repatriation.natureOfFund}", null, locale), repatriation.requestNo, messageSource.getMessage("status.${exchange.status}", null, locale) ]
            loggerService.addMessage(exchange, messageSource.getMessage("efem.deleted.ec.fromRepats", exParams, locale))
            loggerService.addMessage(repatriation, messageSource.getMessage("repats.deleted.ec", repatParams, locale))
        }
    }

    def getActiveRepatriationByEcReference(String ecReference, Long repat_id) {
        def criteria = ClearanceOfDom.createCriteria()
        return criteria.list {
            eq("ecReference", ecReference)
            createAlias('repats', 'r', JoinType.INNER_JOIN)
            ne("r.id", repat_id)
            'in'("r.status", Statuses.CHECKED_STATUS_FOR_FINAL_AMOUNT)
            resultTransformer(new AliasToBeanResultTransformer(Repatriation.class))
            projections {
                property("r.id", "id")
                property("r.requestNo", "requestNo")
                property("r.status", "status")
            }
        }
    }

    def updatePrefinancingRepatriation(Repatriation repatriation) {
        List<ClearanceOfDom> listOfNewClearances = repatriation.clearances.findAll {!it.id }
        listOfNewClearances.each { ClearanceOfDom dom ->
            Exchange exchange = Exchange.findByRequestNo(dom.ecReference)
            if (exchange) {
                setNewBalanceAndStatus(dom, exchange)
                exchange.save(flush: true, deepValidate: false, validate: false)
            }
        }
    }

    void updateEcDeleted(Repatriation result, List<LinkedHashMap> ecChanged = []){
        if(ecChanged.size() > 0){
            handleClearanceDeleted(ecChanged, result)
        }
    }

    void updateEcAmountUpdated(Repatriation result, List<LinkedHashMap> ecChanged = []){
        if(ecChanged.size() > 0){
            handleClearanceAmountUpdated(ecChanged, result)
        }
    }

    void handleClearanceDeleted( clearances,Repatriation repatriation){
        clearances?.each { clearance ->
            ClearanceOfDom dom = repatriation?.clearances?.find{ clearance.ecReference == it.ecReference && clearance.rank == it.rank && clearance.status != it.status }
            if(dom){
                updateEcOfClearanceDeleted(clearance)
            }
        }
    }

    void handleClearanceAmountUpdated(clearances,Repatriation repatriation){
        BigDecimal amountDiff = BigDecimal.ZERO
        clearances?.each { clearance ->
            ClearanceOfDom dom = repatriation?.clearances?.find{ clearance.ecReference == it.ecReference && clearance.rank == it.rank && clearance.repatriatedAmtInCurr != it.repatriatedAmtInCurr }
            if(dom){
                amountDiff = dom.repatriatedAmtInCurr - clearance.repatriatedAmtInCurr
                updateEcOfClearanceUpdated(clearance, amountDiff)
            }
        }
    }

    void updateEcOfClearanceDeleted(clearance) {
        Exchange exchange = Exchange.findByRequestNo(clearance?.ecReference)
        exchange?.balanceAs = exchange?.balanceAs + clearance?.repatriatedAmtInCurr
        Integer result = ClearanceOfDom.findAllByEcReferenceAndIdNotEqual(clearance?.ecReference, clearance?.id)
                ?.count { it?.repats?.status in Statuses.CHECKED_STATUS_FOR_FINAL_AMOUNT && it.status == true }
        if (result == BigDecimal.ZERO) {
            exchange?.status = Statuses.ST_APPROVED
            exchange.isFinalAmount = exchange.isFinalAmount?:false
        }
        exchange.merge(flush: true, deepValidate: false, validate: false)
        loggerService.saveDocumentHistory(exchange, exchange?.status, Operations.OP_UPDATE)
    }

    void updateEcOfClearanceUpdated(clearance, BigDecimal amountDiff) {
        Exchange exchange = Exchange.findByRequestNo(clearance?.ecReference)
        exchange?.balanceAs = exchange?.balanceAs - amountDiff
        exchange.merge(flush: true, deepValidate: false, validate: false)
    }

    BigDecimal loadRepatriatedAmountFromClearance(Long repatriationId, Integer rank, String ecReference){
        Repatriation.findById(repatriationId)?.getClearanceOfDom(rank, ecReference)?.repatriatedAmtInCurr?:BigDecimal.ZERO
    }

}
