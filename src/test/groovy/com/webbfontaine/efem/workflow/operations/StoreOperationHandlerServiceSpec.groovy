package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED

class StoreOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<StoreOperationHandlerService> {

    @Unroll
    def "test beforePersist() when domain has no errors"(){

        given:
        Exchange exchange = new Exchange()
        def commitOperation = UPDATE_STORED
        service.exchangeService = Mock(ExchangeService)

        when:
        service.beforePersist(exchange, commitOperation)

        then:
        assert exchange?.lastTransactionDate?.toLocalDate() == LocalDate.now()

    }
}
