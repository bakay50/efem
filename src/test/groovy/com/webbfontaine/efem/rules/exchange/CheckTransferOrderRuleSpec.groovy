package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.ExchangeRequestType.getEA


class CheckTransferOrderRuleSpec extends Specification implements DataTest{

    def setup() {
        mockDomains(Exchange)
        mockDomains(TransferOrder)
    }

    @Unroll
    def "test CheckTransferOrderRule"(){
        given:
        Exchange exchange = new Exchange(requestType:EA, status: Statuses.ST_APPROVED, requestNo: "EA001").save(flush: true, deepValidate: false, validate: false)
        TransferOrder transfer = new TransferOrder(requestNo: "TR001", status: transferStatus, orderClearanceOfDoms: orders).save(flush: true, deepValidate: false, validate: false)
        when:
        Rule rule = new CheckTransferOrderRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        orders                                          | transferStatus        | expected
        [new OrderClearanceOfDom(eaReference: "EA001")] | Statuses.ST_REQUESTED | true
        [new OrderClearanceOfDom(eaReference: "EA001")] | Statuses.ST_CANCELLED | false
        [new OrderClearanceOfDom(eaReference: "EA001")] | Statuses.ST_QUERIED   | true
        [new OrderClearanceOfDom(eaReference: "EA001")] | Statuses.ST_VALIDATED | true
        [new OrderClearanceOfDom(eaReference: "EA001")] | Statuses.ST_STORED    | false
    }

}