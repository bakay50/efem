package com.webbfontaine.transferOrder

import com.webbfontaine.efem.ConvertDigitsToLetterService
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.LoggerService
import com.webbfontaine.efem.TransferBusinessLogicService
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.TransferOrderWorkflow
import com.webbfontaine.efem.workflow.TransferOrderWorkflowService
import com.webbfontaine.efem.workflow.transferOrder.operations.StoreTransferOperationHandlerService
import com.webbfontaine.grails.plugins.validation.rules.DocVerificationService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Unroll

class TransferOrderServiceSpec extends Specification implements ServiceUnitTest<TransferOrderService>, DataTest {

    def setupSpec() {
        mockDomain(TransferOrder)
        mockDomain(OrderClearanceOfDom)
        mockDomain(Exchange)
    }

    def "test doPersist"() {
        given:
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123'
        )

        GroovyMock(TransferOrderWorkflow, global: true)
        def mockTransferOrderWorkflowService = Stub(TransferOrderWorkflowService) {
            getEndStatus(*_) >> {
                return Statuses.ST_STORED
            }
        }
        def mockdocVerificationService = Stub(DocVerificationService) {
            documentHasErrors(*_) >> {
                return false
            }
        }

        def storeOperation = new StoreTransferOperationHandlerService()
        storeOperation.transferOrderWorkflowService = mockTransferOrderWorkflowService
        storeOperation.docVerificationService = mockdocVerificationService
        storeOperation.loggerService = Mock(LoggerService)
        def mockBusinessLogicService = Stub(TransferBusinessLogicService) {
            resolveOperation(*_) >> {
                return storeOperation
            }

        }
        service.transferBusinessLogicService = mockBusinessLogicService
        setRequestParams()

        when:
        def result = service.doPersist(transfer, Operation.STORE)

        then:
        result.id == 1
        result.status == Statuses.ST_STORED

    }

    def "test doAddClearanceChecking"() {
        given:
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123'
        )

        def mockTransferOrderValidationService = Stub(TransferOrderValidationService) {
            validateClearanceOfDom(*_) >> {
                return true
            }
        }

        OrderClearanceOfDom clearanceOfDom = new OrderClearanceOfDom()
        service.transferOrderValidationService = mockTransferOrderValidationService

        when:
        def result = service.doAddClearanceChecking(transfer, clearanceOfDom)

        then:
        result.rank == 1
        transfer.orderClearanceOfDoms.size() == 1
        result.transfer.requestNo == transfer.requestNo
    }

    def "test retrieveExchangeData"() {
        given:
        Exchange exchange = new Exchange(requestNo: "EA202012", status: "Approved", declarantCode: '1234', importerCode: '0001',
                requestType: "EA",
                currencyCode: "XOF", bankCode: "SGBCI",
                amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(10.00),
                year: LocalDate.now().year, registrationNumberBank: "bank123", registrationDateBank: LocalDate.now())

        exchange.save(flush: true, validate: false, failOnError: false)

        when:
        def result = service.retrieveExchangeData("EA202012")

        then:
        result.requestNo == "EA202012"
    }

    def "test updateEA"() {
        given:
        TransferOrder transfer = new TransferOrder(
                requestNumberSequence: '1'
                , requestYear: '2020'
                , requestNo: 'CT00123', status: Statuses.ST_APPROVED,
                amntSetInMentionedCurr: 10,
                destinationBank: "SGBCI",
                executionRef: "ref01",
                executionDate: LocalDate.now(),
                currencyPayCode: "EURO",
                bankAccntNoCredit: 10
        )

        def mockTransferOrderValidationService = Stub(TransferOrderValidationService) {
            validateClearanceOfDom(*_) >> {
                return true
            }
        }
        def exchange = new Exchange(requestNo: "EA2020000023", amountSettledMentionedCurrency: 1000, status: Statuses.ST_APPROVED)
        Exchange.metaClass.static.findByRequestNo = { it ->
            return exchange
        }
        transfer.addOrderClearanceOfDoms(new OrderClearanceOfDom(rank: 1, eaReference: "EA2020000023", amountSettledMentionedCurrency : new BigDecimal(100)))
        service.transferOrderValidationService = mockTransferOrderValidationService

        when:
        service.updateEA(transfer)

        then:
        transfer.orderClearanceOfDoms.get(0)?.eaReference == "EA2020000023"
        exchange?.status == Statuses.ST_EXECUTED
    }

    def "test updateAmountExchange if Exchange has more execution"() {
        given:
        Exchange exchangeInstance = populateFirstExchangesData()
        exchangeInstance.save(flush: true, validate: false, failOnError: false)
        TransferOrder transfer = populateFirstTransferData()
        transfer.save(flush: true, validate: false, failOnError: false)
        when:
        service.updateAmountExchange(transfer)
        then:
        exchangeInstance.requestNo == "EA200002020"
        exchangeInstance.balanceAs == 1310
        exchangeInstance.status == "Executed"
    }

    def "test updateAmountExchange if Exchange has one execution"() {
        given:
        Exchange exchangeInstance = populateSecondExchangeData()
        exchangeInstance.save(flush: true, validate: false, failOnError: false)
        TransferOrder transfer = populateSecondTransferData()
        transfer.save(flush: true, validate: false, failOnError: false)
        when:
        service.updateAmountExchange(transfer)
        then:
        exchangeInstance.requestNo == "EA300002020"
        exchangeInstance.balanceAs == 2800
        exchangeInstance.status == "Approved"
    }

    def "test save execution method()"() {
        given:
        Exchange exchange = new Exchange(requestNo: 'EA2020', status: 'Approved', declarantCode: '1234', importerCode: '0001')
        Execution execution = new Execution(rank: 1, executingBankCode: 'SGB1', executionReference: 'EXE2045')
        when:
        service.saveExecution(exchange, execution)
        then:
        execution.hasErrors() == false
        execution.executingBankCode == 'SGB1'
        execution.executionReference == 'EXE2045'
    }

    void "test updateExchangeBalanceAndStatus method()"() {
        given:
        Exchange exchange = new Exchange(requestNo: 'EA2020', status: 'Approved', declarantCode: '1234', importerCode: '0001', balanceAs: new BigDecimal(700.00))
        Execution execution = new Execution(rank: 1, executingBankCode: 'SGB1', executionReference: 'EXE2045', amountSettledMentionedCurrency: new BigDecimal(50.00))
        when:
        service.updateExchangeBalanceAndStatus(exchange, execution)
        then:
        exchange.hasErrors() == false
        exchange.status == Statuses.ST_EXECUTED
    }

    void "test setTransferAmountRequested()"() {
        given:
        def params = ['ratePayment': new BigDecimal("655.957")]
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.orderClearanceOfDoms = [
                new OrderClearanceOfDom(eaReference: "EA2020000039", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("2500"), amountRequestedMentionedCurrency: new BigDecimal("500.00")),
                new OrderClearanceOfDom(eaReference: "EA2020000009", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("1500"), amountRequestedMentionedCurrency: new BigDecimal("500.00"))
        ]
        service.convertDigitsToLetterService = Stub(ConvertDigitsToLetterService)

        when:
        service.setTransferAmountRequested(transferOrder, params)
        then:
        transferOrder.transferAmntRequested == new BigDecimal("1000")
        transferOrder.transferNatAmntRequest == new BigDecimal("655957.00")
    }

    void "test setTransferAmountExecuted()"() {
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        def params = ['ratePayment': new BigDecimal("655.957")]
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.orderClearanceOfDoms = [
                new OrderClearanceOfDom(eaReference: "EA2020000039", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("2500"), amountSettledMentionedCurrency: new BigDecimal("500.00")),
                new OrderClearanceOfDom(eaReference: "EA2020000009", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("1500"), amountSettledMentionedCurrency: new BigDecimal("500.00"))
        ]
        service.convertDigitsToLetterService = Stub(ConvertDigitsToLetterService)
        when:
        service.setTransferAmountExecuted(transferOrder, params)

        then:
        transferOrder.transferAmntExecuted == new BigDecimal("1000")
        transferOrder.transferNatAmntExecuted == new BigDecimal("655957.00")
    }

    def "test setTransferOrderFromEADocument()"() {
        given:
        def params = [id: 2500]
        Exchange exchange = new Exchange(id: 2500, requestNo: 'EA202045', bankCode: "SGB1", bankName: "S.G.B.C.I", countryProvenanceDestinationCode: "FR" , countryOfDestinationName: "France",
                currencyPayCode: "EUR" ,currencyPayName: "EURO", currencyPayRate: new BigDecimal("655.957"),importerCode: "1331590S", balanceAs: new BigDecimal("1500"))
        Exchange.metaClass.static.findById = {it -> return exchange}
        when:
        def result = service.setTransferOrderFromEADocument(params)

        then:
        result.importerCode == exchange.importerCode
        result.countryBenefBankCode == exchange.countryProvenanceDestinationCode
        result.countryBenefBankName == exchange.countryProvenanceDestinationName
        result.destinationBank == exchange.provenanceDestinationBank
        result.bankAccntNoCredit == exchange.bankAccountNocreditedDebited
        result.bankAccntNoDebited == exchange.accountNumberBeneficiary
        result.currencyPayCode == exchange.currencyPayCode
        result.currencyPayName == exchange.currencyPayName
        result.ratePayment == exchange.currencyPayRate
        result.bankCode == exchange.bankCode
        result.bankName == exchange.bankName
        result.orderClearanceOfDoms[0].eaReference == exchange.requestNo
        result.orderClearanceOfDoms[0].amountToBeSettledMentionedCurrency == exchange.balanceAs

    }

    def "test handleSetCancelOrderClearanceOfDom()"() {
        given:
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.executionRef = "EX2536"
        transferOrder.orderClearanceOfDoms = [
                new OrderClearanceOfDom(eaReference: "EA2020000039", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("200"), amountSettledMentionedCurrency: new BigDecimal("200.00"))
        ]
        Exchange.metaClass.static.findByRequestNo = { return retrieveEA }

        when:
        service.handleSetCancelOrderClearanceOfDom(transferOrder)

        then:
        retrieveEA.balanceAs == new BigDecimal("1200")
        retrieveEA.executions.get(0).state == "1"

        where:
        retrieveEA << new Exchange(requestNo: "EA2020000039", balanceAs: new BigDecimal("1000"), executions: [new Execution(executionReference: "EX2536", state: "0")])
    }

    @Unroll
    def "test handleChangeEAStatusWhenAllExecutionsCancel()"() {
        given:
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.orderClearanceOfDoms = [
                new OrderClearanceOfDom(eaReference: "EA2020000039", authorizationDate: new LocalDate(), bankName: "S.G.B.C.I", registrationNoBank: "sgb-1258", amountToBeSettledMentionedCurrency: new BigDecimal("200"), amountSettledMentionedCurrency: new BigDecimal("200.00"))
        ]
        Exchange.metaClass.static.findByRequestNo = { return retrieveEA }
        service.loggerService = Mock(LoggerService)

        when:
        service.handleChangeEAStatusWhenAllExecutionsCancel(transferOrder)

        then:
        retrieveEA.status == status

        where:
        retrieveEA | status
        new Exchange(requestNo: "EA2020000039", balanceAs: new BigDecimal("1000"), status: Statuses.ST_EXECUTED,executions: [new Execution(executionReference: "EX2536", state: "1"), new Execution(executionReference: "EX2545", state: "1")]) | Statuses.ST_APPROVED
        new Exchange(requestNo: "EA2020000039", balanceAs: new BigDecimal("1000"),status: Statuses.ST_EXECUTED,executions: [new Execution(executionReference: "EX2536", state: "1"), new Execution(executionReference: "EX2545", state: "0")]) | Statuses.ST_EXECUTED
    }


    def populateFirstTransferData(){
        return new TransferOrder(
                requestNumberSequence: '2'
                ,requestYear: '2020'
                ,requestNo: '202000047',
                requestDate : LocalDate.now(),
                eaReference : 'R001',
                importerCode : '0101142A',
                importerNameAddress : 'Bolloré',
                countryBenefBankCode : 'SGB1',
                countryBenefBankName : 'SGBCI',
                destinationBank: 'SIB',
                byCreditOfAccntOfCorsp: 'B001',
                bankAccntNoCredit: 'SDC0012',
                nameOfAccntHoldCredit: '',
                bankCode: 'SIB',
                bankName: 'SIB',
                bankAccntNoDebited: 'VBG102',
                charges: '',
                currencyPayCode: 'XOF',
                currencyPayName: 'XOF',
                ratePayment : 622.45 ,
                executionRef: 'EX00120',
                executionDate : LocalDate.now(),
                transferAmntRequested: 1500,
                transferNatAmntRequest: 2500,
                transferAmntExecuted: 1000,
                transferNatAmntExecuted: 1000,
                amntSetInMentionedCurr: 600,
                lastTransactionDate : LocalDateTime.now(),
                orderClearanceOfDoms : [
                        new OrderClearanceOfDom(rank: 1, eaReference: "EA200002020", bankCode: "SGBCI",state: '1',authorizationDate:LocalDate.now(),registrationNoBank:'R001',amountToBeSettledMentionedCurrency:1500,amountRequestedMentionedCurrency:1000,amountSettledMentionedCurrency:500),
                        new OrderClearanceOfDom(rank: 2, eaReference: "EA200002020", bankCode: "SGBCI",state: '0',authorizationDate:LocalDate.now(),registrationNoBank:'R002',amountToBeSettledMentionedCurrency:100,amountRequestedMentionedCurrency:500,amountSettledMentionedCurrency:700),
                        new OrderClearanceOfDom(rank: 3, eaReference: "EA200002020", bankCode: "SGBCI",state: '1',authorizationDate:LocalDate.now(),registrationNoBank:'R003',amountToBeSettledMentionedCurrency:2500,amountRequestedMentionedCurrency:1500,amountSettledMentionedCurrency:800)
                ]
        )
    }

    def populateFirstExchangesData(){
        return new Exchange(requestNo: "EA200002020", status: "Executed", declarantCode: '1234', importerCode: '0001',
                requestType: "EA",
                currencyCode: "XOF", bankCode: "SGBCI",
                amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(10.00),
                year: LocalDate.now().year, registrationNumberBank: "bank123", registrationDateBank: LocalDate.now(),
                executions :[
                        new Execution(rank:1,
                                executionReference:'EX001',
                                executionDate:LocalDate.now(),
                                currencyExCode:'XOF',
                                currencyExName: 'XOF',
                                currencyExRate:652,
                                amountMentionedExCurrency:1000,
                                amountNationalExCurrency:1000,
                                amountSettledNationalExCurrency:1000,
                                amountSettledMentionedCurrency:1000,
                                countryProvenanceDestinationExCode:'CI',
                                countryProvenanceDestinationExName:'côte ivoire',
                                provenanceDestinationExBank:'DIAMOND BANK',
                                bankAccountNumberCreditedDebited:'DIA001',
                                accountExBeneficiary:'ACC001',
                                creditCorrespondentAccount:'CRE001',
                                creditForeignCfaOrEuro:'CREC001',
                                accountOwnerCredited:'ACCO001',
                                executingBankCode:'SGBCI',
                                executingBankName:'SGBCI',
                                executionDomNumber:'EX00012',
                                executionDomDate:LocalDate.now(),
                                executionDomBankCode:'SIB',
                                executionDomBankName:'SIB'),
                        new Execution(rank:2,
                                executionReference:'EX002',
                                executionDate:LocalDate.now(),
                                currencyExCode:'XOF',
                                currencyExName: 'XOF',
                                currencyExRate:652,
                                amountMentionedExCurrency:1000,
                                amountNationalExCurrency:1000,
                                amountSettledNationalExCurrency:1000,
                                amountSettledMentionedCurrency:1000,
                                countryProvenanceDestinationExCode:'CI',
                                countryProvenanceDestinationExName:'côte ivoire',
                                provenanceDestinationExBank:'DIAMOND BANK',
                                bankAccountNumberCreditedDebited:'DIA001',
                                accountExBeneficiary:'ACC001',
                                creditCorrespondentAccount:'CRE001',
                                creditForeignCfaOrEuro:'CREC001',
                                accountOwnerCredited:'ACCO001',
                                executingBankCode:'SGBCI',
                                executingBankName:'SGBCI',
                                executionDomNumber:'EX00012',
                                executionDomDate:LocalDate.now(),
                                executionDomBankCode:'SIB',
                                executionDomBankName:'SIB')
                ])
    }

    def populateSecondExchangeData(){
        return new Exchange(requestNo: "EA300002020", status: "Executed", declarantCode: '1234', importerCode: '0001',
                requestType: "EA",
                currencyCode: "XOF", bankCode: "SGBCI",
                amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(1000.00),
                year: LocalDate.now().year, registrationNumberBank: "bank123", registrationDateBank: LocalDate.now(),
                executions :[
                        new Execution(rank:1,
                                executionReference:'EX003',
                                executionDate:LocalDate.now(),
                                currencyExCode:'XOF',
                                currencyExName: 'XOF',
                                currencyExRate:652,
                                amountMentionedExCurrency:1000,
                                amountNationalExCurrency:1000,
                                amountSettledNationalExCurrency:1000,
                                amountSettledMentionedCurrency:1000,
                                countryProvenanceDestinationExCode:'CI',
                                countryProvenanceDestinationExName:'côte ivoire',
                                provenanceDestinationExBank:'DIAMOND BANK',
                                bankAccountNumberCreditedDebited:'DIA001',
                                accountExBeneficiary:'ACC001',
                                creditCorrespondentAccount:'CRE001',
                                creditForeignCfaOrEuro:'CREC001',
                                accountOwnerCredited:'ACCO001',
                                executingBankCode:'SGBCI',
                                executingBankName:'SGBCI',
                                executionDomNumber:'EX00012',
                                executionDomDate:LocalDate.now(),
                                executionDomBankCode:'SIB',
                                executionDomBankName:'SIB')
                ])
    }

    def populateSecondTransferData(){
        new TransferOrder(
                requestNumberSequence: '2'
                ,requestYear: '2020'
                ,requestNo: '202000048',
                requestDate : LocalDate.now(),
                eaReference : 'R001',
                importerCode : '0101142A',
                importerNameAddress : 'Bolloré',
                countryBenefBankCode : 'SGB1',
                countryBenefBankName : 'SGBCI',
                destinationBank: 'SIB',
                byCreditOfAccntOfCorsp: 'B001',
                bankAccntNoCredit: 'SDC0012',
                nameOfAccntHoldCredit: '',
                bankCode: 'SIB',
                bankName: 'SIB',
                bankAccntNoDebited: 'VBG102',
                charges: '',
                currencyPayCode: 'XOF',
                currencyPayName: 'XOF',
                ratePayment : 622.45 ,
                executionRef: 'EX00120',
                executionDate : LocalDate.now(),
                transferAmntRequested: 1500,
                transferNatAmntRequest: 2500,
                transferAmntExecuted: 1000,
                transferNatAmntExecuted: 1000,
                amntSetInMentionedCurr: 600,
                lastTransactionDate : LocalDateTime.now(),
                orderClearanceOfDoms : [
                        new OrderClearanceOfDom(rank: 1, eaReference: "EA300002020", bankCode: "SGBCI",state: '1',authorizationDate:LocalDate.now(),registrationNoBank:'R001',amountToBeSettledMentionedCurrency:1500,amountRequestedMentionedCurrency:1000,amountSettledMentionedCurrency:800),
                        new OrderClearanceOfDom(rank: 2, eaReference: "EA300002020", bankCode: "SGBCI",state: '0',authorizationDate:LocalDate.now(),registrationNoBank:'R002',amountToBeSettledMentionedCurrency:100,amountRequestedMentionedCurrency:500,amountSettledMentionedCurrency:700),
                        new OrderClearanceOfDom(rank: 3, eaReference: "EA300002020", bankCode: "SGBCI",state: '1',authorizationDate:LocalDate.now(),registrationNoBank:'R003',amountToBeSettledMentionedCurrency:2500,amountRequestedMentionedCurrency:1500,amountSettledMentionedCurrency:1000)
                ]
        )
    }


    private static void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.commitOperation = Operation.STORE
        webRequest.params.commitOperationName = Operation.STORE.name()
        RequestContextHolder.setRequestAttributes(webRequest)
    }

}