package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.LoggerService
import com.webbfontaine.efem.MessagingNotifService
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

import static com.webbfontaine.efem.workflow.Operation.VALIDATE_REQUESTED

class ValidatedTransferOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<ValidatedTransferOperationHandlerService>, DataTest {

    void setupSpec() {
        mockDomain(TransferOrder)
    }

    void "test after persist when domain has no errors "() {
        given:
        TransferOrder transfer1 = new TransferOrder()
        TransferOrder transfer2 = new TransferOrder()
        def hasErrors = false
        def commitOperation = VALIDATE_REQUESTED
        service.messagingNotifService = Mock(MessagingNotifService)
        service.transferOrderService = Mock(TransferOrderService)
        service.loggerService = Mock(LoggerService)
        def exchange = new Exchange(requestNo: "EA2020000023", amountSettledMentionedCurrency: 1000, status: Statuses.ST_APPROVED)
        Exchange.metaClass.static.findByRequestNo = { it ->
            return exchange
        }
        transfer1.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA2020000023"))
        when:
        service.afterPersist(transfer1, transfer2, hasErrors, commitOperation)

        then:
        exchange?.status == Statuses.ST_APPROVED

    }
}
