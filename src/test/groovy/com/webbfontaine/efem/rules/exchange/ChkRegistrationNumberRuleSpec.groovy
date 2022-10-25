package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import spock.lang.Specification


class ChkRegistrationNumberRuleSpec extends Specification implements DataTest {

    def setupSpec(){
        mockDomain(Exchange)
    }

    def "Test registration number must be unique"(){
        setup:
        def existingEx = new Exchange(year: LocalDate.now().year, registrationNumberBank: "bank123", bankCode: "codeBank")
        existingEx.save(flush : true, validate : false, failOnError : false)
        Rule rule = new ChkRegistrationNumberRule()
        Exchange exchange = new Exchange(registrationNumberBank: "bank123", bankCode: "codeBank", registrationDateBank: LocalDate.now())

        when:
        rule.apply(new RuleContext(exchange, exchange.errors))

        then:"exchange should be rejected since it has the same regNum and the prev document is not cancelled"
        exchange.hasErrors()
        def errorField = exchange.errors.getFieldError()
        errorField.code == "custom.checkRegistrationNumberBank.message"
        errorField.field == "registrationNumberBank"

        and:
        Exchange exchange2 = new Exchange(registrationNumberBank: "completelyDiff", bankCode: "diffBank", registrationDateBank: LocalDate.now())

        when:
        rule.apply(new RuleContext(exchange2, exchange2.errors))

        then:"A completely different exchange should not be rejected"
        !exchange2.hasErrors()

        when:"Cancelled status should not reject the previous exchange"
        existingEx.status = Statuses.ST_CANCELLED
        existingEx.merge(flush : true, validate : false, failOnError : false)
        exchange.clearErrors()
        rule.apply(new RuleContext(exchange, exchange.errors))

        then:
        !exchange.hasErrors()


    }

    void setupData(){
        new Exchange(year: LocalDate.now().year, registrationNumberBank: "bank123", bankCode: "codeBank", registrationDateBank: LocalDate.now()).save(flush: true)
    }

}