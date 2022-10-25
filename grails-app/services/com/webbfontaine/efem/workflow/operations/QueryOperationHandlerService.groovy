package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class QueryOperationHandlerService extends AfterApproveAndRequestOperationHandlerService{

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation) {
        LOGGER.debug("In beforePersist() of ${QueryOperationHandlerService}")

        def commitOperationName = QUERY_REQUESTED.humanName()

        if (getActionName().equals(QUERY_REQUESTED)){
            commitOperation = QUERY_REQUESTED
        }else  if (getActionName().equals(QUERY_PARTIALLY_APPROVED)){
            commitOperation = QUERY_PARTIALLY_APPROVED
        }

        domainInstance.lastTransactionDate = LocalDateTime.now()
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
    }
}
