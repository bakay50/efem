package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.CurrencyTransferBusinessLogicService
import com.webbfontaine.efem.LoggerService
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.CurrencyTransferWorkflow
import com.webbfontaine.efem.workflow.CurrencyTransferWorkflowService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.currencyTransfer.operations.StoreCurrencyTransferOperationHandlerService
import com.webbfontaine.grails.plugins.validation.rules.DocVerificationService
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Unroll

class CurrencyTransferServiceSpec extends Specification implements ServiceUnitTest<CurrencyTransferService>, DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
        mockDomain(Exchange)
        mockDomain(Repatriation)
    }

    def "test doCheckAddClearance()"() {
        given:
        CurrencyTransfer currencyInstance = new CurrencyTransfer(requestNo: "1",
                currencyCode: "EUR", amountTransferred: 5000)
        ClearanceDomiciliation domiciliation = new ClearanceDomiciliation()
        domiciliation.amountTransferredInCurr = 5000
        domiciliation.domiciliationCodeBank = "SIB"
        when:
        def result = service.doCheckAddClearance(currencyInstance, domiciliation)
        then:
        result instanceof ClearanceDomiciliation
        result.amountTransferredInCurr == 5000
        result.domiciliationCodeBank == "SIB"
    }

    def "test case doCheckAddClearance with amountTransferredInCurr equal to zero or null"() {
        given:
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer(requestNo: "1",
                currencyCode: "EUR", amountTransferred: 5000)
        ClearanceDomiciliation clearanceDomiciliation = new ClearanceDomiciliation()
        clearanceDomiciliation.amountTransferredInCurr = amountValue
        clearanceDomiciliation.domiciliationCodeBank = "SGBCI"
        when:
        def result = service.doCheckAddClearance(currencyTransferInstance, clearanceDomiciliation)
        then:
        result.errors['amountTransferredInCurr'].code == messageCodeError
        where:
        amountValue | messageCodeError
        0           | "clerance.errors.amountTransferredInCurr.zero"
        null        | "clerance.errors.amountTransferredInCurr.zero"
    }

    def "test doPersist"() {
        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123')

        GroovyMock(CurrencyTransferWorkflow, global: true)
        def mockCurrencyTransferWorkflowService = Stub(CurrencyTransferWorkflowService) {
            getEndStatus(*_) >> {
                return Statuses.ST_STORED
            }
        }
        def mockdocVerificationService = Stub(DocVerificationService) {
            documentHasErrors(*_) >> {
                return false
            }
        }

        def storeOperation = new StoreCurrencyTransferOperationHandlerService()
        storeOperation.currencyTransferWorkflowService = mockCurrencyTransferWorkflowService //new CurrencyTransferWorkflowService()

        storeOperation.docVerificationService = mockdocVerificationService
        storeOperation.loggerService = Mock(LoggerService)

        def mockBusinessLogicService = Stub(CurrencyTransferBusinessLogicService) {
            resolveOperation(*_) >> {
                return storeOperation
            }

        }
        service.currencyTransferBusinessLogicService = mockBusinessLogicService
        setRequestParams()

        when:
        def result = service.doPersist(currencyTransfer, Operation.STORE)

        then:
        result.id == 1
        result.status == Statuses.ST_STORED

    }

    def "test checkCommitOperation"() {
        given:
        def params = ['operationSTORE': 'Stocker'] as Map
        def mockCurrencyTransferWorkflowService = Mock(CurrencyTransferWorkflowService)
        mockCurrencyTransferWorkflowService.getCommitOperation(params)
        mockCurrencyTransferWorkflowService.getOperationName(params?.commitOperation)
        mockCurrencyTransferWorkflowService.validationNotRequired(params?.commitOperation)
        service.currencyTransferWorkflowService = mockCurrencyTransferWorkflowService

        when:
        def result = service.checkCommitOperation(params)

        then:
        result == true
        params.validationRequired == true
        params.operationSTORE == 'Stocker'

    }

    @Unroll
    def "test setRepatriatedAmountToBank()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.getUserProperty(UserProperties.ADB) >> banckCode
        Exchange exchange = new Exchange()
        exchange.requestNo = 'EC0245'
        exchange.bankCode = banckCode

        ClearanceOfDom.metaClass.static.findAllByEcReferenceAndBankCode = { ecReference, bankCode -> repatriatedEc }

        when:
        def result = service.setRepatriatedAmountToBank(exchange)

        then:
        result == totalAmount

        where:
        banckCode | totalAmount            | repatriatedEc
        "NSIA"    | new BigDecimal("700")  | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("400")), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("300"))]
        "SGB1"    | new BigDecimal("1500") | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("500")), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("500")), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("500"))]
        "ECO1"    | new BigDecimal("0")    | null


    }

    @Unroll
    void "Test handleSetTransferRate with data amountTransferred: #amountTransferred, amountRepatriated: #amountRepatriated and result: #expected"() {
        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        currencyTransfer.amountTransferred = amountTransferred
        currencyTransfer.amountRepatriated = amountRepatriated

        when:
        BigDecimal result = service.handleSetTransferRate(currencyTransfer)

        then:
        result == expected

        where:
        amountTransferred      | amountRepatriated      | expected
        new BigDecimal("4000") | new BigDecimal("5000") | new BigDecimal("80")
        new BigDecimal("1000") | new BigDecimal("1500") | new BigDecimal("66.6666666700")
        new BigDecimal("850")  | new BigDecimal("2000") | new BigDecimal("42.5")
        null                   | null                   | new BigDecimal("0")
        new BigDecimal("0")    | new BigDecimal("2000") | new BigDecimal("0")
    }

    private void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.commitOperation = Operation.STORE
        webRequest.params.commitOperationName = Operation.STORE.name()
        RequestContextHolder.setRequestAttributes(webRequest)
    }

    def "test setAmountTransferred method()"() {
        given:
        List<ClearanceDomiciliation> domiciliationList = [new ClearanceDomiciliation(amountTransferredInCurr: new BigDecimal("2000")),
                                                          new ClearanceDomiciliation(amountTransferredInCurr: new BigDecimal("1000")),
                                                          new ClearanceDomiciliation(amountTransferredInCurr: null)]
        CurrencyTransfer currencyInstance = new CurrencyTransfer(requestNo: "1", requestDate: new LocalDate(), clearanceDomiciliations: domiciliationList)

        when:
        service.setAmountTransferred(currencyInstance)

        then:
        currencyInstance.amountTransferred == new BigDecimal("3000")
    }

    @Unroll
    def "test updateRepatriation"() {
        given:
        Repatriation repatriationInstance = new Repatriation(
                requestNumberSequence: '1', requestYear: '2021', requestNo: 'REP123', status: Statuses.ST_CONFIRMED, requestDate: LocalDate.parse("15/10/2021" as String, DateTimeFormat.forPattern("dd/MM/yyyy"))
        ).save(flush: true, validate: false, failOnError: false)

        CurrencyTransfer currencyTransfer = new CurrencyTransfer(repatriationNo: "REP123", repatriationDate: LocalDate.parse("15/10/2021" as String, DateTimeFormat.forPattern("dd/MM/yyyy")))

        service.repatriationService = Stub(RepatriationService) {
            findRepatriationByRequestNoAndDate(_,_) >> repatriationInstance
        }
        service.loggerService = Mock(LoggerService)
        def commitOperation = operation

        when:
        service.updateRepatriation(currencyTransfer, commitOperation)

        then:
        repatriationInstance?.status == status

        where:
        operation                    | status
        Operation.CANCEL_TRANSFERRED | Statuses.ST_CONFIRMED
        Operation.TRANSFER           | Statuses.ST_CEDED
        Operation.TRANSFER_STORED    | Statuses.ST_CEDED
        Operation.STORE              | Statuses.ST_CONFIRMED

    }

}
