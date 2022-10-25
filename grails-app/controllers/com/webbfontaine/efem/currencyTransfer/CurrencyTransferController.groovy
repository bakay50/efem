package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import com.webbfontaine.efem.constants.UtilConstants
import grails.converters.JSON
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import org.joda.time.LocalDateTime
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.Lock

import static com.webbfontaine.efem.workflow.Operation.CANCEL_TRANSFERRED
import static com.webbfontaine.efem.workflow.Operation.DELETE_STORED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady Diarra
 * Date: 16/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class CurrencyTransferController {

    def currencyTransferBusinessLogicService
    def currencyTransferService
    def referenceService
    def docVerificationService
    def currencyTransferSearchService
    def xmlService
    def repatriationService
    def synchronizationService

    def list(CurrencyTransferSearchCommand searchCommand) {
        render view: 'list', model: [searchCommand: searchCommand, referenceService: referenceService]
    }

    def create() {
        LOGGER.debug("in create() of ${CurrencyTransferController}")
        CurrencyTransfer currencyTransferInstance
        if (params?.id) {
            currencyTransferInstance = repatriationService.loadRepatriationFromParams(params)
            currencyTransferInstance.transferRate = currencyTransferService.handleSetTransferRate(currencyTransferInstance)
        } else {
            currencyTransferInstance = new CurrencyTransfer()
        }
        if (chainModel?.currencyTransferInstance) {
            currencyTransferInstance = chainModel?.currencyTransferInstance
        } else {
            params.conversationId = currencyTransferService.addToSessionStore(currencyTransferInstance)
        }
        currencyTransferBusinessLogicService.initDocumentForCreate(currencyTransferInstance)
        params.isDocumentEditable = true
        render(view: 'edit', model: [hasDocErrors: currencyTransferInstance.hasErrors(), currencyTransferInstance: currencyTransferInstance], referenceService: referenceService)
    }

    def retrieveExchangeReference() {
        Exchange exchange = currencyTransferService.retrieveExchangeData(params.ecReference)
        currencyTransferService.checkCurrencyCode(exchange)
        if (exchange.hasErrors()) {
            def responseError = [
                    error        : 'error',
                    respErrorData: g.render(template: '/currencyTransfer/clearanceDoms/clearanceDomError', model: [clearanceOfDom: exchange])
            ]
            render responseError as JSON
        } else {
            LOGGER.debug("in retrieveExchangeReference() of resp : ${exchange}")
            BigDecimal sumOfRepatriatedAmountToBank = currencyTransferService.setRepatriatedAmountToBank(exchange)
            def result = [exchange: exchange, repatriatedAmount: sumOfRepatriatedAmountToBank]
            render result as JSON
        }
    }

    def save() {
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params?.conversationId)
        if (currencyTransferInstance) {
            Lock lock = synchronizationService.lock(CurrencyTransfer.class, currencyTransferInstance.id)
            lock.lock()
            try {
                bindData(currencyTransferInstance, params)
                Operation commitOperation = params.commitOperation as Operation

                docVerificationService.removeAllErrors(currencyTransferInstance)
                if (docVerificationService.deepVerify(currencyTransferInstance)) {
                    CurrencyTransfer result = currencyTransferService.doPersist(currencyTransferInstance, commitOperation)
                    flash.currencyTransferInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "currencyTransfer.operation.${commitOperation.humanName()}")])
                    redirect(action: 'show', id: result.id)

                } else {
                    params.id = currencyTransferInstance?.id
                    chain(action: 'create', params: setParams(), model: [currencyTransferInstance: currencyTransferInstance])
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during currencyTransfer Save >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, currencyTransferInstance: currencyTransferInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }

        } else {
            notFound()
        }
    }

    def show() {
        CurrencyTransfer currencyTransferInstance
        params.isDocumentEditable = false

        if (flash.currencyTransferInstanceToShow) {
            currencyTransferInstance = flash.currencyTransferInstanceToShow
            currencyTransferInstance?.operations?.clear()
        } else {
            currencyTransferInstance = CurrencyTransfer.read(params.id)
        }

        if (currencyTransferInstance) {
            currencyTransferInstance.isDocumentEditable = false
            params.conversationId = currencyTransferService.addToSessionStore(currencyTransferInstance)
            currencyTransferBusinessLogicService.initDocumentForView(currencyTransferInstance)
            [currencyTransferInstance: currencyTransferInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    def edit() {
        CurrencyTransfer currencyTransferInstance = params.id ? currencyTransferService.loadCurrencyTransfer(Long.parseLong(params.id)) : currencyTransferService.findFromSessionStore(params.converstionId)
        params.isDocumentEditable = true
        if (currencyTransferInstance) {
            currencyTransferBusinessLogicService.initDocumentForEdit(currencyTransferInstance, params)
            params.conversationId = currencyTransferService.addToSessionStore(currencyTransferInstance)
            if (currencyTransferInstance.startedOperation in [DELETE_STORED, CANCEL_TRANSFERRED]) {
                currencyTransferInstance.isDocumentEditable = false
                params.isDocumentEditable = true
            }
            render view: 'edit', model: [currencyTransferInstance: currencyTransferInstance, referenceService: referenceService]

        } else {
            notFound()
        }
    }

    def update() {
        LOGGER.debug("in update() of ${CurrencyTransferController}")
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        if (currencyTransferInstance) {
            Lock lock = synchronizationService.lock(CurrencyTransfer.class, currencyTransferInstance.id)
            lock.lock()
            try {
                Operation commitOperation = params?.commitOperation
                params.op = params.startedOperation
                bindData(currencyTransferInstance, params)
                if (docVerificationService.deepVerify(currencyTransferInstance)) {
                    CurrencyTransfer result = currencyTransferService.doPersist(currencyTransferInstance, commitOperation)
                    flash.currencyTransferInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "currencyTransfer.operation.${commitOperation?.humanName()}")])
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = currencyTransferInstance?.id
                    params.op = currencyTransferInstance?.startedOperation
                    respond currencyTransferInstance.errors, view: 'edit', model: [hasDocErrors: true, currencyTransferInstance: currencyTransferInstance, referenceService: referenceService]
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during currencyTransfer Update >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, currencyTransferInstance: currencyTransferInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }
        } else {
            notFound()
        }
    }

    def delete() {
        LOGGER.debug("in delete() of ${CurrencyTransferController}");
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        if (currencyTransferInstance) {
            currencyTransferInstance.delete(flush: true)
            flash.commitOperation = params.commitOperation
            flash.message = message(code: 'default.operation.done.message', args: [message(code: "currencyTransfer.operation.${Operations.OP_DELETE}")])
        } else {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'currencyTransfer.deleted.title', default: 'EA'), currencyTransferInstance?.id])
        }
        redirect(action: 'list')
    }

    def search(CurrencyTransferSearchCommand searchCommand) {
        Map searchResultModel = currencyTransferSearchService.getSearchResults(searchCommand, params)
        searchResultModel.referenceService = referenceService
        searchResultModel.linkParams = searchCommand.searchParams
        searchResultModel.domain = UtilConstants.CURRENCY_TRANSFER
        flash.searchResultMessage = searchResultModel.searchResultMessage
        if (request.isXhr()) {
            render(template: '/utils/search/searchResults', model: searchResultModel)
        } else {
            render(view: 'list', model: searchResultModel)
        }
    }

    def importXML() {
        LOGGER.debug("in importXML() of ${CurrencyTransferController}")
        params.isDocumentEditable = true
        MultipartFile xmlFile = params.userFile
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        def extensionName = xmlFile?.getOriginalFilename()?.toUpperCase()?.split('\\.')?.getAt(1)
        if (extensionName != UtilConstants.XML_EXT) {
            currencyTransfer.errors.rejectValue('id', 'importXML.invalid.file', 'Please choose an XML file')
        } else {
            GPathResult xmlResult = new XmlSlurper().parseText(new String(xmlFile.bytes, StandardCharsets.UTF_8))
            currencyTransfer = xmlService.buildCurrencyTransfer(currencyTransfer, xmlResult)
            currencyTransferBusinessLogicService.initDocumentForCreate(currencyTransfer)
            if (!currencyTransfer.hasErrors()) {
                def conversationId = currencyTransferService.addToSessionStore(currencyTransfer)
                params.conversationId = conversationId
                flash.message = g.message(code: 'importXML.file.successful', default: 'File imported')
            }
        }
        render(view: 'edit', model: [currencyTransferInstance: currencyTransfer, referenceService: referenceService])
    }

    def exportXML() {
        LOGGER.debug("in export XML() of ${CurrencyTransferController}")
        CurrencyTransfer currencyTransfer = params.id ? currencyTransferService.loadCurrencyTransfer(Long.parseLong(params.id)) : currencyTransferService.findFromSessionStore(params.converstionId)
        bindData(currencyTransfer, params)
        def xml = xmlService.buildCurrencyTransferXMLFile(currencyTransfer)
        String filename = "currency-transfer-${new LocalDateTime().toDate().time}.xml"
        response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")
        response.contentType = "text/xml"
        response.outputStream << XmlUtil.serialize(xml.toString())

    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'currencyTransfer.notfound.label', default: 'e-Forex'), params.id])
        redirect action: "index", method: "GET"
    }

    private setParams() {
        [conversationId: params?.conversationId]
    }

    def loadRepatriation() {
        LOGGER.debug("in loadRepatriation() of ${CurrencyTransferController}, params: ${params}")
        if (params?.repatriationNo && params?.repatriationDate) {
            CurrencyTransfer currencyTransferInstance = repatriationService.loadRepatriationFromParams(params)
            def hasErrors = currencyTransferInstance?.hasErrors()
            if(!hasErrors){
                currencyTransferBusinessLogicService.initDocumentForCreate(currencyTransferInstance)
                currencyTransferService.addToSessionStore(currencyTransferInstance, params?.conversationId)
                currencyTransferInstance.transferRate = currencyTransferService.handleSetTransferRate(currencyTransferInstance)
                flash.message = message(code: "currencyTransfer.loadRepatriation.successful", default: "Repatriation data has been imported.")
                chain(action: 'create', params: setParams(), model: [currencyTransferInstance: currencyTransferInstance])
            }else{
                render(view: 'edit', model: [hasDocErrors: currencyTransferInstance.hasErrors(), currencyTransferInstance: currencyTransferInstance])
            }
        }
    }

}
