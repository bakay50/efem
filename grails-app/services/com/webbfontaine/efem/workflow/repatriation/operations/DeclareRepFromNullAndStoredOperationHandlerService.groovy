package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.constants.Statuses.ST_PRE_CONFIRMED
import static com.webbfontaine.efem.constants.Statuses.ST_STORED
import static com.webbfontaine.efem.workflow.Operation.DECLARE
import static com.webbfontaine.efem.workflow.Operation.DECLARE_STORED
import static com.webbfontaine.efem.constant.MailConstants.REP_DECLARE

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class DeclareRepFromNullAndStoredOperationHandlerService extends ClientRepOperationHandlerService {
    def repatriationService
    def sequenceGenerator
    def notificationService
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    @Override
    def beforePersist(Repatriation repatriation, def commitOperation){
        def commitOperationName
        LOGGER.debug("in beforePersist() of ${DeclareRepFromNullAndStoredOperationHandlerService}")
        repatriation?.lastTransactionDate = LocalDateTime.now()
        def params = GrailsWebRequest.lookup().params

        repatriation?.requestDate = new LocalDate()
        def year = repatriation?.requestDate?.getYear()
        repatriation?.requestNumberSequence = sequenceGenerator.repatriationNextRequestNumber(year as String)

        repatriation?.requestYear = year
        if(!repatriation?.requestNo){
            repatriation?.requestNo =repatriationService.generateRequestNumber(repatriation)
        }

        if(repatriation?.status == null){
            commitOperation = DECLARE
            commitOperationName = DECLARE.humanName()
        }else if(repatriation?.status == ST_STORED){
            commitOperation = DECLARE_STORED
            commitOperationName = DECLARE_STORED.humanName()
        }

        if(UserUtils.isBankAgent()){
            repatriation?.status = ST_PRE_CONFIRMED
            repatriation.storedOwner = RepatriationConstants.BANK
        }else{
            repatriation.storedOwner = RepatriationConstants.TRADER
        }

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
            messagingNotifService.sendAfterCommit(result, REP_DECLARE)
        }

    }


}
