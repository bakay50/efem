package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.operations.OperationClass
import com.webbfontaine.grails.plugins.workflow.operations.CustomActionOperation
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CreateOperationInterceptor {

    SpringSecurityService springSecurityService

    CreateOperationInterceptor() {
        match(controller: 'exchange', action: 'create')
    }

    boolean before() { return true }

    boolean after() {
        if(model) {
            def exchangeInstance = model?.exchangeInstance
            def domainOperations =  exchangeInstance?.operations
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
