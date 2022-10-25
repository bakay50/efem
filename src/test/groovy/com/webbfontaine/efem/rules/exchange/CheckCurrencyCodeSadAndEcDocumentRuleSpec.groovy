package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_001
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_003
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_002
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC

class CheckCurrencyCodeSadAndEcDocumentRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain Exchange
        defineBeans() {
            sadService(SadService)
        }
    }

    @Unroll
    void "test CheckCurrencyCodeSadAndEcDocumentRule()"() {
        given:
        WebRequestUtils.getParams() >> new GrailsParameterMap([clearanceOfficeCode: "CIAB1", clearanceOfficeName: "ABIDJAN-PORT", declarationSerial: "C", declarationNumber: "360", declarationDate: "02/10/2020"], null)
        Exchange exchange = new Exchange()
        exchange.sadInstanceId = sadId
        exchange.requestType = EC
        exchange.geoArea = area
        exchange.countryOfDestinationCode = countryOfDestinationCode

        CheckCurrencyCodeSadAndEcDocumentRule.metaClass.static.retrieveSad = { sadData }
        when:
        Rule rule = new CheckCurrencyCodeSadAndEcDocumentRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | area     | countryOfDestinationCode | currency | sadData                              | sadId
        true     | AREA_001 | "BF"                     | "XOF"    | [data: [invoiceCurrencyCode: "USD"]] | null
        true     | AREA_002 | "SN"                     | "XOF"    | [data: [invoiceCurrencyCode: "USD"]] | null
        true     | AREA_002 | "FR"                     | "EUR"    | [data: [invoiceCurrencyCode: "USD"]] | null
        true     | AREA_003 | "FR"                     | "EUR"    | [data: [invoiceCurrencyCode: "USD"]] | null
        false    | AREA_003 | "FR"                     | "EUR"    | [data: [invoiceCurrencyCode: "USD"]] | "78541"
    }
}
