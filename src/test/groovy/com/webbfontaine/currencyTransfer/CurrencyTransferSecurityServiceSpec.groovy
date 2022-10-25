package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.security.Roles
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class CurrencyTransferSecurityServiceSpec extends Specification implements ServiceUnitTest<CurrencyTransferSecurityService>, DataTest {

    @Override
    Closure doWithSpring() {
        return {
            springSecurityService(SpringSecurityService)
        }
    }

    def setup() {
        mockDomain(CurrencyTransfer)
    }

    def "test hasAccess()"() {
        given:
        def currencyTransfer = new CurrencyTransfer(status: status)
        when:
        UserUtils.authenticate(roles)
        def result = service.hasAccess(currencyTransfer)
        then:
        result == canAccess
        where:
        status                  | roles                             | canAccess
        Statuses.ST_STORED      | Roles.BANK_AGENT.authority        | true
        Statuses.ST_STORED      | Roles.ADMIN.authority             | true
        Statuses.ST_STORED      | Roles.GOVT_SUPERVISOR.authority   | true
        Statuses.ST_STORED      | Roles.DECLARANT.authority         | false
        Statuses.ST_STORED      | Roles.TRADER.authority            | false
        Statuses.ST_TRANSFERRED | Roles.BANK_AGENT.authority        | true
    }
}
