package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.XmlService
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.concurrent.SynchronizationService
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RepatriationControllerSpec extends Specification implements ControllerUnitTest<RepatriationController>, DataTest {
    def setup() {
        mockDomains(Exchange)
        mockDomains(Repatriation)
    }

    def "test retrieveExchangeReference() functionality"() {
        given:
        Exchange exchange = new Exchange(requestDate: new LocalDate(),
                bankCode: "SGBCI", bankName: "SGBCI", registrationNumberBank: "000123", registrationDateBank: new LocalDate(),
                dateOfBoarding: new LocalDate(), currencyCode: "XOF", geoArea: "001", amountNationalCurrency: new BigDecimal("5000.00"),
                amountMentionedCurrency: new BigDecimal("5000.00"), finalAmountInDevise: new BigDecimal("5000.00"),
                balanceAs: new BigDecimal("5000.00")).save()


        def repatriationService = Stub(RepatriationService) {
            retrieveExchangeData(*_) >> {
                return [error: false, criteria: exchange]
            }

        }
        controller.repatriationService = repatriationService
        controller.params.conversationId = 'test'

        when:
        controller.retrieveExchangeReference()
        then:
        response.getContentType() == "application/json;charset=UTF-8"

    }

    void "Test for Import Repatriation"() {
        given:

        Repatriation repatriationInstance = new Repatriation(natureOfFund: "Repatriation", code: "0101142A",
                nameAndAddress: "BOLLORE TRANSPORT &amp; LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI",
                repatriationBankCode: "SGB1", repatriationBankName: "S.G.B.C.I", currencyCode: "EUR", currencyName: "Euro",
                currencyRate: new BigDecimal(655.957), receivedAmount: new BigDecimal(100),
                receivedAmountNat: new BigDecimal(327978.5), receptionDate: TypeCastUtils.toLocalDate("23/10/2020"),
                countryOfOriginCode: "FR", countryOfOriginName: "France", bankOfOriginCode: "BNP",
                bankOfOriginName: "BNP Paribas")

        controller.xmlService = [importFileXml: { a, b -> repatriationInstance }]
        controller.repatBusinessLogicService = [initDocumentForCreate: { a -> true }]
        controller.repatriationService = [addToSessionStore: { a -> true }]
        controller.params.conversationId = 'test'
        controller.params.domainName = 'Repatriation'
        when:
        Repatriation.withTransaction {
            controller.importXML()
        }
        then:
        repatriationInstance.natureOfFund == "Repatriation"
        repatriationInstance.code == "0101142A"
        repatriationInstance.nameAndAddress == "BOLLORE TRANSPORT &amp; LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI"
        repatriationInstance.repatriationBankCode == "SGB1"
        repatriationInstance.repatriationBankName == "S.G.B.C.I"
        repatriationInstance.currencyCode == "EUR"
        repatriationInstance.currencyName == "Euro"
        repatriationInstance.currencyRate == new BigDecimal(655.957)
        repatriationInstance.receivedAmount == new BigDecimal(100)
        repatriationInstance.receivedAmountNat == new BigDecimal(327978.5)
        repatriationInstance.receptionDate == TypeCastUtils.toLocalDate("23/10/2020")
        repatriationInstance.countryOfOriginCode == "FR"
        repatriationInstance.countryOfOriginName == "France"
        repatriationInstance.bankOfOriginCode == "BNP"
        repatriationInstance.bankOfOriginName == "BNP Paribas"
    }

    void "Test for Export Repatriation"() {
        given:
        controller.params.domainName = mockDomainName
        controller.xmlService = Mock(XmlService)

        when:
        controller.exportXML()

        then:
        called * controller.xmlService.exportDomainToXml(controller.params.domainName, _)

        where:
        mockDomainName                           | called
        UtilConstants.EXCHANGE.toUpperCase()     | 0
        UtilConstants.REPATRIATION.toUpperCase() | 1
    }

    static String sampleRepatriationXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repatriation>\n" +
                "  <requestNo>2020000058</requestNo>\n" +
                "  <requestDate>23/10/2020</requestDate>\n" +
                "  <natureOfFund>Repatriation</natureOfFund>\n" +
                "  <code>0101142A</code>\n" +
                "  <amountTransferredNat/>\n" +
                "  <amountRemainingNat/>\n" +
                "</repatriation>\n"
    }

    def "test update() functionality"() {

        given:
        controller.docVerificationService = [deepVerify: { repatriation -> true }]
        controller.repatriationService = [findFromSessionStore: { a -> new Repatriation(requestNo: '2000123') }, doPersist: { a, b -> new Repatriation(id: 999, requestNo: '2000123') }]
        controller.synchronizationService = Stub(SynchronizationService)
        when:
        controller.update()

        then:
        flash.repatriationInstanceToShow.requestNo == '2000123'

    }

    def "test save() functionality"() {

        given:
        controller.params.commitOperation = Operation.STORE
        controller.docVerificationService = [deepVerify: { repatriation -> true }, removeAllErrors: { repatriation -> true }]
        controller.repatriationService = [findFromSessionStore: { a -> new Repatriation(requestNo: '2000123') }, doPersist: { a, b -> new Repatriation(id: 999, requestNo: '2000123') }]
        controller.synchronizationService = Stub(SynchronizationService)
        when:
        controller.save()

        then:
        flash.repatriationInstanceToShow.requestNo == '2000123'

    }
}
