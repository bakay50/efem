package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.workflow.Operation

class OperationClass extends com.webbfontaine.grails.plugins.workflow.operations.OperationClass {
    OperationClass(Operation operation, ops) {
        super(operation.name(), operation.action, Collections.emptyMap(), ops)
    }
}
