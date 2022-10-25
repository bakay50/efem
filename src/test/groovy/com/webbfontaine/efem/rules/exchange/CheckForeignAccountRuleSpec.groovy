package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.ExchangeRequestType.getEA


class CheckForeignAccountRuleSpec extends Specification {

    @Unroll
    def "test CheckForeignAccountRule"(){
        given:
        Exchange exchange = new Exchange(requestType:EA, currencyPayCode:currencyPayCode, countryProvenanceDestinationCode:countryProvenanceDestinationCode, attachedDocs: docs)

        when:
        Rule rule = new CheckForeignAccountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        docs                                                                 | currencyPayCode  | countryProvenanceDestinationCode      | expected
        [new AttachedDoc(docType: "6059"), new AttachedDoc(docType: "0008")] | "USD"            | "CI"                                  | false
        [new AttachedDoc(docType: "6059")]                                   | "USD"            | "CI"                                  | true
        [new AttachedDoc(docType: "0007")]                                   | "USD"            | "CI"                                  | true
        []                                                                   | "USD"            | "FR"                                  | false
    }

}