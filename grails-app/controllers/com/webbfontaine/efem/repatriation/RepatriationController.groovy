package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.command.RepatriationSearchCommand
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.apache.http.HttpStatus

import java.util.concurrent.locks.Lock

import static com.webbfontaine.efem.workflow.Operation.UPDATE_CLEARANCE
import static com.webbfontaine.efem.workflow.Operation.DELETE_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_CONFIRMED


/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class RepatriationController {
    def xmlService
    def repatBusinessLogicService
    def springSecurityService
    def referenceService
    def repatriationService
    def docVerificationService
    def repatriationSearchService
    def synchronizationService
    SessionStore sessionStoreService


    def index() {
        redirect(action: 'list')
    }

    def list(RepatriationSearchCommand searchCommand) {
        LOGGER.debug("in list() of ${RepatriationController}")

        [searchCommand: searchCommand, referenceService: referenceService]
    }

    def search(RepatriationSearchCommand searchCommand) {
        Map searchResultModel = repatriationSearchService.getSearchResults(searchCommand, params)

        searchResultModel.referenceService = referenceService
        searchResultModel.linkParams = searchCommand.searchParams
        searchResultModel.domain = UtilConstants.REPATRIATION

        flash.searchResultMessage = searchResultModel.searchResultMessage

        if (request.isXhr()) {
            render(template: '/utils/search/searchResults', model: searchResultModel)
        } else {
            render(view: 'list', model: searchResultModel)
        }
    }


    def create() {
        LOGGER.debug("in create() of ${RepatriationController}")
        Repatriation repatriationInstance = new Repatriation()

        if (chainModel?.repatriationInstance) {
            repatriationInstance = chainModel?.repatriationInstance
        } else {
            params.conversationId = repatriationService.addToSessionStore(repatriationInstance)
        }
        repatBusinessLogicService.initDocumentForCreate(repatriationInstance)
        params.isDocumentEditable = true
        render(view: 'edit', model: [hasDocErrors: repatriationInstance.hasErrors(), repatriationInstance: repatriationInstance, referenceService: referenceService])
    }

    @Transactional
    def save() {
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params?.conversationId)
        if (repatriationInstance) {
            Lock lock = synchronizationService.lock(Repatriation.class, repatriationInstance.id)
            lock.lock()
            try {
                bindData(repatriationInstance, params)
                Operation commitOperation = params.commitOperation as Operation
                docVerificationService.removeAllErrors(repatriationInstance)
                if (docVerificationService.deepVerify(repatriationInstance)) {
                    Repatriation result = repatriationService.doPersist(repatriationInstance, commitOperation)
                    flash.repatriationInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation.humanName()}")])
                    flash.successOperation = true
                    flash.endOperation = commitOperation?.humanName()
                    redirect(action: 'show', id: result.id)

                } else {
                    params.id = repatriationInstance?.id
                    chain(action: 'create', params: setParams(), model: [repatriationInstance: repatriationInstance])
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during repatriation Save >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, repatriationInstance: repatriationInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }

        } else {
            notFound()
        }
    }

    def show() {
        Repatriation repatriationInstance
        params.isDocumentEditable = false

        if (flash.repatriationInstanceToShow) {
            repatriationInstance = flash.repatriationInstanceToShow
            repatriationInstance?.operations?.clear()
        } else {
            repatriationInstance = Repatriation.read(params.id)
        }

        if (repatriationInstance) {
            repatriationInstance.isDocumentEditable = false
            params.conversationId = repatriationService.addToSessionStore(repatriationInstance)
            repatBusinessLogicService.initDocumentForView(repatriationInstance)
            [repatriationInstance: repatriationInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    def edit() {
        Repatriation repatriationInstance = params.id ? repatriationService.loadRepatriation(Long.parseLong(params.id)) : repatriationService.findFromSessionStore(params.converstionId)
        params.isDocumentEditable = true

        if (repatriationInstance) {
            repatriationInstance.ecChanged = repatriationInstance.status == Statuses.ST_STORED ? []: defineEcChanged(repatriationInstance?.clearances)
            repatBusinessLogicService.initDocumentForEdit(repatriationInstance, params)
            params.conversationId = repatriationService.addToSessionStore(repatriationInstance)
            if (repatriationInstance.startedOperation == DELETE_STORED) {
                params.isDocumentEditable = false
            }
            if (repatriationInstance.startedOperation in [UPDATE_CONFIRMED,UPDATE_CLEARANCE]) {
                repatriationInstance.isDocumentEditable = true
            }
            render view: 'edit', model: [repatriationInstance: repatriationInstance, referenceService: referenceService]

        } else {
            notFound()
        }
    }

    List<LinkedHashMap> defineEcChanged(List<ClearanceOfDom> clearances){
        List<LinkedHashMap> ecChanged = []
        clearances.each {
            ecChanged.add(["ecReference":it.ecReference,"rank":it.rank,"status":it.status,"repatriatedAmtInCurr":it.repatriatedAmtInCurr,"id":it.id])
        }
        return ecChanged
    }

    def update() {
        LOGGER.debug("in update() of ${RepatriationController}")
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        if (repatriationInstance) {
            Lock lock = synchronizationService.lock(Repatriation.class, repatriationInstance.id)
            lock.lock()
            try {
                Operation commitOperation = params?.commitOperation
                params.op = params.startedOperation
                bindData(repatriationInstance, params)
                if (docVerificationService.deepVerify(repatriationInstance)) {
                    Repatriation result = repatriationService.doPersist(repatriationInstance, commitOperation)
                    flash.repatriationInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation?.humanName()}")])
                    flash.successOperation = true
                    flash.endOperation = commitOperation?.humanName()
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = repatriationInstance?.id
                    params.op = repatriationInstance?.startedOperation
                    respond repatriationInstance.errors, view: 'edit', model: [hasDocErrors: true, repatriationInstance: repatriationInstance, referenceService: referenceService]
                    return
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during exchange Update >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, repatriationInstance: repatriationInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }
        } else {
            notFound()
            return
        }
    }

    def delete() {
        LOGGER.debug("in delete() of ${RepatriationController}");
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        if (repatriationInstance) {
            repatriationInstance.delete(flush: true)
            flash.commitOperation = params.commitOperation
            flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${Operations.OP_DELETE}")])
        } else {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exchange.EA.title', default: 'EA'), repatriationInstance?.id])
        }
        redirect(action: 'list')
    }

    def verify() {
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        def instanceModel
        flash.message = null
        params.commitOperation = Operation.VERIFY
        params.isDocumentEditable = true
        bindData(repatriationInstance, params)
        if (docVerificationService.deepVerify(repatriationInstance)) {
            instanceModel = [hasDocErrors: false, repatriationInstance: repatriationInstance, referenceService: referenceService]
            flash.message = message(code: "verification.done.message", default: "Verify operation is done")
        } else {
            instanceModel = [hasDocErrors: true, repatriationInstance: repatriationInstance, referenceService: referenceService]
        }

        render view: 'edit', model: instanceModel
    }


    def setNotFound() {
        flash.errorMessage = message(code: 'default.not.found.message', args: ["Document", params.id])
        redirect(action: "list")
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'repatriation.label', default: 'Repatriation'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.SC_NOT_FOUND }
        }
    }

    def retrieveExchangeReference() {
        def resp = repatriationService.retrieveExchangeData(params.ecReference, params.exporterCode)
        if (resp?.error) {
            ClearanceOfDom clearanceOfDom = new ClearanceOfDom()
            clearanceOfDom.errors.rejectValue('rank', resp?.messageError, resp?.messageError)
            def response = [
                    error       : 'error',
                    responseData: g.render(template: '/repatriation/clearanceOfDom/clearanceDomError', model: [clearanceOfDom: clearanceOfDom])
            ]
            render response as JSON
        } else {
            LOGGER.debug("in retrieveExchangeReference() of resp :  ${resp.criteria}")
            def requestParam = [resp?.criteria?.requestDate, resp?.criteria?.bankName, resp?.criteria?.registrationNumberBank, resp?.criteria?.registrationDateBank, resp?.criteria?.amountMentionedCurrency, resp?.criteria?.finalAmountInDevise, resp?.criteria?.currencyCode, resp?.criteria?.dateOfBoarding, resp?.criteria?.bankCode]
            render requestParam as JSON
        }
    }

    private setParams() {
        [
                requestNo            : params?.requestNo,
                requestDate          : params?.requestDate,
                natureOfFund         : params?.natureOfFund,
                code                 : params?.code,
                repatriationBankCode : params?.repatriationBankCode,
                currencyCode         : params?.currencyCode,
                currencyRate         : params?.currencyRate,
                receivedAmountNat    : params?.receivedAmountNat,
                receivedAmount       : params?.receivedAmount,
                receptionDate        : params?.receptionDate,
                countryOfOriginCode  : params?.countryOfOriginCode,
                bankOfOriginCode     : params?.bankOfOriginCode,
                bankNotificationDate : params?.bankNotificationDate,
                executionRef         : params?.executionRef,
                executionDate        : params?.executionDate,
                currencyTransfertDate: params?.currencyTransfertDate,
                amountTransferred    : params?.amountTransferred,
                amountRemaining      : params?.amountRemaining,
                amountTransferredNat : params?.amountTransferredNat,
                amountRemainingNat   : params?.amountRemainingNat,
                status               : params?.status,
                conversationId       : params?.conversationId]
    }

    def importXML() {
        params.isDocumentEditable = true
        LOGGER.debug("Trying to import Repatriation xml file.")
        try {
            def domainInstance
            String docType = params?.domainName
            domainInstance = xmlService.importFileXml(request, docType)
            Repatriation repatriationInstance = new Repatriation()
            if (domainInstance instanceof Repatriation) {
                repatriationInstance = domainInstance
            }
            params.conversationId = repatriationService.addToSessionStore(repatriationInstance)
            repatBusinessLogicService.initDocumentForCreate(repatriationInstance)
            render(view: 'edit', model: [hasDocErrors: repatriationInstance.hasErrors(), repatriationInstance: repatriationInstance, referenceService: referenceService])
        } catch (IllegalArgumentException ex) {
            flash.errorMessage = wf.message(code: 'default.operation.error')
            LOGGER.warn("WARNING - Exception during import Repatriation : Invalid data in xml content >> ", ex)
        }
    }

    def exportXML() {
        String domainName = params?.domainName
        if (domainName.toUpperCase() == UtilConstants.REPATRIATION.toUpperCase()) {
            def xml = xmlService.exportDomainToXml(domainName, params)
            def fileName = domainName.toUpperCase()
            response.contentType = 'application/xml'
            response.setHeader 'Content-disposition', "attachment; filename=\"${fileName}-${params?.id}.xml\""
            response.outputStream << xml
            response.outputStream.flush()
        }
    }
}
