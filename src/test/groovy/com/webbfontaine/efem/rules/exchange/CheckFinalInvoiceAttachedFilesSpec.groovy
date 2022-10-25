package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED

class CheckFinalInvoiceAttachedFilesSpec extends Specification implements DataTest {
    def setupSpec() {
        mockDomain(Exchange)
        mockDomain(AttachedDoc)
    }

    @Unroll
    def "test when final amound is attached "() {
        given:
        Exchange exchange = new Exchange(requestType: requestType, isFinalAmount: isFinalAmount, attachedDocs: docs)
        exchange.startedOperation = startedOperation

        when:

        Rule rule = new CheckFinalInvoiceAttachedFiles()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))


        then:
        exchange?.hasErrors() == expectedResult

        where:
        isFinalAmount | docs                                                                 | startedOperation | requestType            | expectedResult
        true          | [new AttachedDoc(docType: "0004"), new AttachedDoc(docType: "0008")] | UPDATE_APPROVED  | ExchangeRequestType.EC | true
        true          | [new AttachedDoc(docType: "0007"), new AttachedDoc(docType: "WF01")] | UPDATE_APPROVED  | ExchangeRequestType.EC | false
        true          | [new AttachedDoc(docType: "0007")]                                   | UPDATE_APPROVED  | ExchangeRequestType.EC | false
        false         | [new AttachedDoc(docType: "0007")]                                   | UPDATE_APPROVED  | ExchangeRequestType.EC | false
        true          | []                                                                   | UPDATE_APPROVED  | ExchangeRequestType.EC | true


    }
}
