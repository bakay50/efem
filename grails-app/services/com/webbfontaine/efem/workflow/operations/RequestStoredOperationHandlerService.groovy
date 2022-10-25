package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.Operation
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.constant.MailConstants.EX_REQUEST

@Slf4j("LOGGER")
class RequestStoredOperationHandlerService extends ClientOperationHandlerService {

    def exchangeService
    def notificationService
    def sequenceGenerator
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
    }

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("in beforePersist() of ${RequestStoredOperationHandlerService}")

        domainInstance.requestDate = new LocalDate()
        def year = domainInstance?.requestDate?.getYear()
        domainInstance.requestYear = year

        if (commitOperation == Operation.REQUEST_STORED){
            String key = "${year}/${domainInstance?.requestType}"
            domainInstance.requestNumberSequence = sequenceGenerator?.nextRequestNumber(key)
        }

        domainInstance.requestNo = exchangeService?.generateRequestNumber(domainInstance)
        domainInstance.balanceAs = exchangeService?.computationOfBalanceAs(domainInstance)
        domainInstance.requestedBy = exchangeService?.getRequestedBy()

        domainInstance.treatmentLevel=0

        domainInstance.lastTransactionDate = LocalDateTime.now()

        LOGGER.debug("Value of Request Sequence Number through Request Stored Operation: ${domainInstance?.requestNumberSequence}")
    }

    def afterPersist(Exchange domainInstance, Exchange result, Object hasErrors, Object commitOperation){
        if(!hasErrors){
            messagingNotifService.sendAfterCommit(result, EX_REQUEST)
        }
    }
}
