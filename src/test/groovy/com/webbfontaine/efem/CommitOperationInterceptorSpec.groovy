package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Edilbert D. Trizona
 * Date: 12/18/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CommitOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CommitOperationInterceptor>, DataTest {

    void setupSpec() {
        mockDomain(Exchange)
    }

    @Unroll
    void "Test CommitOperationInterceptor based on version"() {
        setup:
        Exchange exchangeInstance = new Exchange()
        exchangeInstance.save(flush: true, validate: false, failOnError: false)
        interceptor.exchangeService = Stub(ExchangeService) {
            findFromSessionStore(_) >> exchangeInstance
            checkCommitOperation(_) >> null
        }
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.isCreate(_) >> false
        GroovyMock(BusinessLogicUtils, global: true)
        BusinessLogicUtils.getOriginalVersion(_) >> version

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
