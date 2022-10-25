package com.webbfontaine.efem

import grails.converters.JSON
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletResponse

@Slf4j("LOGGER")
class SupDeclarationController {

    def exchangeService
    def referenceService
    def sadService
    def docVerificationService
    def supDeclarationService

    def addSupDeclaration() {
        LOGGER.debug("in addSupDeclaration() of ${ExecutionController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        Exchange exchange
        SupDeclaration supDeclaration = new SupDeclaration()
        bindData(supDeclaration, params)
        if (exchangeInstance) {
            docVerificationService.removeAllErrors(supDeclaration)
            if (docVerificationService.deepVerify(supDeclaration)) {
                exchange = sadService.retrieveExchangeFromSad(params)
                supDeclarationService.setDeclarationCifAmount(exchange, supDeclaration)
                supDeclarationService.setDeclarationRemainingBalance(exchange,supDeclaration)
                exchangeInstance.addSupDeclaration(supDeclaration)
                supDeclarationService.setRemainingBalanceEADeclaredAmount(exchangeInstance, exchange)
                supDeclarationService.setRemainingBalanceTransferDoneAmount(exchangeInstance)
                def model = [exchangeInstance: exchangeInstance,
                             template        : g.render(template: "/exchange/templates/declarationList",
                                     model: [supDeclarationlist: exchangeInstance.supDeclarations, exchangeInstance: exchangeInstance, addExecution: true, referenceService: referenceService])]
                render model as JSON
            } else {
                modelRequest(supDeclaration)
            }
        } else {
            badRequest()
        }

    }

    def editSupDeclaration() {
        LOGGER.debug("in editSupDeclaration() of ${ExecutionController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)

        if (exchangeInstance) {
            SupDeclaration supDeclaration = supDeclarationService.editSupdeclaration(exchangeInstance)

            if (!supDeclaration.hasErrors()) {
                supDeclarationService.updateSupDeclaration(supDeclaration)
                Exchange exchange = sadService.retrieveExchangeFromSad(params)
                supDeclarationService.setDeclarationCifAmount(exchange, supDeclaration)
                supDeclarationService.setDeclarationRemainingBalance(exchange,supDeclaration)
                supDeclarationService.setRemainingBalanceEADeclaredAmount(exchangeInstance, exchange)
                supDeclarationService.setRemainingBalanceTransferDoneAmount(exchangeInstance)
            }

            def model = [exchangeInstance: exchangeInstance,
                         template        : g.render(template: "/exchange/templates/declarationList",
                                 model: [supDeclarationlist: exchangeInstance.supDeclarations, exchangeInstance: exchangeInstance, addExecution: true, referenceService: referenceService, supDeclaration: supDeclaration])]
            render model as JSON

        } else {
            badRequest()
        }
    }

    def cancelEditSupDeclaration() {
        LOGGER.debug("in cancelEditSupDeclaration() of ${SupDeclarationController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            def model = [template: g.render(template: "/exchange/templates/declarationList",
                    model: [supDeclarationlist: exchangeInstance.supDeclarations, exchangeInstance: exchangeInstance, addExecution: true, referenceService: referenceService])]
            render model as JSON
        } else {
            badRequest()
        }
    }

    def deleteSupDeclaration() {
        LOGGER.debug("in deleteSupDeclaration() of ${SupDeclarationController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            SupDeclaration supDeclaration = supDeclarationService.deleteSupDeclaration(exchangeInstance)
            Exchange exchange = sadService.retrieveExchangeFromSad(params)
            supDeclarationService.setRemainingBalanceEADeclaredAmount(exchangeInstance, exchange)
            supDeclarationService.setRemainingBalanceTransferDoneAmount(exchangeInstance)
            def model = [exchangeInstance: exchangeInstance,
                         template        : g.render(template: "/exchange/templates/declarationList",
                                 model: [supDeclarationlist: exchangeInstance.supDeclarations, exchangeInstance: exchangeInstance, addExecution: true, referenceService: referenceService, supDeclaration: supDeclaration])]
            render model as JSON
        } else {
            badRequest()
        }
    }

    private def modelRequest(SupDeclaration supDeclaration) {
        def model = [error: 'error', responseData: g.render(template: '/exchange/templates/supDeclarationError', model: [supDeclaration: supDeclaration])]
        render model as JSON
    }

    private def badRequest() {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        render(contentType: 'text/json') {
            [message: message(code: "default.bad.request.message", "Requested action outdated. Please reopen the document")]
        }
    }
}
