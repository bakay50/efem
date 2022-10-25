package com.webbfontaine.efem

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import grails.gorm.transactions.Transactional
import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class TransferBusinessLogicService {
    def springSecurityService
    def transferOrderWorkflowService
    def storeTransferOperationHandlerService
    def requestStoredTransferOperationHandlerService
    def clientTransferOperationHandlerService
    def cancelTransferOperationHandlerService
    def updateValidatedTransferOperationHandlerService
    def updateQueriedTransferOperationHandlerService
    def queryTransferOperationHandlerService
    def validatedTransferOperationHandlerService

    def initDocumentForCreate(TransferOrder transfer) {
        transfer.startedOperation = CREATE
        transferOrderWorkflowService.initOperations(transfer)
        transfer.isDocumentEditable = true
        transfer.addAttachedDocs = true
        transfer.editAttachedDocs = true
        transfer?.isAttachmentEditable = true
        transfer.isEaOpearationsEditable = true
        initOwner(transfer)
        transfer
    }

    def initDocumentForView(TransferOrder transfer) {
        def domainStatus = transfer.status.toUpperCase().replaceAll("\\s", "_")
        transfer?.startedOperation = "VIEW_${domainStatus}" as Operation
        transfer?.isAttachmentEditable = false
    }

    def initDocumentForEdit(domain, Map params) {
        transferOrderWorkflowService.initOperationsForEdit(domain, params)
        BusinessLogicUtils.setTransferIsEaOperationsEditable(domain)
        BusinessLogicUtils.setAttachmentAccess(domain)
        initOwner(domain)
    }

    OperationHandlerService resolveOperation(Operation operation) {
        def operationHandler

        switch (operation) {
            case STORE:
            case UPDATE_STORED:
                operationHandler = storeTransferOperationHandlerService
                break

            case REQUEST:
            case REQUEST_STORED:
            operationHandler = requestStoredTransferOperationHandlerService
                break

            case VALIDATE:
            case VALIDATE_REQUESTED:
                operationHandler = validatedTransferOperationHandlerService
                break

            case CANCEL_QUERIED:
            case CANCEL_VALIDATED:
                operationHandler = cancelTransferOperationHandlerService
                break

            case QUERY_REQUESTED:
                operationHandler = queryTransferOperationHandlerService
                break

            case UPDATE_QUERIED:
                operationHandler = updateQueriedTransferOperationHandlerService
                break

            case UPDATE_VALIDATED:
                operationHandler = updateValidatedTransferOperationHandlerService
                break

            default:
                operationHandler = clientTransferOperationHandlerService
                break
        }

        if (operationHandler) {
            return operationHandler
        } else {
            throw new UnsupportedOperationException("Operation ${operation.humanName()} not supported!")
        }

    }

    def initOwner(TransferOrder transfer) {
        if (!transfer?.userOwner) {
            String userOwner = springSecurityService.principal.username
            transfer.userOwner = userOwner
        }
    }

}
