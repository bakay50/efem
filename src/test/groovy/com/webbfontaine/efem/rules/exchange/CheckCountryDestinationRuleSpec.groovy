package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckCountryDestinationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    void "test country Destination rule"() {
        given:
        Exchange exchange = new Exchange(requestType: requestType, countryOfDestinationCode: countryOfDestinationCode, countryPartyCode:countryPartyCode)

        when:
        Rule rule = new CheckCountryDestinationRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        countryOfDestinationCode | countryPartyCode | requestType            | expected
        "CI"                     | "CI"             | ExchangeRequestType.EC | true
        "CI"                     | "ML"             | ExchangeRequestType.EC | true
        "ML"                     | "CI"             | ExchangeRequestType.EC | true
        "ML"                     | "BJ"             | ExchangeRequestType.EC | false
        "CI"                     | "BJ"             | ExchangeRequestType.EC | true
        "BJ"                     | "BJ"             | ExchangeRequestType.EC | false
        "CI"                     | "BJ"             | ExchangeRequestType.EA | false
    }

}
