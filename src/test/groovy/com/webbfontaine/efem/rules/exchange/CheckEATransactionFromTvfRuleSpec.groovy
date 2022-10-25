package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification

class CheckEATransactionFromTvfRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    void "test CheckEATransactionFromTvfRule()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.basedOn = basedOn
        exchange.operType = operType

        when:
        Rule rule = new CheckEATransactionFromTvfRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        operType | requestType            | basedOn | expected
        "EA006"  | ExchangeRequestType.EA | "TVF"   | true
        "EA002"  | ExchangeRequestType.EA | "TVF"   | false
        "EA001"  | ExchangeRequestType.EA | "TVF"   | false
    }
}
