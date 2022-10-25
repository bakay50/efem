package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.workflow.Operation.DOMICILIATE
import static com.webbfontaine.efem.workflow.Operation.APPROVE_REQUESTED
import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED
import static com.webbfontaine.efem.constants.Statuses.ST_CANCELLED


class CheckRegistrationNumberBankRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain Exchange
    }


    @ConfineMetaClassChanges(Exchange)
    @Unroll
    void "test CheckRegistrationNumberBankRule()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Exchange exchange = new Exchange()
        exchange.id = 123
        exchange.startedOperation = startedOperation as Operation
        exchange.requestType = requestType
        exchange.bankCode = bankCode
        exchange.registrationNumberBank = registrationNoBank
        
        Exchange.metaClass.static.findByRequestYearAndBankCodeAndRegistrationNumberBankAndStatusNotEqual = { year, bank, regNo, stat -> return foundExchange }

        when:
        Rule rule = new CheckRegistrationNumberBankRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        startedOperation  | requestType | foundExchange                                                                                                    | bankCode | registrationNoBank | expected
        DOMICILIATE       | EC          | new Exchange(id: 25, bankCode: "SGB1", registrationNumberBank: "240", requestYear: 2020, status: ST_REQUESTED)   | "SGB1"   | "240"              | true
        DOMICILIATE       | EC          | null                                                                                                             | "SGB1"   | "00240"            | false
        DOMICILIATE       | EC          | new Exchange(id: 25, bankCode: "SGB1", registrationNumberBank: "00240", requestYear: 2020, status: ST_CANCELLED) | "SGB1"   | "00240"            | false
        APPROVE_REQUESTED | EA          | new Exchange(id: 25, bankCode: "SGB1", registrationNumberBank: "00240", requestYear: 2020, status: ST_REQUESTED) | "SGB1"   | "00240"            | true
        APPROVE_REQUESTED | EA          | null                                                                                                             | "SGB1"   | "308"              | false
    }
}
