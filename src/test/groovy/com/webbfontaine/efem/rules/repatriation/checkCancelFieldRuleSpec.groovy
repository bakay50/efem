package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class checkCancelFieldRuleSpec extends Specification implements DataTest {

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        mockDomain(Repatriation)
        Repatriation repatriation = new Repatriation(id: 1, requestNo: "0001", comments: comments)
        repatriation.setStartedOperation(startedOperation)
        repatriation.save(flush: true, validate: false, failOnError: false)

        when:
        Rule rule = new checkCancelFieldRule()
        rule.apply(new RuleContext(repatriation, repatriation.errors as Errors))

        then:
        repatriation?.errors.allErrors?.code[0] == expectedError
        where:
        comments             | expectedError                           | startedOperation
        "Comments of Cancel" | null                                    | Operation.CANCEL_QUERIED
        null                 | "client.transfer.queryMandatoryMessage" | Operation.CANCEL_QUERIED
        ""                   | "client.transfer.queryMandatoryMessage" | Operation.CANCEL_CONFIRMED
        ""                   | "client.transfer.queryMandatoryMessage" | Operation.QUERY_DECLARED
        null                 | "client.transfer.queryMandatoryMessage" | Operation.QUERY_DECLARED

    }
}
