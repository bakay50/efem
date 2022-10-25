package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED

class StoreTransferOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<StoreTransferOperationHandlerService>, DataTest {

    void setupSpec() {
        mockDomain(TransferOrder)
    }

    @Unroll
    def "test beforePersist() when domain has no errors"() {
        given:
        TransferOrder transfer = new TransferOrder()
        def commitOperation = UPDATE_STORED

        when:
        service.beforePersist(transfer, commitOperation)

        then:
        assert transfer?.lastTransactionDate?.toLocalDate() == LocalDate.now()

    }
}
