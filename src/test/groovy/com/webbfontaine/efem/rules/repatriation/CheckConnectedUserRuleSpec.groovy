package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckConnectedUserRuleSpec extends Specification implements  DataTest {

    def setup() {
        mockDomain(Repatriation)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBank
        UserUtils.isTrader() >> isTrader
        UserUtils.getUserProperty(UserProperties.ADB) >> bank
        UserUtils.getUserProperty(UserProperties.TIN) >> exporter
        Repatriation RepatriationInstance = new Repatriation(repatriationBankCode: "ECO1", code: "EXP01")

        when:
        Rule rule = new CheckConnectedUserRule()
        rule.apply(new RuleContext(RepatriationInstance, RepatriationInstance.errors as Errors))

        then:
        RepatriationInstance?.hasErrors() == expected
        where:
        bank   | isBank | isTrader | exporter | expected
        "SGB1" | true   | false    | "ALL"    | true
        "ALL"  | false  | true     | "EX01"   | true
        "ECO1" | true   | false    | "ALL"    | false
        "ALL"  | false  | true     | "EXP01"  | false

    }
}
