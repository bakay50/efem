package com.webbfontaine.efem

import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class SupDeclarationServiceSpec extends Specification implements ServiceUnitTest<SupDeclarationService>, DataTest {

    def setup() {
        mockDomain SupDeclaration
        mockDomain Exchange
        mockDomain OrderClearanceOfDom
        defineBeans {
            exchangeService(ExchangeService)
        }
    }

    def "test setDeclarationCifAmount()"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()
        Exchange exchange = new Exchange()
        exchange.cifAmtFcx = new BigDecimal("2500")

        when:
        service.setDeclarationCifAmount(exchange, supDeclaration)

        then:
        supDeclaration.declarationCifAmount == exchange.cifAmtFcx
    }

    @Unroll
    def "test setRemainingBalanceTransferDoneAmount()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestNo = 'EC2396'
        exchange.currencyRate = new BigDecimal("655")
        exchange.supDeclarations = declaration

        OrderClearanceOfDom.metaClass.static.findAllByEaReference = { domiciliations }

        when:
        service.setRemainingBalanceTransferDoneAmount(exchange)

        then:
        exchange.remainingBalanceTransferDoneAmount == result

        where:
        declaration                                                                                                                                   | domiciliations                                                                                                                                                     | result
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("300")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("700"))]  | [new OrderClearanceOfDom(amountSettledMentionedCurrency: new BigDecimal("1500")), new OrderClearanceOfDom(amountSettledMentionedCurrency: new BigDecimal("2000"))] | new BigDecimal("2500")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("1000"))] | [new OrderClearanceOfDom(amountSettledMentionedCurrency: new BigDecimal("2500")), new OrderClearanceOfDom(amountSettledMentionedCurrency: new BigDecimal("2500"))] | new BigDecimal("3500")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("1000"))] | [new OrderClearanceOfDom(amountSettledMentionedCurrency: new BigDecimal("2500")), new OrderClearanceOfDom(amountSettledMentionedCurrency: null)]                   | new BigDecimal("1000")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("1000"))] | null                                                                                                                                                               | new BigDecimal("-1500")
    }

    @Unroll
    def "test setRemainingBalanceEADeclaredAmount()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.supDeclarations = declarations
        exchange.amountMentionedCurrency = new BigDecimal("5000")
        Exchange exchangeFromSad = new Exchange()
        exchangeFromSad.currencyRate = new BigDecimal("655")

        when:
        service.setRemainingBalanceEADeclaredAmount(exchange, exchangeFromSad)

        then:
        exchange.remainingBalanceDeclaredAmount == result

        where:
        declarations                                                                                                                                   | result
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("2000"))]  | new BigDecimal("2500")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("2000")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("2000"))] | new BigDecimal("1000")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("2000")), new SupDeclaration(declarationAmountWriteOff: null)]                   | new BigDecimal("3000")
        null                                                                                                                                           | new BigDecimal("5000")

    }

    @Unroll
    def "test setDeclarationRemainingBalance()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.clearanceOfficeCode = 'CIAB1'
        exchange.declarationSerial = 'C'
        exchange.declarationNumber = '396'
        exchange.cifAmtFcx = amountCif
        exchange.currencyRate = new BigDecimal("655")
        SupDeclaration declaration = new SupDeclaration()

        SupDeclaration.metaClass.static.findAllByClearanceOfficeCodeAndDeclarationSerialAndDeclarationNumberAndDeclarationDate = { office, serial, number, declarationDate -> supDeclarations }

        service.exchangeService = [doSumOfExchangeAmount: { a, b, c, d, e, f -> sumAmountInCurrencyOfAllDeclaration }]

        when:
        service.setDeclarationRemainingBalance(exchange, declaration)

        then:
        declaration.declarationRemainingBalance == result

        where:
        supDeclarations                                                                                                                              | amountCif               | sumAmountInCurrencyOfAllDeclaration |       result
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500")), new SupDeclaration(declarationAmountWriteOff: new BigDecimal("500"))] | new BigDecimal("5000")  | new BigDecimal("1000")              |new BigDecimal("3000")
        [new SupDeclaration(declarationAmountWriteOff: new BigDecimal("1500")), new SupDeclaration(declarationAmountWriteOff: null)]                 | new BigDecimal("2500")  | new BigDecimal("500")               |new BigDecimal("500")
        null                                                                                                                                         | new BigDecimal("10000") | new BigDecimal("4000")              |new BigDecimal("6000")
    }

}
