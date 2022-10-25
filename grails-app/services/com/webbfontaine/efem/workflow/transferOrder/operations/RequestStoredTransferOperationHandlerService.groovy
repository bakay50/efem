package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.UserUtils

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.Operation
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.constant.MailConstants.TR_REQUEST
import static com.webbfontaine.efem.constant.MailConstants.TR_VALIDATE

import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
@Slf4j("LOGGER")
class RequestStoredTransferOperationHandlerService extends ClientTransferOperationHandlerService {
    def sequenceGenerator
    def transferOrderService
    def notificationService
    def messagingNotifService
    

    @Override
    BpmService getBpmService() {
        return transferOrderWorkflowService
    }

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation) {
        LOGGER.debug("in beforePersist() of ${RequestStoredTransferOperationHandlerService}")

        domainInstance.requestDate = new LocalDate()
        def year = domainInstance?.requestDate?.getYear()
        domainInstance.requestYear = year
        String key = "${year}"
        domainInstance?.requestNumberSequence = sequenceGenerator.transferNextRequestNumber(key)
        domainInstance.requestNo = transferOrderService?.generateRequestNumber(domainInstance)
        domainInstance.lastTransactionDate = LocalDateTime.now()
        if (!domainInstance.requestNo) {
            domainInstance.errors.rejectValue("requestNo", "default.requestNo.error")
        }
        def params = GrailsWebRequest?.lookup()?.params
        def commitOperationName
        if (userIsBankAgent()) {
            handleValidateRequestForBankAgent(domainInstance, commitOperation, commitOperationName, params)
        }
        params.put('commitOperation', params?.commitOperation ?: commitOperation)
        params.put('commitOperationName', params?.commitOperation ?: commitOperationName)

        LOGGER.debug("End of beforePersist  with requestNumberSequence : ${domainInstance.requestNumberSequence}, requestNo : ${domainInstance.requestNo} and requestDate : ${domainInstance.requestDate}")
    }

    @Override
    def afterPersist(TransferOrder domainInstance, TransferOrder result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            if (UserUtils.isTrader()) {
                messagingNotifService.sendAfterCommit(result, TR_REQUEST)
            }else{
                transferOrderService.updateEA(domainInstance)
                transferOrderService.logConcernedEA(domainInstance)
                messagingNotifService.sendAfterCommit(result, TR_VALIDATE)
            }
        }
    }

    def handleValidateRequestForBankAgent(TransferOrder domainInstance, commitOperation, commitOperationName, params) {
        domainInstance.status = ST_REQUESTED
        commitOperation = Operation.VALIDATE
        commitOperationName = Operation.VALIDATE.humanName()
        params.put('commitOperation', commitOperation)
        params.put('commitOperationName', commitOperationName)
    }

    boolean userIsBankAgent() {
        return UserUtils.isBankAgent()
    }

}
