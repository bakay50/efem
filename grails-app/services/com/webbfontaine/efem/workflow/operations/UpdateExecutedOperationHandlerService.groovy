package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.constant.MailConstants.EX_UPDATE

@Slf4j("LOGGER")
class UpdateExecutedOperationHandlerService extends ClientOperationHandlerService{

    def clearanceDomService
    def messagingNotifService

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${UpdateExecutedOperationHandlerService}")

        commitOperation = UPDATE_EXECUTED
        def commitOperationName = UPDATE_EXECUTED.name()

        domainInstance.lastTransactionDate = LocalDateTime.now()
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
        WebRequestUtils.params.setProperty('operationsStarted', commitOperation)

    }

    @Override
    def afterPersist(Exchange domainInstance, Exchange result, Object hasErrors, Object commitOperation) {

        if(!hasErrors){
            messagingNotifService.sendAfterCommit(result, EX_UPDATE)
            clearanceDomService.updateFinalAmount(result.requestNo, result.finalAmountInDevise)
        }

    }
}
