package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.currencyTransfer.CreateOperationInterceptor
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 05/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CreateOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CreateOperationInterceptor>, DataTest {

    void setupSpec() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    void "Test CreateOperationInterceptor "() {
        setup:

        def model = ["currencyTransferInstance": new CurrencyTransfer()]
        interceptor.model = model

        when:
        interceptor.after()

        then:
        response.redirectedUrl.contains('/access-denied')

    }
}
