package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.rules.transferOrder.checking.AmountValidationRule
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class AmountValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        defineBeans() {
            springSecurityService(SpringSecurityService)
        }
        mockDomain(TransferOrder)
    }

    @Unroll
    void "test for AmountValidationRule should expected error message"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBankAgent
        TransferOrder transferInstance = new TransferOrder()

        transferInstance.addOrderClearanceOfDoms(new OrderClearanceOfDom(eaReference: "EA01",amountToBeSettledMentionedCurrency: amountToBeSettledMentionedCurrency,
                amountRequestedMentionedCurrency:amountRequestedMentionedCurrency, amountSettledMentionedCurrency: amountSettledMentionedCurrency))

        when:
        Rule rule = new AmountValidationRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        transferInstance?.errors.allErrors?.code[0] == expectedError
        where:
        amountToBeSettledMentionedCurrency | amountRequestedMentionedCurrency | amountSettledMentionedCurrency | expectedError                                                          | isBankAgent
        80                                 | 90                               | 80                             | "orderClearanceOfDom.amountToBeSettledMentionedCurrency.greater.error" | false
        90                                 | 90                               | 100                            | "orderClearanceOfDom.amountSettledMentionedCurrency.greater.error"     | false
        100                                | 100                              | 90                             | null                                                                   | true
        0                                  | 0                                | 0                              | "orderClearanceOfDom.amountRequestedMentionedCurrency.zero.error"      | true

    }
}
