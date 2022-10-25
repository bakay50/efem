package com.webbfontaine.rimm.bank

import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import grails.gorm.transactions.Transactional

@Transactional
class BankService {
    SessionStore sessionStoreService
    def bankBusinessLogicService
    def bankWorkflowService

    def addToSessionStore(Bank bank, conversationId = null) {
        if (conversationId) {
            sessionStoreService.put(conversationId, bank)
        } else {
            sessionStoreService.put(bank)
        }
    }

    def findFromSessionStore(def conversationId) {
        sessionStoreService.get(conversationId) as Bank
    }

    def doPersist(Bank bank, Operation commitOperation) {
        OperationHandlerService opHandler = bankBusinessLogicService.resolveOperation(commitOperation)
        Bank.withTransaction { transactionStatus ->
            bank = opHandler.execute(bank, transactionStatus, commitOperation)
        }
        bank
    }

    def checkCommitOperation(params) {
        def commitOperation = bankWorkflowService.getCommitOperation(params)
        if (commitOperation) {
            params.commitOperation = Operation.valueOf(commitOperation.id)
            params.commitOperationName = bankWorkflowService.getOperationName(params?.commitOperation)
        }

        if (bankWorkflowService.validationNotRequired(params?.commitOperation)) {
            params.validationNotRequired = true
        } else {
            params.validationRequired = true
        }
    }

    def loadBank(id) {
        Bank.findById(id)
    }

}
