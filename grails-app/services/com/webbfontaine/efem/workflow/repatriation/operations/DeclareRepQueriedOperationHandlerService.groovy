package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.constant.MailConstants.REP_DECLARE
import static com.webbfontaine.efem.workflow.Operation.DECLARE_QUERIED

@Slf4j("LOGGER")
class DeclareRepQueriedOperationHandlerService extends ClientRepOperationHandlerService{

    def repatriationService
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def beforePersist(Repatriation repatriation, def commitOperation){
        def commitOperationName
        LOGGER.debug("in beforePersist() of ${DeclareRepQueriedOperationHandlerService}")
        repatriation?.lastTransactionDate = LocalDateTime.now()
        def params = GrailsWebRequest.lookup().params

        commitOperation = DECLARE_QUERIED
        commitOperationName = DECLARE_QUERIED.humanName()
        repatriation.storedOwner = RepatriationConstants.TRADER

        repatriation.bankNotificationDate = new LocalDate()

        LOGGER.debug("Value of Request Sequence Number through Store Operation: ${repatriation?.requestNumberSequence}")

        params.put('commitOperation', params?.commitOperation ?: commitOperation)
        params.put('commitOperationName', params?.commitOperation ?: commitOperationName)

    }

    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {

        if (!hasErrors) {
            repatriationService.updateEcBalanceAndStatus(domainInstance, commitOperation)
            repatriationService.logConcernedEC(domainInstance)
            repatriationService.logConcernedEcDeleted(result, domainInstance?.ecRefToBeDeleted)
            repatriationService.updateEcDeleted(result,domainInstance?.ecChanged)
            repatriationService.updateEcAmountUpdated(result,domainInstance?.ecChanged)
            messagingNotifService.sendAfterCommit(result, REP_DECLARE)
        }

    }
}
