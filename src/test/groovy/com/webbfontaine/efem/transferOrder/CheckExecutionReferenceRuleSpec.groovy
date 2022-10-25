package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.rules.transferOrder.checking.CheckExecutionReferenceRule
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.Statuses.ST_CANCELLED
import static com.webbfontaine.efem.constants.Statuses.ST_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.VALIDATE
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.CREATE

class CheckExecutionReferenceRuleSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain TransferOrder
        defineBeans() {
            transferOrderService(TransferOrderService)
        }
    }

    @Unroll
    void "test CheckExecutionReferenceRule()"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([commitOperation: commitOperation], null)
        int currentYear = new LocalDate().year
        new TransferOrder(id: 1, bankCode: "bank1", executionRef: "001", requestYear: currentYear, status: ST_CANCELLED).save(flush: true)
        new TransferOrder(id: 2, bankCode: "bank1", executionRef: "002", requestYear: currentYear, status: ST_CANCELLED).save(flush: true)
        new TransferOrder(id: 3, bankCode: "bank2", executionRef: "001", requestYear: currentYear, status: ST_VALIDATED).save(flush: true)

        TransferOrder transferOrder = new TransferOrder(id: 4, bankCode: bankCode, executionRef: executionRef).save(flush: true)
        transferOrder.setStartedOperation(startedOperation)
        transferOrder.save(flush: true, validate: false)
        mockDomain(TransferOrder)
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true

        when:
        Rule rule = new CheckExecutionReferenceRule()
        rule.apply(new RuleContext(transferOrder, transferOrder.errors as Errors))

        then:
        transferOrder.hasErrors() == expected

        where:
        startedOperation | expected | executionRef | bankCode | commitOperation
        REQUEST          | false    | "001"        | "bank1"  | REQUEST
        VALIDATE         | true     | "001"        | "bank2"  | VALIDATE
        VALIDATE         | false    | "001"        | "bank1"  | VALIDATE
        VALIDATE         | false    | "002"        | "bank1"  | VALIDATE
        CREATE           | false    | "002"        | "bank1"  | STORE
    }
}
