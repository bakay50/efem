package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.constants.CurrencyTransferRequestType
import com.webbfontaine.grails.plugins.workflow.Transition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.containers.TransitionsContainer

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.security.Roles.*
import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 23/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class CurrencyTransferWorkflow extends AbstractWorkflow {
    @Override
    Object defineOperations(OperationsContainer operations) {

        //Bank Agent role
        (CurrencyTransferRequestType.allOpViews + CurrencyTransferRequestType.bankAgentOperations).each {
            def roles = [BANK_AGENT.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Admin
        def adminRole = [ADMIN.getAuthority()]
        CurrencyTransferRequestType.allOpViews.each {
            addOperation(operations, it, adminRole)
        }

        //Gov Supervisor
        def govsupervisorRole = [GOVT_SUPERVISOR.getAuthority()]
        CurrencyTransferRequestType.govsupervisorViews.each {
            addOperation(operations, it, govsupervisorRole)
        }
        deleteOperation(operations, [BANK_AGENT.getAuthority()])

    }

    @Override
    Object defineTransitions(TransitionsContainer transitions) {
        CURRENCYTRANSFER_STATUS.each { status ->
            transitions.addTransition(new Transition(status, operationById(Operation.valueOf("VIEW_${status.toString().toUpperCase().replaceAll("\\s", "_")}")), status))
        }
        transitions.addTransition(new Transition(null, operationById(STORE), ST_STORED))
        transitions.addTransition(new Transition(null, operationById(TRANSFER), ST_TRANSFERRED))
        transitions.addTransition(new Transition(ST_STORED, operationById(DELETE_STORED), null))
        transitions.addTransition(new Transition(ST_STORED, operationById(UPDATE_STORED), ST_STORED))
        transitions.addTransition(new Transition(ST_STORED, operationById(TRANSFER_STORED), ST_TRANSFERRED))
        transitions.addTransition(new Transition(ST_TRANSFERRED, operationById(UPDATE_TRANSFERRED), ST_TRANSFERRED))
        transitions.addTransition(new Transition(ST_TRANSFERRED, operationById(CANCEL_TRANSFERRED), ST_CANCELLED))
    }
}
