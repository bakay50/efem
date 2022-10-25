package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.currencyTransfer.CurrencyTransferSecurityService
import grails.plugin.springsecurity.SpringSecurityService


class ShowOperationInterceptor {

    CurrencyTransferSecurityService currencyTransferSecurityService
    SpringSecurityService springSecurityService

    ShowOperationInterceptor() {
        match(controller: 'currencyTransfer', action: 'show')
    }
    boolean before() {
        if (params.id) {
            def currencyInstance = CurrencyTransfer.get(params.id)
            if (currencyInstance) {
                if (!currencyTransferSecurityService.hasAccess(currencyInstance)) {
                    log.warn("User ${springSecurityService?.principal?.username} don't have access to doc but try to access it. Id = ${currencyInstance.id} ")
                    redirect(uri: '/access-denied')
                    return false
                }
            }
        }
        return true
    }

}
