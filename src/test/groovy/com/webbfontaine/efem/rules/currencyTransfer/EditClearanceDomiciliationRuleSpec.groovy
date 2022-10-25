package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class EditClearanceDomiciliationRuleSpec extends Specification implements  DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([amountTransferredInCurr: amountTransferredInCurr, rank:rank, ecReference:ecReference], null)
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer(requestNo: "1",
                currencyCode: "EUR", amountTransferred: 500)
        currencyTransferInstance.addClearanceDomiciliation(new ClearanceDomiciliation(ecReference: "EC01", amountTransferredInCurr: 450))
        def domiciliations = currencyTransferInstance.getClearanceDomiciliations()
        Exchange.metaClass.static.findByRequestNo = {
            new Exchange(requestNo: "EC01", currencyCode:"EUR")
        }

        when:
        Rule rule = new EditClearanceDomiciliationRule()
        rule.apply(new RuleContext(currencyTransferInstance, currencyTransferInstance.errors as Errors))

        then:
        domiciliations[0]?.errors.allErrors?.code[0] == expectedError
        where:
        ecReference | amountTransferredInCurr   |rank   | expectedError
        "EC01"      | 0                         |1      |"clerance.errors.amountTransferredInCurr.zero"
        "EC01"      | 501                       |1      |"clerance.errors.amountTransferredInCurr.Great"
        "EC01"      | 400                       |1      | null
    }
}
