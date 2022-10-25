package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class RequestDateValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }
    @Unroll
    def "Check RequestDateValidationRuleSpec if Request date is #testCase"() {
        given:
        Exchange exchange = new Exchange(registrationDateBank: registrationDate, requestDate: LocalDate.now())
        exchange.save(flush : true, validate : false, failOnError : false)

        when:
        Rule rule = new RegistrationDateBankValidationRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        testCase                 | registrationDate                 | expectedResult
        'Prior to Request Date'  | LocalDate.now().minusDays(2)   | true
        'Higher to Request Date' | LocalDate.now().plusDays(2)    | false
        'Equal to Request Date'  | LocalDate.now()                  | false
    }
}
