package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.grails.web.util.WebUtils
import static com.webbfontaine.efem.constant.MailConstants.REP_QUERY

@Slf4j("LOGGER")
class QueryRepOperationHandlerService extends ClientRepOperationHandlerService{

    def notificationService
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
            def queryMessage = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("comments")
            if (queryMessage) {
                loggerService.addMessage(result, queryMessage as String)
            }
            messagingNotifService.sendAfterCommit(result, REP_QUERY)
        }

    }
}
