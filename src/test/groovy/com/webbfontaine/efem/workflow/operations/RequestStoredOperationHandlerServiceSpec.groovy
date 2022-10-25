package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.MessagingNotifService
import com.webbfontaine.efem.UserUtils
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.*

class RequestStoredOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<RequestStoredOperationHandlerService> {

    @Unroll
    def "test beforePersist() when domain has no errors"(){

        given:
        Exchange exchange = new Exchange()
        def commitOperation = REQUEST
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> true
        service.exchangeService = Mock(ExchangeService)

        when:
        service.beforePersist(exchange, commitOperation)

        then:
        assert exchange?.lastTransactionDate?.toLocalDate() == LocalDate.now()
        assert exchange?.requestDate == LocalDate.now()

    }

    @Unroll
    void "test after persist when domain hasErrors = #mockError"() {
        given:
        Exchange exchange1 = new Exchange()
        Exchange exchange2 = new Exchange()
        def hasErrors = mockError
        def commitOperation = REQUEST_STORED
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(exchange1, exchange2, hasErrors, commitOperation)

        then:
        isNotify * service.messagingNotifService.sendAfterCommit(exchange2,_)

        where:
        mockError | isNotify
        true      | 0
        false     | 1
    }
}
