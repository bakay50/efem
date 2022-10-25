package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.Exchange
import grails.converters.JSON
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.slf4j.LoggerFactory
import grails.transaction.Transactional

import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.Statuses.ST_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_EXECUTED


/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Transactional(readOnly = true)
class ClearanceOfDomController {
    private static final LOGGER = LoggerFactory.getLogger(ClearanceOfDomController);
    def conversationService
    def clearanceDomService
    def repatriationService
    def docVerificationService

    def addClearanceDom() {
        LOGGER.debug("in addClearanceDom() of ${ClearanceOfDomController}");
        def response
        def finalInv
        LOGGER.debug("in addClearanceDom() EC ${params?.ecReference} From params :  ${params?.bankCode}")
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        clearanceDomService.updateParamsFieldsClearance(params)
        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(params)
        LOGGER.debug("in addClearanceDom() after new Clearance :  ${clearanceOfDom.bankCode}")
        if (!clearanceOfDom.bankCode && params.ecReference) {
            def bankCode = repatriationService.checkingCurrencyOfExchangeData(params.ecReference)?.bankCode
            LOGGER.debug("in addClearanceDom() banCode  retrieve :  ${bankCode}")
            if (bankCode) {
                clearanceOfDom.bankCode = bankCode
            }
        }

        docVerificationService.removeAllErrors(clearanceOfDom)
        if (docVerificationService.deepVerify(clearanceOfDom)) {
            clearanceOfDom = repatriationService.doAddClearanceChecking(repatriationInstance, clearanceOfDom)
            finalInv = Exchange?.findByRequestNo(clearanceOfDom?.ecReference)?.isFinalAmount
            LOGGER.debug("containe final invoice {}", finalInv)
        }

        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        response = [clearanceOfDom      : clearanceOfDom,
                    repatriationInstance: repatriationInstance,
                    finalInv            : finalInv,
                    template            : g.render(template: "/repatriation/tabs/listOfClearanceDoms",
                            model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances, clearanceOfDom: clearanceOfDom])
        ]
        render response as JSON
    }

    def editClearanceDom() {
        LOGGER.debug("in editClearanceDom() of ${ClearanceOfDomController}");
        def response
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        if (repatriationInstance) {
            def clearanceOfDom = clearanceDomService.editClearanceDoms(repatriationInstance)
            LOGGER.debug("sending response data with new Clearance Data {}", clearanceOfDom)
            LOGGER.debug("in editClearanceDom() EC ${clearanceOfDom?.ecReference} From ClearanceOfDom :  ${clearanceOfDom?.bankCode}")
            response = [
                    repatriationInstance: repatriationInstance,
                    template            : g.render(template: "/repatriation/tabs/listOfClearanceDoms",
                            model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances, clearanceOfDom: clearanceOfDom])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def deleteClearanceDom() {
        LOGGER.debug("in deleteClearanceDom() of ${ClearanceOfDomController}");
        def response
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        if (repatriationInstance) {
            ClearanceOfDom clearanceOfDom = clearanceDomService.deleteClearanceDoms(repatriationInstance)
            response = [
                    repatriationInstance: repatriationInstance,
                    template            : g.render(template: "/repatriation/tabs/listOfClearanceDoms",
                            model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances, clearanceOfDom: clearanceOfDom])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    def cancelEditClearanceDom() {
        LOGGER.debug("in cancelEditClearanceDom() of ${ClearanceOfDomController}");
        def response
        GrailsWebRequest.lookup().params.put("conversationId", params.conversationId)
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        if (repatriationInstance) {
            response = [
                    repatriationInstance: repatriationInstance,
                    template            : g.render(template: "/repatriation/clearanceOfDom/clearanceDomList", model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances])
            ]
            render response as JSON
        } else {
            clearanceNotFound()
        }
    }

    private def clearanceNotFound() {
        Repatriation repatriation = Repatriation.newInstance()
        repatriation?.setIsDocumentEditable(false)
        repatriation.errors.rejectValue('clearances', 'exch.errors.conversationExpired', 'Cannot find conversational instance of exchange. Please ask support team.')
        def responseErrorData = [error: 'error', responseData: g.render(template: '/repatriation/clearanceOfDom/clearanceDomError', model: [clearanceOfDom: new ClearanceOfDom()])]
        render responseErrorData as JSON
    }

    def initializeClearanceData() {
        LOGGER.debug("in initializeClearanceData() of ${ClearanceOfDomController}")
        def response
        def clearancesToDelete = []
        Repatriation repatriationInstance = repatriationService.findFromSessionStore(params.conversationId)
        if (repatriationInstance?.clearances) {
            repatriationInstance?.clearances.each {
                clearancesToDelete.add(it)
            }
            deleteClearance(clearancesToDelete, repatriationInstance)
            if (repatriationInstance) {
                response = [
                        repatriationInstance: repatriationInstance,
                        template            : g.render(template: "/repatriation/clearanceOfDom/clearanceDomList", model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances])
                ]
                render response as JSON
            } else {
                clearanceNotFound()
            }
        } else {
            response = [
                    repatriationInstance: repatriationInstance,
                    template            : g.render(template: "/repatriation/clearanceOfDom/clearanceDomList", model: [repatriationInstance: repatriationInstance, clearenceOfDomList: repatriationInstance?.clearances])
            ]
            render response as JSON
        }
    }

    def deleteClearance(Collection clearancesToDelete, Repatriation repatriationInstance) {
        clearancesToDelete?.each { ClearanceOfDom cl ->
            if (cl.id) {
                ClearanceOfDom cl2 = repatriationInstance.clearances.find {
                    it.id == cl.id
                }
                repatriationInstance.removeClearanceOfDom(cl2)
                cl2.delete(flush: true)
            } else {
                repatriationInstance.removeClearanceOfDom(cl)
            }

        }
    }

    def retrieveExchangeIdFromClearance() {
        LOGGER.debug("in retrieveExchangeIdFromClearance of ${ClearanceOfDomController}")
        def reference = params?.ecReference
        Map result = [ecReference_id : null, ecReference: null]
        if (reference) {
            Exchange exchange = Exchange.findByRequestNoAndRequestTypeAndStatusInList(reference, EC, [ST_APPROVED, ST_EXECUTED])
            if (exchange) {
                result = [ecReference_id : exchange.id, ecReference: exchange.requestNo]
            }
        }
        render result as JSON
    }
}
