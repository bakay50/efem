package com.webbfontaine.efem.workflow.currencyTransfer.operations

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.grails.web.util.WebUtils

@Slf4j("LOGGER")
class CancelCurrencyTransferOperationHandlerService extends CurrencyTransferOperationHandlerService {
    def currencyTransferService

    @Override
    BpmService getBpmService() {
        return currencyTransferWorkflowService
    }

    @Override
    def afterPersist(CurrencyTransfer currencyTransfer, CurrencyTransfer result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            currencyTransferService.updateRepatriation(result, commitOperation)
        }
        def queryMessage = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("comments")
        if (queryMessage) {
            loggerService.addMessage(result, queryMessage as String)
        }
    }

}
