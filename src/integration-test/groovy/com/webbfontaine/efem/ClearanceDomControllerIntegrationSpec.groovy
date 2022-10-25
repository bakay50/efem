package com.webbfontaine.efem

import com.webbfontaine.currencyTransfer.CurrencyTransferService
import com.webbfontaine.efem.currencyTransfer.ClearanceDomController
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired

@Integration
@Rollback
class ClearanceDomControllerIntegrationSpec extends InitIntegrationSpec {

    @Autowired
    ClearanceDomController clearanceDomController
    CurrencyTransferService currencyTransferService

    @Override
    String getControllerName() {
        return "ClearanceDomController"
    }

    def setup() {
        clearanceDomController.params.conversationId = currencyTransferService.addToSessionStore(new CurrencyTransfer(clearanceDomiciliations: [
                new ClearanceDomiciliation(rank: 1, ecReference: "EC987")
        ]))
    }

    void "addClearanceDom()"() {
        given:
        clearanceDomController.params.rank = 1
        clearanceDomController.params.ecReference = "EC9512"
        clearanceDomController.params.ecDate = new LocalDate()
        clearanceDomController.params.domiciliationCodeBank = 2364
        clearanceDomController.params.domiciliationNo = 96547
        clearanceDomController.params.domiciliationDate = new LocalDate()
        clearanceDomController.params.domiciliatedAmounttInCurr = 8745225
        clearanceDomController.params.invoiceFinalAmountInCurr = 4524888
        clearanceDomController.params.amountTransferredInCurr = 452136
        when:
        clearanceDomController.addClearanceDom()
        def responseJson = clearanceDomController.response.json
        then:
        clearanceDomController.response.text.contains('"template":')
        clearanceDomController.response.text.contains('"clearanceOfDom":')
        clearanceDomController.response.text.contains('"currencyTransferInstance":')
        responseJson.clearanceOfDom.ecReference == "EC9512"
        responseJson.clearanceOfDom.amountTransferredInCurr == 452136
        assert clearanceDomController.response.status == 200
    }

    void "editClearanceDom()"() {
        given:
        clearanceDomController.params.rank = 1
        clearanceDomController.params.ecReference = "EC9512"
        clearanceDomController.params.ecDate = new LocalDate()
        clearanceDomController.params.domiciliationCodeBank = 2364
        clearanceDomController.params.domiciliationNo = 96547
        clearanceDomController.params.domiciliationDate = new LocalDate()
        clearanceDomController.params.domiciliatedAmounttInCurr = 8745225
        clearanceDomController.params.invoiceFinalAmountInCurr = 4524888
        clearanceDomController.params.amountTransferredInCurr = 85214
        when:
        clearanceDomController.editClearanceDom()
        def responseJson = clearanceDomController.response.json
        then:
        clearanceDomController.response.text.contains('"template":')
        clearanceDomController.response.text.contains('"clearanceOfDom":')
        clearanceDomController.response.text.contains('"currencyTransferInstance":')
        responseJson.clearanceOfDom.ecReference == "EC9512"
        responseJson.clearanceOfDom.amountTransferredInCurr == 85214
        assert clearanceDomController.response.status == 200
    }

    void "deleteClearanceDom()"() {
        given:
        clearanceDomController.params.rank = 3
        when:
        clearanceDomController.deleteClearanceDom()
        then:
        clearanceDomController.response.text.contains('"template":')
        clearanceDomController.response.text.contains('"currencyTransferInstance":')
        assert clearanceDomController.response.status == 200
    }

    void "cancelEditClearanceDom()" () {
        given:
        clearanceDomController.params.rank = 5
        when:
        clearanceDomController.cancelEditClearanceDom()
        def responseJson = clearanceDomController.response.json
        then:
        clearanceDomController.response.text.contains('"template":')
        clearanceDomController.response.text.contains('"currencyTransferInstance":')
        responseJson.currencyTransferInstance.clearanceDomiciliations.size() == 1
        assert clearanceDomController.response.status == 200
    }
}
