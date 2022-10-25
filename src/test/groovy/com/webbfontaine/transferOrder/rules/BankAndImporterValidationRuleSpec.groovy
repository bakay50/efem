package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.rules.transferOrder.checking.BankAndImporterValidationRule
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 19/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class BankAndImporterValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(TransferOrder)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        LocalDate exeDate = LocalDate.now()
        TransferOrder transferInstance = new TransferOrder(requestYear: exeDate.getYear(),
                bankCode: bank, importerCode: importer, executionRef: "100", executionDate: LocalDate.now())

        transferInstance.addOrderClearanceOfDoms(new OrderClearanceOfDom(eaReference: "EA01", bankCode: "SIB"))
        transferInstance.addOrderClearanceOfDoms(new OrderClearanceOfDom(eaReference: "EA02", bankCode: "SIB"))
        Exchange.metaClass.static.findByRequestNo = {
            new Exchange(requestNo: "EA01", bankCode: "SIB", importerCode: "010101")
        }

        when:
        Rule rule = new BankAndImporterValidationRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        transferInstance?.errors.allErrors?.code[0] == expectedError
        where:
        bank    | importer | expectedError
        "SIB"   | "010101" | null
        "SGBCI" | "010101" | "transferOrder.bankCode.same.inList.error"
        "SIB"   | "010102" | "transferOrder.importerCode.same.inList.error"

    }
}
