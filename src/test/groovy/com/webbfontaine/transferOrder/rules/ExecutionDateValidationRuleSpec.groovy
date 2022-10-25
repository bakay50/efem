package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.rules.transferOrder.checking.ExecutionDateValidationRule
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class ExecutionDateValidationRuleSpec extends Specification implements DataTest{

    def setup() {
        mockDomains(TransferOrder)
    }
    @Unroll
    def "Check ExecutionDateValidationRuleSpec if ExecutionDate is not greater than current date"() {
        given:
        TransferOrder transferOrder = new TransferOrder(executionDate: executionDate)

        when:
        Rule rule = new ExecutionDateValidationRule()
        rule.apply(new RuleContext(transferOrder, transferOrder.errors as Errors))

        then:
        transferOrder?.hasErrors() == expected
        transferOrder?.errors.allErrors?.code[0] == errorCode

        where:
        currentDate     | executionDate                | errorCode                                   | expected
        LocalDate.now() | LocalDate.now().plusDays(2)  | 'transferOrder.executionDate.greater.error' | true
        LocalDate.now() | LocalDate.now()              | null                                        | false
        LocalDate.now() | LocalDate.now().minusDays(4) | null                                        | false
    }
}
