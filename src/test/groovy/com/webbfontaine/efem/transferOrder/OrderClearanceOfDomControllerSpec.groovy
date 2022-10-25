package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.transferOrder.OrderClearanceDomService
import com.webbfontaine.transferOrder.TransferOrderService
import com.webbfontaine.transferOrder.TransferOrderValidationService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 12/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@ConfineMetaClassChanges([UserUtils])
class OrderClearanceOfDomControllerSpec extends Specification implements ControllerUnitTest<OrderClearanceOfDomController>, DataTest {
    def setup() {
        mockDomain(TransferOrder)
        mockDomain(OrderClearanceOfDom)
    }

    def "test addClearanceDom() functionality"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        TransferOrder instance = new TransferOrder()
        controller.orderClearanceDomService = [updateParamsFieldsClearance: { params -> false }]
        controller.transferOrderService = Stub(TransferOrderService) {
            doAddClearanceChecking(*_) >> new OrderClearanceOfDom()
            findFromSessionStore(_) >> instance
        }
        controller.transferOrderValidationService = Stub(TransferOrderValidationService) {
            doAmountCheckingOnDoc(_) >> null
        }
        TransferOrderValidationService
        controller.params.conversationId = 'test'

        when:
        controller.addClearanceDom()

        then:
        response.getContentAsString().contains("clearanceOfDom")
        response.getContentAsString().contains("template")
        response.getContentType() == "application/json;charset=UTF-8"
    }


    def "test cancelEditClearanceDom() functionality"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        TransferOrder instance = new TransferOrder(requestNo: "E0002020")

        instance.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA0001", bankCode: "sgb"))
        instance.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 2, eaReference: "EA0002", bankCode: "sgb"))

        controller.transferOrderService = Stub(TransferOrderService) {
            findFromSessionStore(_) >> instance
        }
        when:
        controller.cancelEditClearanceDom()

        then:
        response.getContentType() == "application/json;charset=UTF-8"
        response.getContentAsString().contains("clearenceRow_1")
        response.getContentAsString().contains("clearenceRow_2")
    }

    def "test editClearanceDom() functionality"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        TransferOrder instance = new TransferOrder(requestNo: "E0002020")
        instance.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA0001", bankCode: "SIB",
                registrationNoBank: "100", amountRequestedMentionedCurrency: 1))
        instance.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 2, eaReference: "EA0002", bankCode: "SIB",
                registrationNoBank: "99", amountRequestedMentionedCurrency: 2))
        controller.docVerificationService = [deepVerify: { transferOrder -> true }, removeAllErrors: { transferOrder -> true }]
        controller.transferOrderService = Stub(TransferOrderService) {
            findFromSessionStore(_) >> instance
        }
        controller.transferOrderValidationService = Stub(TransferOrderValidationService) {
            doAmountCheckingOnDoc(_) >> null
        }
        controller.orderClearanceDomService = new OrderClearanceDomService()
        setRequestParams()
        when:
        controller.editClearanceDom()

        then:
        instance.orderClearanceOfDoms.get(0).registrationNoBank == "101"
        instance.orderClearanceOfDoms.get(0).bankCode == "BICICI"
        instance.orderClearanceOfDoms.get(0).amountRequestedMentionedCurrency == 1200

    }

    private void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.rank = 1
        webRequest.params.eaReference = "EA0001"
        webRequest.params.registrationNoBank = "101"
        webRequest.params.bankCode = "BICICI"
        webRequest.params.amountRequestedMentionedCurrency = 1200
        RequestContextHolder.setRequestAttributes(webRequest)
    }

    def "test retrieveExchangeIdFromClearance"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.eaReference = "EA01"
        RequestContextHolder.setRequestAttributes(webRequest)
        controller.exchangeService = [findExchangeByRequestTypeAndRequestNo: { a,b -> new Exchange(requestNo: "EA01") }]

        when:
        controller.retrieveExchangeIdFromClearance()

        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.contentAsString.contains("eaReference_id")

    }

}
