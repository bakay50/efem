package com.webbfontaine.efem.workflow.currencyTransfer.operations

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class UpdateCurrencyTransferOperationHandlerService extends CurrencyTransferOperationHandlerService {
    def currencyTransferService

    @Override
    BpmService getBpmService() {
        return currencyTransferWorkflowService
    }

    @Override
    def beforePersist(CurrencyTransfer currencyTransfer, def commitOperation){
        LOGGER.debug("in beforePersist() of ${UpdateCurrencyTransferOperationHandlerService}")
        currencyTransfer?.lastTransactionDate = LocalDateTime.now()
    }

    @Override
    def afterPersist(CurrencyTransfer currencyTransfer, CurrencyTransfer result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            currencyTransferService.updateRepatClearance(result, commitOperation)
        }
    }

}
