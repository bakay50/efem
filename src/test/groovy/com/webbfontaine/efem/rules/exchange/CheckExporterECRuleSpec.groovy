package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckExporterECRuleSpec extends Specification implements DataTest{

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    def "test CheckExporterECRule"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(_) >> prop

        Exchange exchange = new Exchange(exporterCode: exporterCode, requestType: requestType)
        exchange.save(flush : true, validate : false, failOnError : false)

        when:
        Rule rule = new CheckExporterECRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        requestType | exporterCode    | prop                      | expectedResult
        "EA"        | "0101142A"      | ["ALL"]                   | false
        "EC"        | "0101142A"      | ["1331590S","0101057G"]   | true
        "EC"        | "1331590S"      | ["1331590S","0101057G"]   | false
    }

}
