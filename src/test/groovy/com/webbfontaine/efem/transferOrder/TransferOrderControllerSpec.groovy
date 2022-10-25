package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.ReferenceService
import com.webbfontaine.efem.TransferBusinessLogicService
import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.transferOrder.TransferOrderService
import com.webbfontaine.transferOrder.search.TransferOrderSearchService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrderControllerSpec extends Specification implements ControllerUnitTest<TransferOrderController>, DataTest {
    def setup() {
        mockDomains(TransferOrder)
    }

    def "test verify() functionality"() {
        given:
        controller.transferOrderService = [findFromSessionStore: { a -> new TransferOrder() } /*, getExchangeList : { a -> [new TransferOrder()]}*/]
        controller.docVerificationService = [deepVerify: { transfer -> true }, documentHasErrors: { transfer -> false }]
        controller.params.conversationId = 'test'

        when:
        controller.verify()

        then:
        controller.modelAndView.model.hasDocErrors == false
        controller.modelAndView.viewName == "/transferOrder/edit"
    }

    def "test delete() functionality"() {
        given:
        controller.transferOrderService = [findFromSessionStore: { a -> new TransferOrder() }, getExchangeList: { a -> [new TransferOrder()] }]
        controller.docVerificationService = [deepVerify: { transfer -> true }, documentHasErrors: { transfer -> false }]
        controller.params.conversationId = 'test'

        when:
        controller.delete()

        then:
        flash.message == "default.operation.done.message"
    }

    void "test search action"() {
        given:
        def transferOrderService = Stub(TransferOrderService)
        def mockSearchService = Stub(TransferOrderSearchService) {
            getSearchResults(*_) >> {
                return [:]
            }
        }
        def mockReferenceService = Mock(ReferenceService)
        controller.transferOrderService = transferOrderService
        controller.transferOrderSearchService = mockSearchService
        controller.referenceService = mockReferenceService
        TransferOrderSearchCommand cmd = new TransferOrderSearchCommand()

        when:
        controller.search(cmd)

        then:
        view == '/transferOrder/list'

        when:
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        views['/grails-app/views/utils/search/_searchResults.gsp'] = '/utils/search/searchResults'
        controller.search(cmd)

        then:
        response.text == '/utils/search/searchResults'
    }

    void "test when list action is called, list view should be rendered"() {
        given:
        TransferOrderSearchCommand cmd = new TransferOrderSearchCommand()

        when:
        controller.list(cmd)

        then:
        view == '/transferOrder/list'
    }

    @Unroll("test show action #testCase")
    void "test show action"() {
        given:
        def transferOrderService = Stub(TransferOrderService)
        def mockBusinessLogicService = Mock(TransferBusinessLogicService)
        controller.transferBusinessLogicService = mockBusinessLogicService
        def mockReferenceService = Mock(ReferenceService)
        controller.transferOrderService = transferOrderService
        controller.referenceService = mockReferenceService
        flash.transferInstanceToShow = instance

        when:
        controller.show()

        then:
        view == expectedView

        and:
        response.redirectUrl == redirectedUrl

        where:
        testCase                                                                                                                       | instance            | access | expectedView              | redirectedUrl
        'should render /transferOrder/edit view when instance of transferOrder is found and no error message'                          | new TransferOrder() | true   | '/transferOrder/show.gsp' | null
        'should redirect to /transferOrder/list view when instance of transferOrder is not found and there should be an error message' | null                | true   | '/transferOrder/show.gsp' | '/transferOrder/index'
        'should redirect to /transferOrder/list view when instance of transferOrder found but user has no access'                      | new TransferOrder() | false  | '/transferOrder/show.gsp' | null
    }

}
