package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.LoggerService
import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.DocVerificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.transaction.TransactionStatus

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 07/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

trait OperationHandlerService<T> {

    DocVerificationService docVerificationService
    LoggerService loggerService
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationHandlerService.class)

    T execute(T domain, TransactionStatus transactionStatus, Operation commitOperation) {
        T result = null
        boolean hasErrors = false

        try {
            def originalStatus = domain?.status
            beforePersist(domain, commitOperation)
            updateStatus(domain, GrailsWebRequest.lookup().params.commitOperation)
            hasErrors = docVerificationService.documentHasErrors(domain)

            if (hasErrors) {
                result = domain

            } else {
                if (!(domain instanceof Bank)){
                    loggerService.saveDocumentHistory(domain, originalStatus, GrailsWebRequest.lookup().params.commitOperationName)
                    LOGGER.debug("Persisting domain...")
                }
                result = persist(domain, result)
            }

            if(!result){
                domain.status = domain.initialStatus
                result = domain
                hasErrors = true
            }

        }catch (IllegalArgumentException ex){
            hasErrors = true
            result = domain
            result.errors.rejectValue('id', "default.operation.error")
            LOGGER.error("ERROR on Operationhandler execute domain persist >> ", ex)
            throw new IllegalArgumentException(ex)
        }finally {
            afterPersist(domain, result, hasErrors, commitOperation)
            if (hasErrors) {
                transactionStatus.setRollbackOnly()
            }
        }

        return result
    }

    private T persist(T domainInstance, T result) {
        if (isCreate()) {
            result = domainInstance.save(flush: true, deepValidate: false, validate: false)
        } else {
            result = domainInstance.merge(flush: true, deepValidate: false, validate: false)
        }
        result
    }

    abstract BpmService getBpmService()

    abstract String getCommitOperation()

    abstract boolean isCreate()

    def beforePersist(T domain, commitOperation = null) {}

    def afterPersist(T domain, T result, hasErrors, commitOperation = null) {}

    def updateStatus(T domain, commitOperation = null) {
        domain.status = getBpmService().getEndStatus(domain, commitOperation)
    }
}