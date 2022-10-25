package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.attachedDoc.RepAttachedDoc
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification

import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.STORE

class RepatriationAttachedFileSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain(Repatriation)
    }

    def "test for attachment of credit notice or the statement of account"() {
        given:
        Repatriation repatriation = new Repatriation()
        repatriation.startedOperation = operation
        repatriation.attachedDocs = docs

        when:
        Rule rule = new RepatriationAttachedFile()
        rule.apply(new RuleContext(repatriation, repatriation.errors as Errors))

        then:
        repatriation?.hasErrors() == expectedResult

        where:
        operation | docs                                                                       | expectedResult
        CREATE    | [new RepAttachedDoc(docType: "0004"), new RepAttachedDoc(docType: "0008")] | true
        CREATE    | [new RepAttachedDoc(docType: "WF08"), new RepAttachedDoc(docType: "0008")] | false
        CREATE    | [new RepAttachedDoc(docType: "WF08")]                                      | false
        STORE     | [new RepAttachedDoc(docType: "0007"), new RepAttachedDoc(docType: "0008")] | false
        CREATE    | []                                                                         | true
    }

}
