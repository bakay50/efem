package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckOperationTypeCompatibilityRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    void "test CheckOperationTypeCompatibilityRule()"() {
        given:
        Exchange exchange = new Exchange(operType: operType, requestType: requestType)
        exchange.setStartedOperation(startedOperation)
        when:
        Rule rule = new CheckOperationTypeCompatibilityRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        operType | requestType            | startedOperation            | expected
        "EC001"  | ExchangeRequestType.EC | Operation.APPROVE_REQUESTED | false
        "EA001"  | ExchangeRequestType.EC | Operation.CREATE            | true
        "EC001"  | ExchangeRequestType.EC | Operation.REQUEST_QUERIED   | false
        "EA001"  | ExchangeRequestType.EA | Operation.CREATE            | false
        "EC001"  | ExchangeRequestType.EA | Operation.REQUEST_QUERIED   | true

    }
}
