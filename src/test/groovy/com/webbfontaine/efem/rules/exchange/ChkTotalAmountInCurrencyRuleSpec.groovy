package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class ChkTotalAmountInCurrencyRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    @Ignore
    void "Test when requesting an Exchange from TVF when #testCase should result in failing the rule: #expectedResult "() {

        given:
        generateExchange()
        Tvf tvfInstance = new Tvf(totCifInFgn: totCifInFgn, incCode: incoterm)
        Exchange exchangeInstance2 = new Exchange(amountMentionedCurrency: amountMentionedCurrency2, tvfInstanceId: '001', tvf: tvfInstance)

        when:
        Rule rule = new ChkTotalAmountInCurrencyRule()
        rule.apply(new RuleContext(exchangeInstance2, exchangeInstance2.errors as Errors))

        then:
        exchangeInstance2?.hasErrors() == expectedResult

        where:
        testCase                                                                                    | totCifInFgn                | amountMentionedCurrency2  | incoterm | expectedResult
        'TVF Total CIF Value is greater than Total Exchange Total Amount in Currency'               | new BigDecimal("50000.00") | new BigDecimal("1000.00") | "CIP"    | false
        'TVF Total CIF Value is less than Total Exchange Total Amount in Currency'                  | new BigDecimal("500.00")   | new BigDecimal("1000.00") | "CIF"    | true
        'TVF Total CIF Value is equal to Total Exchange Total Amount in Currency'                   | new BigDecimal("52000.00") | new BigDecimal("1015.00") | "FOB"    | false
        'TVF Total CIF Value is less than Total Exchange Total Amount in Currency with incoterm'    | new BigDecimal("500.00")   | new BigDecimal("1000.00") | "DAP"    | false
        'TVF Total CIF Value is greater than Total Exchange Total Amount in Currency with incoterm' | new BigDecimal("50000.00") | new BigDecimal("1000.00") | "DAT"    | false
        'TVF Total CIF Value is equal to Total Exchange Total Amount in Currency with incoterm'     | new BigDecimal("52000.00") | new BigDecimal("1015.00") | "DDP"    | false


    }

    void generateExchange() {
        Exchange exchange1 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 1, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("20000.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_REQUESTED, requestNo: "123").save()
        Exchange exchange2 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 2, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("15000.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_QUERIED, requestNo: "121").save()
        Exchange exchange5 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "003", id: 3, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("15.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_REQUESTED, requestNo: "122").save()
        Exchange exchange6 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 3, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("20000.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_STORED, requestNo: "122").save()

    }


}
