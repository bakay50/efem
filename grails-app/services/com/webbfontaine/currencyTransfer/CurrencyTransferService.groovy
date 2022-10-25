package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.CurrencyTransferConstants
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.ibm.icu.text.DecimalFormat
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.efem.rules.currencyTransfer.TransferCurrencyCodeRule
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
@Transactional
class CurrencyTransferService {

    SessionStore sessionStoreService
    def messageSource
    def grailsApplication
    def currencyTransferBusinessLogicService
    def currencyTransferWorkflowService
    def repatriationService
    def loggerService

    def addToSessionStore(CurrencyTransfer currencyTransfer, conversationId = null) {
        if (conversationId) {
            sessionStoreService.put(conversationId, currencyTransfer)
        } else {
            sessionStoreService.put(currencyTransfer)
        }
    }

    def findFromSessionStore(def conversationId) {
        sessionStoreService.get(conversationId) as CurrencyTransfer
    }

    Exchange retrieveExchangeData(ecReference) {
        Exchange exchange = findECDocument(ecReference)
        return validateExchangeData(exchange)
    }

    def checkExchangeStatus(Exchange exchange) {
        return (exchange?.status != Statuses.ST_EXECUTED)
    }

    def checkExchangeCurrencyCode(Exchange exchange) {
        return (exchange?.currencyCode in CurrencyTransferConstants.ALLOWED_CURRENCYCODE)
    }

    def checkExchangeGeoArea(Exchange exchange) {
        return (!exchange?.geoArea?.contains(CurrencyTransferConstants.ALLOWED_AREA))
    }

    Exchange validateExchangeData(Exchange exchange) {
        if (!exchange) {
            exchange = Exchange.newInstance()
            exchange.errors.rejectValue("requestNo",
                    "repatriation.correctEc.error",
                    "Please type valide reference of exchange")
        } else if (checkExchangeStatus(exchange)) {
            exchange.errors.rejectValue("status",
                    "repatriation.status.error",
                    "The Exchange Commitment is not into the correct status.")
        } else if (checkExchangeGeoArea(exchange)) {
            exchange.errors.rejectValue("geoArea",
                    "repatriation.AreaGeo.error",
                    "This foreign exchange commitment {0} can not be submitted to currency repatriation")
        } else if (checkExchangeCurrencyCode(exchange)) {
            exchange.errors.rejectValue("currencyCode",
                    "clerance.errors.currency.code.label",
                    "Exchange Commitment should be in the foreign currency")
        }
        exchange
    }

    def findECDocument(ecReference) {
        return Exchange.findByRequestNoAndRequestType(ecReference, CurrencyTransferConstants.REQUEST_TYPE_EC)
    }

    def doCheckAddClearance(CurrencyTransfer currencyTransfer, ClearanceDomiciliation domiciliation) {
        currencyTransfer?.addClearanceDomiciliation(domiciliation)
        def allPrevAmount = currencyTransfer?.clearanceDomiciliations?.findAll {
            it?.ecReference == domiciliation?.ecReference && !it?.id
        }?.amountTransferredInCurr?.sum()
        def totalSumRep = allPrevAmount ?: BigDecimal.ZERO
        def total = totalSumRep
        def result = total?.compareTo(currencyTransfer?.amountTransferred)
        getErrorAmountTransferredInCurr(domiciliation, currencyTransfer)
        if (result == 1) {
            currencyTransfer?.removeClearanceDomiciliation(domiciliation)
            if (currencyTransfer?.clearanceDomiciliations?.size() > 0) {
                domiciliation.errors.rejectValue(CurrencyTransferConstants.amountTransferredInCurr,
                        'clerance.errors.amountTransferredInCurr.sum',
                        'The sum of the amounts transferred in foreign currency for this EC ' +
                                'reference foreign exchange liability must not exceed the amount transferred.')
            } else {
                domiciliation.errors.rejectValue(CurrencyTransferConstants.amountTransferredInCurr,
                        'clerance.errors.amountTransferredInCurr.Great',
                        'The sum of Amount in Currency for this Exchange Commitment EC ' +
                                'Reference must not exceed the Final Invoice Amount in Currency')
            }
        }
        domiciliation
    }

    def doPersist(CurrencyTransfer currencyTransfer, Operation commitOperation) {
        OperationHandlerService opHandler = currencyTransferBusinessLogicService.resolveOperation(commitOperation)
        CurrencyTransfer.withTransaction { transactionStatus ->
            currencyTransfer = opHandler.execute(currencyTransfer, transactionStatus, commitOperation)
        }
        currencyTransfer
    }

    def generateRequestNumber(CurrencyTransfer domainInstance) {
        String nbr = new DecimalFormat("000000").format(domainInstance?.requestNumberSequence)
        return "${domainInstance?.requestDate?.getYear()}${nbr}"
    }

    def checkCommitOperation(params) {
        def commitOperation = currencyTransferWorkflowService.getCommitOperation(params)
        if (commitOperation) {
            params.commitOperation = Operation.valueOf(commitOperation.id)
            params.commitOperationName = currencyTransferWorkflowService.getOperationName(params?.commitOperation)
        }

        if (currencyTransferWorkflowService.validationNotRequired(params?.commitOperation)) {
            params.validationNotRequired = true
        } else {
            params.validationRequired = true
        }
    }

    def loadCurrencyTransfer(id) {
        CurrencyTransfer.findById(id, [fetch: [attachedDocs: CurrencyTransferConstants.joinAssociation,
                                                logs: CurrencyTransferConstants.joinAssociation]
        ])
    }

    def checkCurrencyCode(Exchange exchange) {
        def rules = [new TransferCurrencyCodeRule()]
        RuleContext ruleContext = new RuleContext(exchange, exchange.errors)
        RuleUtils.executeSetOfRules(rules, ruleContext)
    }

    BigDecimal setRepatriatedAmountToBank(Exchange exchange) {
        String bankCode = UserUtils.getUserProperty(UserProperties.BNK) ?: UserUtils.getUserProperty(UserProperties.ADB)
        List<ClearanceOfDom> repatriatedEc = ClearanceOfDom.findAllByEcReferenceAndBankCode(exchange.requestNo, bankCode)
        BigDecimal allRepatriatedAmtInCurr = repatriatedEc*.repatriatedAmtInCurr?.sum() as BigDecimal
        BigDecimal totalAmount = allRepatriatedAmtInCurr ?: BigDecimal.ZERO
        return totalAmount
    }

    BigDecimal handleSetTransferRate(CurrencyTransfer currencyTransfer) {

        BigDecimal out = BigDecimal.ZERO
        if (currencyTransfer.amountTransferred && currencyTransfer.amountRepatriated) {

            out = (currencyTransfer.amountTransferred / currencyTransfer.amountRepatriated) * 100
        }
        return out
    }

    private static def getErrorAmountTransferredInCurr(ClearanceDomiciliation domiciliation, CurrencyTransfer currencyTransfer) {
        if (domiciliation?.amountTransferredInCurr == null || domiciliation?.amountTransferredInCurr in CurrencyTransferConstants.ZERO) {
            currencyTransfer.removeClearanceDomiciliation(domiciliation)
            domiciliation.errors.rejectValue(CurrencyTransferConstants.amountTransferredInCurr,
                    'clerance.errors.amountTransferredInCurr.zero',
                    'The Amount transferred in Currency must be greater than zero.')
        }
    }

    def setAmountTransferred(CurrencyTransfer currencyTransfer) {
        List<ClearanceDomiciliation> domiciliationList = currencyTransfer?.clearanceDomiciliations?.findAll()
        BigDecimal allAmountTransferredInCurr = domiciliationList?.sum { it.amountTransferredInCurr ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        currencyTransfer.amountTransferred = allAmountTransferredInCurr
    }

    def updateRepatClearance(CurrencyTransfer currencyTransfer, def operation){
        if(operation == Operation.UPDATE_TRANSFERRED){
            currencyTransfer.clearanceDomiciliations.each { clearance ->
                if(clearance?.repatClearance > 0 && clearance.amountTransferredInCurr > BigDecimal.ZERO){
                    ClearanceOfDom repatClearance = ClearanceOfDom.findById(clearance?.repatClearance)
                    repatClearance.ceded = true
                    repatClearance.save(flush: true, deepValidate: false, validate: false)
                }
            }
        }
    }

    def updateClearanceOfDom(CurrencyTransfer currencyTransfer, List<ClearanceOfDom> clearanceOfDoms, def operation){
        boolean ceded = true
        clearanceOfDoms?.each {ClearanceOfDom clearance ->
            Exchange exchange = Exchange.findByRequestNo(clearance.ecReference)
            if (exchange && operation in [Operation.CANCEL_TRANSFERRED]) {
                ceded = false
                Locale locale = LocaleContextHolder.getLocale()
                Object[] exParams = [messageSource.getMessage("currencyTransfer.label", null, locale), currencyTransfer.requestNo, messageSource.getMessage("status.${exchange.status}", null, locale) ]
                loggerService.addMessage(exchange, messageSource.getMessage("efem.cancel.ec.fromRepats", exParams, locale))

                if(!currencyTransfer.isPrefinancingWithoutEC){
                    Object[] currencyParams = [ clearance.ecReference, messageSource.getMessage("currencyTransfer.label", null, locale), messageSource.getMessage("status.${currencyTransfer.status}", null, locale) ]
                    loggerService.addMessage(currencyTransfer, messageSource.getMessage("repats.cancel.ec", currencyParams, locale))
                }
            }
            clearance.ceded = ceded
            clearance.save(flush: true, deepValidate: false, validate: false)
        }
    }

    def updateRepatriation(CurrencyTransfer currencyTransfer, def operation){
        Repatriation repatriation = repatriationService.findRepatriationByRequestNoAndDate(currencyTransfer.repatriationNo, currencyTransfer.repatriationDate)
        String logOperation = Statuses.ST_CEDED
        List<ClearanceOfDom> clearanceOfDoms = repatriation?.clearances
        if(operation in [Operation.TRANSFER, Operation.TRANSFER_STORED]){
            repatriation.setStatus(Statuses.ST_CEDED)
        } else if(operation in [Operation.CANCEL_TRANSFERRED]){
            logOperation = Statuses.ST_CANCELLED
            repatriation.setStatus(Statuses.ST_CONFIRMED)

            if(currencyTransfer.isPrefinancingWithoutEC){
                Object[] repParams = [repatriation.requestNo,currencyTransfer.requestNo ]
                loggerService.addMessage(currencyTransfer, messageSource.getMessage("efem.cancel.currencyTransfer", repParams, Locale.FRENCH))
                loggerService.addMessage(repatriation, messageSource.getMessage("efem.cancel.currencyTransfer", repParams, Locale.FRENCH))
            }
        }
        updateClearanceOfDom(currencyTransfer, clearanceOfDoms, operation)
        repatriation.save(flush: true, deepValidate: false, validate: false)
        loggerService.saveDocumentHistory(repatriation, repatriation.status, logOperation)
    }
}
