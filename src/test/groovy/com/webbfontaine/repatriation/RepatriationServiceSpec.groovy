package com.webbfontaine.repatriation

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.LoggerService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.util.Holders
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.StaticMessageSource
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import grails.plugins.jodatime.simpledatastore.SimpleMapJodaTimeMarshaller
import org.joda.time.LocalDateTime
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RepatriationServiceSpec extends Specification implements ServiceUnitTest<RepatriationService>, DataTest {
    @Shared
    @Autowired
    MessageSource messageSource

    def setupSpec() {
        def mf = grailsApplication.getMappingContext().getMappingFactory()
        mf.registerCustomType(new SimpleMapJodaTimeMarshaller(LocalDate))
        mf.registerCustomType(new SimpleMapJodaTimeMarshaller(LocalDateTime))

        mockDomain(Exchange)
        mockDomain(Repatriation)
        mockDomain(ClearanceOfDom)

        defineBeans() {
            sessionStoreService(SessionStore)
            loggerService(LoggerService)
        }

        messageSource = Holders.applicationContext.getBean("messageSource") as StaticMessageSource
        messageSource.useCodeAsDefaultMessage
        initMessageSource(messageSource)
    }

    void generateExchange() {
        Exchange exchange = new Exchange(requestNo: "EC0001", requestType: ExchangeRequestType.EC, exporterCode: "0020136Z", status: Statuses.ST_APPROVED, currencyCode: "EURO"
                , geoArea: "003", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")
                , declarationNumber: "123", declarationSerial: "C", clearanceOfficeCode: "CIAB").save()
    }

    def "test for findECDocument method should return Exchange Ec instance"() {
        given:

        Exchange exchange = new Exchange(requestNo: "EC0001", requestType: ExchangeRequestType.EC, exporterCode: "0020136Z", status: Statuses.ST_APPROVED, currencyCode: "EURO"
                , geoArea: "003", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")
                , declarationNumber: "123", declarationSerial: "C", clearanceOfficeCode: "CIAB", declarationDate: new LocalDate()).save()

        service.metaClass.static.applyCriteriaOnEcReference = { a, b -> return exchange }

        when:
        def result = service.findECDocument("EC0001")
        then:
        result?.requestNo == "EC0001"
    }


    def "test for retrieveExchangeData method should return Exchange Ec instance"() {
        given:
        Exchange exchange = new Exchange(requestNo: "EC0001", requestType: ExchangeRequestType.EC, exporterCode: "0020136Z", status: Statuses.ST_APPROVED, currencyCode: "EURO"
                , geoArea: "003", areaPartyCode: "003", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")
                , declarationNumber: "123", declarationSerial: "C", clearanceOfficeCode: "CIAB", declarationDate: new LocalDate()).save()
        service.metaClass.static.findECDocument = { a -> exchange }
        when:
        def result = service.retrieveExchangeData("EC0001", "0020136Z")
        then:
        result.error == false
        result.criteria?.requestNo == "EC0001"
    }

    private static initMessageSource(messageSource) {

        messageSource.addMessage('repatriation.exporterCode.error', Locale.US, "exporterCode")
        messageSource.addMessage('repatriation.AreaGeo.error', Locale.US, "AreaGeo")
        messageSource.addMessage('repatriation.currency.error', Locale.US, "currencyCode")
        messageSource.addMessage('repatriation.status.error', Locale.US, "status")
        messageSource.addMessage('repatriation.correctEc.error', Locale.US, "correctEc")
        messageSource.addMessage('repatriation.exchangeDeclaration.error', Locale.US, "declaration")

        messageSource.addMessage('repatriation.exporterCode.error', Locale.FRANCE, "exporterCode")
        messageSource.addMessage('repatriation.AreaGeo.error', Locale.FRANCE, "AreaGeo")
        messageSource.addMessage('repatriation.currency.error', Locale.FRANCE, "currencyCode")
        messageSource.addMessage('repatriation.status.error', Locale.FRANCE, "status")
        messageSource.addMessage('repatriation.correctEc.error', Locale.FRANCE, "correctEc")
        messageSource.addMessage('repatriation.exchangeDeclaration.error', Locale.FRANCE, "declaration")
    }

    @ConfineMetaClassChanges(Exchange)
    def "test updateEcBalanceAndStatus"() {
        given:
        Repatriation repatriation = new Repatriation(
                requestNumberSequence: '1', requestYear: '2020', requestNo: 'REP00123', status: Statuses.ST_DECLARED,
        )

        def exchange = new Exchange(requestNo: "001", balanceAs: new BigDecimal(1000.00), status: Statuses.ST_APPROVED)
        Exchange.metaClass.static.findByRequestNo = { it ->
            return exchange
        }
        repatriation.addClearanceOfDom(new ClearanceOfDom(rank: 1, ecReference: "001", repatriatedAmtInCurr: new BigDecimal(200.00)))
        def commitOperation = Operation.DECLARE

        when:
        service.updateEcBalanceAndStatus(repatriation, commitOperation)

        then:
        repatriation.clearances.get(0)?.ecReference == "001"
        exchange?.status == Statuses.ST_EXECUTED
        exchange?.balanceAs == new BigDecimal(800.00)
    }

    void "test setNewBalanceAndStatus method()"() {
        given:
        Exchange exchange = new Exchange(requestNo: '001', status: Statuses.ST_APPROVED, declarantCode: '1234', importerCode: '0001', balanceAs: new BigDecimal(1000.00))
        ClearanceOfDom clearanceOfDom = new ClearanceOfDom(rank: 1, ecReference: "001", repatriatedAmtInCurr: new BigDecimal(200.00))
        when:
        service.setNewBalanceAndStatus(clearanceOfDom, exchange)
        then:
        exchange.hasErrors() == false
        exchange?.status == Statuses.ST_EXECUTED
        exchange?.balanceAs == new BigDecimal(800.00)
    }

    def "test loadRepatriationFromParams()"() {
        given:
        def params = [repatriationNo: "001", repatriationDate: "10/02/2021"]

        Repatriation repatriation = new Repatriation(id: 1, requestNo: "001", requestDate: LocalDate.parse("10/02/201" as String, DateTimeFormat.forPattern("dd/MM/yyyy")), repatriationBankCode: "SGB1", repatriationBankName: "S.G.B.C.I",
                currencyCode: "EUR", currencyName: "EURO", currencyRate: new BigDecimal("655.957"), importerCode: "1331590S", receivedAmount: new BigDecimal("1500"),
                receivedAmountNat: new BigDecimal("2000"), status: Statuses.ST_CONFIRMED)

        Repatriation.metaClass.static.findByRequestNoAndRequestDate = { a, b -> return repatriation }

        when:
        def currencyTransfer
        currencyTransfer = service.loadRepatriationFromParams(params)

        then:
        currencyTransfer?.bankCode == repatriation?.repatriationBankCode
        currencyTransfer?.bankName == repatriation?.repatriationBankName
        currencyTransfer?.currencyCode == repatriation?.currencyCode
        currencyTransfer?.currencyName == repatriation?.currencyName
        currencyTransfer?.currencyRate == repatriation?.currencyRate
        currencyTransfer?.amountTransferred == BigDecimal.ZERO
        currencyTransfer?.amountTransferredNat == BigDecimal.ZERO
        currencyTransfer?.amountRepatriated == repatriation?.receivedAmount
        currencyTransfer?.amountRepatriatedNat == repatriation?.receivedAmountNat
        currencyTransfer?.hasErrors() == false
    }

    @Ignore
    def "test handleSetCancelRepatriationClearanceOfDom()"() {
        given:
        Repatriation repatriation = new Repatriation()

        repatriation.clearances = [
                new ClearanceOfDom(ecReference: "EC2020010039", ecDate: new LocalDate(), domiciliaryBank: "ECO1", domiciliationNo: "NUM96", domiciliationDate: new LocalDate(), domAmtInCurr: new BigDecimal("5000"), invFinalAmtInCurr: new BigDecimal("5000"), repatriatedAmtInCurr: new BigDecimal("500"))
        ]

        Exchange.metaClass.static.findByRequestNo = { return retrieveEC }

        when:
        service.handleSetCancelRepatriationClearanceOfDom(repatriation)

        then:
        retrieveEC.balanceAs == new BigDecimal("1500")

        where:
        retrieveEC << new Exchange(requestNo: "EC2020010039", balanceAs: new BigDecimal("1000"))
    }

    @Unroll
    void "Test updatePrefinancingRepatriation()"() {
        given:
        initializeExchangeEc()
        Exchange exchangeResult = new Exchange()
        Repatriation repatriation = new Repatriation()
        repatriation.clearances = domiciliations

        when:
        service.updatePrefinancingRepatriation(repatriation)
        repatriation.clearances.findAll { ClearanceOfDom dom ->
            Exchange exchange = Exchange.findByRequestNo(dom.ecReference)
            if (exchange) {
                exchangeResult?.balanceAs = exchange.balanceAs
                exchangeResult?.status = exchange.status
            }
        }
        then:
        exchangeResult.balanceAs == solde
        exchangeResult.status == finalStatus
        where:
        domiciliations << [
                [new ClearanceOfDom(ecReference: "EC2022001", repatriatedAmtInCurr: new BigDecimal("1000"))],
                [new ClearanceOfDom(ecReference: "EC20220045", repatriatedAmtInCurr: new BigDecimal("3000"))],
        ]
        solde << [new BigDecimal("4000"), new BigDecimal("5000")]
        finalStatus << [Statuses.ST_EXECUTED, Statuses.ST_EXECUTED]
    }

    static initializeExchangeEc() {
        exchangeData.each { item -> initExchangeEc(item) }
    }

    private static exchangeData = [
            [requestNo: "EC2022001", status: Statuses.ST_APPROVED, balanceAs: new BigDecimal("5000"), amountMentionedCurrency: new BigDecimal("5000"), amountNationalCurrency: new BigDecimal("5000")],
            [requestNo: "EC20220045", status: Statuses.ST_APPROVED, balanceAs: new BigDecimal("8000"), amountMentionedCurrency: new BigDecimal("5000"), amountNationalCurrency: new BigDecimal("5000")],

    ]

    private static initExchangeEc(Map attrs) {
        def data = attrs.collectEntries { it }
        Exchange exchange = new Exchange(data)
        exchange.save(flush: true, failOnError: true)
    }

    @Unroll
    void "Test updateEcDeleted"() {
        given:
        List<ClearanceOfDom> clearancesBeforeDelete = domiciliationsBeforeDelete()

        service.sessionStoreService = Stub(SessionStore) {
            get(_) >> clearancesBeforeDelete
        }
        service.loggerService = Stub(LoggerService) {
            saveDocumentHistory(_) >> true
        }

        Exchange exchange = new Exchange(requestNo: "EC001", status: Statuses.ST_EXECUTED, balanceAs: new BigDecimal("900"))

        ClearanceOfDom.metaClass.static.findAllByEcReferenceAndIdNotEqual = {a, b -> return clearancesFound }
        Exchange.metaClass.static.findByRequestNo = {a -> return exchange  }


        Repatriation repatriation = new Repatriation(requestNo: "REP001")
        ClearanceOfDom domiciliation = new ClearanceOfDom(ecReference: "EC002")
        domiciliation?.id = 2
        repatriation?.addToClearances(domiciliation)

        when:
        service.updateEcDeleted(repatriation)

        then:
        exchange?.balanceAs == repatriatedAmtInCurr
        exchange?.status == status

        where:
        clearancesFound                             | status               | repatriatedAmtInCurr
        initializeClearance(Statuses.ST_DECLARED)   | Statuses.ST_EXECUTED | new BigDecimal("900")
        initializeClearance(Statuses.ST_STORED)     | Statuses.ST_APPROVED | new BigDecimal("1000")
        null                                        | Statuses.ST_EXECUTED | new BigDecimal("900")
        new ArrayList<ClearanceOfDom>()             | Statuses.ST_APPROVED | new BigDecimal("1000")
    }

    private static List<ClearanceOfDom> initializeClearance(status){
        Repatriation repatriation = new Repatriation(status : status)
        List<ClearanceOfDom> clearances = new ArrayList<>()
        clearances.add(new ClearanceOfDom(ecReference: "EC001", repats: repatriation))
        clearances
    }

    private static List<ClearanceOfDom> domiciliationsBeforeDelete(){
        List<ClearanceOfDom> clearancesBeforeDelete = new ArrayList<>()
        ClearanceOfDom clearance1 = new ClearanceOfDom(ecReference: "EC001", repatriatedAmtInCurr: new BigDecimal("100"))
        clearance1?.id = 1
        ClearanceOfDom clearance2 = new ClearanceOfDom(ecReference: "EC002", repatriatedAmtInCurr: new BigDecimal("200"))
        clearance1?.id = 2
        clearancesBeforeDelete.add(clearance1)
        clearancesBeforeDelete.add(clearance2)
        clearancesBeforeDelete
    }

}
