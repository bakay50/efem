package com.webbfontaine.efem

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CommitRepOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CommitRepOperationInterceptor> , DataTest {
    void setupSpec() {
        mockDomain(Repatriation)
    }

    @Unroll
    void "Test CommitRepOperationInterceptor based on version"() {
        setup:
        Repatriation repatriationInstance = new Repatriation()
        repatriationInstance.save(flush: true, validate: false, failOnError: false)
        interceptor.repatriationService = Stub(RepatriationService) {
            findFromSessionStore(_) >> repatriationInstance
            checkCommitOperation(_) >> null
        }
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.isCreate(_) >> false
        GroovyMock(BusinessLogicUtils, global: true)
        BusinessLogicUtils.getRepOriginalVersion(_) >> version

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
