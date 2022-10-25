package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.rules.transferOrder.checking.ImporterValidationRule
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 13/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ImporterValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(Exchange)
        mockDomain(TransferOrder)
    }

    void "test for ImporterValidationRule should expected error message"() {
        given:
        TransferOrder transferInstance = new TransferOrder(currencyPayCode: "XOF", importerCode: "000001")
        transferInstance.addOrderClearanceOfDoms(new OrderClearanceOfDom(eaReference: "EA2020000022"))
        Exchange.metaClass.static.findByRequestNo = {
            return new Exchange(currencyPayCode: "EURO", importerCode: "000002")
        }

        when:
        Rule rule = new ImporterValidationRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        assert transferInstance?.errors.allErrors.code[0] == "transferOrder.importerCode.same.error"
    }
}
