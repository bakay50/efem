package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class ChkRepatirationAmountNegativeRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    void "Test ChkRepatirationAmountNegativeRule "() {

        given:
        Repatriation repatriationInstance = new Repatriation(receivedAmount: receivedAmount, receivedAmountNat: receivedAmountNat)

        when:
        Rule rule = new CheckRepatriationAmountNegativeRule()
        rule.apply(new RuleContext(repatriationInstance, repatriationInstance.errors as Errors))

        then:
        repatriationInstance?.hasErrors() == expectedResult

        where:
        receivedAmount  | receivedAmountNat | expectedResult
        new BigDecimal("100")  | new BigDecimal("100")  | false
        new BigDecimal("100")  | new BigDecimal("-100") | true
        new BigDecimal("-100") | new BigDecimal("100")  | true

    }

}
