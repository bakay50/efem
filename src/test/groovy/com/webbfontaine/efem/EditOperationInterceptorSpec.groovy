package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.ExchangeWorkflowService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 18/09/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class EditOperationInterceptorSpec extends Specification implements InterceptorUnitTest<EditOperationInterceptor>, DataTest {

    void setupSpec() {
        mockDomain(Exchange)
    }

    void "Test EditOperationInterceptor"() {
        setup:
        UserUtils.authenticate(role)
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        MockHttpServletRequest request = new MockHttpServletRequest()
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.id = 1
        RequestContextHolder.setRequestAttributes(webRequest)
        interceptor.exchangeWorkflowService = Stub(ExchangeWorkflowService)

        new Exchange(id:1,requestDate: new LocalDate(), ecReference: "ec1",requestType: "EC",
                bankCode: "SGB1", bankName: "SGBCI", status: ST_REQUESTED,currencyCode: "EUR", geoArea: geoarea, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("5000.00"), finalAmountInDevise: new BigDecimal("5000.00"),balanceAs: new BigDecimal("5000.00")).save()

        when:
        def result = interceptor.before()

        then:
        result == false
        response.redirectedUrl.contains(containsExpected)

        where:
        geoarea | containsExpected  | role
        "001"   | "/access-denied"  | "ROLE_EFEMCI_BANK_AGENT"
        "002"   | "/access-denied"  | "ROLE_EFEMCI_BANK_AGENT"
        "003"   | "/access-denied"  | "ROLE_EFEMCI_GOVT_OFFICER"

    }
}
