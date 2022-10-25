package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.Repatriation
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
class StoreRepOperationHandlerService extends ClientRepOperationHandlerService {

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def beforePersist(Repatriation repatriation, def commitOperation) {
        LOGGER.debug("in beforePersist() of ${StoreRepOperationHandlerService}")
        repatriation?.lastTransactionDate = LocalDateTime.now()

        if (UserUtils.isBankAgent()) {
            repatriation.storedOwner = RepatriationConstants.BANK
        } else {
            repatriation.storedOwner = RepatriationConstants.TRADER
        }
        LOGGER.debug("Value of storedOwner through Store Operation: ${repatriation?.storedOwner}")
    }

}
