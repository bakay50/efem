package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.rules.transferOrder.checking.CurrencyCodeValidationRule
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
class CurrencyCodeValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(Exchange)
        mockDomain(TransferOrder)
    }

    void "test for CurrencyCodeValidationRule should expected error message"() {
        given:
        TransferOrder transferInstance = new TransferOrder(currencyPayCode: "XOF")
        transferInstance.addOrderClearanceOfDoms(new OrderClearanceOfDom(eaReference: "EA2020000022"))
        Exchange.metaClass.static.findByRequestNo = {
            return new Exchange(currencyCode: "EURO")
        }

        when:
        Rule rule = new CurrencyCodeValidationRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        assert transferInstance?.errors.allErrors.code[0] == "transferOrder.currencyPayCode.xof.error"
        assert transferInstance?.errors.allErrors.code[1] == "transferOrder.currencyPayCode.same.error"
    }
}
