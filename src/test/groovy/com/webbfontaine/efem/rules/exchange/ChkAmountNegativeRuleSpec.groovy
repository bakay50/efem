package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class ChkAmountNegativeRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    void "Test when requesting an Exchange from TVF when #testCase should result in failing the rule: #expectedResult "() {

        given:
        Exchange exchangeInstance = new Exchange(amountMentionedCurrency: amountMentionedCurrency, amountNationalCurrency: amountNationalCurrency, finalAmount: finalAmount,finalAmountInDevise:finalAmountInDevise)

        when:
        Rule rule = new CheckAmountNegativeRule()
        rule.apply(new RuleContext(exchangeInstance, exchangeInstance.errors as Errors))

        then:
        exchangeInstance?.hasErrors() == expectedResult

        where:
        amountMentionedCurrency | amountNationalCurrency | finalAmount            | finalAmountInDevise    | expectedResult
        new BigDecimal("100")   | new BigDecimal("100")  | new BigDecimal("100")  | new BigDecimal("100")  | false
        new BigDecimal("100")   | new BigDecimal("-100") | new BigDecimal("100")  | new BigDecimal("100")  | true
        new BigDecimal("-100")  | new BigDecimal("100")  | new BigDecimal("100")  | new BigDecimal("100")  | true
        new BigDecimal("100")   | new BigDecimal("100")  | new BigDecimal("100")  | null                   | false
        new BigDecimal("100")   | new BigDecimal("100")  | null                   | new BigDecimal("100")  | false
        new BigDecimal("100")   | new BigDecimal("100")  | new BigDecimal("-100") | new BigDecimal("100")  | true
        new BigDecimal("100")   | new BigDecimal("100")  | new BigDecimal("100")  | new BigDecimal("-100") | true

    }

}
