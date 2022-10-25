package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.rules.transferOrder.checking.CheckTransferApproveRule
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.constants.ExchangeRequestType.getEA


class CheckTransferApproveRuleSpec extends Specification implements DataTest{

    def setup() {
        mockDomains(Exchange)
        mockDomains(TransferOrder)
    }

    @Unroll
    def "test CheckTransferApproveRule"(){
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isSuperAdministrator() >> false
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([commitOperation: op], null)
        Exchange exchange = new Exchange(requestType:EA, status: eaStatus, requestNo: "EA001").save(flush: true, deepValidate: false, validate: false)
        TransferOrder transfer = new TransferOrder(requestNo: "TR001", status: transferStatus, orderClearanceOfDoms: new OrderClearanceOfDom(eaReference: "EA001")).save(flush: true, deepValidate: false, validate: false)
        when:
        Rule rule = new CheckTransferApproveRule()
        rule.apply(new RuleContext(transfer, transfer.errors as Errors))

        then:
        transfer.hasErrors() == expected

        where:
        op       | eaStatus              | transferStatus        | expected
        REQUEST  | Statuses.ST_APPROVED  | Statuses.ST_REQUESTED | false
        VALIDATE | Statuses.ST_APPROVED  | Statuses.ST_REQUESTED | false
        VALIDATE | Statuses.ST_REQUESTED | Statuses.ST_REQUESTED | true
    }

}