package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.grails.plugins.workflow.WorkflowConstants
import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.grails.plugins.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.OperationConstants
import com.webbfontaine.grails.plugins.workflow.operations.SecuredOperation
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.gorm.transactions.Transactional

@Transactional
class BankWorkflowService extends BpmService {
    def bankWorkflow

    @Override
    protected WorkflowDefinition getDocumentWorkflow() {
        return bankWorkflow
    }

    def initOperationsForEdit(Bank domainInstance, Map params) {
        initOperations(domainInstance)
        domainInstance.operations.removeAll {
            !(it instanceof UpdateOperation || it instanceof OperationClass)
        }

        if (domainInstance.operations.size() == 1) {
            domainInstance.startedOperation = domainInstance.operations.toArray()[0].id
        } else {
            domainInstance.startedOperation = params[WorkflowConstants.START_OPERATION]
            if (domainInstance.startedOperation) {
                domainInstance.operations.removeAll {
                    it.id as Operation != domainInstance.startedOperation
                }
            } else {
                domainInstance.operations.removeAll {
                    it.properties.containsKey(OperationConstants.HIDDEN_OPERATION)
                }
                if (domainInstance.operations.size() == 1) {
                    domainInstance.startedOperation = domainInstance.operations.toArray()[0].id
                }

            }

        }
        if (domainInstance.operations) {
            def operation = domainInstance.operations.toArray()[0]

            if (operation instanceof OperationClass) {
                domainInstance.operations = operation.operations.findAll({
                    !(it instanceof SecuredOperation) || hasAccessToOperation(it)
                }) as Set
            }
        }
    }
}
