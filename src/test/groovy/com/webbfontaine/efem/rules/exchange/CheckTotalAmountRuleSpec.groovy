/*
package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.ErcService
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

class CheckTotalAmountRuleSpec extends Specification implements DataTest {

    def setupSpec(){
        mockDomain(Exchange)
        def mockErcService = GroovyMock(ErcService)
        mockErcService.getAvdDetails(*_) >> {
            [totCIFValD:"500"]
        }
        applicationContext.beanFactory.registerSingleton("ercService", mockErcService)
    }

    @Unroll
    def "test for CheckTotalAmountRule base on Sad with Scenario: #testCase"(){
        given:
        GroovyMock(RuleUtils, global:true)
        generateExchange()
        Exchange exchange = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", amountNationalCurrency: new BigDecimal(currency), amountMentionedCurrency: new BigDecimal(currency), balanceAs: new BigDecimal("5000.00"),
                                        sadIncoterm: "CFS", attachedDocCodeSad: docCodeSad, convertedCif: new BigDecimal(convertedCif), totalAmountOfCif: new BigDecimal(convertedCif) , clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_REQUESTED, requestNo: "123")

        when:
        Rule rule = new CheckTotalAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        testCase                                                                    | convertedCif  | currency | docCodeSad| expectedResult
        "pass in 1st if (ttlAmt > sadCif AND attachCod == 900 AND ttlAmt > avdCif)" |    "20"       |  "50"    |   "900"   | true
        "pass in 2nd(ttlAmt > sadCif AND attachCod != 900)"                         |    "10"       |  "30"    |   "800"   | true
        "no error"                                                                  |    "10"       |  "5"     |   "800"   | false


    }

    void generateExchange(){
        Exchange exchange3 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", id: 7, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5.00"), balanceAs: new BigDecimal("5000.00"),
                            clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_REQUESTED).save()
        Exchange exchange4 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", id: 9, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("10.00"), balanceAs: new BigDecimal("5000.00"),
                            clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_PARTIALLY_APPROVED).save()
        Exchange exchange6 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "005", id: 8, amountNationalCurrency: new BigDecimal("0.00"), amountMentionedCurrency: new BigDecimal("0.00"), balanceAs: new BigDecimal("5000.00"),
                            clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_APPROVED).save()


    }
}
*/
