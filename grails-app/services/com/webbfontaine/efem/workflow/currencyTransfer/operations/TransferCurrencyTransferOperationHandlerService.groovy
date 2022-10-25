package com.webbfontaine.efem.workflow.currencyTransfer.operations

import com.webbfontaine.efem.constants.CurrencyTransferRequestType
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class TransferCurrencyTransferOperationHandlerService  extends CurrencyTransferOperationHandlerService {
    def currencyTransferService
    def sequenceGenerator

    @Override
    BpmService getBpmService() {
        return currencyTransferWorkflowService
    }

    @Override
    def beforePersist(CurrencyTransfer currencyTransfer, def commitOperation){
        LOGGER.debug("in beforePersist() of ${TransferCurrencyTransferOperationHandlerService}")
        currencyTransfer?.lastTransactionDate = LocalDateTime.now()
        currencyTransfer?.attachedDocs.each {
            if (it.docType == CurrencyTransferRequestType.DOC_SWIFT_MESSAGE){
            currencyTransfer?.currencyTransferDate = it.docDate
            }
        }
        currencyTransfer?.requestDate = new LocalDate()
        def year = currencyTransfer?.requestDate?.getYear()
        currencyTransfer?.requestNumberSequence = (Integer) (sequenceGenerator ? sequenceGenerator.currencyNextRequestNumber(year as String) : 1)
        currencyTransfer?.requestYear = year
        currencyTransfer?.requestNo = currencyTransferService.generateRequestNumber(currencyTransfer)
        LOGGER.debug("Value of Request Sequence Number through Store Operation: ${currencyTransfer?.requestNumberSequence}")
    }

    @Override
    def afterPersist(CurrencyTransfer currencyTransfer, CurrencyTransfer result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            currencyTransferService.updateRepatriation(result, commitOperation)
        }
    }

}
