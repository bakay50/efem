package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.repatriation.RepatriationService
import com.webbfontaine.repatriation.constants.NatureOfFund
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import static com.webbfontaine.efem.constant.MailConstants.REP_UPDATE

@Slf4j("LOGGER")
class UpdateConfirmOperationHandlerService extends ClientRepOperationHandlerService {
    RepatriationService repatriationService
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            LOGGER.debug("in after persist of ${UpdateConfirmOperationHandlerService.class}")
            LOGGER.debug("domain instance -> {}", domainInstance)
            repatriationService.updateEcBalanceAndStatus(domainInstance, commitOperation)
            repatriationService.logConcernedEC(domainInstance)
            messagingNotifService.sendAfterCommit(result, REP_UPDATE)
            repatriationService.logConcernedEcDeleted(result, domainInstance?.ecRefToBeDeleted)
            repatriationService.updateEcDeleted(result,domainInstance?.ecChanged)
            repatriationService.updateEcAmountUpdated(result,domainInstance?.ecChanged)
        }
    }
}
