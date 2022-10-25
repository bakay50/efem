package com.webbfontaine.transferOrder


import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

@ConfineMetaClassChanges([UserUtils])
class OrderClearanceDomServiceSpec extends Specification implements ServiceUnitTest<OrderClearanceDomService>, DataTest {

    def setupSpec() {
        mockDomain(TransferOrder)
        mockDomain(OrderClearanceOfDom)
    }

    def "test editClearanceDoms"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123'
        )
        transfer.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA00001", bankCode: "SGBCI"))

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.rank = 1
        webRequest.params.eaReference = "EA00001"

        webRequest.params.bankCode = "SIB"

        RequestContextHolder.setRequestAttributes(webRequest)

        when:
        def result = service.editClearanceDoms(transfer)

        then:
        result.bankCode == "SIB"
    }


    def "test deleteClearanceDoms by Trader"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123'
        )
        transfer.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA00001", bankCode: "SGBCI",state:'0'))

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.rank = 1
        webRequest.params.eaReference = "EA00001"

        RequestContextHolder.setRequestAttributes(webRequest)

        when:
        service.deleteClearanceDoms(transfer)

        then:
        transfer.orderClearanceOfDoms.size() == 0

    }

    def "test deleteClearanceDoms by SuperAdmin"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isSuperAdministrator() >> true
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '2'
                ,requestYear: '2020'
                ,requestNo: 'C000123',
                orderClearanceOfDoms : [
                        new OrderClearanceOfDom(rank: 1, eaReference: "EA00002", bankCode: "SGBCI",state: '0',authorizationDate:LocalDate.now(),registrationNoBank:'R001',amountToBeSettledMentionedCurrency:1500,amountRequestedMentionedCurrency:1000,amountSettledMentionedCurrency:500),
                        new OrderClearanceOfDom(rank: 2, eaReference: "EA00002", bankCode: "SGBCI",state: '0',authorizationDate:LocalDate.now(),registrationNoBank:'R002',amountToBeSettledMentionedCurrency:100,amountRequestedMentionedCurrency:500,amountSettledMentionedCurrency:700),
                        new OrderClearanceOfDom(rank: 3, eaReference: "EA00002", bankCode: "SGBCI",state: '0',authorizationDate:LocalDate.now(),registrationNoBank:'R003',amountToBeSettledMentionedCurrency:2500,amountRequestedMentionedCurrency:1500,amountSettledMentionedCurrency:800)
                ]
        )

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.rank = 1
        webRequest.params.eaReference = "EA00002"

        RequestContextHolder.setRequestAttributes(webRequest)

        when:
        service.deleteClearanceDoms(transfer)

        then:
        transfer.orderClearanceOfDoms.findAll {it.state == '1'}.size() == 1
        transfer.orderClearanceOfDoms.findAll {it.state == '0'}.size() == 2

    }
}
