package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.constant.MailConstants.EX_UPDATE

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 10/27/2014
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class UpdateApprovedOperationHandlerService extends ClientOperationHandlerService {

    def exchangeWorkflowService
    def clearanceDomService
    def messagingNotifService

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${UpdateApprovedOperationHandlerService}")

        commitOperation = UPDATE_APPROVED
        def commitOperationName = UPDATE_APPROVED.humanName()

        LOGGER.debug("Exchange Number of Executions ${domainInstance?.executions?.size() ?: 0}")

        if(domainInstance?.executions?.size() >= 1){
            domainInstance.status = ST_PRE_EXEUTE
        }

        LOGGER.debug("Exchnage commitOperation ${commitOperation} commitOperationName ${commitOperationName} status ${domainInstance.status}")

        domainInstance.lastTransactionDate = LocalDateTime.now()
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
        WebRequestUtils.params.setProperty('operationsStarted', commitOperation)

    }

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
    }

    @Override
    def afterPersist(Exchange domainInstance, Exchange result, Object hasErrors, Object commitOperation) {
        if(!hasErrors){
            messagingNotifService.sendAfterCommit(result, EX_UPDATE)
            clearanceDomService.updateFinalAmount(result.requestNo, result.finalAmountInDevise)
        }

    }
}