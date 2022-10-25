package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.security.Roles
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService

@Transactional
class CurrencyTransferSecurityService {

    SpringSecurityService springSecurityService

    def hasAccess(def document) {
        boolean isValid = false
        if (document?.status in Statuses.CURRENCYTRANSFER_STATUS && (userHasRole(Roles.BANK_AGENT) ||userHasRole(Roles.ADMIN) || userHasRole(Roles.GOVT_SUPERVISOR))) {
            isValid = true
        }
        isValid
    }

    boolean userHasRole(Roles roles) {
        return userAuthorities?.any { it.authority == roles.authority }
    }

    def getUserAuthorities() {
        return springSecurityService?.authentication?.authorities
    }

    boolean roleHasAccess(){
        userAuthorities?.any { Roles.values().collect {it.authority}.contains(it.authority) }
    }
}
