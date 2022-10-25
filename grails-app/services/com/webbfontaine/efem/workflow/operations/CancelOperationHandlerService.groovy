package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class CancelOperationHandlerService extends AfterApproveAndRequestOperationHandlerService {

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation){
        LOGGER.debug("In beforePersist() of ${CancelOperationHandlerService}")
        def currentStatus = domainInstance.status
        def commitOperationName

        if(currentStatus == ST_QUERIED) {
            commitOperation = CANCEL_QUERIED
            commitOperationName = CANCEL_QUERIED.name()
        } else {
            commitOperation = CANCEL_APPROVED
            commitOperationName = CANCEL_APPROVED.name()
        }

        domainInstance.lastTransactionDate = LocalDateTime.now()
        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        WebRequestUtils.params.setProperty('initialStatus', currentStatus)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)

    }
}
