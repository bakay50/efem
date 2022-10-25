package com.webbfontaine.efem.rules.repatriation


import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.CheckClearanceRepatriatedAmtInCurrNotNullRule
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckClearanceRepatriatedAmtInCurrNotNullRuleSpec extends Specification implements DataTest
{
    def setup()
    {
        mockDomain(ClearanceOfDom)
    }

    @Unroll
    void "test check if clearance of dom repatriatedAmtInCurr field is null or equals to zero"()
    {
        given:
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(rank: 1, repatriatedAmtInCurr: repatriatedAmtInCurr, ecReference: "EC02020")

        when:
        Rule rule = new CheckClearanceRepatriatedAmtInCurrNotNullRule()
        rule.apply(new RuleContext(clearanceOfDom, clearanceOfDom.errors as Errors))

        then:
        clearanceOfDom?.errors?.allErrors?.code[0] == expectedClearanceError

        where:
        repatriatedAmtInCurr   | expectedClearanceError
        null                   | "clerance.errors.repatriatedAmtInCurr.zero"
        BigDecimal.ZERO        | "clerance.errors.repatriatedAmtInCurr.zero"
        new BigDecimal("5000") | null
    }
}
