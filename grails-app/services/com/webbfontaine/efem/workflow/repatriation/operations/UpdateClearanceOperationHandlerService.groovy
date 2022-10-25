package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.MessagingNotifService
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constant.MailConstants.REP_UPDATE

@Slf4j("LOGGER")
class UpdateClearanceOperationHandlerService extends ClientRepOperationHandlerService {

    def repatriationService
    MessagingNotifService messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }
    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            repatriationService.updateEcBalanceAndStatus(domainInstance, commitOperation)
            repatriationService.logConcernedEC(domainInstance)
            repatriationService.setCurrencyFromRepatriation(result)

            repatriationService.logConcernedEcDeleted(result, domainInstance?.ecRefToBeDeleted)
            repatriationService.updateEcDeleted(result,domainInstance?.ecChanged)
            repatriationService.updateEcAmountUpdated(result,domainInstance?.ecChanged)
            messagingNotifService.sendAfterCommit(result, REP_UPDATE)
        }
    }
}
