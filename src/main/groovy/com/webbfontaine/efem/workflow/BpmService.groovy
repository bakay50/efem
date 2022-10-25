package com.webbfontaine.efem.workflow

import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.grails.plugins.workflow.WorkflowService
import com.webbfontaine.grails.plugins.workflow.operations.OperationConstants

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 05/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
abstract class BpmService extends WorkflowService {

    protected abstract WorkflowDefinition getDocumentWorkflow()

    @Override
    WorkflowDefinition getWorkFlow(Object domainInstance) {
        return getDocumentWorkflow()
    }

    String getEndStatus(Object domainInstance, Operation operationId) {
        def workflow = this.getWorkFlow(domainInstance)
        return getEndStatus(workflow, domainInstance.status, operationId.name())
    }

    String getOperationName(Operation operationId) {
        getWorkFlow().operationById(operationId?.name())?.name
    }

    boolean validationNotRequired(Operation commitOperation) {
        getWorkFlow().operationById(commitOperation?.name())?.properties?.containsKey(OperationConstants.NO_VALIDATION)
    }

    @Override
    protected Object accessOperations(Object domainInstance) {
        return null
    }
}
