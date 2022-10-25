package com.webbfontaine.efem


import com.webbfontaine.efem.workflow.ExchangeWorkflowService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.plugin.springsecurity.SpringSecurityService

class EditOperationInterceptor {

    SpringSecurityService springSecurityService
    ExchangeWorkflowService exchangeWorkflowService

    EditOperationInterceptor() {
        match(controller: 'exchange', action: 'edit')
    }

    boolean before() {
        if (params.id) {
            Exchange exchange = Exchange.get(params.id)

            if (exchange) {
                exchangeWorkflowService.initOperations(exchange)
                exchangeWorkflowService.removeCancelOperation(exchange)
                setStartedOperation(exchange, params)
                if (!exchange.operations?.any {
                    it instanceof UpdateOperation || it instanceof OperationClass
                } || checkForBankAgent(exchange) || checkForGovOfficer(exchange)) {
                    log.warn("User ${springSecurityService?.principal?.username} don't have access to any operation but try to edit document. Id = ${params.id}")
                    redirect(uri: '/access-denied')
                    return false
                }

            } else {
                log.warn("User ${springSecurityService?.principal?.username} don't have access to doc but try to edit it. Id = ${params.id} ")
                redirect(uri: '/access-denied')
                return false
            }
        }

        return true
    }

    private boolean checkForBankAgent(Exchange exchange){
        return BusinessLogicUtils.handleRemoveOperationForBankOfficer(exchange)
    }

    private boolean checkForGovOfficer(Exchange exchange){
        return BusinessLogicUtils.handleRemoveOperationForGovOfficer(exchange)
    }

    private void setStartedOperation(Exchange exchange, Map params) {
        exchange?.operations?.removeAll {
            !(it instanceof UpdateOperation || it instanceof com.webbfontaine.grails.plugins.workflow.operations.OperationClass || it instanceof com.webbfontaine.grails.plugins.workflow.operations.OperationClass)
        }

        if (exchange?.operations?.size() == 1) {
            params.op = exchange?.operations[0]?.id
            exchange?.startedOperation = exchange?.operations[0]?.id as Operation
        }
    }

}
