package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.CheckAmountReceivedRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.validation.Errors
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Ignore

class CheckAmountReceivedRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(Exchange)
    }

    @Ignore
    void "CheckAmountReceived"() {
        given:
        ClearanceOfDom newClearance = new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal(3000), ecReference: "0001")
        CheckAmountReceivedRule.metaClass.getRepatriation = {
            return new Repatriation(requestNo: "001")
        }
        setRequestParams()

        when:
        Rule rule = new CheckAmountReceivedRule()
        rule.apply(new RuleContext(newClearance, newClearance.errors as Errors))

        then:
        newClearance?.errors.allErrors?.code[0] == 'clerance.errors.repatriatedAmtInCurr.sum'
    }

    private void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.amountReceived = 2000
        webRequest.params.conversationId = "123456"
        RequestContextHolder.setRequestAttributes(webRequest)
    }
}
