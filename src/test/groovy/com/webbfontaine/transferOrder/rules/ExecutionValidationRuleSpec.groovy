package com.webbfontaine.transferOrder.rules

import com.webbfontaine.efem.rules.transferOrder.checking.ExecutionValidationRule
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import static com.webbfontaine.efem.workflow.Operation.REQUEST

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 19/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExecutionValidationRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain(TransferOrder)
        defineBeans {
            transferOrderService(TransferOrderService)
        }
    }

    void "test for ExecutionValidationRule should expected error message"() {
        given:
        LocalDate exeDate = LocalDate.now()
        TransferOrder transferInstance = new TransferOrder(requestYear: exeDate.getYear(), bankCode: "SIB", executionRef: "100",
                executionDate: LocalDate.now()).save()
        transferInstance?.startedOperation = REQUEST

        when:
        Rule rule = new ExecutionValidationRule()
        rule.apply(new RuleContext(transferInstance, transferInstance.errors as Errors))

        then:
        assert transferInstance?.errors.allErrors.code[0] == "transferOrder.executionRef.unique.error"
    }
}
