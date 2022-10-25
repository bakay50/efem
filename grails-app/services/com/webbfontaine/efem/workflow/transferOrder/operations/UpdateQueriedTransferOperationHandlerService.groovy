package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.transferOrder.TransferOrder
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest

import static com.webbfontaine.efem.workflow.Operation.UPDATE_QUERIED

@Slf4j("LOGGER")
class UpdateQueriedTransferOperationHandlerService extends ClientTransferOperationHandlerService {

    @Override
    def beforePersist(TransferOrder domainInstance, def commitOperation){
        LOGGER.debug("in Before Persist of ${UpdateQueriedTransferOperationHandlerService}")
        commitOperation = UPDATE_QUERIED
        def commitOperationName =  UPDATE_QUERIED.name()

        GrailsWebRequest.lookup().params.setProperty('commitOperation', commitOperation)
        GrailsWebRequest.lookup().params.put('commitOperationName', commitOperationName)
    }
}
