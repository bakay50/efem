package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.CheckClearanceEcCurrencyCodeRule
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckClearanceEcCurrencyCodeRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(ClearanceOfDom)
        mockDomain(Exchange)
    }

    @Unroll
    void "test check if clearance of dom EC currency code : #currencyCode is equals to Repatriation currency code : #currencyCodeRepatriation"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([currencyCode: currencyCodeRepatriation], null)
        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(rank: 1, currencyCodeEC: currencyCode, ecReference: "EC02020")
        Exchange.metaClass.static.findByRequestNo = {
            new Exchange(requestNo: "EC02020", currencyCode: currencyCode)
        }

        when:
        Rule rule = new CheckClearanceEcCurrencyCodeRule()
        rule.apply(new RuleContext(clearanceOfDom, clearanceOfDom.errors as Errors))

        then:
        clearanceOfDom?.errors?.allErrors?.code[0] == expectedClearanceError

        where:
        currencyCode | currencyCodeRepatriation | expectedClearanceError
        "EUR"        | "EUR"                    | null
        "EUR"        | "USD"                    | "clearanceDom.currencyCodeEC.error"
        "XOF"        | "EUR"                    | "clearanceDom.currencyCodeEC.error"
    }
}
