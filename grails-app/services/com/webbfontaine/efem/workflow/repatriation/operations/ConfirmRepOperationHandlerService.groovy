package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.constant.MailConstants.REP_CONFIRM

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class ConfirmRepOperationHandlerService extends ClientRepOperationHandlerService {
    def notificationService
    def repatriationService
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def beforePersist(Repatriation repatriation, def commitOperation){
        LOGGER.debug("in beforePersist() of ${ConfirmRepOperationHandlerService}")

        LOGGER.debug("Value of Request Sequence Number through Confirm Operation: ${repatriation?.requestNumberSequence}")
    }

    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {

        if (!hasErrors) {
            repatriationService.updateEcBalanceAndStatus(domainInstance, commitOperation)
            repatriationService.logConcernedEC(domainInstance)
            repatriationService.logConcernedEcDeleted(result, domainInstance?.ecRefToBeDeleted)
            repatriationService.updateEcDeleted(result,domainInstance?.ecChanged)
            repatriationService.updateEcAmountUpdated(result,domainInstance?.ecChanged)
            messagingNotifService.sendAfterCommit(result, REP_CONFIRM)
        }

    }


}
