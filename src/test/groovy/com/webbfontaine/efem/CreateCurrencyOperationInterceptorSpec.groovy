package com.webbfontaine.efem

import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 28/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CreateCurrencyOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CreateCurrencyOperationInterceptor>, DataTest {

    @Unroll
    void "Test CreateCurrencyOperationInterceptor "() {
        setup:
        GroovyMock(AppConfig, global: true)
        AppConfig.isCurrencyEnabled() >> isEnabled

        when:
        interceptor.before()

        then:
        response.redirectedUrl == redirectedUrl

        where:
        isEnabled | redirectedUrl
        false     | '/access-denied'

    }
}
