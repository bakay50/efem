package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.rules.transferOrder.checking.OrderClearanceAmountRule
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class OrderClearanceAmountRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(OrderClearanceOfDom)
    }

    @Unroll
    def "test OrderClearanceAmountRule"() {
        given:
        OrderClearanceOfDom order = new OrderClearanceOfDom(amountToBeSettledMentionedCurrency: amountToBeSettledMentionedCurrency,
                amountRequestedMentionedCurrency:amountRequestedMentionedCurrency, amountSettledMentionedCurrency: amountSettledMentionedCurrency)

        when:
        Rule rule = new OrderClearanceAmountRule()
        rule.apply(new RuleContext(order, order.errors as Errors))

        then:
        order?.hasErrors() == hasError
        order?.errors?.allErrors?.code[0] == errorMessage

        where:
        amountToBeSettledMentionedCurrency | amountRequestedMentionedCurrency | amountSettledMentionedCurrency | errorMessage                                                           | hasError
        80                                 | 90                               | 80                             | "orderClearanceOfDom.amountToBeSettledMentionedCurrency.greater.error" | true
        90                                 | 90                               | 100                            | "orderClearanceOfDom.amountSettledMentionedCurrency.greater.error"     | true
        100                                | 100                              | 90                             | null                                                                   | false
    }
}
