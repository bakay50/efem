package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.ExchangeRequestType.*
import static com.webbfontaine.efem.workflow.Operation.DOMICILIATE
import static com.webbfontaine.efem.workflow.Operation.REQUEST

class CheckMandatoryRegistrationNumberBankRuleSpec extends Specification implements DataTest{

    @Unroll
    void "test Check Mandatory Registration Number Bank()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBankAgent
        Exchange exchange = new Exchange(requestType : requestType, registrationNumberBank: registrationNumberBank)
        exchange?.startedOperation = startedOperation
        when:
        Rule rule = new CheckMandatoryRegistrationNumberBankRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        startedOperation  | requestType | isBankAgent  | registrationNumberBank | expected
        REQUEST           | EA          | false        | null                   | false
        DOMICILIATE       | EA          | false        | null                   | false
        DOMICILIATE       | EC          | false        | null                   | false
        DOMICILIATE       | EC          | true         | null                   | true
        DOMICILIATE       | EC          | true         | "REG001"               | false
    }
}
