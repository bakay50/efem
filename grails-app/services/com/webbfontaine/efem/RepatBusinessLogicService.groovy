package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.supportInfo.SupportInformationUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.security.josso.Constants
import grails.gorm.transactions.Transactional

import static com.webbfontaine.efem.workflow.Operation.*

@Transactional
class RepatBusinessLogicService {
    def repatriationWorkflowService
    def jossoConfigService
    def taxpayerService

    OperationHandlerService storeRepOperationHandlerService
    OperationHandlerService declareRepFromNullAndStoredOperationHandlerService
    OperationHandlerService declareRepQueriedOperationHandlerService
    OperationHandlerService confirmRepOperationHandlerService
    OperationHandlerService queryRepOperationHandlerService
    OperationHandlerService clientRepOperationHandlerService
    OperationHandlerService cancelRepOperationHandlerService
    OperationHandlerService updateConfirmOperationHandlerService
    OperationHandlerService updateQueriedRepOperationHandlerService
    OperationHandlerService updateClearanceOperationHandlerService

    OperationHandlerService resolveOperation(Operation operation) {
        def operationHandler
        switch (operation) {
            case STORE:
            case UPDATE_STORED:
                operationHandler = storeRepOperationHandlerService
                break
            case DECLARE:
            case DECLARE_STORED:
                operationHandler = declareRepFromNullAndStoredOperationHandlerService
                break
            case DECLARE_QUERIED:
                operationHandler = declareRepQueriedOperationHandlerService
                break
            case CONFIRM:
            case CONFIRM_STORED:
            case CONFIRM_DECLARED:
                operationHandler = confirmRepOperationHandlerService
                break
            case QUERY_DECLARED:
                operationHandler = queryRepOperationHandlerService
                break
            case CANCEL_QUERIED:
            case CANCEL_CONFIRMED:
                operationHandler = cancelRepOperationHandlerService
                break
            case UPDATE_CONFIRMED:
                operationHandler = updateConfirmOperationHandlerService
                break
            case UPDATE_QUERIED:
                operationHandler = updateQueriedRepOperationHandlerService
                break
            case UPDATE_CLEARANCE:
                operationHandler = updateClearanceOperationHandlerService
                break
            default:
                operationHandler = clientRepOperationHandlerService
                break
        }

        if (operationHandler) {
            return operationHandler
        } else {
            throw new UnsupportedOperationException("Operation ${operation.humanName()} not supported!")
        }

    }


    def initDocumentForCreate(Repatriation repatriation) {
        repatriation.startedOperation = CREATE
        repatriationWorkflowService.initOperations(repatriation)
        repatriation.isDocumentEditable = true
        repatriation.addAttachedDocs = true
        repatriation.editAttachedDocs = true
        repatriation?.isAttachmentEditable = true
        setImporterDetails(repatriation)
        repatriation
    }

    def initDocumentForView(Repatriation repatriation) {
        def domainStatus = repatriation.status.toUpperCase().replaceAll("\\s", "_")
        repatriation.startedOperation = "VIEW_${domainStatus}" as Operation
        repatriation.isAttachmentEditable = false
    }

    def initDocumentForEdit(domain, Map params) {
        repatriationWorkflowService.initOperationsForEdit(domain, params)
        BusinessLogicUtils.setRepatriationOperationsIsDocumentEditable(domain)
        BusinessLogicUtils.setAttachmentAccess(domain)
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

    def setImporterDetails(Repatriation repatriation) {
        def importer = taxpayerService.getCompany()
        if (importer) {
            repatriation.with {
                code = importer.code
                nameAndAddress = importer.description + "\n" + BusinessLogicUtils.concatenateAddresses(importer.address1, importer.address2,
                        importer.address3, importer.address4)
            }
        }
    }
}
