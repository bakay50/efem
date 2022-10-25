package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.efem.workflow.Operation
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
@Slf4j("LOGGER")
class ClientRepOperationHandlerService implements OperationHandlerService<Repatriation> {

    def repatriationWorkflowService

    @Override
    BpmService getBpmService() {
        return repatriationWorkflowService
    }

    private static String getActionName() {
        GrailsWebRequest.lookup().actionName
    }

    @Override
    String getCommitOperation() {
        ((GrailsWebRequest) RequestContextHolder.currentRequestAttributes()).getParams().commitOperation
    }

    @Override
    boolean isCreate() {
        ExchangeOperationHandlerUtils.isCreate(getCommitOperation() as Operation)
    }

}
