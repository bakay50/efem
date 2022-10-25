package com.webbfontaine.efem

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class CommitTransferOperationInterceptorSpec extends Specification implements InterceptorUnitTest<CommitTransferOperationInterceptor>, DataTest {
    void setupSpec() {
        mockDomain(TransferOrder)
    }

    @Unroll
    void "Test CommitTransferOperationInterceptor based on version"() {
        setup:
        TransferOrder transferInstance = new TransferOrder()
        transferInstance.save(flush: true, validate: false, failOnError: false)
        interceptor.transferOrderService = Stub(TransferOrderService) {
            findFromSessionStore(_) >> transferInstance
            checkCommitOperation(_) >> null
        }
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.isCreate(_) >> false
        GroovyMock(BusinessLogicUtils, global: true)
        BusinessLogicUtils.getTransferOriginalVersion(_) >> version

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
