package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.STORE

class CheckAmountMentionnedInCurrRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    def "test CheckAmountMentionnedInCurrRule"() {
        given:
        Exchange exchange = new Exchange(amountMentionedCurrency: amountMentionedCurrency)
        exchange.startedOperation = startedOperation

        when:
        Rule rule = new CheckAmountMentionnedInCurrRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        amountMentionedCurrency | startedOperation | expectedResult
        null                    | CREATE           | true
        0                       | CREATE           | true
        10000                   | REQUEST          | false
        0                       | STORE            | false
    }
}
