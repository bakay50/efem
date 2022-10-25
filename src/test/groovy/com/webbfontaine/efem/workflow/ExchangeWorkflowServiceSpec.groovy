package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.Statuses.ST_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_EXECUTED
import static com.webbfontaine.efem.constants.Statuses.ST_PARTIALLY_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED
import static com.webbfontaine.efem.constants.Statuses.ST_STORED

class ExchangeWorkflowServiceSpec extends Specification implements ServiceUnitTest<ExchangeWorkflowService>, DataTest {

    def setup() {
        defineBeans() {
            exchangeWorkflow(ExchangeWorkflow)
            springSecurityService(SpringSecurityService)
        }

        mockDomain(Exchange)
    }

    def "test initOperation()"() {
        given:
        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }
        Exchange exchange = new Exchange()
        exchange.status = status

        when:
        service.initOperations(exchange)

        then:
        exchange.operations.find { it.id == operation.name() }

        where:
        role                 | status    | operation
        "ROLE_EFEMCI_TRADER" | null      | Operation.STORE
        "ROLE_EFEMCI_TRADER" | ST_STORED | Operation.UPDATE_STORED
    }

    def "test isDomiciliateWhenEc()"() {
        given:
        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Exchange exchange = new Exchange()
        exchange.status = status
        exchange.requestType = requestType
        service.initOperations(exchange)

        when:
        service.isDomiciliateWhenEc(exchange)

        then:
        exchange.operations.collect { it.id == operation.name() }

        where:
        role                     | status       | operation                   | requestType
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.APPROVE_REQUESTED | ExchangeRequestType.EA
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.DOMICILIATE       | ExchangeRequestType.EC
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.UPDATE_STORED     | ExchangeRequestType.EC
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.REJECT_REQUESTED  | ExchangeRequestType.EA
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.VIEW_REQUESTED    | ExchangeRequestType.EA
        "ROLE_EFEMCI_BANK_AGENT" | ST_REQUESTED | Operation.VIEW_REQUESTED    | ExchangeRequestType.EC
    }

    def "test removeOperationByArea()"() {

        given:
        UserUtils.authenticate("ROLE_EFEMCI_BANK_AGENT")
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Exchange exchange = new Exchange(requestDate: new LocalDate(), ecReference: "ec1", requestType: "EC",
                bankCode: "SGB1", bankName: "SGBCI", status: status, currencyCode: "EUR", geoArea: geoarea, amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("5000.00"), finalAmountInDevise: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")).save(failOnError: true, flush: true)
        service.initOperations(exchange)

        when:
        service.removeOperationByArea(exchange)

        then:
        exchange.operations?.each { it.name == Operations.OP_VIEW }

        where:
        status       | geoarea
        ST_REQUESTED | "001"
        ST_REQUESTED | "002"

    }

    def "test removeOperationsForGovOfficerByArea()"() {

        given:
        UserUtils.authenticate("ROLE_EFEMCI_GOVT_OFFICER")
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovOfficer() >> true
        Exchange exchange = new Exchange(requestDate: new LocalDate(), ecReference: "ec1", requestType: "EC",
                bankCode: "SGB1", bankName: "SGBCI", status: ST_REQUESTED, currencyCode: "EUR", geoArea: "002", amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("7000.00"), finalAmountInDevise: new BigDecimal("7000.00"), balanceAs: new BigDecimal("5000.00")).save(failOnError: true, flush: true)
        service.initOperations(exchange)

        when:
        service.removeOperationsForGovOfficerByArea(exchange)

        then:
        exchange.operations?.each { it.name == Operations.OP_VIEW }

    }

    def "test requestTransferOrderForEA()"() {
        given:
        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isRole
        UserUtils.isTrader() >> isRole
        Exchange exchange = new Exchange()
        exchange.status = status
        exchange.requestType = requestType
        service.initOperations(exchange)
        when:
        service.requestTransferOrderForEA(exchange)
        then:
        exchange.operations.collect { it.id == operation.name() }
        where:
        role                     | status      | operation                        | requestType            | isRole
        "ROLE_EFEMCI_BANK_AGENT" | ST_EXECUTED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EA | true
        "ROLE_EFEMCI_BANK_AGENT" | ST_APPROVED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EA | true
        "ROLE_EFEMCI_BANK_AGENT" | ST_APPROVED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EC | true
        "ROLE_EFEMCI_TRADER"     | ST_APPROVED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EA | true
        "ROLE_EFEMCI_TRADER"     | ST_EXECUTED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EA | true
        "ROLE_EFEMCI_TRADER"     | ST_APPROVED | Operation.REQUEST_TRANSFER_ORDER | ExchangeRequestType.EC | true
    }

    def "test removeGovOfficerOperationsForEA()"() {
        given:
        UserUtils.authenticate("ROLE_EFEMCI_GOVT_OFFICER")
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovOfficer() >> true
        Exchange exchange = new Exchange()
        exchange.status = ST_REQUESTED
        exchange.requestType = ExchangeRequestType.EA
        service.initOperations(exchange)
        when:
        service.removeGovOfficerOperationsForEA(exchange)
        then:
        exchange.operations?.each { it.name == Operations.OP_VIEW }


    }

    @Unroll
    def "test method applyTreatmentLevel()"() {
        given:
        def role = ["ROLE_EFEMCI_GOVT_OFFICER"]
        service.springSecurityService = [principal: [userProperties: [LVL: LVL]]]

        LinkedHashSet<Operation> operations = new LinkedHashSet<Operation>()
        operations.add(new UpdateOperation(Operation.PARTIALLY_APPROVED.name(), Operation.PARTIALLY_APPROVED.humanName(), role))
        operations.add(new UpdateOperation(Operation.QUERY_PARTIALLY_APPROVED.name(), Operation.QUERY_PARTIALLY_APPROVED.humanName(), role))

        Exchange exchange = new Exchange(status: status, treatmentLevel: treatmentLevel)
        exchange?.operations = operations

        when:
        service.applyTreatmentLevel(exchange)

        then:
        exchange.operations?.size() == size

        where:
        LVL   | treatmentLevel | status                | size
        ""    | 1              | ST_APPROVED           | 2
        ""    | 1              | ST_PARTIALLY_APPROVED | 0
        "ALL" | 1              | ST_PARTIALLY_APPROVED | 0
        null  | 1              | ST_PARTIALLY_APPROVED | 0
        "1"   | 1              | ST_PARTIALLY_APPROVED | 2
        "1"   | 2              | ST_PARTIALLY_APPROVED | 0
        "2"   | 1              | ST_PARTIALLY_APPROVED | 2

    }
}
