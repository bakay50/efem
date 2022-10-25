package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.constants.RepatriationRequestType
import com.webbfontaine.grails.plugins.workflow.Transition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.containers.TransitionsContainer
import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.security.Roles.*
import static Operation.*
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class RepatriationWorkflow extends AbstractWorkflow {
    @Override
    Object defineOperations(OperationsContainer operations) {
        //Trader and Declarant role
        (RepatriationRequestType.allOpViews + RepatriationRequestType.traderOperations).each {
            def roles = [TRADER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Bank Agent role
        (RepatriationRequestType.allOpViews + RepatriationRequestType.bankAgentOperations).each {
            def roles = [BANK_AGENT.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Government Officer role
        (RepatriationRequestType.allOpViews + RepatriationRequestType.govOfficerOperations).each {
            def roles = [GOVT_OFFICER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Admin
        def adminRole = [ADMIN.getAuthority()]
        RepatriationRequestType.allOpViews.each {
            addOperation(operations, it, adminRole)
        }

        //Gov Supervisor
        def govsupervisorRole = [GOVT_SUPERVISOR.getAuthority()]
        RepatriationRequestType.allOpViews.each {
            addOperation(operations, it, govsupervisorRole)
        }

        def superAdminRole = [SUPER_ADMINISTRATOR.getAuthority()]
        RepatriationRequestType.allOpViews.each {
            addOperation(operations, it, superAdminRole)
        }

        deleteOperation(operations, [BANK_AGENT.getAuthority(), TRADER.getAuthority()])
    }

    @Override
    Object defineTransitions(TransitionsContainer transitions) {
        REPATRIATION_STATUS.each { status ->
            transitions.addTransition(new Transition(status, operationById(Operation.valueOf("VIEW_${status.toString().toUpperCase().replaceAll("\\s", "_")}")), status))
        }
        transitions.addTransition(new Transition(null, operationById(STORE), ST_STORED))
        transitions.addTransition(new Transition(null, operationById(DECLARE), ST_DECLARED))
        transitions.addTransition(new Transition(ST_PRE_CONFIRMED, operationById(DECLARE), ST_CONFIRMED))
        transitions.addTransition(new Transition(ST_STORED, operationById(UPDATE_STORED), ST_STORED))
        transitions.addTransition(new Transition(ST_STORED, operationById(DECLARE_STORED), ST_DECLARED))
        transitions.addTransition(new Transition(ST_PRE_CONFIRMED, operationById(DECLARE_STORED), ST_CONFIRMED))
        transitions.addTransition(new Transition(ST_STORED, operationById(DELETE_STORED), null))

        transitions.addTransition(new Transition(ST_QUERIED, operationById(UPDATE_QUERIED), ST_QUERIED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(DECLARE_QUERIED), ST_DECLARED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(CANCEL_QUERIED), ST_CANCELLED))
        transitions.addTransition(new Transition(ST_DECLARED, operationById(CONFIRM_DECLARED), ST_CONFIRMED))
        transitions.addTransition(new Transition(ST_DECLARED, operationById(QUERY_DECLARED), ST_QUERIED))
        transitions.addTransition(new Transition(ST_CONFIRMED, operationById(UPDATE_CONFIRMED), ST_CONFIRMED))
        if(AppConfig.isCurrencyEnabled()){
            transitions.addTransition(new Transition(ST_CONFIRMED, operationById(REQUEST_CURRENCY_TRANSFER), ST_CEDED))
            transitions.addTransition(new Transition(ST_CEDED, operationById(UPDATE_CLEARANCE), ST_CEDED))
        }
        transitions.addTransition(new Transition(ST_CONFIRMED, operationById(CANCEL_CONFIRMED), ST_CANCELLED))
    }
}
