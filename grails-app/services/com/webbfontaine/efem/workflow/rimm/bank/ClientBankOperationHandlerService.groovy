package com.webbfontaine.efem.workflow.rimm.bank

import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import grails.gorm.transactions.Transactional
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

@Transactional
class ClientBankOperationHandlerService implements OperationHandlerService<Bank> {

    def bankWorkflowService

    @Override
    BpmService getBpmService() {
        return bankWorkflowService
    }

    private static String getActionName() {
        GrailsWebRequest.lookup().actionName
    }

    @Override
    String getCommitOperation() {
        ((GrailsWebRequest) RequestContextHolder.currentRequestAttributes()).getParams().commitOperation
    }

    @Override
    boolean isCreate() {
        ExchangeOperationHandlerUtils.isCreate(getCommitOperation() as Operation)
    }
}
