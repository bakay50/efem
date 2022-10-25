package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import static com.webbfontaine.efem.constants.Statuses.ST_PARTIALLY_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operations.OP_CANCEL
import static com.webbfontaine.efem.workflow.Operations.OP_APPROVE
import static com.webbfontaine.efem.workflow.Operations.OP_DOMICILIATE
import static com.webbfontaine.efem.workflow.Operations.OP_VIEW
import static com.webbfontaine.efem.workflow.Operations.OP_REQUEST_TRANSFER_ORDER

@Transactional
class ExchangeWorkflowService extends BpmService {

    def exchangeWorkflow
    def commonWorkflowService

    @Override
    protected WorkflowDefinition getDocumentWorkflow() {
        return exchangeWorkflow
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
                ExchangeRequestType.bankAgentOperations?.collect { it.action })) {
            domainInstance?.isAttachmentEditable = false
        }
        applyTreatmentLevel(domainInstance)
    }

    def removeCancelOperation(Exchange exchange) {
        def getCancelOperation = {
            exchange.operations.collect().find {
                it.name == OP_CANCEL
            }
        }

        if (Objects.equals(exchange.status, ST_APPROVED)) {
            def approvedBy = exchange.approvedBy ? exchange.approvedBy.split(";") : []

            if (!SpringSecurityUtils.getPrincipalAuthorities().any { approvedBy.contains(it.authority) }) {
                def cancelOperation = getCancelOperation()
                exchange.operations.remove(cancelOperation)
            }
        }
    }

    def applyTreatmentLevel(Exchange exchange) {
        def userTreatmentLevel = springSecurityService.principal.userProperties?.LVL
        if (Objects.equals(exchange.status, ST_PARTIALLY_APPROVED)) {
            if (userTreatmentLevel in [UserProperties.ALL, null, UserProperties.EMPTY] || exchange.treatmentLevel > Integer.parseInt(userTreatmentLevel)) {
                doRemoveOperations(exchange)
            }
        }
        exchange
    }

    def doRemoveOperations(Exchange exchange) {
        def tmp = []
        tmp.addAll(exchange?.operations.collect())
        tmp?.each { def operation ->
            if (operation instanceof UpdateOperation) {
                exchange?.operations.remove(operation)
            }
        }
    }

    def isDomiciliateWhenEc(Exchange exchange) {
        if (UserUtils.isBankAgent()) {
            def getApproveOperation = {
                exchange.operations.collect().find { it.name == OP_APPROVE }
            }
            def getDomiciliateOperation = {
                exchange.operations.collect().find { it.name == OP_DOMICILIATE }
            }

            if (Objects.equals(exchange?.status, ST_REQUESTED) && exchange.requestType == ExchangeRequestType.EC) {
                def approveOperation = getApproveOperation()
                exchange?.operations?.remove(approveOperation)
            } else {
                def domiciliateOperation = getDomiciliateOperation()
                exchange?.operations?.remove(domiciliateOperation)
            }
        }
        exchange
    }

    def removeOperationByArea(Exchange exchange) {
        if (BusinessLogicUtils.handleRemoveOperationForBankOfficer(exchange)) {
            exchange?.operations?.removeAll { it.name != OP_VIEW }
        }
        exchange
    }

    def removeOperationsForGovOfficerByArea(Exchange exchange) {
        if (BusinessLogicUtils.handleRemoveOperationForGovOfficer(exchange)) {
            exchange?.operations?.removeAll { it.name != OP_VIEW }
        }
        exchange
    }

    def requestTransferOrderForEA(Exchange exchange) {
        if (exchange.requestType == ExchangeRequestType.EC && (UserUtils.isBankAgent() || UserUtils.isTrader())) {
            exchange?.operations?.removeAll { it.name == OP_REQUEST_TRANSFER_ORDER }
        }
        exchange
    }

    def removeGovOfficerOperationsForEA(Exchange exchange) {
        if (exchange.requestType == ExchangeRequestType.EA) {
            if (UserUtils.isGovOfficer() && Objects.equals(exchange?.status, ST_REQUESTED)) {
                exchange?.operations?.removeAll { it.name != OP_VIEW }
            }
        }
        exchange
    }
}
