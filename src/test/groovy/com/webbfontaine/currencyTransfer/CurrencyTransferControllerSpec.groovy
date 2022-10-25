package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.CurrencyTransferBusinessLogicService
import com.webbfontaine.efem.ReferenceService
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import com.webbfontaine.efem.currencyTransfer.CurrencyTransferController
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.concurrent.SynchronizationService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.locks.Lock

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 05/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferControllerSpec extends Specification implements ControllerUnitTest<CurrencyTransferController>, DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
        mockDomain(Exchange)
    }

    def "test create() functionality"() {
        given:
        controller.currencyTransferService = [addToSessionStore: { a -> new CurrencyTransfer() }]
        def mockBusinessLogicService = Mock(CurrencyTransferBusinessLogicService)
        controller.currencyTransferBusinessLogicService = mockBusinessLogicService
        def mockReferenceService = Mock(ReferenceService)
        controller.referenceService = mockReferenceService

        when:
        controller.create()

        then:
        controller.modelAndView.model.hasDocErrors == false
        controller.modelAndView.viewName == "/currencyTransfer/edit"
    }

    def "test retrieveExchangeReference() method"() {
        given:
        Exchange exchange = new Exchange(requestDate: new LocalDate(), ecReference: "ec45",
                bankCode: "SGBCI", bankName: "SGBCI", registrationNumberBank: "000123", registrationDateBank: new LocalDate(), status: "Executed",
                dateOfBoarding: new LocalDate(), currencyCode: "EUR", geoArea: "003", amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("5000.00"), finalAmountInDevise: new BigDecimal("5000.00"),
                balanceAs: new BigDecimal("5000.00")).save()
        def currencyService = Stub(CurrencyTransferService) {
            retrieveExchangeData(*_) >> {
                return exchange
            }
        }
        controller.currencyTransferService = currencyService
        controller.params.conversationId = 'test'
        when:
        controller.retrieveExchangeReference()
        then:
        response.getContentType() == "application/json;charset=UTF-8"
        response.status == 200
    }

    @Unroll("test show action #testCase")
    void "test show action"() {
        given:
        def currencyTransferService = Stub(CurrencyTransferService)
        def mockBusinessLogicService = Mock(CurrencyTransferBusinessLogicService)
        controller.currencyTransferBusinessLogicService = mockBusinessLogicService
        def mockReferenceService = Mock(ReferenceService)
        controller.currencyTransferService = currencyTransferService
        controller.referenceService = mockReferenceService
        flash.currencyTransferInstanceToShow = instance

        when:
        controller.show()

        then:
        view == expectedView

        and:
        response.redirectUrl == redirectedUrl

        where:
        testCase                                                                                                                             | instance               | access | expectedView                 | redirectedUrl
        'should render /currencyTransfer/edit view when instance of currencyTransfer is found and no error message'                          | new CurrencyTransfer() | true   | '/currencyTransfer/show.gsp' | null
        'should redirect to /currencyTransfer/list view when instance of currencyTransfer is not found and there should be an error message' | null                   | true   | '/currencyTransfer/show.gsp' | '/currencyTransfer/index'
        'should redirect to /currencyTransfer/list view when instance of currencyTransfer found but user has no access'                      | new CurrencyTransfer() | false  | '/currencyTransfer/show.gsp' | null
    }

    def "test delete() functionality"() {
        given:
        controller.currencyTransferService = [findFromSessionStore: { a -> new CurrencyTransfer() }]

        when:
        controller.delete()

        then:
        flash.message == "default.operation.done.message"
    }

    def "test edit() functionality"() {
        given:
        controller.currencyTransferService = [findFromSessionStore: { a -> new CurrencyTransfer(id: 999, requestNo: 'CT00123') }, addToSessionStore: { a -> new CurrencyTransfer(id: 999, requestNo: 'CT00123') }]
        def mockBusinessLogicService = Mock(CurrencyTransferBusinessLogicService)
        controller.currencyTransferBusinessLogicService = mockBusinessLogicService

        when:
        controller.edit()

        then:
        controller.modelAndView.viewName == "/currencyTransfer/edit"

    }

    def "test update() functionality"() {

        given:
        controller.docVerificationService = [deepVerify: { currencyTransfer -> true }]
        controller.currencyTransferService = [findFromSessionStore: { a -> new CurrencyTransfer(requestNo: 'CT00123') }, doPersist: { a, b -> new CurrencyTransfer(id: 999, requestNo: 'CT00123') }]
        controller.synchronizationService = Stub(SynchronizationService)

        when:
        controller.update()

        then:
        flash.currencyTransferInstanceToShow.requestNo == 'CT00123'

    }

    def "test save() functionality"() {

        given:
        controller.params.commitOperation = Operation.STORE
        controller.docVerificationService = [deepVerify: { currencyTransfer -> true }, removeAllErrors: { currencyTransfer -> true }]
        controller.currencyTransferService = [findFromSessionStore: { a -> new CurrencyTransfer(requestNo: 'CT00123') }, doPersist: { a, b -> new CurrencyTransfer(id: 999, requestNo: 'CT00123') }]
        controller.synchronizationService = Stub(SynchronizationService)

        when:
        controller.save()

        then:
        flash.currencyTransferInstanceToShow.requestNo == 'CT00123'

    }

    void "test search action"() {
        given:
        def currencyTransferService = Stub(CurrencyTransferService)
        def mockSearchService = Stub(CurrencyTransferSearchService) {
            getSearchResults(*_) >> {
                return [:]
            }
        }
        def mockReferenceService = Mock(ReferenceService)
        controller.currencyTransferService = currencyTransferService
        controller.currencyTransferSearchService = mockSearchService
        controller.referenceService = mockReferenceService
        CurrencyTransferSearchCommand cmd = new CurrencyTransferSearchCommand()

        when:
        controller.search(cmd)

        then:
        view == '/currencyTransfer/list'

    }

    void "test when list action is called, list view should be rendered"() {
        given:
        CurrencyTransferSearchCommand cmd = new CurrencyTransferSearchCommand()

        when:
        controller.list(cmd)

        then:
        view == '/currencyTransfer/list'
    }

}
