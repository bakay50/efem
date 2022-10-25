package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
@Transactional
class RejectOperationHandlerService extends AfterApproveAndRequestOperationHandlerService{

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${RejectOperationHandlerService}")
        def currentStatus = domainInstance.status
        def commitOperationName

        if(currentStatus == ST_REQUESTED) {
            commitOperation = REJECT_REQUESTED
            commitOperationName = REJECT_REQUESTED.name()
        } else {
            commitOperation = REJECT_PARTIALLY_APPROVED
            commitOperationName = REJECT_PARTIALLY_APPROVED.name()
        }

        domainInstance.lastTransactionDate = LocalDateTime.now()
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)

    }
}
