package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import grails.converters.JSON
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.slf4j.LoggerFactory

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class OrderClearanceOfDomController {
    private static final LOGGER = LoggerFactory.getLogger(OrderClearanceOfDomController);
    def orderClearanceDomService
    def transferOrderService
    def transferOrderValidationService
    def docVerificationService
    def exchangeService

    def addClearanceDom() {
        LOGGER.debug("in addClearanceDom() of ${OrderClearanceOfDomController}");
        def response
        TransferOrder transferOrderInstance = transferOrderService.findFromSessionStore(params.conversationId)
        orderClearanceDomService.updateParamsFieldsClearance(params)
        OrderClearanceOfDom clearanceOfDom = new OrderClearanceOfDom(params)
        def resp = transferOrderValidationService.doAmountCheckingOnDoc(clearanceOfDom)
        if (resp) {
            response = setResponseError(clearanceOfDom, resp)
        } else {
            transferOrderService.checkAndSetBankCode(clearanceOfDom, params?.eaReference)
            transferOrderService.doAddClearanceChecking(transferOrderInstance, clearanceOfDom)
            transferOrderService.setTransferAmountRequested(transferOrderInstance, params)
            transferOrderService.setTransferAmountExecuted(transferOrderInstance, params)
            GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
            Boolean hasError = clearanceOfDom?.errors?.hasErrors() || transferOrderInstance.errors.hasErrors()
            response = [error           : hasError,
                        clearanceOfDom  : clearanceOfDom,
                        transferInstance: transferOrderInstance,
                        template        : g.render(template: getResponseTemplate(hasError),
                                model: [transferInstance: transferOrderInstance, clearenceOfDomList: transferOrderInstance?.orderClearanceOfDoms, clearanceOfDom: clearanceOfDom])
            ]
        }
        render response as JSON
    }

    String getResponseTemplate(Boolean error) {
        if (error) {
            return "/transferOrder/orderClearanceOfDom/orderClearanceDomError"
        } else {
            return "/transferOrder/tabs/listOfOrderClearanceDoms"
        }
    }

    def editClearanceDom() {
        LOGGER.debug("in editClearanceDom() of ${OrderClearanceOfDomController}");
        def response
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        if (transferInstance) {
            def clearanceOfDom = orderClearanceDomService.editClearanceDoms(transferInstance)
            def resp = transferOrderValidationService.doAmountCheckingOnDoc(clearanceOfDom)
            if (resp) {
                response = setResponseError(clearanceOfDom, resp)
            } else {
                LOGGER.debug("sending response data with new Clearance Data {}", clearanceOfDom)
                docVerificationService.removeAllErrors(transferInstance)
                transferOrderValidationService.validateClearanceOfDom(clearanceOfDom)
                transferOrderService.setTransferAmountRequested(transferInstance, params)
                transferOrderService.setTransferAmountExecuted(transferInstance, params)
                Boolean hasError = clearanceOfDom?.errors?.hasErrors() || transferInstance.errors.hasErrors()
                response = [error           : hasError,
                            transferInstance: transferInstance,
                            template        : g.render(template: getResponseTemplate(hasError),
                                    model: [transferInstance: transferInstance, clearenceOfDomList: transferInstance?.orderClearanceOfDoms, clearanceOfDom: clearanceOfDom])
                ]
            }
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def cancelEditClearanceDom() {
        LOGGER.debug("in cancelEditClearanceDom() of ${OrderClearanceOfDomController}");
        def response
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        orderClearanceDomService.editClearanceDoms(transferInstance)
        if (transferInstance) {
            response = [
                    transferInstance: transferInstance,
                    template        : g.render(template: "/transferOrder/orderClearanceOfDom/orderClearanceDomList", model: [transferInstance: transferInstance, clearenceOfDomList: transferInstance?.orderClearanceOfDoms])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    private def clearanceNotFound() {
        TransferOrder transferOrder = TransferOrder.newInstance()
        transferOrder?.setIsDocumentEditable(false)
        transferOrder.errors.rejectValue('clearances', 'exch.errors.conversationExpired', 'Cannot find conversational instance of exchange. Please ask support team.')
        def responseErrorData = [error: 'error', responseData: g.render(template: '/transferOrder/orderClearanceOfDom/orderClearanceDomError', model: [clearanceOfDom: new OrderClearanceOfDom()])]
        render responseErrorData as JSON
    }

    def deleteClearanceDom() {
        LOGGER.debug("in deleteClearanceDom() of ${OrderClearanceOfDomController}");
        def response
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        TransferOrder transferInstance = transferOrderService.findFromSessionStore(params.conversationId)
        if (transferInstance) {
            OrderClearanceOfDom clearanceOfDom = orderClearanceDomService.deleteClearanceDoms(transferInstance)
            transferOrderService.setTransferAmountRequested(transferInstance, params)
            transferOrderService.setTransferAmountExecuted(transferInstance, params)
            response = [
                    transferInstance: transferInstance,
                    template        : g.render(template: "/transferOrder/tabs/listOfOrderClearanceDoms",
                            model: [transferInstance: transferInstance, clearenceOfDomList: transferInstance?.orderClearanceOfDoms, clearanceOfDom: clearanceOfDom])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def deleteClearance(Collection clearancesToDelete, TransferOrder transferInstance) {
        clearancesToDelete?.each { OrderClearanceOfDom cl ->
            if (cl.id) {
                OrderClearanceOfDom cl2 = transferInstance.orderClearanceOfDoms.find {
                    it.id == cl.id
                }
                transferInstance.removeOrderClearanceOfDoms(cl2)
                cl2.delete(flush: true)
            } else {
                transferInstance.removeOrderClearanceOfDoms(cl)
            }

        }
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

    def setResponseError(OrderClearanceOfDom clearanceOfDom, resp) {
        clearanceOfDom.errors.rejectValue('rank', resp?.errorMessage, resp?.errorMessage)
        def response = [error   : true,
                        template: g.render(template: '/transferOrder/orderClearanceOfDom/orderClearanceDomError', model: [clearanceOfDom: clearanceOfDom])
        ]
        response
    }

    def retrieveExchangeIdFromClearance() {
        String reference = params?.eaReference
        Map result = [eaReference_id : null]
        if (reference) {
            Exchange exchange = exchangeService.findExchangeByRequestTypeAndRequestNo(ExchangeRequestType.EA, reference)
            result = [eaReference_id : exchange?.id]
        }
        render result as JSON
    }

}
