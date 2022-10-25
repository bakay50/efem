package com.webbfontaine.efem.rules.exchange.declaration

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.TvfConstants.EX_FLOW
import static com.webbfontaine.efem.constants.TvfConstants.IM_FLOW

class CheckSadDeclarationRuleSpec extends Specification implements DataTest{
    def setup() {
        mockDomain SupDeclaration
        mockDomain Exchange
    }


    @Unroll
    def "test of checkDeclarationSadExistRule() for data: #sad"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()

        CheckSadDeclarationRule.metaClass.static.handleGetSad = { sad }
        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDeclarationSadExistRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), sad)

        then:
        supDeclaration.hasErrors() == result

        where:
        result | sad
        true   | [statusCode: 404, data: []]
        false  | [statusCode: 200, data: []]
    }

    @Unroll
    def "test of checkDeclarationSadCurrencyCodeRule()"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()

        CheckSadDeclarationRule.metaClass.static.handleGetSad = { sadData }
        CheckSadDeclarationRule.metaClass.static.handleGetExchange = { exchangeInstance }

        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDeclarationSadCurrencyCodeRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), sadData)

        then:
        supDeclaration.hasErrors() == results

        where:
        results | sadData                                               | exchangeInstance
        true    | [statusCode: 200, data: [invoiceCurrencyCode: "USD"]] | new Exchange(currencyCode: "XOF")
        true    | [statusCode: 200, data: [invoiceCurrencyCode: "EUR"]] | new Exchange(currencyCode: "USD")
        true    | [statusCode: 200, data: [invoiceCurrencyCode: "YPN"]] | new Exchange(currencyCode: "EUR")
        false   | [statusCode: 200, data: [invoiceCurrencyCode: "EUR"]] | new Exchange(currencyCode: "EUR")
        false   | [statusCode: 200, data: [invoiceCurrencyCode: "XOF"]] | new Exchange(currencyCode: "XOF")
    }

    @Unroll
    def "test of checkDuplicateDeclarationSessionRule() with declarations : #declarations"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()
        supDeclaration.clearanceOfficeCode = "CIAB1"
        supDeclaration.declarationSerial = "C"
        supDeclaration.declarationNumber = 396
        supDeclaration.declarationDate = new LocalDate()

        CheckSadDeclarationRule.metaClass.static.handleGetExchange = { new Exchange(supDeclarations: declarations) }

        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDuplicateDeclarationSessionRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), supDeclaration)

        then:
        supDeclaration.hasErrors() == results

        where:
        results | declarations
        true    | [new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate())]
        true    | [new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate()), new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 395, declarationDate: new LocalDate())]
        false   | [new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 395, declarationDate: new LocalDate())]
    }


    @Unroll
    def "test of checkDeclarationSadTypeRule() with sad data : #sadData"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()

        CheckSadDeclarationRule.metaClass.static.handleGetSad = { sadData }

        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDeclarationSadTypeRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), sadData)

        then:
        supDeclaration.hasErrors() == results

        where:
        results | sadData
        true    | [statusCode: 200, data: [typeOfDeclaration: EX_FLOW]]
        false   | [statusCode: 200, data: [typeOfDeclaration: IM_FLOW]]
    }

    @Unroll
    def "test of checkDeclarationSadStatusRule() with sad data: #sad"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()

        CheckSadDeclarationRule.metaClass.static.handleGetSad = { sad }

        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDeclarationSadStatusRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), sad)
        then:
        supDeclaration.hasErrors() == result

        where:
        result | sad
        true   | [statusCode: 200, data: [status: "Assessed"]]
        false   | [statusCode: 200, data: [status: "Paid"]]
        false  | [statusCode: 200, data: [status: "Totally exited"]]
        false  | [statusCode: 200, data: [status: "Exited"]]
    }


    @Unroll
    def "test of checkDeclarationRemainingBalanceRule()"() {
        given:
        SupDeclaration supDeclaration = new SupDeclaration()

        CheckSadDeclarationRule.metaClass.static.handleGetSad = { sadData }
        CheckSadDeclarationRule.metaClass.static.getExchangeFromSad = { exchangeFromSad }

        SupDeclaration.metaClass.static.findAllByClearanceOfficeCodeAndDeclarationSerialAndDeclarationNumberAndDeclarationDate = { office, serial, number, declarationDate -> supDeclarations }

        when:
        Rule rule = new CheckSadDeclarationRule()
        rule.checkDeclarationRemainingBalanceRule(new RuleContext(supDeclaration, supDeclaration.errors as Errors), sadData)

        then:
        supDeclaration.hasErrors() == results

        where:
        results | sadData | exchangeFromSad                                                                                                                                                                                | supDeclarations
        true    | []      | new Exchange(requestNo: 'REF1', totalAmountOfCif: 500000, currencyRate: 655, clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: "396", declarationDate: new LocalDate()) | [new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate(), declarationAmountWriteOff: 250), new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate(), declarationAmountWriteOff: 2500)]
        false   | []      | new Exchange(requestNo: 'REF2', totalAmountOfCif: 500000, currencyRate: 655, clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: "396", declarationDate: new LocalDate()) | [new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate(), declarationAmountWriteOff: 250), new SupDeclaration(clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate(), declarationAmountWriteOff: 250)]

    }
}
