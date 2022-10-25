package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.constants.CurrencyTransferRequestType
import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import grails.gorm.transactions.Transactional
import static com.webbfontaine.efem.constants.Statuses.ST_STORED
import static com.webbfontaine.efem.workflow.Operation.STORE

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class CurrencyTransferWorkflowService extends BpmService {

    def currencyTransferWorkflow
    def commonWorkflowService

    @Override
    WorkflowDefinition getWorkFlow(Object domainInstance) {
        return getDocumentWorkflow()
    }

    @Override
    protected WorkflowDefinition getDocumentWorkflow() {
        return currencyTransferWorkflow
    }

    @Override
    String getEndStatus(Object domainInstance, Operation operationId) {
        def endStatus

        if ([STORE].contains(operationId)) {
            endStatus = ST_STORED
        } else {
            endStatus = super.getEndStatus(domainInstance, operationId)
        }

        return endStatus
    }

    @Override
    def initOperationsForEdit(domainInstance, Map params) {
        initOperations(domainInstance)
        commonWorkflowService?.getDomainOperations(domainInstance, params)
        if (BusinessLogicUtils.checkStartedOperationInListOfOperationsByRoles(domainInstance?.startedOperation?.action,
                CurrencyTransferRequestType.bankAgentOperations?.collect { it.action })) {
            domainInstance?.isAttachmentEditable = false
        }
    }

}
