package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED

class CheckBalanceAndFinalAmountRuleSpec extends Specification implements DataTest{

    def setupSpec(){
        mockDomain(Exchange)
    }

    def "test when final amound is checked "(){
        given:
        Exchange exchange = new Exchange(startedOperation: startedOperation, requestType: requestType, finalAmountInDevise: finalAmountInDevise, balanceAs: balanceAs)

        when:
        Rule rule = new CheckBalanceAndFinalAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        finalAmountInDevise     |   startedOperation     | requestType              | balanceAs | expectedResult
        10000                   |   UPDATE_APPROVED      | ExchangeRequestType.EC   | 10000     | false
        10001                   |   UPDATE_EXECUTED      | ExchangeRequestType.EA   | 10000     | false
        10000                   |   UPDATE_APPROVED      | ExchangeRequestType.EC   | 10000     | false
        10000                   |   UPDATE_APPROVED      | ExchangeRequestType.EC   | 10001     | true
        10001                   |   UPDATE_EXECUTED      | ExchangeRequestType.EC   | 10000     | true


    }

}
