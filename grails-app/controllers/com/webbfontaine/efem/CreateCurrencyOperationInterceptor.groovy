package com.webbfontaine.efem

import groovy.util.logging.Slf4j
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 28/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class CreateCurrencyOperationInterceptor {

    CreateCurrencyOperationInterceptor() {
        match(controller: 'currencyTransfer', action: 'create|list|search')
    }

    boolean before() {
        if (!AppConfig.isCurrencyEnabled() || (!UserUtils.isBankAgent() && !UserUtils.isGovSupervisor() && !UserUtils.isAdministrator())) {
            redirect(uri: '/access-denied')
            return false
        }
        return true
    }
}
