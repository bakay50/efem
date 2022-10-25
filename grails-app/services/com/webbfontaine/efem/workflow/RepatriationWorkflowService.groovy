package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.RepatriationRequestType
import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.gorm.transactions.Transactional

import static com.webbfontaine.efem.constants.Statuses.ST_CEDED
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED
import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operations.OP_UPDATE_CLEARANCE
import static com.webbfontaine.efem.workflow.Operations.OP_REQUEST_CURRENCY_TRANSFER

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class RepatriationWorkflowService extends BpmService {

    def repatriationWorkflow
    def commonWorkflowService
    def clearanceDomService

    @Override
    WorkflowDefinition getWorkFlow(Object domainInstance) {
        return getDocumentWorkflow()
    }

    @Override
    protected WorkflowDefinition getDocumentWorkflow() {
        return repatriationWorkflow
    }

    @Override
    String getEndStatus(Object domainInstance, Operation operationId) {
        def endStatus

        if ([REQUEST].contains(operationId)) {
            endStatus = ST_REQUESTED
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
                RepatriationRequestType.bankAgentOperations?.collect { it.action })) {
            domainInstance?.isAttachmentEditable = false
        }
    }

    def requestCurrencyTransfer(Repatriation repatriation) {
        if (repatriation.status != ST_CONFIRMED || (repatriation.status == ST_CONFIRMED && !UserUtils.isBankAgent())) {
            repatriation?.operations?.removeAll { it.name == OP_REQUEST_CURRENCY_TRANSFER }
        }
        if (!(repatriation.status == ST_CEDED && repatriation.natureOfFund == NatureOfFund.NOF_PRE)) {
            repatriation?.operations?.removeAll { it.name == OP_UPDATE_CLEARANCE }
        }
        repatriation
    }


}
