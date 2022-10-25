package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.joda.time.LocalDate

import static com.webbfontaine.efem.workflow.Operation.*
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class MandatoryFieldRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    def "test for #testcase"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Exchange exchange = new Exchange(requestType: value, comments: "test", countryOfExportCode: "AIB",
                bankCode: "test", nationalityCode: newVal, countryProvenanceDestinationCode: newVal, dateOfBoarding: LocalDate.now(), operType: value,
                amountMentionedCurrency: new BigDecimal(600), provenanceDestinationBank: "AI", bankAccountNocreditedDebited: "test")
        exchange.setStartedOperation(startedOp)
        exchange.save(flush: true, validate: false, failOnError: false)

        when:
        Rule rule = new MandatoryFieldRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expected

        where:
        testcase                                        | startedOp       | expected  | value | newVal
        'Not all mandatory fields has value '           | REQUEST         | true      | ""    | "AIB"
        'All mandatory fields has value '               | UPDATE_EXECUTED | false     | "EA"  | ""

    }
}