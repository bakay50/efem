package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils


class CommitOperationInterceptor {

    def exchangeService

    CommitOperationInterceptor(){
        match(controller: 'exchange', action: '(save|update)')
    }

    boolean before() {
        if (!session) {
            redirect(uri: '/')
            return false
        }
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)
        exchangeService.checkCommitOperation(params)
        if (!ExchangeOperationHandlerUtils.isCreate(params.commitOperation)) {
            def dbVersion = BusinessLogicUtils.getOriginalVersion(exchangeInstance?.id)
            if (dbVersion) {
                if (dbVersion != exchangeInstance.version) {
                    redirect(uri: '/access-denied')
                    return false
                }
            }
        }
        true
    }

    boolean after() {
        if(model){
            def exchangeInstance = model?.exchangeInstance
            if (exchangeInstance?.hasErrors()) {
                params.isDocumentEditable = true
            }
        }
        return true
    }
}
