package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.UtilConstants.XOF

class CheckWAEMUCountryExportAndCurrencyCodeRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain Exchange
    }

    @Unroll
    void "test CheckWAEMUCountryExportAndCurrencyCodeRule()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.countryOfExportCode = countryOfExportCode
        exchange.currencyCode = currencyCode
        exchange.countryPartyCode = countryPartyCode

        when:
        Rule rule = new CheckWAEMUCountryExportAndCurrencyCodeRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        requestType | countryPartyCode | countryOfExportCode | currencyCode | expected
        EA          | "BF"             | "BF"                | XOF          | true
        EA          | "BJ"             | "BJ"                | XOF          | true
        EA          | "TG"             | "TG"                | XOF          | true
        EA          | "NE"             | "NE"                | XOF          | true
        EA          | "ML"             | "ML"                | XOF          | true
        EA          | "GW"             | "GW"                | XOF          | true
        EA          | "SN"             | "SN"                | XOF          | true
        EA          | "SN"             | "SN"                | "EUR"        | false
        EA          | "FR"             | "FR"                | "EUR"        | false
        EC          | "SN"             | "SN"                | XOF          | false
        EC          | "SN"             | "SN"                | "EUR"        | true
    }
}
