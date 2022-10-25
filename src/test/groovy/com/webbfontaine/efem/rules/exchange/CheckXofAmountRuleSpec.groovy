package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.springframework.validation.Errors
import grails.testing.gorm.DataTest
import spock.lang.Specification

class CheckXofAmountRuleSpec extends Specification implements DataTest{

    def setupSpec(){
        mockDomain(Exchange)
    }

    def "test For XOF Amount when the amount inputted is #amount and convert XOF amount is #xof in EA"(){
        given:
        Tvf tvfInstance = new Tvf()
        Exchange exchange = new Exchange(tvfInstanceId: 1, tvf: tvfInstance, amountMentionedCurrency:amount, amountNationalCurrency:xof)

        when:
        Rule rule = new CheckXofAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        expectedResult  |   amount                  | xof
        true            | new BigDecimal("500.00")  | new BigDecimal("327980.00")
        false           | new BigDecimal("800.00")  | new BigDecimal("524768.00")

    }
}
