package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class RequestQueriedOperationHandlerService extends AfterApproveAndRequestOperationHandlerService{

    def exchangeService

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("in Before Persist of ${RequestQueriedOperationHandlerService}")
        commitOperation = REQUEST_QUERIED
        def commitOperationName =  REQUEST_QUERIED.name()
        exchangeService.checkReferenceAutorisation(domainInstance)
        domainInstance.lastTransactionDate = LocalDateTime.now()
        domainInstance?.treatmentLevel = 0
        domainInstance?.requestedBy = exchangeService.getRequestedBy()
        domainInstance.balanceAs = domainInstance.amountMentionedCurrency
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
    }

}
