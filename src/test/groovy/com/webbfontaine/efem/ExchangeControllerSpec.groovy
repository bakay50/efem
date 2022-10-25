package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 8/15/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class ExchangeControllerSpec extends Specification implements ControllerUnitTest<ExchangeController>, DataTest {


    def setup() {
        mockDomains(Exchange)
    }

    @Unroll("Test return of action when #scenario")
    def testCheckMaxAmountInXofCurrency() {
        given:
        controller.exchangeService = [findFromSessionStore: { a -> new Exchange() }, getExchangeList: { a -> [new Exchange()] },
                                      getSumOfAmountNationalCurrency : {a -> mockComputedXof}]
        grailsApplication.config.com.webbfontaine.efem.maxAmountInXofCurrency = "500000.00"
        controller.params.conversationId = 'test'

        when:
        controller.checkMaxAmountInXofCurrency()

        then:
        controller.response.json.isExceed == expected

        where:
        scenario                                           | mockComputedXof | expected
        'totalAmountNationalCurrency do not exceed 500000' | 499999.00       | false
        'totalAmountNationalCurrency exceed 500000'        | 500001.00       | true
    }

    def "test verify() functionality"() {
        given:
        controller.exchangeService = [findFromSessionStore: { a -> new Exchange() }, getExchangeList: { a -> [new Exchange()] }, getConvertedCIF: { a -> 500 }]
        controller.docVerificationService = [deepVerify: { exchange -> true }, documentHasErrors: { exchange -> false }]
        controller.params.conversationId = 'test'

        when:
        controller.verify()

        then:
        controller.modelAndView.model.hasDocErrors == false
        controller.modelAndView.viewName == "/exchange/edit"

    }

    def "test delete() functionality"() {
        given:
        controller.exchangeService = [findFromSessionStore: { a -> new Exchange() }, getExchangeList: { a -> [new Exchange()] }]
        controller.docVerificationService = [deepVerify: { exchange -> true }, documentHasErrors: { exchange -> false }]
        controller.params.conversationId = 'test'

        when:
        controller.delete()

        then:
        flash.message == "default.operation.done.message"

    }

    void "Test for Import Exchange"() {
        given:

        Exchange exchangeInstance = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType.BASE_ON_SAD, clearanceOfficeCode: "CIAB1",
                declarationSerial: "C", declarationNumber: "359", declarationDate: TypeCastUtils.toLocalDate("02/10/2020"),
                countryOfExportCode: "BE", bankCode: "SGB1", bankName: "S.G.C.I", commentHeader: "RAS", exporterCode: "0101142A",
                exporterNameAddress: "BOLLORE TRANSPORT AND LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI", importerCode: "1331590S",
                importerNameAddress: "WEBB FONTAINE COTE D'IVOIRE 04 BP 225 ABIDJAN 04 BIETRY-BVD VGE",
                declarantNameAddress: "CI LOGISTIQUE 18 BP 1395 ABIDJAN 18", declarantCode: "00267T",
                beneficiaryName: "VMD HOGE MAUW 900 2370 ARENDONK BEL", beneficiaryAddress: "GIUM Belgique", nationalityCode: "CI", resident: "yes",
                operType: "EA001", operTypeName: "IMPORTATION EFFECTIVE", currencyCode: "EUR", currencyName: "Euro",
                currencyRate: new BigDecimal(655.957), currencyPayCode: "EUR", currencyPayName: "Euro",
                currencyPayRate: new BigDecimal(655.957), isFinalAmount: false, amountMentionedCurrency: new BigDecimal(25000),
                amountNationalCurrency: new BigDecimal(16398925), balanceAs: new BigDecimal(25000),
                countryProvenanceDestinationCode: "FR", provenanceDestinationBank: "SG", bankAccountNocreditedDebited: "CI108220015427542",
                exportationTitleNo: "001200A", accountNumberBeneficiary: "CI784587000000004")

        controller.xmlService = [importFileXml: { a, b -> exchangeInstance }]
        controller.exchangeService = [addToSessionStore: { a -> true }]
        controller.businessLogicService = [initDocumentForCreate: { a, b -> true }]
        controller.params.conversationId = 'test'
        controller.params.domainName = 'Exchange'
        when:
        Exchange.withTransaction {
            controller.importXML()
        }
        then:
        exchangeInstance.requestType == "EA"
        exchangeInstance.basedOn == "SAD"
        exchangeInstance.clearanceOfficeCode == "CIAB1"
        exchangeInstance.declarationSerial == "C"
        exchangeInstance.declarationNumber == "359"
        exchangeInstance.declarationDate == TypeCastUtils.toLocalDate("02/10/2020")
        exchangeInstance.countryOfExportCode == "BE"
        exchangeInstance.bankCode == "SGB1"
        exchangeInstance.bankName == "S.G.C.I"
        exchangeInstance.commentHeader == "RAS"
        exchangeInstance.exporterCode == "0101142A"
        exchangeInstance.exporterNameAddress == "BOLLORE TRANSPORT AND LOGISTICS 01 BP 1727 ABIDJAN (VILLE) 01 TREICHVILLE-1 AV CHRISTIANI"
        exchangeInstance.importerCode == "1331590S"
        exchangeInstance.importerNameAddress == "WEBB FONTAINE COTE D'IVOIRE 04 BP 225 ABIDJAN 04 BIETRY-BVD VGE"
        exchangeInstance.declarantNameAddress == "CI LOGISTIQUE 18 BP 1395 ABIDJAN 18"
        exchangeInstance.declarantCode == "00267T"
        exchangeInstance.beneficiaryName == "VMD HOGE MAUW 900 2370 ARENDONK BEL"
        exchangeInstance.beneficiaryAddress == "GIUM Belgique"
        exchangeInstance.nationalityCode == "CI"
        exchangeInstance.resident == "yes"
        exchangeInstance.operType == "EA001"
        exchangeInstance.operTypeName == "IMPORTATION EFFECTIVE"
        exchangeInstance.currencyCode == "EUR"
        exchangeInstance.currencyName == "Euro"
        exchangeInstance.currencyRate == new BigDecimal(655.957)
        exchangeInstance.currencyPayCode == "EUR"
        exchangeInstance.currencyPayName == "Euro"
        exchangeInstance.currencyPayRate == new BigDecimal(655.957)
        exchangeInstance.amountMentionedCurrency == new BigDecimal(25000)
        exchangeInstance.amountNationalCurrency == new BigDecimal(16398925)
        exchangeInstance.balanceAs == new BigDecimal(25000)
        exchangeInstance.isFinalAmount == false
        exchangeInstance.countryProvenanceDestinationCode == "FR"
        exchangeInstance.provenanceDestinationBank == "SG"
        exchangeInstance.bankAccountNocreditedDebited == "CI108220015427542"
        exchangeInstance.exportationTitleNo == "001200A"
        exchangeInstance.accountNumberBeneficiary == "CI784587000000004"
    }

    void "Test for Export Exchange"() {
        given:
        controller.params.domainName = mockDomainName
        controller.xmlService = Mock(XmlService)

        when:
        controller.exportXML()

        then:
        called * controller.xmlService.exportDomainToXml(controller.params.domainName, _)

        where:
        mockDomainName                           | called
        UtilConstants.EXCHANGE.toUpperCase()     | 1
        UtilConstants.REPATRIATION.toUpperCase() | 0
    }

    @Unroll
    void "test retrieveNotification()"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBank
        UserUtils.isTrader() >> isTrader
        UserUtils.getUserProperty(property) >> userProp
        controller.notificationService = Stub(NotificationService) {
            handleQueryNotification(*_) >> 33
        }

        when:
        controller.retrieveNotificationCount(userProp, property)
        controller.retrieveNotification()

        then:
        controller.response.text.contains('"numberNotif":')
        controller.response.json.numberNotif == 33

        where:
        userProp   | isBank | isTrader | property
        "SGB1"     | true   | false    | UserProperties.ADB
        "0101142A" | false  | true     | UserProperties.TIN
    }

    static String sampleExchangeXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange>\n" +
                "  <requestType>EA</requestType>\n" +
                "  <countryOfExportCode>FR</countryOfExportCode>\n" +
                "  <bankCode>SGB1</bankCode>\n" +
                "  <bankName>S.G.B.C.I</bankName>\n" +
                "  <domiciliationNumber>DEZ-12</domiciliationNumber>\n" +
                "  <importerCode>0101142A</importerCode>\n" +
                "  <amountMentionedCurrency>1000</amountMentionedCurrency>\n" +
                "  <amountNationalCurrency>655957</amountNationalCurrency>\n" +
                "  <balanceAs>1000</balanceAs>\n" +
                "</exchange>\n"
    }

}
