package com.webbfontaine.efem

import com.webbfontaine.efem.attachedDoc.RepAttachedDoc
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.RepatriationService
import com.webbfontaine.efem.attachedDoc.TransferAttachedDoc
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import groovy.util.slurpersupport.GPathResult
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification


class XmlServiceSpec extends Specification implements ServiceUnitTest<XmlService>, DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
        mockDomain(Repatriation)
        Repatriation repatriation = new Repatriation(id: "1", natureOfFund: "Repatriation", code: "0101142A",
                nameAndAddress: "BOLLORE TRANSPORT AND LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI",
                repatriationBankCode: "SGB1", repatriationBankName: "S.G.B.C.I", currencyCode: "EUR", currencyName: "Euro",
                currencyRate: new BigDecimal(655.957), receivedAmount: new BigDecimal(500),
                receivedAmountNat: new BigDecimal(327978.5), receptionDate: TypeCastUtils.toLocalDate("23/10/2020"),
                countryOfOriginCode: "FR", countryOfOriginName: "France", bankOfOriginCode: "BNP",
                bankOfOriginName: "BNP Paribas")
        repatriation?.clearances = [
                new ClearanceOfDom(rank: 1, ecReference: "EC2020000056", ecDate: TypeCastUtils.toLocalDate("23/10/2020"), domiciliaryBank: "S.G.B.C.I", domiciliationNo: "SGB-123",
                        domiciliationDate: TypeCastUtils.toLocalDate("23/10/2020"), domAmtInCurr: new BigDecimal("1500"), invFinalAmtInCurr: new BigDecimal("1500"), repatriatedAmtInCurr: new BigDecimal("500"))
        ]
        repatriation.attachedDocs = [new RepAttachedDoc(rank: 1, docType: "WF08", docTypeName: "AVIS DE CREDIT OU RELEVE DE COMPTE", docRef: "12369-89", docDate: TypeCastUtils.toLocalDate("21/10/2020"))]
        repatriation.save(flush: true)
        mockDomain(TransferOrder)
    }

    void "Test for Import currency transfer"() {
        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()

        when:
        service.buildCurrencyTransfer(currencyTransfer, readXml(sampleCurrencyTransferXML()))

        then:
        currencyTransfer.bankCode == "ECO1"
        currencyTransfer.amountTransferred == new BigDecimal("2500")
    }

    void "Test for Import transfer order"() {
        given:
        TransferOrder transferOrder = new TransferOrder()
        GroovyMock(UserUtils, global: true)
        when:
        service.buildTransferOrder(transferOrder, XMLFileInput())

        then:
        transferOrder.importerCode == "0101142A"
        transferOrder.importerNameAddress == "BOLLORE TRANSPORT & LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI"
        transferOrder.destinationBank == "BNP"
        transferOrder.charges == "All charges to be paid by the ordering party"
        transferOrder.currencyPayCode == "EUR"
        transferOrder.bankCode == "SGB1"
        transferOrder.countryBenefBankCode == "FR"
        transferOrder.byCreditOfAccntOfCorsp == "FRANCE"
        transferOrder.bankAccntNoCredit == "456987-89"
        transferOrder.nameOfAccntHoldCredit == "alcom"
        transferOrder.bankAccntNoDebited == "65478"
        transferOrder.orderClearanceOfDoms[0].eaReference == "EA2020000010"
        transferOrder.orderClearanceOfDoms[0].authorizationDate == ReferenceUtils.gPathToLocalDate("29/09/2020")
        transferOrder.orderClearanceOfDoms[0].bankName == "S.G.B.C.I"
        transferOrder.orderClearanceOfDoms[0].amountToBeSettledMentionedCurrency == new BigDecimal("1500")
        transferOrder.orderClearanceOfDoms[0].registrationDateBank == ReferenceUtils.gPathToLocalDate("29/09/2020")
        transferOrder.orderClearanceOfDoms[1].eaReference == "EA2020000019"
        transferOrder.orderClearanceOfDoms[1].bankName == "BIAO"
        transferOrder.attachedDocs[0].docRef == "123485"
        transferOrder.attachedDocs[0].docType == "WF04"
        transferOrder.attachedDocs[0].docTypeName == "COPIE DES 10 DERNIERS D'ESCALES"
        transferOrder.attachedDocs[0].docDate == ReferenceUtils.gPathToLocalDate("04/08/2020")
    }

    void "Test export transfer order with buildTransferOrderXMLFile()"() {
        given:
        GroovyMock(UserUtils, global: true)
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.importerCode = "0101142A"
        transferOrder.importerNameAddress = "BOLLORE TRANSPORT & LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI"
        transferOrder.destinationBank = "BNP"
        transferOrder.charges = "All charges to be paid by the ordering party"
        transferOrder.currencyPayCode = "EUR"
        transferOrder.bankCode = "SGB1"
        transferOrder.countryBenefBankCode = "FR"
        transferOrder.byCreditOfAccntOfCorsp = "FRANCE"
        transferOrder.bankAccntNoCredit = "456987-89"
        transferOrder.nameOfAccntHoldCredit = "alcom"
        transferOrder.bankAccntNoDebited = "65478"
        transferOrder?.orderClearanceOfDoms =[
                new OrderClearanceOfDom(eaReference: "EA2020000010", authorizationDate: ReferenceUtils.gPathToLocalDate("29/09/2020"), bankName: "S.G.B.C.I", amountSettledMentionedCurrency: new BigDecimal("1500"), registrationDateBank: ReferenceUtils.gPathToLocalDate("29/09/2020")),
                new OrderClearanceOfDom(eaReference: "EA2020000019", authorizationDate: ReferenceUtils.gPathToLocalDate("15/09/2020"), bankName: "BIAO", amountSettledMentionedCurrency: new BigDecimal("3000"), registrationDateBank: ReferenceUtils.gPathToLocalDate("15/09/2020"))
        ]
        transferOrder.attachedDocs = [new TransferAttachedDoc(docRef: "123485", docType: "WF04", docTypeName: "COPIE DES 10 DERNIERS D'ESCALES", docDate: ReferenceUtils.gPathToLocalDate("04/08/2020"))]

        when:
        def result = service.buildTransferOrderXMLFile(transferOrder)
        then:
        result.toString() == exportTransferOrderXMLResult()
    }

    def readXml(xml) {
        GPathResult xmlResult = new XmlSlurper().parseText(new String(xml))

        return xmlResult
    }


    static String sampleCurrencyTransferXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><CURRENCY_TRANSFER>\n" +
                "<header>\n" +
                "    <bank_code>ECO1</bank_code>\n" +
                "    <bank_name>ECOBANK-COTE D'IVOIRE</bank_name>\n" +
                "    <currency_code>EUR</currency_code>\n" +
                "    <currency_name>Euro</currency_name>\n" +
                "    <currency_rate>655.957</currency_rate>\n" +
                "    <amount_transferred>2500.00</amount_transferred>\n" +
                "    <amount_transferred_nat>1639892.50</amount_transferred_nat>\n" +
                "    <currency_transfer_date>07/08/2020</currency_transfer_date>\n" +
                "</header>\n" +
                "<clearance_domiciliations>\n" +
                "    <domiciliation>\n" +
                "      <ec_reference>EC2020</ec_reference>\n" +
                "      <ec_date>14/07/2020</ec_date>\n" +
                "      <domiciliation_code_bank>SGB1</domiciliation_code_bank>\n" +
                "      <domiciliation_no>SGB/POIN</domiciliation_no>\n" +
                "      <domiciliation_date/>\n" +
                "      <domiciliated_amountt_in_curr>1000</domiciliated_amountt_in_curr>\n" +
                "      <invoice_final_amount_in_curr/>\n" +
                "      <repatriated_amount_to_bank/>\n" +
                "      <amount_transferred_in_curr>1400</amount_transferred_in_curr>\n" +
                "    </domiciliation>\n" +
                "</clearance_domiciliations>\n" +
                "<attachments>\n" +
                "    <attachment>\n" +
                "      <doc_ref>123485</doc_ref>\n" +
                "      <doc_type>WF04</doc_type>\n" +
                "      <doc_type_name>COPIE DES 10 DERNIERS D'ESCALES</doc_type_name>\n" +
                "      <doc_date>04/08/2020</doc_date>\n" +
                "    </attachment>\n" +
                "</attachments>\n" +
                "</CURRENCY_TRANSFER>\n"
    }

    void "test import xml file"() {
        given:
        MockMultipartHttpServletRequest requestFile = new MockMultipartHttpServletRequest()
        service.referenceService = Mock(ReferenceService)
        service.xmlValidationService = Mock(XmlValidationService)

        MockMultipartFile multipartFile = new MockMultipartFile('xmlFile', 'file.xml', 'text/xml', sampleDomainXML() as byte[])
        requestFile.addFile(multipartFile)

        Repatriation repatriation = new Repatriation();
        def domainName = UtilConstants.REPATRIATION
        when:
        repatriation = service.importFileXml(requestFile, domainName)

        then:
        repatriation.natureOfFund == "Repatriation"
        repatriation.code == "0101142A"
        repatriation.nameAndAddress == "BOLLORE TRANSPORT AND LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI"
        repatriation.repatriationBankCode == "SGB1"
        repatriation.repatriationBankName == "S.G.B.C.I"
        repatriation.currencyCode == "EUR"
        repatriation.currencyName == "Euro"
        repatriation.currencyRate == new BigDecimal("655.957")
        repatriation.receivedAmount == new BigDecimal("500")
        repatriation.receivedAmountNat == new BigDecimal("327978.5")
        repatriation.receptionDate == TypeCastUtils.toLocalDate("23/10/2020")
        repatriation.countryOfOriginCode == "FR"
        repatriation.countryOfOriginName == "France"
        repatriation.bankOfOriginCode == "BNP"
        repatriation.bankOfOriginName == "BNP Paribas"

    }

    void "test export xml file"() {
        given:
        service.repatriationService = Mock(RepatriationService)
        service.exchangeService = Mock(ExchangeService)
        MockHttpServletRequest response = new MockHttpServletRequest()
        GrailsWebRequest webRequest = new GrailsWebRequest(response, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        response.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.id = 1
        RequestContextHolder.setRequestAttributes(webRequest)

        def domainName = UtilConstants.REPATRIATION.toUpperCase()
        when:
        def xmlContent = service.exportDomainToXml(domainName, webRequest)

        then:

        readXml(xmlContent) == readXml(sampleDomainXML())
    }


    static String sampleDomainXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repatriation>\n" +
                "<requestNo/>\n" +
                "<requestDate/>\n" +
                "<natureOfFund>Repatriation</natureOfFund>\n" +
                "<code>0101142A</code>\n" +
                "<nameAndAddress>BOLLORE TRANSPORT AND LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI</nameAndAddress>\n" +
                "<repatriationBankCode>SGB1</repatriationBankCode>\n" +
                "<repatriationBankName>S.G.B.C.I</repatriationBankName>\n" +
                "<currencyCode>EUR</currencyCode>\n" +
                "<currencyRate>655.957</currencyRate>\n" +
                "<receivedAmountNat>327978.50</receivedAmountNat>\n" +
                "<currencyName>Euro</currencyName>\n" +
                "<receivedAmount>500.00</receivedAmount>\n" +
                "<receptionDate>23/10/2020</receptionDate>\n" +
                "<countryOfOriginCode>FR</countryOfOriginCode>\n" +
                "<countryOfOriginName>France</countryOfOriginName>\n" +
                "<bankOfOriginCode>BNP</bankOfOriginCode>\n" +
                "<bankOfOriginName>BNP Paribas</bankOfOriginName>\n" +
                "<bankNotificationDate/>\n" +
                "<executionRef/>\n" +
                "<executionDate/>\n" +
                "<currencyTransfertDate/>\n" +
                "<amountTransferred/>\n" +
                "<amountRemaining/>\n" +
                "<amountTransferredNat/>\n" +
                "<amountRemainingNat/>\n" +
                "<clearances>\n" +
                "<itemClearances>\n" +
                "<rank>1</rank>\n" +
                "<ecReference>EC2020000056</ecReference>\n" +
                "<ecDate>23/10/2020</ecDate>\n" +
                "<domiciliaryBank>S.G.B.C.I</domiciliaryBank>\n" +
                "<domiciliationNo>SGB-123</domiciliationNo>\n" +
                "<domiciliationDate>23/10/2020</domiciliationDate>\n" +
                "<dateOfBoarding/>\n" +
                "<domAmtInCurr>1500</domAmtInCurr>\n" +
                "<invFinalAmtInCurr>1500</invFinalAmtInCurr>\n" +
                "<repatriatedAmtInCurr>500</repatriatedAmtInCurr>\n" +
                "</itemClearances>\n" +
                "</clearances>\n" +
                "<attachedDocs>\n" +
                "<attachment>\n" +
                "<rank>1</rank>\n" +
                "<docType>WF08</docType>\n" +
                "<docTypeName>AVIS DE CREDIT OU RELEVE DE COMPTE</docTypeName>\n" +
                "<docRef>12369-89</docRef>\n" +
                "<docDate>21/10/2020</docDate>\n" +
                "<attachedFile/>\n" +
                "</attachment>\n" +
                "</attachedDocs>\n" +
                "</repatriation>"
    }
    static MultipartFile XMLFileInput() {
        return new MockMultipartFile('xmlFile',  sampleTransferOrderXML() as byte[])
    }

    static String sampleTransferOrderXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TRANSFER_ORDER>\n" +
                "  <header>\n" +
                "    <ea_reference/>\n" +
                "    <importer_code>0101142A</importer_code>\n" +
                "    <importer_name_address>BOLLORE TRANSPORT &amp; LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI</importer_name_address>\n" +
                "    <country_benef_bank_code>FR</country_benef_bank_code>\n" +
                "    <country_benef_bank_name/>\n" +
                "    <destination_bank>BNP</destination_bank>\n" +
                "    <by_credit_account>FRANCE</by_credit_account>\n" +
                "    <bank_account_no_credit>456987-89</bank_account_no_credit>\n" +
                "    <name_account_hold_credit>alcom</name_account_hold_credit>\n" +
                "    <bank_name/>\n" +
                "    <bank_code>SGB1</bank_code>\n" +
                "    <bank_account_no_debit>65478</bank_account_no_debit>\n" +
                "    <charges>All charges to be paid by the ordering party</charges>\n" +
                "    <currency_pay_code>EUR</currency_pay_code>\n" +
                "    <currency_pay_name/>\n" +
                "    <rate_payment/>\n" +
                "  </header>\n" +
                "  <clearance_domiciliations>\n" +
                "    <domiciliation>\n" +
                "      <ea_reference>EA2020000010</ea_reference>\n" +
                "      <authorization_date>29/09/2020</authorization_date>\n" +
                "      <bank_name>S.G.B.C.I</bank_name>\n" +
                "      <registration_no_bank/>\n" +
                "      <registration_date_bank>29/09/2020</registration_date_bank>\n" +
                "      <amount_to_be_settle_mentioned_currency>1500</amount_to_be_settle_mentioned_currency>\n" +
                "    </domiciliation>\n" +
                "    <domiciliation>\n" +
                "      <ea_reference>EA2020000019</ea_reference>\n" +
                "      <authorization_date>30/09/2020</authorization_date>\n" +
                "      <bank_name>BIAO</bank_name>\n" +
                "      <registration_no_bank/>\n" +
                "      <registration_date_bank>30/09/2020</registration_date_bank>\n" +
                "      <amount_to_be_settle_mentioned_currency>1500</amount_to_be_settle_mentioned_currency>\n" +
                "    </domiciliation>\n" +
                "  </clearance_domiciliations>\n" +
                "  <attachments>\n" +
                "    <attachment>\n" +
                "      <doc_ref>123485</doc_ref>\n" +
                "      <doc_type>WF04</doc_type>\n" +
                "      <doc_type_name>COPIE DES 10 DERNIERS D'ESCALES</doc_type_name>\n" +
                "      <doc_date>04/08/2020</doc_date>\n" +
                "    </attachment>\n" +
                "  </attachments>\n" +
                "</TRANSFER_ORDER>\n"
    }

    static String exportTransferOrderXMLResult() {
        return "<TRANSFER_ORDER><header><ea_reference/><importer_code>0101142A</importer_code><importer_name_address>BOLLORE TRANSPORT &amp; LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI</importer_name_address><country_benef_bank_code>FR</country_benef_bank_code><country_benef_bank_name/><destination_bank>BNP</destination_bank><by_credit_account>FRANCE</by_credit_account><bank_account_no_credit>456987-89</bank_account_no_credit><name_account_hold_credit>alcom</name_account_hold_credit><bank_name/><bank_code>SGB1</bank_code><bank_account_no_debit>65478</bank_account_no_debit><charges>All charges to be paid by the ordering party</charges><currency_pay_code>EUR</currency_pay_code><currency_pay_name/><rate_payment/></header><clearance_domiciliations><domiciliation><ea_reference>EA2020000010</ea_reference><authorization_date>29/09/2020</authorization_date><bank_name>S.G.B.C.I</bank_name><registration_no_bank/><registration_date_bank>29/09/2020</registration_date_bank><amount_to_be_settle_mentioned_currency/></domiciliation><domiciliation><ea_reference>EA2020000019</ea_reference><authorization_date>15/09/2020</authorization_date><bank_name>BIAO</bank_name><registration_no_bank/><registration_date_bank>15/09/2020</registration_date_bank><amount_to_be_settle_mentioned_currency/></domiciliation></clearance_domiciliations><attachments><attachment><doc_ref>123485</doc_ref><doc_type>WF04</doc_type><doc_type_name>COPIE DES 10 DERNIERS D'ESCALES</doc_type_name><doc_date>04/08/2020</doc_date></attachment></attachments></TRANSFER_ORDER>"
    }
}
