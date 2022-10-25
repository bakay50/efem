package com.webbfontaine.repatriation.rules

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.repatriation.AtleastOneEcValidationRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.UPDATE_CONFIRMED

class AtleastOneEcValidationRuleSpec extends Specification implements DataTest{
    def setup() {
        mockDomain(Repatriation)
    }

    @Unroll
    void "test for OneECAtLeastValidation should expected error message"() {
        given:
        Repatriation repatriationInstance = new Repatriation(requestNo: "001", requestNumberSequence: "000001", natureOfFund:natureOfFund, clearances:clearances)
        repatriationInstance.startedOperation = startedOperation
        when:
        Rule rule = new AtleastOneEcValidationRule()
        rule.apply(new RuleContext(repatriationInstance, repatriationInstance.errors as Errors))

        then:
        repatriationInstance.hasErrors() == expected

        where:
        natureOfFund         | clearances                                                                       | startedOperation | expected
        NatureOfFund.NOF_PRE | []                                                                               | null             | false
        NatureOfFund.NOF_REP | []                                                                               | null             | true
        NatureOfFund.NOF_REP | [new ClearanceOfDom(ecReference: "001"), new ClearanceOfDom(ecReference: "002")] | null             | false
        NatureOfFund.NOF_PRE | []                                                                               | UPDATE_CONFIRMED | true

    }
}
