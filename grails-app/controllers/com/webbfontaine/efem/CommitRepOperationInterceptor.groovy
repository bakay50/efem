package com.webbfontaine.efem

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class CommitRepOperationInterceptor {
    def repatriationService

    CommitRepOperationInterceptor(){
        match(controller: 'repatriation', action: '(save|update)')
    }

    boolean before() {
        if (!session) {
            redirect(uri: '/')
            return false
        }
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        repatriationService.checkCommitOperation(params)
        if (!ExchangeOperationHandlerUtils.isCreate(params.commitOperation)) {
            def dbVersion = BusinessLogicUtils.getRepOriginalVersion(repatriationInstance?.id)
            if (dbVersion) {
                if (dbVersion != repatriationInstance.version) {
                    redirect(uri: '/access-denied')
                    return false
                }
            }
        }
        true
    }

    boolean after() {
        if(model){
            def repatriationInstance = model?.repatriationInstance
            if (repatriationInstance?.hasErrors()) {
                params.isDocumentEditable = true
            }
        }
        return true
    }
}
