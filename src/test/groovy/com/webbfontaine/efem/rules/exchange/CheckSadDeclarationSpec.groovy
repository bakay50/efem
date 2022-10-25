package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.TvfConstants.*
import static com.webbfontaine.efem.constants.ExchangeRequestType.*


class CheckSadDeclarationSpec extends Specification {

    @Unroll
    def "#testCase hasErrors == #expected"() {
        given:
        Exchange exchange = new Exchange()
        exchange.requestType = reqType
        exchange.sadTypeOfDeclaration = decType

        when:
        Rule rule = new CheckSadDeclaration()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        testCase                                             | expected | reqType | decType
        "Sad is correct"                                     | false    | EA      | IM_FLOW
        "User changed SAD from IM to EX when Exchange is EA" | true     | EA      | EX_FLOW
        "Sad is EX and Exchange is EC"                       | false    | EC      | EX_FLOW
        "Sad is IM and Exchange is EC"                       | false    | EC      | IM_FLOW
    }

}