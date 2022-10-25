/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webbfontaine.efem.workflow


import org.grails.web.servlet.mvc.GrailsWebRequest

import static Operations.*
import static com.webbfontaine.efem.workflow.Operation.*


/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 10/21/14
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExchangeOperationHandlerUtils {

    static CREATE_OPERATIONS = [STORE, REQUEST,DECLARE,REGISTER]
    static REQUEST_OPERATIONS = [OA_REQUEST_FROM_NEW,OI_REQUEST_FROM_NULL,
                                 OA_REQUEST_QUERIED]

    static boolean isCreate(Operation commitOperation) {
        return commitOperation in CREATE_OPERATIONS
    }

    static REQUEST_RAPATRIATIONS_OPERATIONS = [OA_STORE_FROM_NEW,OA_DECLARE_FROM_NEW,OA_DECLARE_QUERIED]

    static String getCommitOperation() {
        GrailsWebRequest?.lookup()?.params?.commitOperation
    }

    static String getOp() {
        GrailsWebRequest?.lookup()?.params?.op
    }
}
