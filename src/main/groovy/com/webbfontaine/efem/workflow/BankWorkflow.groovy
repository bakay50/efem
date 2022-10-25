package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.constants.BankRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.grails.plugins.workflow.Transition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.containers.TransitionsContainer
import com.webbfontaine.grails.plugins.workflow.operations.CustomActionOperation
import com.webbfontaine.grails.plugins.workflow.operations.OperationClass
import static com.webbfontaine.efem.constants.Statuses.RIMM_STATUS
import static com.webbfontaine.efem.security.Roles.SUPER_ADMINISTRATOR
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALID
import static com.webbfontaine.efem.workflow.Operation.REGISTER
import static com.webbfontaine.efem.workflow.Operation.ACTIVATE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_VALID
import static com.webbfontaine.grails.plugins.workflow.containers.Buttons.BUTTON_DANGER
import static com.webbfontaine.grails.plugins.workflow.operations.OperationConstants.NO_VALIDATION

class BankWorkflow extends AbstractWorkflow {

    @Override
    Object defineOperations(OperationsContainer operations) {

        def superAdministratorRole = [SUPER_ADMINISTRATOR.getAuthority()]
        (BankRequestType.allOpViews + BankRequestType.superAdministratorOperations).each {
            addOperation(operations, it, superAdministratorRole)
        }
        cancelOperation(operations, superAdministratorRole)
    }

    @Override
    Object defineTransitions(TransitionsContainer transitions) {
        RIMM_STATUS.each { status ->
            transitions.addTransition(new Transition(status, operationById(Operation.valueOf("VIEW_${status.toString().toUpperCase().replaceAll("\\s", "_")}")), status))
        }
        transitions.addTransition(new Transition(null, operationById(REGISTER), Statuses.ST_VALID))
        transitions.addTransition(new Transition(Statuses.ST_VALID, operationById(CANCEL_VALID), Statuses.ST_INVALID))
        transitions.addTransition(new Transition(Statuses.ST_INVALID, operationById(ACTIVATE), Statuses.ST_VALID))
        transitions.addTransition(new Transition(Statuses.ST_VALID, operationById(UPDATE_VALID), Statuses.ST_VALID))
    }

    static void cancelOperation(OperationsContainer operation, def roles = Collections.EMPTY_MAP) {
        Map confirmationProps = new HashMap()
        confirmationProps.put(NO_VALIDATION, true)
        confirmationProps.put(BUTTON_DANGER, true)
        addConfirmRequiredProp(CANCEL_VALID, confirmationProps)
        CustomActionOperation opCancel = new CustomActionOperation(CANCEL_VALID.name(), CANCEL_VALID.humanName(), "cancel", roles, confirmationProps)
        OperationClass ocCancel = new OperationClass(CANCEL_VALID.name(), CANCEL_VALID.humanName(), [icon: "trash"], [opCancel])
        operation.addOperationClass(ocCancel)
    }
}
