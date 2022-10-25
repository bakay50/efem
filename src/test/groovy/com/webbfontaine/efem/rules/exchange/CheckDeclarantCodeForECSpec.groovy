package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckDeclarantCodeForECRuleSpec extends Specification implements DataTest{
    def setup() {
        mockDomain(Exchange)
    }

    @Unroll
    void "test CheckDeclarantCodeForEC()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> declarantUserConnect
        UserUtils.isTrader() >> traderUserConnect
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> depProp
        Exchange exchange = new Exchange(declarantCode : "DEC2", requestType : requestType, status: status)
        exchange.startedOperation = startedOperation

        when:
        Rule rule = new CheckDeclarantCodeForECRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        declarantUserConnect | traderUserConnect | requestType            | status             | depProp                 |  startedOperation          | expected
        true                 | false             |ExchangeRequestType.EC  | Statuses.ST_STORED | ["DEC1"]                |  Operation.CREATE          | false
        true                 | false             |ExchangeRequestType.EC  | null               | ["ALL"]                 |  Operation.REQUEST_QUERIED | false
        true                 | false             |ExchangeRequestType.EC  | null               | ["DEC1", "DEC2", "DEC3"]|  Operation.CREATE          | false
        true                 | false             |ExchangeRequestType.EC  | null               | ["DEC1", "DEC3", "DEC4"]|  Operation.CREATE          | true
        false                | true              |ExchangeRequestType.EC  | null               | ["ALL"]                 |  Operation.CREATE          | false
    }
}
