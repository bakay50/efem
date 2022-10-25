package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.utils.transfer.Response
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import groovy.xml.XmlUtil
import org.joda.time.LocalDateTime
import org.springframework.web.multipart.MultipartFile

import java.util.concurrent.locks.Lock

import static com.webbfontaine.efem.workflow.Operation.DELETE_STORED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class TransferOrderController {
    def transferBusinessLogicService
    def springSecurityService
    def referenceService
    def transferOrderService
    def transferOrderSearchService
    def docVerificationService
    def transferOrderValidationService
    def xmlService
    def synchronizationService

    def index() {
        redirect(action: 'list')
    }

    def list(TransferOrderSearchCommand searchCommand) {
        render view: 'list', model: [searchCommand: searchCommand, referenceService: referenceService]
    }

    def search(TransferOrderSearchCommand searchCommand) {
        Map searchResultModel = transferOrderSearchService.getSearchResults(searchCommand, params)
        searchResultModel.referenceService = referenceService
        searchResultModel.linkParams = searchCommand.searchParams
        searchResultModel.domain = UtilConstants.TRANSFER
        flash.searchResultMessage = searchResultModel.searchResultMessage
        if (request.isXhr()) {
            render(template: '/utils/search/searchResults', model: searchResultModel)
        } else {
            render(view: 'list', model: searchResultModel)
        }
    }

    def create() {
        LOGGER.debug("in create() of ${TransferOrderController}")
        TransferOrder transferInstance
        if (params.id) {
            transferInstance = transferOrderService.setTransferOrderFromEADocument(params)
        } else {

            transferInstance = new TransferOrder()
        }
        if (chainModel?.transferInstance) {
            transferInstance = chainModel?.transferInstance
        } else {
            params.conversationId = transferOrderService.addToSessionStore(transferInstance)
        }
        transferBusinessLogicService.initDocumentForCreate(transferInstance)
        params.isDocumentEditable = true
        render(view: 'edit', model: [hasDocErrors: transferInstance.hasErrors(), transferInstance: transferInstance, referenceService: referenceService])
    }

    @Transactional
    def save() {
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params?.conversationId)
        if (transferInstance) {
            Lock lock = synchronizationService.lock(TransferOrder.class, transferInstance.id)
            lock.lock()
            try {
                bindData(transferInstance, params)
                Operation commitOperation = params.commitOperation as Operation
                docVerificationService.removeAllErrors(transferInstance)
                if (docVerificationService.deepVerify(transferInstance)) {
                    TransferOrder result = transferOrderService.doPersist(transferInstance, commitOperation)
                    if (result.hasErrors()) {
                        params.id = transferInstance?.id
                        chain(action: 'create', params: setParams(), model: [transferInstance: transferInstance])
                        return
                    }
                    result.isEaOpearationsEditable = false
                    flash.transferInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation.humanName()}")])
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = transferInstance?.id
                    chain(action: 'create', params: setParams(), model: [transferInstance: transferInstance])
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during transferOrder Save >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, transferInstance: transferInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }

        } else {
            notFound()
        }
    }

    def show() {
        TransferOrder transferInstance
        params.isDocumentEditable = false

        if (flash.transferInstanceToShow) {
            transferInstance = flash.transferInstanceToShow
            transferInstance?.operations?.clear()
        } else {
            transferInstance = TransferOrder.read(params.id)
        }

        if (transferInstance) {
            transferInstance.isDocumentEditable = false
            params.conversationId = transferOrderService.addToSessionStore(transferInstance)
            transferBusinessLogicService.initDocumentForView(transferInstance)
            [transferInstance: transferInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    def edit() {
        TransferOrder transferInstance = params.id ? transferOrderService.loadTransfer(Long.parseLong(params.id)) : transferOrderService.findFromSessionStore(params.converstionId)
        params.isDocumentEditable = true

        if (transferInstance) {
            transferBusinessLogicService.initDocumentForEdit(transferInstance, params)
            params.conversationId = transferOrderService.addToSessionStore(transferInstance)
            if (transferInstance.startedOperation == DELETE_STORED) {
                params.isDocumentEditable = false
            }
            render view: 'edit', model: [transferInstance: transferInstance, referenceService: referenceService]

        } else {
            notFound()
        }
    }

    def update() {
        LOGGER.debug("in update() of ${TransferOrderController}")

        if (params.commitOperationName == Operations.OP_DELETE) {
            redirect(action: 'delete', params: params)
        } else {
            doUpdate(params)
        }
    }

    def doUpdate(params) {
        LOGGER.debug("in doUpdate() of ${TransferOrderController}")

        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        if (transferInstance) {
            Lock lock = synchronizationService.lock(TransferOrder.class, transferInstance.id)
            lock.lock()
            try {
                Operation commitOperation = params?.commitOperation
                params.op = params.startedOperation
                bindData(transferInstance, params)
                if (docVerificationService.deepVerify(transferInstance)) {
                    TransferOrder result = transferOrderService.doPersist(transferInstance, commitOperation)
                    flash.exhangeInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.message = message(code: 'default.operation.done.message', args: [message(code: "transferOrder.operation.${commitOperation?.humanName()}")])
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = transferInstance?.id
                    params.op = transferInstance?.startedOperation
                    respond transferInstance.errors, view: 'edit', model: [hasDocErrors: true, transferInstance: transferInstance, referenceService: referenceService]
                    return
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during Transfer Order Update >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, transferInstance: transferInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }
        } else {
            notFound()
            return
        }
    }

    def delete() {
        LOGGER.debug("in delete() of ${TransferOrderController}");

        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        if (transferInstance) {
            transferInstance.delete(flush: true)
            flash.commitOperation = params.commitOperation
            flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${Operations.OP_DELETE}")])
        } else {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exchange.EA.title', default: 'EA'), transferInstance?.id])
        }
        redirect(action: 'list')
    }

    def verify() {
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        def instanceModel
        flash.message = null
        params.commitOperation = Operation.VERIFY
        params.isDocumentEditable = true
        bindData(transferInstance, params)
        if (docVerificationService.deepVerify(transferInstance)) {
            instanceModel = [hasDocErrors: false, transferInstance: transferInstance, referenceService: referenceService]
            flash.message = message(code: "verification.done.message", default: "Verify operation is done")
        } else {
            instanceModel = [hasDocErrors: true, transferInstance: transferInstance, referenceService: referenceService]
        }

        render view: 'edit', model: instanceModel
    }

    def importXML() {
        LOGGER.debug("in importXML() of ${TransferOrderController}")
        params.isDocumentEditable = true
        MultipartFile xmlFile = params.userFile
        TransferOrder transferOrder = new TransferOrder()
        def extensionName = xmlFile?.getOriginalFilename()?.toUpperCase()?.split('\\.')?.getAt(1)
        if (extensionName != UtilConstants.XML_EXT) {
            transferOrder.errors.rejectValue('id', 'importXML.invalid.file', 'Please choose an XML file')
        } else {
            transferOrder = xmlService.buildTransferOrder(transferOrder, xmlFile)
            transferBusinessLogicService.initDocumentForCreate(transferOrder)
            if (!transferOrder.hasErrors()) {
                def conversationId = transferOrderService.addToSessionStore(transferOrder)
                params.conversationId = conversationId
                flash.message = g.message(code: 'importXML.file.successful', default: 'File imported')
            }
        }
        render(view: 'edit', model: [transferInstance: transferOrder, referenceService: referenceService])
    }

    def exportXML() {
        LOGGER.debug("in export XML() of ${TransferOrderController}")
        TransferOrder transferOrder = params.id ? transferOrderService.loadTransfer(Long.parseLong(params.id)) : transferOrderService.findFromSessionStore(params.converstionId)
        bindData(transferOrder, params)
        def xml = xmlService.buildTransferOrderXMLFile(transferOrder)
        String filename = "transfer-order-${new LocalDateTime().toDate().time}.xml"
        response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")
        response.contentType = "text/xml"
        response.outputStream << XmlUtil.serialize(xml.toString())

    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'exchange.label', default: 'e-Forex'), params.id])
        redirect action: "index", method: "GET"
    }

    private setParams() {
        [conversationId: params?.conversationId]
    }

    def retrieveExchangeByRef() {
        def response
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        def result = transferOrderValidationService.doAllCheckingOnDoc(transferInstance, params)
        if (result?.error) {
            OrderClearanceOfDom clearanceOfDom = new OrderClearanceOfDom()
            clearanceOfDom.errors.rejectValue('rank', result?.errorMessage, result?.errorMessage)
            response = [
                    error       : 'error',
                    responseData: g.render(template: '/transferOrder/orderClearanceOfDom/orderClearanceDomError', model: [clearanceOfDom: clearanceOfDom])
            ]
            render response as JSON
        } else {
            LOGGER.debug("in retrieveExchangeReference() of resp :  ${result?.content}")
            response = bindResponseData(result?.content)
            render response as JSON

        }
    }

    def bindResponseData(exchange) {
        Response response = new Response()
        response?.authorizationDate = exchange?.authorizationDate
        response?.bankName = exchange?.bankName
        response?.registrationNumberBank = exchange?.registrationNumberBank
        response?.registrationDateBank = exchange?.registrationDateBank
        response?.balance = exchange?.balanceAs
        response?.bankCode = exchange?.bankCode
        response
    }

    def loadExecution() {
        LOGGER.debug("in loadExecution() of ${TransferOrderController} Params value ${params}")
        Exchange exchangeInstance = Exchange.get(params?.id as Integer)
        Execution execution = exchangeInstance.getExecution(params.rank as Integer)
        TransferOrder transferInstance = transferOrderService.foundTransferorderByCriteria(execution?.executionDate?.getYear(), execution?.executingBankCode, execution?.executionReference)
        LOGGER.debug("transferInstance found ${transferInstance} ")
        transferInstance?.isDocumentEditable = false
        params.conversationId = transferOrderService.addToSessionStore(transferInstance)
        transferBusinessLogicService.initDocumentForView(transferInstance)
        render(view: 'show', model: [transferInstance: transferInstance, referenceService: referenceService])
    }
}
