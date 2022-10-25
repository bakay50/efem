package com.webbfontaine.efem.constants

import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.QUERY_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.REQUEST_QUERIED
import static com.webbfontaine.efem.workflow.Operation.REQUEST_STORED
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_QUERIED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.VALIDATE
import static com.webbfontaine.efem.workflow.Operation.VIEW_CANCELLED
import static com.webbfontaine.efem.workflow.Operation.VIEW_QUERIED
import static com.webbfontaine.efem.workflow.Operation.VIEW_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.VIEW_STORED
import static com.webbfontaine.efem.workflow.Operation.VIEW_VALIDATED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferRequestType {
    static String  EA = "EA"

    static allOpViews = [VIEW_STORED, VIEW_REQUESTED,VIEW_VALIDATED, VIEW_QUERIED, VIEW_CANCELLED]

    static traderOperations = [STORE,UPDATE_STORED,UPDATE_QUERIED, REQUEST,REQUEST_STORED,REQUEST_QUERIED,
                               CANCEL_QUERIED,UPDATE_VALIDATED]
    static bankAgentOperations =[STORE,REQUEST,REQUEST_STORED,UPDATE_STORED,UPDATE_VALIDATED, VALIDATE,
                                 QUERY_REQUESTED,UPDATE_STORED]
    static govOfficerOperations = [UPDATE_VALIDATED]
    static superAdminOperations = [CANCEL_VALIDATED,UPDATE_VALIDATED]


}


