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

class CheckCountryProvenanceRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    void "test country provenance rule"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.countryProvenanceDestinationCode = countryProvenanceDestinationCode
        exchange.setStartedOperation(startedOperation)

        when:
        Rule rule = new CheckCountryProvenanceRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        countryProvenanceDestinationCode | requestType            | expected | startedOperation
        "CI"                             | ExchangeRequestType.EC | true     | Operation.CREATE
        "CI"                             | ExchangeRequestType.EC | true     | Operation.REQUEST
        "FR"                             | ExchangeRequestType.EC | false    | Operation.CREATE
        "CI"                             | ExchangeRequestType.EC | true     | Operation.UPDATE_APPROVED
        "CI"                             | ExchangeRequestType.EC | false    | Operation.UPDATE_CONFIRMED
        "CI"                             | ExchangeRequestType.EA | false    | Operation.CREATE
        "CI"                             | ExchangeRequestType.EA | false    | Operation.APPROVE_REQUESTED
    }
}
