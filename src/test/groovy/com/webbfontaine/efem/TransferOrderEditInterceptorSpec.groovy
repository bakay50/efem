package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

class TransferOrderEditInterceptorSpec extends Specification implements InterceptorUnitTest<TransferOrderEditInterceptor>, DataTest {

    def setup() {
        mockDomain(TransferOrder)
    }

    void "Test transferOrderEdit interceptor matching"() {
        setup:
        UserUtils.authenticate("ROLE_EFEMCI_BANK_AGENT")
        GroovyMock(UserUtils, global: true)
        UserUtils.getUserProperty(UserProperties.ADB) >> bankProperty
        UserUtils.isBankAgent() >> true
        MockHttpServletRequest request = new MockHttpServletRequest()
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.id = 1
        RequestContextHolder.setRequestAttributes(webRequest)
        new TransferOrder(bankCode:bankCode).save()
        when:
        interceptor.before()

        then:
        response.redirectedUrl == containsExpected
        where:
        bankProperty | bankCode | containsExpected
        ["SGB1"]     | "SGB1"   | null
        ["ECO1"]     | "SGB1"   | '/access-denied'


    }


}
