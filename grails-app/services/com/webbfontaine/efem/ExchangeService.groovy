package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.rimm.RimmGeoArea
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import static com.webbfontaine.efem.constants.Statuses.CHECKED_STATUS_FOR_AMOUNT_RULE
import static com.webbfontaine.efem.workflow.Operation.APPROVE_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.DOMICILIATE
import java.math.RoundingMode
import java.text.DecimalFormat
import static com.webbfontaine.efem.UserUtils.*
import static com.webbfontaine.efem.constants.ExchangeRequestType.*
import static com.webbfontaine.efem.security.Roles.*

@Slf4j("LOGGER")
@Transactional
class ExchangeService {

    SessionStore sessionStoreService
    def businessLogicService
    def exchangeWorkflowService
    def beanDataLoadService

    def addToSessionStore(Exchange exchange, conversationId = null) {
        if (conversationId) {
            sessionStoreService.put(conversationId, exchange)
        } else {
            sessionStoreService.put(exchange)
        }
    }

    def findFromSessionStore(def conversationId) {
        sessionStoreService.get(conversationId) as Exchange
    }

    def loadExchange(id) {
        Exchange.findById(id, [fetch: [attachedDocs: 'join', executions: 'join', logs: 'join', supDeclarations: 'join']])
    }

    def doPersist(Exchange exchange, Operation commitOperation) {
        OperationHandlerService opHandler = businessLogicService.resolveOperation(commitOperation)

        Exchange.withTransaction { transactionStatus ->
            exchange = opHandler.execute(exchange, transactionStatus, commitOperation)

        }

        exchange
    }

    def generateRequestNumber(Exchange domainInstance) {
        String nbr = new DecimalFormat("000000").format(domainInstance?.requestNumberSequence)
        return "${domainInstance?.requestType}${domainInstance?.requestDate.getYear()}${nbr}"
    }

    def checkCommitOperation(params) {
        def commitOperation = exchangeWorkflowService.getCommitOperation(params)
        if (commitOperation) {
            params.commitOperation = Operation.valueOf(commitOperation.id)
            params.commitOperationName = exchangeWorkflowService.getOperationName(params?.commitOperation)
        }

        if (exchangeWorkflowService.validationNotRequired(params?.commitOperation)) {
            params.validationNotRequired = true
        } else {
            params.validationRequired = true
        }
    }

    BigDecimal setBalanceAs(Exchange domainInstance) {
        LOGGER.debug("Setting up Balance with Amount ${domainInstance?.amountMentionedCurrency ?: BigDecimal.ZERO}")
        def amountMentionedCurrency = domainInstance?.amountMentionedCurrency ?: BigDecimal.ZERO
        def finalAmountInDevise = domainInstance?.finalAmountInDevise ?: BigDecimal.ZERO
        return amountMentionedCurrency + finalAmountInDevise
    }

    def checkReferenceAutorisation(Exchange exchangeInstance) {
        if (exchangeInstance?.attachedDocs?.size() > 0
                && exchangeInstance.requestType == EA
                && !(exchangeInstance?.currencyPayCode in EXCEPTION_CURRENCY)
                && ((exchangeInstance?.countryProvenanceDestinationCode in EXCEPTION_COUNTRY)
                || (exchangeInstance?.countryProvenanceDestinationCode in CI_CURRENCY))) {
            exchangeInstance.errors.rejectValue("attachedDocs", "default.invoiceautorisation.message", "Veuillez joindre la Lettre d'autorisation de détention de compte étranger en devise (code 6059) au dossier SVP");
        }
    }

    def getCurrencyRate(def currencyCode) {
        def criteria = [code: currencyCode]
        def htDate = new Date().clearTime()
        beanDataLoadService.loadFullBeanData("HT_RATE", criteria, "get", "", htDate)?.exchangeRate
    }

    def supDeclarationChecking(Exchange exchange, SupDeclaration supDeclaration) {
        if (exchange?.sadInstanceId && exchange?.sadStatus && exchange?.sadStatus != Statuses.ST_PAID) {
            supDeclaration.errors.rejectValue('clearanceOfficeCode', 'load.sad.status.error', 'The declaration doesn’t have valid status')
        } else if (!exchange.isDocumentValid) {
            supDeclaration.errors.rejectValue('clearanceOfficeCode', 'load.sad.status.error', 'The declaration doesn’t have valid status')
        } else if (!exchange.isSadOwner) {
            supDeclaration.errors.rejectValue('clearanceOfficeCode', 'load.sad.ownership.error', 'The declaration cannot be used by the connected user')
        } else if (exchange?.sadInstanceId == null) {
            supDeclaration.errors.rejectValue('clearanceOfficeCode', 'exchange.errors.existe.declarationNumber', 'The declaration doesn\'t exist. ')
        }
    }

    def computationOfBalanceAs(Exchange exchangeInstance) {
        BigDecimal totalCurrency = 0
        exchangeInstance.executions?.amountMentionedExCurrency?.each { BigDecimal eachCurrency ->
            totalCurrency = eachCurrency + totalCurrency
        }
        exchangeInstance.totalAmountMentionedExCurrency = totalCurrency
        exchangeInstance.balanceAs = exchangeInstance?.amountMentionedCurrency - exchangeInstance?.totalAmountMentionedExCurrency
        return exchangeInstance?.balanceAs
    }

    BigDecimal getSumOfAmountNationalCurrency(Exchange exchange) {
        BigDecimal sumOfAmountNationalCurrency = BigDecimal.ZERO
        List notIncludedStatus = [Statuses.ST_CANCELLED, Statuses.ST_REJECTED]
        if (EA.equals(exchange?.requestType)) {
            sumOfAmountNationalCurrency = doSumOfExchangeAmount(exchange, notIncludedStatus, false, AMOUNT_IN_NATIONAL_CURRENCY, true, false)
        }
        sumOfAmountNationalCurrency
    }


    BigDecimal doSumOfExchangeAmount(Exchange exchange, statusList, inList, field, checkOnId, checkOnRequestNumber) {
        def criteria = Exchange.createCriteria()
        def sumAmountResult = criteria.list() {
            applyCriteriaOnRequestType(exchange, criteria)
            applyCriteriaOnStatus(inList, statusList, criteria)
            applyCriteriaOnRequestNumber(exchange, checkOnId, checkOnRequestNumber, criteria)
            projections {
                sum(field)
            }
        }

        sumAmountResult ? new BigDecimal(sumAmountResult[0] ?: BigDecimal.ZERO) : BigDecimal.ZERO
    }

    static void applyCriteriaOnRequestType(Exchange exchange, criteria) {
        if (exchange?.basedOn == BASE_ON_TVF) {
            criteria.eq("tvfNumber", exchange?.tvfNumber)
            criteria.eq("tvfDate", exchange?.tvfDate)
        } else {
            criteria.eq("clearanceOfficeCode", exchange?.clearanceOfficeCode)
            criteria.eq("declarationNumber", exchange?.declarationNumber)
            criteria.eq("declarationSerial", exchange?.declarationSerial)
            criteria.eq("declarationDate", exchange?.declarationDate)
        }
    }

    static void applyCriteriaOnStatus(inList, statusList, criteria) {
        if (inList) {
            criteria.'in'('status', statusList)
        } else {
            criteria.not { criteria.'in'('status', statusList) }
        }
    }

    static void applyCriteriaOnRequestNumber(exchange, checkOnId, checkOnRequestNumber, criteria) {
        if (checkOnId) {
            if (checkOnRequestNumber) {
                criteria.isNotNull("requestNo")
            } else {
                criteria.ne("requestNo", exchange?.requestNo)
            }
        } else {
            criteria.ne("id", exchange?.id)
        }
    }

    BigDecimal getConvertedCIF(Exchange exchangeInstance) {
        if (exchangeInstance?.basedOn == ExchangeRequestType.BASE_ON_SAD && exchangeInstance.requestType == ExchangeRequestType.EA) {
            BigDecimal totalConverted = new BigDecimal(exchangeInstance?.totalAmountOfCif / exchangeInstance?.currencyRate)
            exchangeInstance?.convertedCif = totalConverted.setScale(0, RoundingMode.HALF_UP)
        }
    }

    def generateRegistrationNumber(Exchange domainInstance) {
        String nbr = new DecimalFormat("000000").format(domainInstance?.registrationNumberSequence)
        return "${domainInstance?.bankCode}${domainInstance?.requestDate.getYear()}${nbr}"
    }

    static def getRequestedBy() {
        def out = isDeclarant() ? DECLARANT.authority : TRADER.authority
        LOGGER.debug("Document is requested by $out")
        return out
    }

    def setInstanceRegistrationDefaultDate(Exchange exchangeInstance) {
        if ((exchangeInstance.startedOperation == APPROVE_REQUESTED || exchangeInstance.startedOperation == DOMICILIATE) && UserUtils.isBankAgent()) {
            exchangeInstance.registrationDateBank = LocalDate.now()
        }
        return exchangeInstance
    }

    static List<Exchange> findExchangeByTvfOrSad(exchange) {
        if (exchange?.basedOn == ExchangeRequestType.BASE_ON_SAD) {
            Exchange.findAllByClearanceOfficeCodeAndDeclarationDateAndDeclarationSerialAndDeclarationNumberAndStatusInListAndRequestNoIsNotNull(
                    exchange?.clearanceOfficeCode, exchange?.declarationDate, exchange?.declarationSerial, exchange?.declarationNumber,
                    Statuses.CHECKED_STATUS_FOR_AMOUNT_RULE)
        } else if (exchange?.basedOn == ExchangeRequestType.BASE_ON_TVF) {
            Exchange.findAllByTvfNumberAndTvfDateAndStatusInListAndRequestNoIsNotNull(exchange?.tvfNumber, exchange?.tvfDate, CHECKED_STATUS_FOR_AMOUNT_RULE)
        }
    }

    def findExchangeByRequestTypeAndRequestNo(requestType, requestNo) {
        return Exchange.findByRequestTypeAndRequestNo(requestType, requestNo)
    }

    def findGeoAreaByCode(String geoArea) {
        return RimmGeoArea.withNewSession {
            RimmGeoArea.findByCode(geoArea)
        }
    }
}
