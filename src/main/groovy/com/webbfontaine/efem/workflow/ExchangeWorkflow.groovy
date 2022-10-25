package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.workflow.Transition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.containers.TransitionsContainer

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.security.Roles.*
import static Operation.*

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 05/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExchangeWorkflow extends AbstractWorkflow {



    @Override
    Object defineOperations(OperationsContainer operations) {



        //Declarant role
        (ExchangeRequestType.allOpViews + ExchangeRequestType.declarantOperations).each {
            def roles = [DECLARANT.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Trader role
        (ExchangeRequestType.allOpViews + ExchangeRequestType.traderOperations).each {
            def roles = [TRADER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Bank Agent role
        (ExchangeRequestType.allOpViews + ExchangeRequestType.bankAgentOperations).each {
            def roles = [BANK_AGENT.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Government Officer role
        (ExchangeRequestType.allOpViews + ExchangeRequestType.govOfficerOperations).each {
            def roles = [GOVT_OFFICER.getAuthority()]
            addOperation(operations, it, roles)
        }

        //Admin
        def adminRole = [ADMIN.getAuthority()]
        ExchangeRequestType.allOpViews.each {
            addOperation(operations, it, adminRole)
        }

        //Gov Supervisor
        def govsupervisorRole = [GOVT_SUPERVISOR.getAuthority()]
        ExchangeRequestType.allOpViews.each {
            addOperation(operations, it, govsupervisorRole)
        }

        // Super admin
        def superAdminRole = [SUPER_ADMINISTRATOR.getAuthority()]
        ExchangeRequestType.allOpViews.each {
            addOperation(operations, it, superAdminRole)
        }

        deleteOperation(operations)
    }

    @Override
    Object defineTransitions(TransitionsContainer transitions) {

        EXCHANGE_STATUS.each { status ->
            transitions.addTransition(new Transition(status, operationById(Operation.valueOf("VIEW_${status.toString().toUpperCase().replaceAll("\\s","_")}")), status))
        }

        transitions.addTransition(new Transition(null, operationById(STORE), ST_STORED))
        transitions.addTransition(new Transition(ST_STORED, operationById(UPDATE_STORED), ST_STORED))
        transitions.addTransition(new Transition(ST_STORED, operationById(REQUEST_STORED), ST_REQUESTED))
        transitions.addTransition(new Transition(ST_STORED, operationById(DELETE_STORED), null))
        transitions.addTransition(new Transition(null, operationById(REQUEST), ST_REQUESTED))
        transitions.addTransition(new Transition(ST_REQUESTED, operationById(QUERY_REQUESTED), ST_QUERIED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(UPDATE_QUERIED), ST_QUERIED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(REQUEST_QUERIED), ST_REQUESTED))
        transitions.addTransition(new Transition(ST_REQUESTED, operationById(APPROVE_REQUESTED), ST_APPROVED))
        transitions.addTransition(new Transition(ST_REQUESTED, operationById(DOMICILIATE), ST_APPROVED))
        transitions.addTransition(new Transition(ST_PARTIALLY_APPROVED, operationById(PARTIALLY_APPROVED), ST_PARTIALLY_APPROVED))
        transitions.addTransition(new Transition(ST_PRE_APPROVE, operationById(APPROVE_PARTIALLY_APPROVED), ST_APPROVED))
        transitions.addTransition(new Transition(ST_APPROVED, operationById(UPDATE_APPROVED), ST_APPROVED))
        transitions.addTransition(new Transition(ST_PRE_EXEUTE, operationById(UPDATE_APPROVED), ST_EXECUTED))
        transitions.addTransition(new Transition(ST_EXECUTED, operationById(UPDATE_EXECUTED), ST_EXECUTED))
        transitions.addTransition(new Transition(ST_REQUESTED, operationById(REJECT_REQUESTED), ST_REJECTED))
        transitions.addTransition(new Transition(ST_PARTIALLY_APPROVED, operationById(REJECT_PARTIALLY_APPROVED), ST_REJECTED))
        transitions.addTransition(new Transition(ST_QUERIED, operationById(CANCEL_QUERIED), ST_CANCELLED))
        transitions.addTransition(new Transition(ST_APPROVED, operationById(CANCEL_APPROVED), ST_CANCELLED))
        transitions.addTransition(new Transition(ST_PARTIALLY_APPROVED, operationById(QUERY_PARTIALLY_APPROVED), ST_QUERIED))
        if(AppConfig.isTransferEnabled()){
            transitions.addTransition(new Transition(ST_APPROVED, operationById(REQUEST_TRANSFER_ORDER), ST_APPROVED))
            transitions.addTransition(new Transition(ST_EXECUTED, operationById(REQUEST_TRANSFER_ORDER), ST_EXECUTED))
        }
    }
}
