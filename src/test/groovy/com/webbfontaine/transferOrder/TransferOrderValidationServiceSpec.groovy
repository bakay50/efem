package com.webbfontaine.transferOrder

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.StaticMessageSource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import grails.util.Holders


class TransferOrderValidationServiceSpec extends Specification implements ServiceUnitTest<TransferOrderValidationService>, DataTest {
    @Shared
    @Autowired
    MessageSource messageSource

    def setupSpec() {
        mockDomain(TransferOrder)
        mockDomain(Exchange)

        messageSource = Holders.applicationContext.getBean("messageSource") as StaticMessageSource
        messageSource.useCodeAsDefaultMessage
        initMessageSource(messageSource)
    }


    @Unroll
    def "test  for doAllCheckingOnDoc"() {
        given:
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123'
        )

        Exchange exchange = new Exchange(requestNo: "EA202012", status: "Approved", declarantCode: '1234', importerCode: '0001',
                requestType: "EA",
                currencyCode: "XOF", bankCode: "SGBCI",
                amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(10.00),
                year: LocalDate.now().year, registrationNumberBank: "bank123", registrationDateBank: LocalDate.now())

        def mocktransferOrderService = Stub(TransferOrderService) {
            retrieveExchangeData(*_) >> {
                return exchange
            }
        }

        service.transferOrderService = mocktransferOrderService

        def params = new HashMap<String, String>()
        params.eaReference = "EA202012"
        params.currencyCode = "XOF"
        params.importerCode = "0001"
        params.bankCode = bankCode

        when:
        def result = service.doAllCheckingOnDoc(transfer, params)

        then:

        result.error == hasError
        result.errorMessage == errorField
        result?.content?.requestNo == expected

        where:
        bankCode | hasError | errorField | expected
        "SGBCI"  | false    | null       | "EA202012"
        "SIB"    | true     | "bankCode" | null

    }

    @Unroll
    def "test  for doAmountCheckingOnDoc"() {
        given:
        GroovyMock(UserUtils, global: true)
        OrderClearanceOfDom orderClearanceOfDom = new OrderClearanceOfDom(amountRequestedMentionedCurrency: amountRequestedMentionedCurrency, amountSettledMentionedCurrency: amountSettledMentionedCurrency)
        UserUtils.isBankAgent() >> userRole
        when:
        def result = service.doAmountCheckingOnDoc(orderClearanceOfDom)

        then:

        result.error == true
        result.errorMessage == errorMessage

        where:
        amountRequestedMentionedCurrency | amountSettledMentionedCurrency | errorMessage                       | userRole
        0                                | 0                              | "amountRequestedMentionedCurrency" | true
        1000                             | 0                              | "amountSettledMentionedCurrency"   | true
        0                                | 1000                           | "amountRequestedMentionedCurrency" | true
    }

    private static initMessageSource(messageSource) {
        messageSource.addMessage('transferOrder.bankCode.equal.error', Locale.US, "bankCode")
        messageSource.addMessage('transferOrder.bankCode.equal.error', Locale.FRANCE, "bankCode")
        messageSource.addMessage('transferOrder.amountRequestedMentionedCurrency.unique.error', Locale.US, "amountRequestedMentionedCurrency")
        messageSource.addMessage('transferOrder.amountRequestedMentionedCurrency.unique.error', Locale.FRANCE, "amountRequestedMentionedCurrency")
        messageSource.addMessage('execution.errors.amountSettledMentionedCurrency', Locale.US, "amountSettledMentionedCurrency")
        messageSource.addMessage('execution.errors.amountSettledMentionedCurrency', Locale.FRANCE, "amountSettledMentionedCurrency")
    }
}
