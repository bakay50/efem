package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import static com.webbfontaine.efem.constant.MailConstants.EX_REJECT
import static com.webbfontaine.efem.constant.MailConstants.EX_QUERY
import static com.webbfontaine.efem.constant.MailConstants.EX_APPROVE
import static com.webbfontaine.efem.constant.MailConstants.EX_CANCEL
import static com.webbfontaine.efem.constant.MailConstants.EX_REQUEST
import com.webbfontaine.efem.workflow.Operation
import org.grails.web.util.WebUtils

import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.constants.Statuses.*

class AfterApproveAndRequestOperationHandlerService extends ClientOperationHandlerService{

    def objectStoreService
    def notificationService
    def messagingNotifService

    @Override
    def afterPersist(Exchange domainInstance, Exchange result, Object hasErrors, Object commitOperation) {
        List<Operation> approveOpList = [APPROVE_PARTIALLY_APPROVED, PARTIALLY_APPROVED,DOMICILIATE]
        if (!hasErrors) {
            def queryMessage = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("comments")
            if (queryMessage) {
                loggerService.addMessage(result, queryMessage as String)
            }
            if(commitOperation == REQUEST_QUERIED){
                messagingNotifService.sendAfterCommit(result, EX_REQUEST)
            } else if (commitOperation in [REJECT_PARTIALLY_APPROVED, REJECT_REQUESTED]) {
                messagingNotifService.sendAfterCommit(result, EX_REJECT)
            } else if (commitOperation in [QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED]) {
                messagingNotifService.sendAfterCommit(result, EX_QUERY)
            } else if (commitOperation in [CANCEL_APPROVED, CANCEL_QUERIED]) {
                messagingNotifService.sendAfterCommit(result, EX_CANCEL)
            } else if ((commitOperation == APPROVE_REQUESTED) || (commitOperation in approveOpList && result.status == ST_APPROVED)) {
                messagingNotifService.sendAfterCommit(result, EX_APPROVE)
            }
            String conversationId = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("conversationId")
            if(conversationId){
                objectStoreService.remove(conversationId)
            }
        }
        super.afterPersist(domainInstance, result, hasErrors, commitOperation)
    }
}
