package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.transferOrder.TransferOrder
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest

import static com.webbfontaine.efem.workflow.Operation.UPDATE_VALIDATED
import static com.webbfontaine.efem.UserUtils.isSuperAdministrator
import static com.webbfontaine.efem.constant.MailConstants.TR_UPDATE

@Slf4j("LOGGER")
class UpdateValidatedTransferOperationHandlerService extends ClientTransferOperationHandlerService{

    def notificationService
    def transferOrderService
    def messagingNotifService

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${UpdateValidatedTransferOperationHandlerService}")

        commitOperation = UPDATE_VALIDATED
        def commitOperationName = UPDATE_VALIDATED.name()

        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)

    }

    @Override
    def afterPersist(TransferOrder domainInstance, TransferOrder result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            if(isSuperAdministrator()){
                transferOrderService.updateAmountExchange(result)
            }
            messagingNotifService.sendAfterCommit(result, TR_UPDATE)
        }
    }
}
