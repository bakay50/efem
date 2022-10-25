package com.webbfontaine.efem.rules.clearanceOfDom


import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.CheckRepatriatedAmountRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification

class CheckRepatriatedAmountRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(ClearanceOfDom)
        mockDomain(Repatriation)
    }

    def "test for the sum of identical EC"() {
        given:
        Repatriation repatriation = new Repatriation(clearances: clearences)
        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(repats: repatriation, ecReference: ec, domAmtInCurr: domAmtInCurr)

        when:
        Rule rule = new CheckRepatriatedAmountRule()
        rule.apply(new RuleContext(clearanceOfDom, clearanceOfDom.errors as Errors))

        then:
        clearanceOfDom?.hasErrors() == expectedResult

        where:
        clearences                                                                                                                                                                       | ec      | domAmtInCurr | expectedResult
        [new ClearanceOfDom(ecReference: 'EC011', repatriatedAmtInCurr: 100, domAmtInCurr: 500), new ClearanceOfDom(ecReference: 'EC011', repatriatedAmtInCurr: 200, domAmtInCurr: 500)] | 'EC011' | 500          | false
        [new ClearanceOfDom(ecReference: 'EC011', repatriatedAmtInCurr: 100, domAmtInCurr: 500), new ClearanceOfDom(ecReference: 'EC014', repatriatedAmtInCurr: 200, domAmtInCurr: 500)] | 'EC015' | 500          | false
        [new ClearanceOfDom(ecReference: 'EC012', repatriatedAmtInCurr: 400, domAmtInCurr: 500), new ClearanceOfDom(ecReference: 'EC012', repatriatedAmtInCurr: 200, domAmtInCurr: 500)] | 'EC012' | 500          | true
        [new ClearanceOfDom(ecReference: 'EC013', repatriatedAmtInCurr: 100, domAmtInCurr: 500)]                                                                                         | 'EC013' | 500          | false
        []                                                                                                                                                                               | 'EC017' | 700          | false

    }

}
