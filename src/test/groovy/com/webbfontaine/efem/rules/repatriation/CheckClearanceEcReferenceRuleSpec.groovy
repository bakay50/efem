package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.CheckClearanceEcReferenceRule
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckClearanceEcReferenceRuleSpec extends Specification implements DataTest {
    def setup() {
        defineBeans() {
            repatriationService(RepatriationService)
        }
        mockDomain(ClearanceOfDom)
        mockDomain(Repatriation)

    }

    @Unroll
    void "test if add the same EC reference multiple times in a current session"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([conversationId: "0225478"], null)

        CheckClearanceEcReferenceRule.metaClass.static.getRepatriation = {
            return new Repatriation(clearances: clearancesOfDom)
        }

        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(rank: 1, ecReference: ecReference)

        when:
        Rule rule = new CheckClearanceEcReferenceRule()
        rule.apply(new RuleContext(clearanceOfDom, clearanceOfDom.errors as Errors))

        then:
        clearanceOfDom?.errors?.allErrors?.code[0] == expectedClearanceError

        where:
        expectedClearanceError                 | ecReference | clearancesOfDom
        "clerance.errors.ecReference.checking" | "EC0052"    | [new ClearanceOfDom(ecReference: "EC0052")]
        "clerance.errors.ecReference.checking" | "EC0022"    | [new ClearanceOfDom(ecReference: "EC0022"), new ClearanceOfDom(ecReference: "EC0047")]
        null                                   | "EC0015"    | [new ClearanceOfDom(ecReference: "EC0022"), new ClearanceOfDom(ecReference: "EC0047")]


    }
}
