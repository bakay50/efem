package com.webbfontaine.efem.workflow.transferOrder.operations

import static com.webbfontaine.efem.constant.MailConstants.TR_QUERY
import com.webbfontaine.efem.transferOrder.TransferOrder
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils

import static com.webbfontaine.efem.workflow.Operation.QUERY_REQUESTED

@Slf4j("LOGGER")
class QueryTransferOperationHandlerService extends ClientTransferOperationHandlerService{

    def objectStoreService
    def notificationService
    def messagingNotifService

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation) {
        LOGGER.debug("In beforePersist() of ${QueryTransferOperationHandlerService}")

        def commitOperationName = QUERY_REQUESTED.humanName()
        commitOperation = QUERY_REQUESTED

        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
    }

    @Override
    def afterPersist(TransferOrder domainInstance, TransferOrder result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            def queryMessage = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("comments")
            if (queryMessage) {
                loggerService.addMessage(result, queryMessage as String)
            }
            messagingNotifService.sendAfterCommit(result, TR_QUERY)
            String conversationId = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("conversationId")
            if(conversationId){
                objectStoreService.remove(conversationId)
            }
        }
        super.afterPersist(domainInstance, result, hasErrors, commitOperation)
    }
}
