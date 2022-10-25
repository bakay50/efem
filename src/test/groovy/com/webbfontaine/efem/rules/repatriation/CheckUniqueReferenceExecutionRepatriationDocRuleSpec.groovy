package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.CONFIRM_DECLARED
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.DECLARE_QUERIED

class CheckUniqueReferenceExecutionRepatriationDocRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain Repatriation
    }


    @Unroll
    def "test of CheckUniqueReferenceExecutionRepatriationDocRule()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Repatriation repatriation = new Repatriation()
        repatriation.id = 256
        repatriation.startedOperation = startedOperation as Operation
        repatriation.executionRef = referenceExecution
        repatriation.repatriationBankCode = bankCode

        Repatriation.metaClass.static.findByRequestYearAndRepatriationBankCodeAndExecutionRef = { year, code, reference -> foundedRepatriation }

        when:
        Rule rule = new CheckUniqueReferenceExecutionRepatriationDocRule()
        rule.apply(new RuleContext(repatriation, repatriation.errors as Errors))

        then:
        repatriation.hasErrors() == expected

        where:
        expected | startedOperation | bankCode | referenceExecution | foundedRepatriation
        true     | DECLARE_QUERIED  | "nsia"   | "nsia022"          | new Repatriation(repatriationBankCode: "nsia", executionRef: "nsia022", requestYear: 2020, id: 41)
        true     | CONFIRM_DECLARED | "sgb1"   | "sgb1-8963"        | new Repatriation(repatriationBankCode: "sgb1", executionRef: "sgb1-8963", requestYear: 2020, id: 456)
        true     | CREATE           | "sib1"   | "sb1-2589"         | new Repatriation(repatriationBankCode: "sib1", executionRef: "sb1-2589", requestYear: 2020, id: 693)
        false    | CREATE           | "sib1"   | "sb1-2589"         | null
    }
}
