package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.transferOrder.TransferOrder
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.constant.MailConstants.TR_UPDATE

@Slf4j("LOGGER")
class ValidatedTransferOperationHandlerService extends ClientTransferOperationHandlerService {

    def notificationService
    def transferOrderService
    def messagingNotifService

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation) {
        LOGGER.debug("In beforePersist() of ${ValidatedTransferOperationHandlerService}")
    }

    @Override
    def afterPersist(TransferOrder domainInstance, TransferOrder result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            transferOrderService.updateEA(domainInstance)
            transferOrderService.logConcernedEA(domainInstance)
            messagingNotifService.sendAfterCommit(result, TR_UPDATE)
        }
    }

}