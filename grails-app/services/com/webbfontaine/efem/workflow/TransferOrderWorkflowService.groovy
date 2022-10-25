package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.TransferRequestType
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.workflow.WorkflowConstants
import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.grails.plugins.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.SecuredOperation
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.gorm.transactions.Transactional
import static com.webbfontaine.efem.constants.Statuses.ST_VALIDATED
import static com.webbfontaine.efem.workflow.Operations.OP_UPDATE

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class TransferOrderWorkflowService extends BpmService {
    TransferOrderWorkflow transferOrderWorkflow

    @Override
    WorkflowDefinition getWorkFlow(Object domainInstance) {
        return getDocumentWorkflow()
    }

    @Override
    protected WorkflowDefinition getDocumentWorkflow() {
        return transferOrderWorkflow
    }

    @Override
    def initOperationsForEdit(domainInstance, Map params) {
        initOperations(domainInstance)

        domainInstance.operations.removeAll {
            !(it instanceof UpdateOperation || it instanceof OperationClass || it instanceof OperationClass)
        }

        if (domainInstance.operations.size() == 1) {
            domainInstance.startedOperation = domainInstance.operations.toArray()[0].id
        } else {
            domainInstance.startedOperation = params[WorkflowConstants.START_OPERATION]

            domainInstance.operations.removeAll {
                def operationId = it.id as Operation
                operationId != domainInstance.startedOperation
            }
        }
        if (domainInstance.operations) {
            def operation = domainInstance.operations.toArray()[0]

            if (operation instanceof OperationClass || operation instanceof OperationClass) {
                domainInstance.operations = operation.operations.findAll({
                    !(it instanceof SecuredOperation) || hasAccessToOperation(it)
                }) as Set
            }
        }

        if (BusinessLogicUtils.checkStartedOperationInListOfOperationsByRoles(domainInstance?.startedOperation?.action,
                TransferRequestType.bankAgentOperations?.collect { it.action })) {
            domainInstance?.isAttachmentEditable = false
        }
    }

    def removeUpdateOperationForGovOfficer(TransferOrder transferInstance){
        if(UserUtils.isGovOfficer() && (transferInstance?.status == ST_VALIDATED)){
            transferInstance?.operations?.removeAll {  it.name == OP_UPDATE }
        }
    }
}
