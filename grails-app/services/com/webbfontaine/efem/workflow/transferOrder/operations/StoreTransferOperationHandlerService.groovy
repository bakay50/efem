package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.BpmService
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
@Slf4j("LOGGER")
class StoreTransferOperationHandlerService extends ClientTransferOperationHandlerService {

    @Override
    BpmService getBpmService() {
        return transferOrderWorkflowService
    }

    @Override
    def beforePersist(TransferOrder transfer, def commitOperation) {
        LOGGER.debug("in beforePersist() of ${StoreTransferOperationHandlerService}")
        transfer?.lastTransactionDate = LocalDateTime.now()

        LOGGER.debug("Value of Request Sequence Number through Store Operation: ${transfer?.requestNumberSequence}")
    }

}
