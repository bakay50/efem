package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.supportInfo.SupportInformationUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.security.josso.Constants
import com.webbfontaine.grails.plugins.utils.TypesCastUtils
import grails.gorm.transactions.Transactional

import static com.webbfontaine.efem.constants.ExchangeRequestType.*
import static com.webbfontaine.efem.workflow.Operation.*

@Transactional
class BusinessLogicService {

    def exchangeWorkflowService
    def repatriationWorkflowService
    OperationHandlerService storeOperationHandlerService
    OperationHandlerService requestStoredOperationHandlerService
    OperationHandlerService requestFromNullOperationHandlerService
    OperationHandlerService requestQueriedOperationHandlerService
    OperationHandlerService clientOperationHandlerService
    OperationHandlerService approveOperationHandlerService
    OperationHandlerService updateApprovedOperationHandlerService
    OperationHandlerService queryOperationHandlerService
    OperationHandlerService updateExecutedOperationHandlerService
    OperationHandlerService updateQueriedOperationHandlerService
    OperationHandlerService cancelOperationHandlerService
    OperationHandlerService rejectOperationHandlerService
    DeclarantService declarantService
    TaxpayerService taxpayerService
    def jossoConfigService
    def executionService

    OperationHandlerService resolveOperation(Operation operation) {
        def operationHandler

        switch (operation) {
            case STORE:
            case UPDATE_STORED:
                operationHandler = storeOperationHandlerService
                break

            case REQUEST:
                operationHandler = requestFromNullOperationHandlerService
                break

            case REQUEST_STORED:
                operationHandler = requestStoredOperationHandlerService
                break

            case QUERY_REQUESTED:
            case QUERY_PARTIALLY_APPROVED:
                operationHandler = queryOperationHandlerService
                break

            case UPDATE_QUERIED:
                operationHandler = updateQueriedOperationHandlerService
                break

            case REQUEST_QUERIED:
                operationHandler = requestQueriedOperationHandlerService
                break

            case DOMICILIATE:
            case APPROVE_REQUESTED:
            case PARTIALLY_APPROVED:
                operationHandler = approveOperationHandlerService
                break

            case UPDATE_APPROVED:
                operationHandler = updateApprovedOperationHandlerService
                break

            case UPDATE_EXECUTED:
                operationHandler = updateExecutedOperationHandlerService
                break

            case CANCEL_QUERIED:
            case CANCEL_APPROVED:
                operationHandler = cancelOperationHandlerService
                break

            case REJECT_PARTIALLY_APPROVED:
            case REJECT_REQUESTED:
                operationHandler = rejectOperationHandlerService
                break

            default:
                operationHandler = clientOperationHandlerService
                break
        }

        if (operationHandler) {
            return operationHandler
        } else {
            throw new UnsupportedOperationException("Operation ${operation.humanName()} not supported!")
        }

    }

    def initDocumentForCreate(Exchange exchange, Map params) {
        exchange.startedOperation = CREATE
        exchangeWorkflowService.initOperations(exchange)
        exchange.basedOn = params?.basedOn
        exchange.requestType = params?.requestType
        exchange.currencyRate = exchange.currencyRate ?: new BigDecimal(params?.currencyRate ?: '0.00')
        exchange.currencyPayRate = exchange.currencyPayRate ?: new BigDecimal(params?.currencyPayRate ?: '0.00')

        if (BASE_ON_SAD.equals(exchange.basedOn)) {
            exchange.clearanceOfficeCode = params.clearanceOfficeCode
            exchange.clearanceOfficeName = exchange.clearanceOfficeName ?: params.clearanceOfficeName
            exchange.declarationSerial = params.declarationSerial
            exchange.declarationNumber = params.declarationNumber
            exchange.declarationDate = TypesCastUtils.toDate(params.declarationDate)
        }

        if (EA.equals(exchange?.requestType)) {
            taxpayerService.setImporterDetails(exchange)
        } else {
            taxpayerService.setExporterDetails(exchange)
            BusinessLogicUtils.setBaseOnForEcDocument(exchange)
        }

        declarantService.setDeclarantDetails(exchange)
    }


    def initDocumentForView(Exchange exchange) {
        def domainStatus = exchange.status.toUpperCase().replaceAll("\\s", "_")
        exchange.startedOperation = "VIEW_${domainStatus}" as Operation
        exchange.setIsExecutionEditable(false)
        exchange.setIsSupDeclarationEditable(false)
        exchange.isAttachmentEditable = false
        executionService.setExchangeAmountSettledFields(exchange)
    }

    def initDocumentForEdit(domain, Map params) {
        if (domain instanceof Exchange) {
            exchangeWorkflowService.initOperationsForEdit(domain, params)
        } else {
            repatriationWorkflowService.initOperationsForEdit(domain, params)
        }
        BusinessLogicUtils.setAttachmentAccess(domain)
        executionService.setExchangeAmountSettledFields(domain)
    }

    @Transactional(readOnly = true)
    def getHistory(domain) {
        def transactionRecords = domain.logs
        transactionRecords
        def domainLogs = [:]
        transactionRecords.each {
            String key = SupportInformationUtils.constructLogDate(it.operationDate)
            if ((key in domainLogs.keySet())) {
                Collection logs = domainLogs.get(key)
                logs << SupportInformationUtils.constructLogMessage(it)
            } else {
                domainLogs.put(key, [SupportInformationUtils.constructLogMessage(it)])
            }
        }
        domainLogs
    }

    def getUsersADB() {
        def bankCodelist
        if (jossoConfigService) {
            def jossoAdmin = AppConfig.getJossoAdmin()
            def bankCodes = jossoConfigService.userRoleManager.findUsersByRole(Constants.DEFAULT_SECURITY_DOMAIN, jossoAdmin, [UtilConstants.BANK_ROLE] as String[], null)*.properties.flatten()
            bankCodelist = bankCodes?.findAll {
                it.name == UserProperties.ADB
            }?.value
            return splitBankCodeList(bankCodelist)

        }

    }

    def splitBankCodeList(bankCodeList) {
        ArrayList splitBankCode = []
        for (bankCode in bankCodeList) {
            if (bankCode.contains(":")) {
                splitBankCode = bankCode.split(":") + splitBankCode
            }
        }
        return splitBankCode ? (bankCodeList + splitBankCode) : bankCodeList

    }

}
