package com.webbfontaine.efem

import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import grails.gorm.transactions.Transactional
import static com.webbfontaine.efem.workflow.Operation.CREATE

@Transactional
class BankBusinessLogicService {
    def bankWorkflowService
    def clientBankOperationHandlerService

    def initDocumentForCreate(Bank bank) {
        bank.startedOperation = CREATE
        bankWorkflowService.initOperations(bank)
    }

    def initDocumentForView(Bank bank) {
        def domainStatus = bank.status.toUpperCase().replaceAll("\\s", "_")
        bank.startedOperation = "VIEW_${domainStatus}" as Operation
        bankWorkflowService.initOperations(bank)
    }

    def initDocumentForEdit(domain, Map params) {
        bankWorkflowService.initOperationsForEdit(domain, params)
    }

    OperationHandlerService resolveOperation(Operation operation) {
        def operationHandler = clientBankOperationHandlerService
        if (operationHandler) {
            return operationHandler
        } else {
            throw new UnsupportedOperationException("Operation ${operation.humanName()} not supported!")
        }
    }
}
