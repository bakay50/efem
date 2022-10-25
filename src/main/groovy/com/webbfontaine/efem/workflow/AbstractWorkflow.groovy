package com.webbfontaine.efem.workflow

import com.webbfontaine.grails.plugins.workflow.WorkflowDefinition
import com.webbfontaine.grails.plugins.workflow.containers.OperationsContainer
import com.webbfontaine.grails.plugins.workflow.operations.CustomActionOperation
import com.webbfontaine.grails.plugins.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import com.webbfontaine.grails.plugins.workflow.operations.ViewOperation

import static com.webbfontaine.efem.security.Roles.DECLARANT
import static com.webbfontaine.efem.security.Roles.TRADER
import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.grails.plugins.workflow.containers.Buttons.BUTTON_DANGER
import static com.webbfontaine.grails.plugins.workflow.containers.Buttons.BUTTON_WARNING
import static com.webbfontaine.grails.plugins.workflow.operations.OperationConstants.*

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 05/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
abstract class AbstractWorkflow extends WorkflowDefinition{

    protected static addOperation(OperationsContainer operations, Operation op, roles) {
        def out = operations.operationById(op.name())

        if (out) {
            out.roles.addAll(roles)

        } else {

            def props = [:]
            def operation
            def cancelRejectOperation = [DELETE, DELETE_STORED, REJECT_REQUESTED, REJECT_PARTIALLY_APPROVED, CANCEL_QUERIED, CANCEL_APPROVED,CANCEL_VALIDATED, CANCEL_CONFIRMED, CANCEL_TRANSFERRED, CANCEL_VALID]

            if(cancelRejectOperation.contains(op)){
                props.put(NO_VALIDATION, true)
                props.put(BUTTON_DANGER, true)

            }else if([QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED, UPDATE_VALID].contains(op)){
                props.put(BUTTON_WARNING, true)
            }

            addConfirmRequiredProp(op, props)

            if (op.view) {
                operation = new ViewOperation(op.name(), OP_VIEW, roles)
            } else {
                operation = createUpdateOperation(op, roles, props)
            }


            operations.addOperation(operation)
        }
    }

    static void deleteOperation(OperationsContainer operation,def roles = Collections.EMPTY_MAP) {
        Map confirmationProps = new HashMap()
        def traderDeclarantRole = [TRADER.getAuthority(), DECLARANT.getAuthority()]
        if(roles){
             traderDeclarantRole = roles
        }
        confirmationProps.put(NO_VALIDATION, true)
        confirmationProps.put(BUTTON_DANGER, true)
        addConfirmRequiredProp(DELETE_STORED, confirmationProps)

        CustomActionOperation opDelete = new CustomActionOperation(DELETE_STORED.name(), DELETE_STORED.humanName(), "delete", traderDeclarantRole, confirmationProps)
        OperationClass ocDelete = new OperationClass(DELETE_STORED.name(), DELETE_STORED.humanName(), [icon: "trash"], [opDelete])

        operation.addOperationClass(ocDelete)
    }

    protected final operationById(Operation op) {
        return super.operationById(op.name())
    }

    protected static UpdateOperation createUpdateOperation(Operation op, roles, props) {
        UpdateOperation operation = new UpdateOperation(op.name(), op.humanName(), roles, props)
        return operation
    }

    static def addConfirmRequiredProp(Operation operation, Map operationProps) {
        if (operation.confirm) {
            operationProps.put(CONFIRMATION_REQUIRED, true)
        }
    }

    protected static addUpdateOperation(operations, Operation op, roles, Map params = Collections.emptyMap()) {
        Map props = new HashMap()
        props.putAll(params)
        addConfirmRequiredProp(op, props)
        def oper = createUpdateOperation(op, roles, props)
        operations.add(oper)
    }
}
