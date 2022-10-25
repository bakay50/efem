package com.webbfontaine.efem.rest

import com.webbfontaine.efem.command.RetrieveExchangeCommand
import grails.converters.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT

class RestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(this)
    RestEfemService restEfemService

    def retrieveAllEADocuments(RetrieveExchangeCommand retrieveExchangeCommand){
        LOGGER.debug("Received request from TVF to retrieve all Exchange Document that uses Tvf Number: ${retrieveExchangeCommand?.tvfNumber}")
        if(!retrieveExchangeCommand.validate()) {
            render(status: SC_NO_CONTENT, text: message(code: "exchange.rest.no.data", default: "No Data."))
            return
        }
        def exchangeDetails = restEfemService.getAllEADocsViaTVF(retrieveExchangeCommand)

        render exchangeDetails as JSON

    }
}
