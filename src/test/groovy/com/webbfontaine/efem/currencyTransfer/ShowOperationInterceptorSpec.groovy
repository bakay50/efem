package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.currencyTransfer.CurrencyTransferSecurityService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class ShowOperationInterceptorSpec extends Specification implements InterceptorUnitTest<ShowOperationInterceptor>, DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
    }

    void "test ShowOperationInterceptor()"() {
        setup:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer(id: 1, status: "Stored")
        def currencyTransferSecurity = Mock(CurrencyTransferSecurityService)
        currencyTransferSecurity.hasAccess(currencyTransfer)
        when:
        def result = interceptor.before()
        then:
        assert result
    }


}
