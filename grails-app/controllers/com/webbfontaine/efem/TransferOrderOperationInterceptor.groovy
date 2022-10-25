package com.webbfontaine.efem

import com.webbfontaine.transferOrder.TransferOrderService
import groovy.util.logging.Slf4j

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 21/12/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class TransferOrderOperationInterceptor {
    TransferOrderService transferOrderService

    TransferOrderOperationInterceptor() {
        match(controller: 'transferOrder', action: 'create|list|search')
    }

    boolean before() {
        if (!transferOrderService.isTransferEnabled()) {
            redirect(uri: '/access-denied')
            return false
        }
        return true
    }
}
