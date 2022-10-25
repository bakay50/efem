package com.webbfontaine.efem.workflow
import com.webbfontaine.efem.constants.TransferRequestType
import com.webbfontaine.grails.plugins.workflow.Transition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.containers.TransitionsContainer
import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.security.Roles.*
import static Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrderWorkflow extends AbstractWorkflow {
    @Override
    Object defineOperations(OperationsContainer operations) {
        //Trader role
        (TransferRequestType.allOpViews + TransferRequestType.traderOperations).each {
            def roles = [TRADER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Bank Agent role
        (TransferRequestType.allOpViews + TransferRequestType.bankAgentOperations).each {
            def roles = [BANK_AGENT.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Government Officer role
        (TransferRequestType.allOpViews + TransferRequestType.govOfficerOperations).each {
            def roles = [GOVT_OFFICER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Admin
        def adminRole = [ADMIN.getAuthority()]
        TransferRequestType.allOpViews.each {
            addOperation(operations, it, adminRole)
        }

        //Super Administrator role
        (TransferRequestType.allOpViews + TransferRequestType.superAdminOperations).each {
            def roles = [SUPER_ADMINISTRATOR.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Gov Supervisor
        def govsupervisorRole = [GOVT_SUPERVISOR.getAuthority()]
        TransferRequestType.allOpViews.each {
            addOperation(operations, it, govsupervisorRole)
        }
        deleteOperation(operations,[TRADER.getAuthority(),BANK_AGENT.getAuthority()])
    }

    @Override
    Object defineTransitions(TransitionsContainer transitions) {

        TRANSFER_STATUS.each { status ->
            transitions.addTransition(new Transition(status, operationById(Operation.valueOf("VIEW_${status.toString().toUpperCase().replaceAll("\\s", "_")}")), status))
        }
        transitions.addTransition(new Transition(null, operationById(STORE), ST_STORED))
        transitions.addTransition(new Transition(null, operationById(REQUEST), ST_REQUESTED))
        
        transitions.addTransition(new Transition(ST_STORED, operationById(UPDATE_STORED), ST_STORED))
        transitions.addTransition(new Transition(ST_STORED, operationById(REQUEST_STORED), ST_REQUESTED))
        transitions.addTransition(new Transition(ST_STORED, operationById(DELETE_STORED), null))

        transitions.addTransition(new Transition(ST_REQUESTED, operationById(VALIDATE), ST_VALIDATED))
        transitions.addTransition(new Transition(ST_REQUESTED, operationById(QUERY_REQUESTED), ST_QUERIED))

        transitions.addTransition(new Transition(ST_QUERIED, operationById(REQUEST_QUERIED), ST_REQUESTED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(UPDATE_QUERIED), ST_QUERIED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(CANCEL_QUERIED), ST_CANCELLED))

        transitions.addTransition(new Transition(ST_VALIDATED, operationById(CANCEL_VALIDATED), ST_CANCELLED))
        transitions.addTransition(new Transition(ST_VALIDATED, operationById(UPDATE_VALIDATED), ST_VALIDATED))

    }
}
