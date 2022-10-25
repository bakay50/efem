package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.CustomActionOperation
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class CreateOperationInterceptor {

    SpringSecurityService springSecurityService

    CreateOperationInterceptor() {
        match(controller: 'repatriation', action: 'create')
    }

    boolean before() { return true }

    boolean after() {
        if(model) {
            def repatriationInstance = model?.repatriationInstance
            def domainOperations =  repatriationInstance?.operations
            if (domainOperations?.any { (it instanceof UpdateOperation || it instanceof CustomActionOperation) || (it instanceof OperationClass) }) {
                params.isDocumentEditable = true
            } else {
                LOGGER.warn("id = {}. User {} try to {} document, but don't have access.", params.id, springSecurityService?.principal?.username, actionName )
                redirect(url: '/access-denied')
            }
            return true
        }
    }
}
