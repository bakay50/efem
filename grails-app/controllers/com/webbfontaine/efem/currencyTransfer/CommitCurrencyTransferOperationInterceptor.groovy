package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 29/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class CommitCurrencyTransferOperationInterceptor {
    def currencyTransferService

    CommitCurrencyTransferOperationInterceptor(){
        match(controller: 'currencyTransfer', action: '(save|update)')
    }

    boolean before() {
        if (!session) {
            redirect(uri: '/')
            return false
        }
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        currencyTransferService.checkCommitOperation(params)
        if (!ExchangeOperationHandlerUtils.isCreate(params.commitOperation)) {
            def dbVersion = BusinessLogicUtils.getCurrencyTransferOriginalVersion(currencyTransferInstance?.id)
            if (dbVersion) {
                if (dbVersion != currencyTransferInstance.version) {
                    redirect(uri: '/access-denied')
                    return false
                }
            }
        }
        true
    }

    boolean after() {
        if(model){
            def currencyTransferInstance = model?.currencyTransferInstance
            if (currencyTransferInstance?.hasErrors()) {
                params.isDocumentEditable = true
            }
        }
        return true
    }
}
