package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class ChkCurrencyAndCountryOfExportRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    def "Check ChkCurrencyAndCountryOfExportRule if Currency Code is #currencyCode"() {
        given:
        Exchange exchange = new Exchange(currencyPayCode: currencyPayCode, requestType: "EA", countryOfExportCode:countryOfExportCode)
        exchange.save(flush : true, validate : false, failOnError : false)

        when:
        Rule rule = new ChkCurrencyAndCountryOfExportRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        currencyPayCode     |countryOfExportCode| expectedResult
        "XOF"               |"FR"               | true
        "XOF"               |"ML"               | false
        "USD"               |"FR"               | false
    }

}
