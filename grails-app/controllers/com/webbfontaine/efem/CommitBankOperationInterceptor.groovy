package com.webbfontaine.efem

class CommitBankOperationInterceptor {

    def bankService

    CommitBankOperationInterceptor(){
        match(controller: 'bank', action: '(save|update)')
    }

    boolean before() {
        if (!session) {
            redirect(uri: '/')
            return false
        }
        bankService.checkCommitOperation(params)
        true
    }

    boolean after() {
        if(model){
            def bankInstance = model?.bankInstance
            if (bankInstance?.hasErrors()) {
                params.isDocumentEditable = true
            }
        }
        return true
    }
}
