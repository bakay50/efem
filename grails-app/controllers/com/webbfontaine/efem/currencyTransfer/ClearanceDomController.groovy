package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import static com.webbfontaine.efem.constants.UtilConstants.CONVERSATION_ID

@Slf4j("LOGGER")
class ClearanceDomController {

    def clearanceDomiciliationService
    def currencyTransferService

    def addClearanceDom() {
        LOGGER.debug("in method addClearanceDom() of ${ClearanceDomController}")
        def response
        def finalInv
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        clearanceDomiciliationService.updateParamsFields(params)
        ClearanceDomiciliation domiciliation = new ClearanceDomiciliation()
        bindData(domiciliation, params)
        params.currencyCode = params.currencyCodeEC
        clearanceDomiciliationService.checkCurrencyCode(domiciliation)
        currencyTransferInstance?.amountTransferred = TypeCastUtils.parseStringToBigDecimal(params?.amountTransferred, true)
        onRequestParams().put(CONVERSATION_ID, params.conversationId)
        if (domiciliation.hasErrors()) {
            response = handleResponse(clearanceOfDom: domiciliation, templatePath: getClearanceErrorTemplatePath(),error: true)
        } else {
            domiciliation = currencyTransferService.doCheckAddClearance(currencyTransferInstance, domiciliation)
            finalInv = Exchange?.findByRequestNo(domiciliation?.ecReference)?.isFinalAmount
            response = handleResponse(clearanceOfDom: domiciliation, currencyTransferInstance: currencyTransferInstance, templatePath: getClearanceDomTemplatePath(),finalInv: finalInv)

        }
        render response as JSON
    }

    def editClearanceDom() {
        LOGGER.debug("in method editClearanceDom() of ${ClearanceDomController}")
        def response
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        onRequestParams().put(CONVERSATION_ID, params.conversationId)
        if (currencyTransferInstance) {
            ClearanceDomiciliation domiciliation = clearanceDomiciliationService.editClearanceDom(currencyTransferInstance)
            bindData(domiciliation, params)
            currencyTransferInstance.isPrefinancingWithoutEC?:currencyTransferService.setAmountTransferred(currencyTransferInstance)
            clearanceDomiciliationService.checkCurrencyCode(domiciliation)
            LOGGER.debug("sending response data with Clearance Data {}", domiciliation)
            if (domiciliation.hasErrors()) {
                response = handleResponse(clearanceOfDom: domiciliation, templatePath: getClearanceErrorTemplatePath(), error: true)
            } else {
                clearanceDomiciliationService.updateClearanceDom(domiciliation)
                currencyTransferInstance.transferRate = currencyTransferInstance.isPrefinancingWithoutEC?currencyTransferInstance.transferRate:currencyTransferService.handleSetTransferRate(currencyTransferInstance)
                response = handleResponse(clearanceOfDom: domiciliation, currencyTransferInstance: currencyTransferInstance, templatePath: getClearanceDomTemplatePath())
            }
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def deleteClearanceDom() {
        LOGGER.debug("in deleteClearanceDom() of ${ClearanceDomController}")
        def response
        onRequestParams().put(CONVERSATION_ID, params.conversationId)
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        if (currencyTransferInstance) {
            ClearanceDomiciliation domiciliation = clearanceDomiciliationService.deleteClearanceDom(currencyTransferInstance)
            response = [
                    currencyTransferInstance: currencyTransferInstance,
                    template: g.render(template: getClearanceDomTemplatePath(),
                            model: [currencyTransferInstance: currencyTransferInstance,
                                    clearenceOfDomList: currencyTransferInstance?.clearanceDomiciliations,
                                    clearanceOfDom: domiciliation])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def cancelEditClearanceDom() {
        LOGGER.debug("in cancelEditClearanceDom() of ${ClearanceDomController}")
        def response
        onRequestParams().put(CONVERSATION_ID, params.conversationId)
        CurrencyTransfer currencyTransferInstance = currencyTransferService.findFromSessionStore(params.conversationId)
        if(params?.amountTransferredInCurr){
            String amount = params.amountTransferredInCurr.toString().replace(",",".")
            currencyTransferInstance.getClearanceDomiciliation(Integer.valueOf(params.rank), params.ecReference).amountTransferredInCurr = new BigDecimal(amount)
        }
        currencyTransferInstance.isPrefinancingWithoutEC?:currencyTransferService.setAmountTransferred(currencyTransferInstance)
        if (currencyTransferInstance) {
            response = [
                    currencyTransferInstance: currencyTransferInstance,
                    template: g.render(template: getClearanceDomTemplatePath(),
                            model: [currencyTransferInstance: currencyTransferInstance,
                                    clearenceOfDomList: currencyTransferInstance?.clearanceDomiciliations
                            ])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def deleteClearance(List<ClearanceDomiciliation> clearanceToDelete, CurrencyTransfer currencyTransferInstance) {
        clearanceToDelete?.each { ClearanceDomiciliation domiciliation ->
            if (domiciliation.id) {
                ClearanceDomiciliation dom = currencyTransferInstance.clearanceDomiciliations.find {
                    it.id == domiciliation.id
                }
                currencyTransferInstance.removeClearanceDomiciliation(dom)
                dom.delete(flush: true)
            } else {
                currencyTransferInstance.removeClearanceDomiciliation(domiciliation)
            }
        }
    }

    private def clearanceNotFound() {
        CurrencyTransfer currencyTransfer = CurrencyTransfer.newInstance()
        currencyTransfer?.setIsDocumentEditable(false)
        currencyTransfer.errors.rejectValue('clearances',
                'exch.errors.conversationExpired',
                'Cannot find conversational instance of exchange. Please ask support team.')
        def responseErrorData = [error: 'error',
                                 responseData: g.render(template: getClearanceErrorTemplatePath(),
                                         model: [clearanceOfDom: new ClearanceDomiciliation()])]
        render responseErrorData as JSON
    }

    private static Map onRequestParams() {
        GrailsWebRequest.lookup().params
    }

    private static String getClearanceDomTemplatePath() {
        return  "/currencyTransfer/tabs/listOfClearenceDoms"
    }

    def retrieveExchangebyReference() {
        def reference = params?.ecReference
        Map result = [ecReference_id: null, ecReference: null]
        if (reference) {
            Exchange exchange = Exchange.findByRequestNo(reference)
            if (exchange) {
                result = [ecReference_id: exchange.id, ecReference: exchange.requestNo]
            }
        }
        render result as JSON
    }
        
    private static String getClearanceErrorTemplatePath() {
        return "/currencyTransfer/clearanceDoms/clearanceDomError"
    }

    private def handleResponse(LinkedHashMap<String, Object> props) {
        def data = [
                error: props.error?: false,
                clearanceOfDom: props.clearanceOfDom,
                finalInv: props.finalInv?: false,
                currencyTransferInstance: props.currencyTransferInstance?: null,
                template: g.render(template: props.templatePath?: '', model: [
                        clearanceOfDom: props.clearanceOfDom,
                        currencyTransferInstance: props.currencyTransferInstance?: null
                ])
        ]
        return data
    }
}
