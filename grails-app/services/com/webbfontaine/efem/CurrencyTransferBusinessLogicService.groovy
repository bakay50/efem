package com.webbfontaine.efem

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import grails.gorm.transactions.Transactional
import static com.webbfontaine.efem.workflow.Operation.CANCEL_TRANSFERRED
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.TRANSFER
import static com.webbfontaine.efem.workflow.Operation.TRANSFER_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_TRANSFERRED

@Transactional
class CurrencyTransferBusinessLogicService {

    def currencyTransferWorkflowService
    def springSecurityService
    OperationHandlerService storeCurrencyTransferOperationHandlerService
    OperationHandlerService transferCurrencyTransferOperationHandlerService
    OperationHandlerService currencyTransferOperationHandlerService
    OperationHandlerService cancelCurrencyTransferOperationHandlerService
    OperationHandlerService updateCurrencyTransferOperationHandlerService

    def initDocumentForCreate(CurrencyTransfer currencyTransfer) {
        currencyTransfer.startedOperation = CREATE
        currencyTransferWorkflowService.initOperations(currencyTransfer)
        currencyTransfer.isDocumentEditable = true
        currencyTransfer.addAttachedDocs = true
        currencyTransfer.editAttachedDocs = true
        currencyTransfer?.isAttachmentEditable = true
        initOwner(currencyTransfer)
        currencyTransfer
    }

    OperationHandlerService resolveOperation(Operation operation) {
        def operationHandler
        switch (operation) {
            case STORE:
            case UPDATE_STORED:
                operationHandler = storeCurrencyTransferOperationHandlerService
                break
            case TRANSFER:
            case TRANSFER_STORED:
                operationHandler = transferCurrencyTransferOperationHandlerService
                break
            case UPDATE_TRANSFERRED:
                operationHandler = updateCurrencyTransferOperationHandlerService
                break
            case CANCEL_TRANSFERRED:
                operationHandler = cancelCurrencyTransferOperationHandlerService
                break
            default:
                operationHandler = currencyTransferOperationHandlerService
                break
        }

        if (operationHandler) {
            return operationHandler
        } else {
            throw new UnsupportedOperationException("Operation ${operation.humanName()} not supported!")
        }
    }

    def initDocumentForView(CurrencyTransfer currencyTransfer) {
        def domainStatus = currencyTransfer.status.toUpperCase().replaceAll("\\s", "_")
        currencyTransfer.startedOperation = "VIEW_${domainStatus}" as Operation
        currencyTransfer.isAttachmentEditable = false
    }

    def initDocumentForEdit(domain, Map params) {
        currencyTransferWorkflowService.initOperationsForEdit(domain, params)
        domain.isDocumentEditable = true
        domain.addAttachedDocs = true
        domain.editAttachedDocs = true
        BusinessLogicUtils.setAttachmentAccess(domain)
    }

    def initOwner(CurrencyTransfer currencyTransfer) {
        if (!currencyTransfer?.userOwner) {
            String userOwner = springSecurityService.principal.username
            currencyTransfer.userOwner = userOwner
        }
    }

}
