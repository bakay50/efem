package com.webbfontaine.efem.workflow

import com.webbfontaine.grails.plugins.workflow.operations.OperationConstants
import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 05/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@CompileStatic
enum Operation implements GlobalOperation{


    CREATE{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }
    },

    STORE{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_STORE
        }
    },

    DECLARE{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_DECLARE
        }
    },
    VALIDATE{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_VALIDATE
        }
    },

    CONFIRM{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CONFIRM
        }
    },

    REQUEST{
        @Override
        boolean isCreate() {
            return true
        }

        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_REQUEST
        }
    },

    QUERY_DECLARED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_QUERY
        }
    },

    QUERY_REQUESTED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_QUERY
        }
    },

    QUERY_PARTIALLY_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_QUERY
        }
    },
    UPDATE_QUERIED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },
    UPDATE_STORED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },
    UPDATE_CONFIRMED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },
    DECLARE_STORED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_DECLARE
        }
    },
    DECLARE_QUERIED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_DECLARE
        }
    },
    CONFIRM_STORED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_CONFIRM
        }
    },
    CONFIRM_DECLARED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_CONFIRM
        }
    },

    REQUEST_STORED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_REQUEST
        }
    },
    VALIDATE_STORED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_VALIDATE
        }
    },

    VALIDATE_REQUESTED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_VALIDATE
        }
    },

    VERIFY{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_VERIFY
        }
    },
    REQUEST_QUERIED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_REQUEST
        }
    },
    REJECT_REQUESTED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_REJECT
        }
    },

    REJECT_PARTIALLY_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_REJECT
        }
    },

    APPROVE_REQUESTED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_APPROVE
        }
    },

    PARTIALLY_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_APPROVE
        }
    },

    APPROVE_PARTIALLY_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_APPROVE
        }
    },

    UPDATE_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },

    EXECUTE_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_EXECUTE
        }
    },

    CANCEL_APPROVED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CANCEL
        }
    },
    CANCEL_CONFIRMED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CANCEL
        }
    },

    CANCEL_QUERIED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CANCEL
        }
    },

    CANCEL_VALIDATED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CANCEL
        }
    },
    DELETE {
        @Override
        boolean isCreate() {
            return false
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_DELETE
        }
    },
    DIRECT_DELETE{
        @Override
        boolean isCreate() {
            return false
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OI_DIRECT_DELETE
        }
    },
    DELETE_STORED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_DELETE
        }
    },
    TRANSFER_STORED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_TRANSFER
        }
    },
    TRANSFER{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_TRANSFER
        }
    },
    UPDATE_TRANSFERRED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },
    CANCEL_TRANSFERRED{
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_CANCEL
        }
    },
    REGISTER{
        @Override
        boolean isCreate() {
            return true
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_REGISTER
        }
    },
    CANCEL_VALID{
        @Override
        boolean isCreate() {
            return false
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_CANCEL_VALID
        }
    },
    ACTIVATE{
        @Override
        boolean isCreate() {
            return false
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_ACTIVATE
        }
    },
    UPDATE_VALID{
        @Override
        boolean isCreate() {
            return false
        }
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE_VALID
        }
    },
    VIEW_TRANSFERRED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_STORED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_CEDED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_REQUESTED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_QUERIED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_APPROVED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_PARTIALLY_APPROVED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_REJECTED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_CANCELLED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },

    VIEW_EXECUTED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_DECLARED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_CONFIRMED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_VALIDATED{
        @Override
        String humanName() {
            return OperationConstants.OP_VIEW
        }
    },
    VIEW_VALID{
        @Override
        String humanName() {
            return Operations.OP_VIEW_VALID
        }
    },
    VIEW_INVALID{
        @Override
        String humanName() {
            return Operations.OP_VIEW_INVALID
        }
    },

    UPDATE_EXECUTED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },

    UPDATE_VALIDATED{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_UPDATE
        }
    },

    DOMICILIATE{
        @Override
        boolean isConfirm() {
            return true
        }
        @Override
        String humanName() {
            return Operations.OP_DOMICILIATE
        }
    },

    REQUEST_TRANSFER_ORDER {
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_REQUEST_TRANSFER_ORDER
        }
    },

    REQUEST_CURRENCY_TRANSFER {
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_REQUEST_CURRENCY_TRANSFER
        }
    },

    UPDATE_CLEARANCE {
        @Override
        boolean isConfirm() {
            return true
        }

        @Override
        String humanName() {
            return Operations.OP_UPDATE_CLEARANCE
        }
    },

    boolean isCreate() {
        return false
    }

    boolean isConfirm() {
        return false
    }

    boolean isView() {
        return name().startsWith('VIEW')
    }

    String humanName() {
        return name()
    }

    static Operation getEnum(String name) {
        for (Operation o : Operation.values()) {
            if (o.name().equals(name))
                return o
        }
        return null
    }

    @Override
    public String getAction() {
        Matcher m = Pattern.compile("^([^_]+)_?").matcher(name())
        if (m.find()) {
            return m.group(1).toLowerCase()
        } else {
            return name().toLowerCase()
        }
    }
}