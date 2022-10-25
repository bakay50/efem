package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED
import static com.webbfontaine.efem.constants.Statuses.ST_DECLARED

class TransferCurrencyCodeRuleSpec extends Specification implements  DataTest {

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
        Exchange exchangeInstance = new Exchange(requestNo: "EC01", currencyCode:"EUR")

        Repatriation.metaClass.static.findByRequestNo = {
            new Repatriation(requestNo: "EC01", repatriationBankCode: "SGB1", status: status)
        }

        when:
        Rule rule = new TransferCurrencyCodeRule()
        rule.apply(new RuleContext(exchangeInstance, exchangeInstance.errors as Errors))

        then:
        exchangeInstance?.errors.allErrors?.code[0] == expectedError
        where:
        bank    |status         |currencyCode    | currencyCodeTransfer    | expectedError
        ["SGB1"]  |ST_DECLARED    |"EUR"           | "EUR"                   | "currencyTransfer.errors.repatriation.status"
        ["SGB1"]  |ST_CONFIRMED   |"EUR"           | "USD"                   | "currencyTransfer.errors.currency.code.label"

    }
}
