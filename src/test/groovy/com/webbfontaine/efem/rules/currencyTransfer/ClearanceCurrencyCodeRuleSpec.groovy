package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.Statuses.ST_CEDED
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED
import static com.webbfontaine.efem.constants.Statuses.ST_DECLARED

class ClearanceCurrencyCodeRuleSpec extends Specification implements  DataTest {

    def setup() {
        mockDomain(Exchange)
        mockDomain(Repatriation)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.ADB) >> bank
        WebRequestUtils.getParams() >> new GrailsParameterMap([currencyCodeTransfer: currencyCodeTransfer], null)
        ClearanceDomiciliation clearanceDomiciliationInstance = new ClearanceDomiciliation(requestNo: "1",
                currencyCode: "EUR")

        Exchange.metaClass.static.findByRequestNo = {
            new Exchange(requestNo: "EC01", currencyCode:currencyCode)
        }

        when:
        Rule rule = new ClearanceCurrencyCodeRule()
        rule.apply(new RuleContext(clearanceDomiciliationInstance, clearanceDomiciliationInstance.errors as Errors))

        then:
        clearanceDomiciliationInstance?.errors.allErrors?.code[0] == expectedClearanceError

        where:
        bank     | status       | currencyCode | currencyCodeTransfer | expectedClearanceError
        ["SGB1"] | ST_DECLARED  | "EUR"        | "EUR"                | "currencyTransfer.errors.repatriation.bank"
        ["SGB1"] | ST_CONFIRMED | "EUR"        | "USD"                | "currencyTransfer.errors.currency.code.label"
        ["SGB1"] | ST_CEDED     | "EUR"        | "USD"                | "currencyTransfer.errors.currency.code.label"

    }
}
