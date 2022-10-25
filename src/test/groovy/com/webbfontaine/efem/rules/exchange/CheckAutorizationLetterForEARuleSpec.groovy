package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification

class CheckAutorizationLetterForEARuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Exchange)
    }

    void "test CheckAutorizationLetterForEARule()"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.basedOn = basedOn
        exchange.operType = operType
        exchange.attachedDocs = attachedFile

        when:
        Rule rule = new CheckAutorizationLetterForEARule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        operType | requestType            | basedOn | attachedFile                                                        | expected
        "EA005"  | ExchangeRequestType.EA | "TVF"   | [new AttachedDoc(docType: "007")]                                   | true
        "EA005"  | ExchangeRequestType.EA | "TVF"   | []                                                                  | true
        "EA005"  | ExchangeRequestType.EA | "TVF"   | [new AttachedDoc(docType: "6057")]                                  | false
        "EA005"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "6058"), new AttachedDoc(docType: "007")] | true
        "EA005"  | ExchangeRequestType.EA | "SAD"   | []                                                                  | true
        "EA005"  | ExchangeRequestType.EA | "SAD"   | [new AttachedDoc(docType: "6057")]                                  | false
    }
}
