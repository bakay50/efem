package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import org.joda.time.LocalDate

import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED

class CheckSadMandatoryFieldsSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain(Exchange)
    }

    def "test for check SAD mandatory fields "() {
        given:
        Exchange exchange = new Exchange(requestType: requestType, clearanceOfficeCode: clearanceOfficeCode, declarationNumber: declarationNumber, declarationDate: declarationDate)
        exchange.startedOperation = startedOperation

        when:
        Rule rule = new CheckSadMandatoryFields()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        clearanceOfficeCode | declarationNumber | declarationDate | startedOperation | requestType            | expectedResult
        'CIAB'              | '350'             | new LocalDate() | UPDATE_APPROVED  | ExchangeRequestType.EA | false
        ''                  | '350'             | new LocalDate() | UPDATE_EXECUTED  | ExchangeRequestType.EC | true
        'CIAB'              | '350'             | new LocalDate() | UPDATE_EXECUTED  | ExchangeRequestType.EC | false
        'CIAB'              | '350'             | new LocalDate() | UPDATE_APPROVED  | ExchangeRequestType.EC | false
        'CIAB'              | null              | new LocalDate() | UPDATE_APPROVED  | ExchangeRequestType.EC | true
        null                | null              | ''              | UPDATE_EXECUTED  | ExchangeRequestType.EC | true
        'CIAB'              | '350'             | ''              | UPDATE_APPROVED  | ExchangeRequestType.EC | true


    }

}
