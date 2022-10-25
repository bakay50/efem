package com.webbfontaine.efem.workflow.operations


import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.efem.workflow.Operation
import grails.gorm.transactions.Transactional
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

@Transactional
class ClientOperationHandlerService implements OperationHandlerService<Exchange>{

    def exchangeWorkflowService

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
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
