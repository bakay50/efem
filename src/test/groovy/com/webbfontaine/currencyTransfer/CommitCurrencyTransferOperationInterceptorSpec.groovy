package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.currencyTransfer.CommitCurrencyTransferOperationInterceptor
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
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
class CommitCurrencyTransferOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CommitCurrencyTransferOperationInterceptor>, DataTest {

    void setupSpec() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    void "Test CommitCurrencyTransferOperationInterceptor based on version"() {
        setup:
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer()
        currencyTransferInstance.save(flush: true, validate: false, failOnError: false)
        interceptor.currencyTransferService = Stub(CurrencyTransferService) {
            findFromSessionStore(_) >> currencyTransferInstance
            checkCommitOperation(_) >> null
        }
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.isCreate(_) >> false
        GroovyMock(BusinessLogicUtils, global: true)
        BusinessLogicUtils.getCurrencyTransferOriginalVersion(_) >> version

        when:
        interceptor.before()

        then:
        response.redirectedUrl == redirectedUrl

        where:
        version | redirectedUrl
        0       | null
        1       | '/access-denied'

    }
}
