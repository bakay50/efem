package com.webbfontaine.efem

import com.webbfontaine.efem.command.RetrieveExchangeCommand
import com.webbfontaine.efem.rest.RestController
import com.webbfontaine.efem.rest.RestEfemService
import grails.converters.JSON
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class RestControllerSpec extends Specification implements ControllerUnitTest<RestController> {

    void "test retrieveAllEADocuments with expected result"() {
        setup:
        def mockService = GroovyMock(RestEfemService)
        mockService.getAllEADocsViaTVF(*_) >> [exchange:[id:1,status:'Requested']]
        RetrieveExchangeCommand retrieveExchangeCommand = new RetrieveExchangeCommand(tvfNumber: 1, tvfDate: '2020-03-05')

        controller.restEfemService = mockService

        when:
        controller.retrieveAllEADocuments(retrieveExchangeCommand)

        then:
        response.text == ([exchange:[id:1,status:'Requested']] as JSON).toString()
    }

}
