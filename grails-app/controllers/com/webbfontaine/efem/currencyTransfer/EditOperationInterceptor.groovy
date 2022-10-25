package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.currencyTransfer.CurrencyTransferSecurityService
import com.webbfontaine.efem.workflow.CurrencyTransferWorkflowService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.plugin.springsecurity.SpringSecurityService

class EditOperationInterceptor {

    SpringSecurityService springSecurityService
    CurrencyTransferWorkflowService currencyTransferWorkflowService
    CurrencyTransferSecurityService currencyTransferSecurityService

    EditOperationInterceptor() {
        match(controller: 'currencyTransfer', action: 'edit')
    }

    boolean before() {
        if (params.id) {
            CurrencyTransfer currencyTransfer = CurrencyTransfer.get(params.id)
            if(currencyTransfer && currencyTransferSecurityService.hasAccess(currencyTransfer)) {
                currencyTransferWorkflowService.initOperations(currencyTransfer)
                setStartedOperation(currencyTransfer, params)

                if(!currencyTransfer.operations?.any {
                    it instanceof UpdateOperation || it instanceof OperationClass
                }) {
                    log.warn("User ${springSecurityService?.principal?.username} don't have access to any operation. ")
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

    private void setStartedOperation(CurrencyTransfer currencyTransfer, Map params) {
        currencyTransfer?.operations?.removeAll {
            !(it instanceof UpdateOperation || it instanceof com.webbfontaine.grails.plugins.workflow.operations.OperationClass || it instanceof com.webbfontaine.grails.plugins.workflow.operations.OperationClass)
        }

        if(currencyTransfer?.operations?.size() == 1){
            params.op =  currencyTransfer?.operations[0]?.id
            currencyTransfer?.startedOperation = currencyTransfer?.operations[0]?.id as Operation
        }
    }
}
