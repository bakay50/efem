package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.security.Roles
import com.webbfontaine.efem.workflow.operations.RequestQueriedOperationHandlerService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

import static com.webbfontaine.efem.workflow.Operation.REQUEST_QUERIED

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 8/22/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RequestQueriedOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<RequestQueriedOperationHandlerService>, DataTest{

    def "Test before persist should set the value of treatment level to 0"(){
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'

        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.commitOperation = REQUEST_QUERIED
        webRequest.params.commitOperationName = REQUEST_QUERIED.name()
        RequestContextHolder.setRequestAttributes(webRequest)
        service.exchangeService = [ checkReferenceAutorisation : { a -> true}, getRequestedBy : { -> Roles.DECLARANT.authority}]
        Exchange domain = new Exchange(treatmentLevel: 100)

        when:
        service.beforePersist(domain, REQUEST_QUERIED)

        then:
        domain.treatmentLevel == 0
    }
}