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

class CheckEATransactionFromSadRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    void "test CheckEATransactionFromSadRule() test case #operType and #attachedFile result : #expected"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.basedOn = basedOn
        exchange.operType = operType
        exchange.attachedDocs = attachedFile

        when:
        Rule rule = new CheckEATransactionFromSadRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        operType | requestType            | basedOn | attachedFile                                                        | expected
        "EA006"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "007")]                                   | true
        "EA006"  | ExchangeRequestType.EA | "SAD"   | []                                                                  | true
        "EA006"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "6054"), new AttachedDoc(docType: "007")] | true
        "EA006"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "6058")]                                  | false
        "EA006"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "6058"), new AttachedDoc(docType: "007")] | false
    }
}
