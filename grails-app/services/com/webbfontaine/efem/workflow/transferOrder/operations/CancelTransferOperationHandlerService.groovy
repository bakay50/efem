package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.TransferOrder
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.constants.Statuses.ST_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALIDATED
import static com.webbfontaine.efem.constant.MailConstants.TR_CANCEL

@Slf4j("LOGGER")
class CancelTransferOperationHandlerService extends ClientTransferOperationHandlerService {

    def objectStoreService
    def transferOrderService
    def messagingNotifService

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${CancelTransferOperationHandlerService}")
        def currentStatus = domainInstance.status
        def commitOperationName

        if(currentStatus == ST_QUERIED) {
            commitOperation = CANCEL_QUERIED
            commitOperationName = CANCEL_QUERIED.name()
        } else {
            commitOperation = CANCEL_VALIDATED
            commitOperationName = CANCEL_VALIDATED.name()
        }

        domainInstance.lastTransactionDate = LocalDateTime.now()
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
            messagingNotifService.sendAfterCommit(result, TR_CANCEL)
            String conversationId = WebUtils.retrieveGrailsWebRequest()?.parameterMap?.get("conversationId")
            if(conversationId){
                objectStoreService.remove(conversationId)
            }
            if (UserUtils.isSuperAdministrator()) {
                transferOrderService.handleSetCancelOrderClearanceOfDom(domainInstance)
                transferOrderService.handleChangeEAStatusWhenAllExecutionsCancel(domainInstance)
            }
        }
        super.afterPersist(domainInstance, result, hasErrors, commitOperation)
    }
}
