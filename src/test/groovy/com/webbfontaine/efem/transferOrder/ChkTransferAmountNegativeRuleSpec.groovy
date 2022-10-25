package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.rules.transferOrder.checking.CheckTransferAmountNegativeRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class ChkTransferAmountNegativeRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(TransferOrder)
    }

    @Unroll
    void "Test ChkTransferAmountNegativeRule "() {

        given:
        TransferOrder transferInstance = new TransferOrder(transferAmntRequested: transferAmntRequested, transferNatAmntRequest: transferNatAmntRequest)

        when:
        Rule rule = new CheckTransferAmountNegativeRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        transferInstance?.hasErrors() == expectedResult

        where:
        transferAmntRequested  | transferNatAmntRequest | expectedResult
        new BigDecimal("100")  | new BigDecimal("100")  | false
        new BigDecimal("100")  | new BigDecimal("-100") | true
        new BigDecimal("-100") | new BigDecimal("100")  | true

    }

}
