package com.webbfontaine.efem

class RoleInterceptor {

    RoleInterceptor() {
        match(controller: 'exchange|repatriation|transferOrder|currencyTransfer', action: '*')
    }

    boolean before() {
        if (!UserUtils.roleHasAccess()){
            redirect(uri: '/access-denied')
            return false
        }
        return true
    }
}
