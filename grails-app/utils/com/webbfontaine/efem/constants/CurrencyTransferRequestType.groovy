package com.webbfontaine.efem.constants

import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA.
 * Date: 23/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferRequestType {

    static govsupervisorViews = [VIEW_TRANSFERRED, VIEW_CANCELLED]
    static allOpViews = [VIEW_STORED, VIEW_TRANSFERRED, VIEW_CANCELLED]
    static bankAgentOperations =[STORE,UPDATE_STORED,TRANSFER_STORED, TRANSFER, UPDATE_TRANSFERRED,CANCEL_TRANSFERRED]
    static String DOC_SWIFT_MESSAGE = 'WF10'

}
