package com.webbfontaine.efem

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CommitTransferOperationInterceptor {
    def transferOrderService

    CommitTransferOperationInterceptor() {
        match(controller: 'transferOrder', action: '(save|update)')
    }

    boolean before() {
        if (!session) {
            redirect(uri: '/')
            return false
        }
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        transferOrderService.checkCommitOperation(params)
        if (!ExchangeOperationHandlerUtils.isCreate(params.commitOperation)) {
            def dbVersion = BusinessLogicUtils.getTransferOriginalVersion(transferInstance?.id)
            if (dbVersion) {
                if (dbVersion != transferInstance.version) {
                    redirect(uri: '/access-denied')
                    return false
                }
            }
        }
        true
    }

    boolean after() {
        if (model) {
            def transferInstance = model?.transferInstance
            if (transferInstance?.hasErrors()) {
                params.isDocumentEditable = true
            }
        }
        return true
    }

}