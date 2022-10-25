package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.constants.ExchangeRequestType.getEC

@ConfineMetaClassChanges([AppConfig])
class CheckGeoAreaRuleSpec extends Specification implements DataTest{
    def setupSpec(){
        mockDomain(Exchange)
    }

    void setup() {
        def listCtryDest= ["BF", "BJ", "TG", "SN", "NE", "ML", "GW"]
        GroovyMock(AppConfig, global: true)
        AppConfig.getListCountryOfDestination() >> listCtryDest
    }

    @Unroll
    def "#testCase geoArea 001 compatible to CountryOfDestination"(){
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = reqType
        exchange.geoArea = geoArea
        exchange.countryOfDestinationCode = countryOfDestCode
        exchange.startedOperation = startedOper

        when:
        Rule rule = new CheckGeoAreaRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        testCase                           | startedOper | reqType  | geoArea  | countryOfDestCode | expected
        "Conditions are correct"           | 'CREATE'    | EC       | '001'    | 'FR'              | true
        "Condition are not correct"        | 'CREATE'    | EC       | '001'    | 'BJ'              | false
    }

    @Unroll
    def "#testCase geoArea 002 compatible to CountryOfDestination"(){
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = reqType
        exchange.geoArea = geoArea
        exchange.countryOfDestinationCode = countryOfDestCode
        exchange.startedOperation = startedOper

        when:
        Rule rule = new CheckGeoAreaRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        testCase                           | startedOper | reqType  | geoArea  | countryOfDestCode | expected
        "Conditions are correct"           | 'CREATE'    | EC       | '002'    | 'FR'              | false
        "Condition are not correct"        | 'CREATE'    | EC       | '002'    | 'BJ'              | false
    }

    @Unroll
    def "#testCase geoArea 003 compatible to CountryOfDestination"(){
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = reqType
        exchange.geoArea = geoArea
        exchange.countryOfDestinationCode = countryOfDestCode
        exchange.startedOperation = startedOper

        when:
        Rule rule = new CheckGeoAreaRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        testCase                            | startedOper | reqType  | geoArea  | countryOfDestCode |expected
        "Conditions are correct"            | 'CREATE'    | EC       | '003'    | 'FR'              |false
        "Condition are not correct"         | 'CREATE'    | EC       | '003'    | 'BJ'              |false
    }

    @Unroll
    def "#testCase setDepartmentInCharge"(){
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = reqType
        exchange.geoArea = geoArea
        exchange.countryOfDestinationCode = countryOfDestCode
        exchange.currencyCode = curCode
        exchange.startedOperation = startedOper

        when:
        Rule rule = new CheckGeoAreaRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.departmentInCharge == expected

        where:
        testCase                           | startedOper | reqType  | geoArea  | countryOfDestCode | curCode    | expected
        "Conditions are correct"           | 'CREATE'    | EC       | '002'    | 'FR'              | 'XOF'      | "FINEX"
        "Condition are not correct"        | 'CREATE'    | EC       | '002'    | 'BJ'              | 'XOF'      | "FINEX"
    }
}