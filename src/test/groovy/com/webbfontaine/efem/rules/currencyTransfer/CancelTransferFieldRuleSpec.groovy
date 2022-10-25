package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CancelTransferFieldRuleSpec extends Specification implements  DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer(requestNo: "1",
                currencyCode: "EUR", amountTransferred: 5000)
        WebRequestUtils.getParams() >> new GrailsParameterMap([comments: comments], null)

        when:
        Rule rule = new CancelTransferFieldRule()
        rule.apply(new RuleContext(currencyTransferInstance, currencyTransferInstance.errors as Errors))

        then:
        currencyTransferInstance?.errors.allErrors?.code[0] == expectedError
        where:
        comments        | expectedError
        "My comment"    | null
        null            | "currencyTransfer.comment.required"
        ""              | "currencyTransfer.comment.required"

    }
}
