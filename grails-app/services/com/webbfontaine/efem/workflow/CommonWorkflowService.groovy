package com.webbfontaine.efem.workflow

import com.webbfontaine.grails.plugins.workflow.WorkflowConstants
import com.webbfontaine.grails.plugins.workflow.WorkflowService
import com.webbfontaine.grails.plugins.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.SecuredOperation
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation

class CommonWorkflowService {

    WorkflowService workflowService

    def getDomainOperations(domainInstance, Map params) {
        domainInstance?.operations?.removeAll {
            !(it instanceof UpdateOperation || it instanceof OperationClass || it instanceof OperationClass)
        }

        if (domainInstance?.operations?.size() == 1) {
            domainInstance.startedOperation = domainInstance.operations.toArray()[0].id
        } else {
            domainInstance.startedOperation = params[WorkflowConstants.START_OPERATION]

            domainInstance?.operations?.removeAll {
                def operationId = it.id as Operation
                operationId != domainInstance.startedOperation
            }
        }

        if (domainInstance?.operations) {
            def operation = domainInstance?.operations?.toArray()[0]
            if (operation instanceof OperationClass || operation instanceof OperationClass) {
                domainInstance.operations = operation?.operations?.findAll {
                    !(it instanceof SecuredOperation) || workflowService.hasAccessToOperation(it)
                } as Set
            }
        }
    }

}
