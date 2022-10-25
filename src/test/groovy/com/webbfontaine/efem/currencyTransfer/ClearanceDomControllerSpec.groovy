package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.currencyTransfer.ClearanceDomiciliationService
import com.webbfontaine.currencyTransfer.CurrencyTransferService
import com.webbfontaine.efem.Exchange
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification

class ClearanceDomControllerSpec extends Specification implements ControllerUnitTest<ClearanceDomController>, DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
        mockDomain(ClearanceDomiciliation)
        mockDomain(Exchange)
    }

    def "test addClearanceDom method()"() {
        given:
        def data = [ecReference            : "ec456", ecDate: new LocalDate(), domiciliationDate: new LocalDate(),
                    domiciliationNo        : "do9654", domiciliatedAmounttInCurr: 6542, invoiceFinalAmountInCurr: 74521,
                    amountTransferredInCurr: 45213
        ]
        CurrencyTransfer currencyInstance = new CurrencyTransfer()
        controller.currencyTransferService = Stub(CurrencyTransferService) {
            findFromSessionStore(_) >> currencyInstance
            doCheckAddClearance(_, _) >> new ClearanceDomiciliation(data)
        }
        controller.clearanceDomiciliationService = Stub(ClearanceDomiciliationService) {
            updateParamsFields(_) >> null
        }
        when:
        controller.addClearanceDom()
        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.text.contains('"clearanceOfDom":')
        controller.response.text.contains('"currencyTransferInstance":')
        controller.response.text.contains('"template":')
    }

    def "test editClearanceDom()"() {
        given:
        def data = [ecReference            : "ec456", ecDate: new LocalDate(), domiciliationDate: new LocalDate(),
                    domiciliationNo        : "do9654", domiciliatedAmounttInCurr: 6542, invoiceFinalAmountInCurr: 74521,
                    amountTransferredInCurr: 96547
        ]
        CurrencyTransfer currencyInstance = new CurrencyTransfer()
        controller.currencyTransferService = Stub(CurrencyTransferService) {
            findFromSessionStore(_) >> currencyInstance
        }
        controller.clearanceDomiciliationService = Stub(ClearanceDomiciliationService) {
            editClearanceDom(_) >> new ClearanceDomiciliation(data)
        }
        when:
        request.method = 'POST'
        controller.editClearanceDom()
        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.text.contains('"clearanceOfDom":')
        controller.response.text.contains('"currencyTransferInstance":')
        controller.response.text.contains('"template":')
    }

    def "test deleteClearanceDom()"() {
        given:
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer()
        controller.currencyTransferService = Stub(CurrencyTransferService) {
            findFromSessionStore(_) >> currencyTransferInstance
        }
        controller.clearanceDomiciliationService = Stub(ClearanceDomiciliationService) {
            deleteClearanceDom(_) >> ClearanceDomiciliation.newInstance()
        }
        when:
        controller.deleteClearanceDom()
        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.text.contains('"currencyTransferInstance":')
        controller.response.text.contains('"template":')
    }

    def "test deleteClearance()"() {
        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        def clearanceToDelete = []
        when:
        def result = controller.deleteClearance(clearanceToDelete, currencyTransfer)
        then:
        result == []
    }

    def "test cancelEditClearanceDom()"() {
        given:
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer()
        controller.currencyTransferService = Stub(CurrencyTransferService) {
            findFromSessionStore(_) >> currencyTransferInstance
        }
        when:
        request.method = 'POST'
        controller.cancelEditClearanceDom()
        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.text.contains('"currencyTransferInstance":')
        controller.response.text.contains('"template":')
    }

}
