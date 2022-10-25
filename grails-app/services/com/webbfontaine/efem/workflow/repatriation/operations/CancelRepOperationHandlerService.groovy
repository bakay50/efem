package com.webbfontaine.efem.workflow.repatriation.operations

import static com.webbfontaine.efem.constant.MailConstants.REP_CANCEL
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import grails.gorm.transactions.Transactional
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import static com.webbfontaine.efem.constants.Statuses.ST_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_CONFIRMED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
@Transactional
class CancelRepOperationHandlerService extends ClientRepOperationHandlerService{

    def notificationService
    def repatriationService
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def beforePersist(Repatriation domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${CancelRepOperationHandlerService}")
        def currentStatus = domainInstance.status
        def commitOperationName
        if(currentStatus == ST_QUERIED) {
            commitOperation = CANCEL_QUERIED
            commitOperationName = CANCEL_QUERIED.name()
        } else {
            commitOperation = CANCEL_CONFIRMED
            commitOperationName = CANCEL_CONFIRMED.name()
        }

        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        WebRequestUtils.params.setProperty('initialStatus', currentStatus)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
    }

    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            if(commitOperation in [CANCEL_QUERIED, CANCEL_CONFIRMED]){
                repatriationService.handleSetCancelRepatriationClearanceOfDom(result)
                messagingNotifService.sendAfterCommit(result, REP_CANCEL)
            }
            def queryMessage = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("comments")
            if (queryMessage) {
                loggerService.addMessage(result, queryMessage as String)
            }
        }
    }
}
