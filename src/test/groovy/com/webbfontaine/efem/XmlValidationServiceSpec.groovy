package com.webbfontaine.efem

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import groovy.util.slurpersupport.GPathResult
import spock.lang.Specification

class XmlValidationServiceSpec extends Specification implements ServiceUnitTest<XmlValidationService>, DataTest {

    void "Test validateContent"() {
        given:
        String docType = "Exchange"

        when:
        def error = service.validateContent(readXml(sampleExchangeXML()),docType)

        then:
        error == null
    }

    void "Test checkXmlToImport"() {

        given:
        Exchange exchangeInstance = new Exchange()

        when:
        service.checkXmlToImport(exchangeInstance,readXml(sampleExchangeXML(bankCode)))

        then:
        exchangeInstance?.errors.allErrors?.code[0] == expectedError

        where:
        bankCode  | expectedError
        "SGB1"    | "importXML.invalid.bank"
        "SIB"     | null
    }

    def readXml(xml) {
        GPathResult xmlResult = new XmlSlurper().parseText(new String(xml))

        return xmlResult
    }


    static String sampleExchangeXML(String bankCode = "SGB1") {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange>\n" +
                "  <requestType>EA</requestType>\n" +
                "  <basedOn>TVF</basedOn>\n" +
                "  <clearanceOfficeCode/>\n" +
                "  <declarationSerial/>\n" +
                "  <declarationNumber/>\n" +
                "  <clearanceOfficeName/>\n" +
                "  <declarationDate/>\n" +
                "  <tvfNumber>516</tvfNumber>\n" +
                "  <tvfDate>05/11/2020</tvfDate>\n" +
                "  <dateOfBoarding/>\n" +
                "  <geoArea/>\n" +
                "  <geoAreaName/>\n" +
                "  <countryOfDestinationCode/>\n" +
                "  <countryOfDestinationName/>\n" +
                "  <countryOfExportCode>FR</countryOfExportCode>\n" +
                "  <bankCode>"+bankCode+"</bankCode>\n" +
                "  <bankName>S.G.B.C.I</bankName>\n" +
                "  <domiciliationNumber>SDFSFSDF</domiciliationNumber>\n" +
                "  <domiciliationDate>05/11/2020</domiciliationDate>\n" +
                "  <domiciliationBankCode>"+bankCode+"</domiciliationBankCode>\n" +
                "  <authorizationDate/>\n" +
                "  <authorizedBy/>\n" +
                "  <commentHeader/>\n" +
                "  <exporterCode/>\n" +
                "  <exporterNameAddress/>\n" +
                "  <importerCode>0101142A</importerCode>\n" +
                "  <importerNameAddress/>\n" +
                "  <declarantNameAddress/>\n" +
                "  <declarantCode>00069Z</declarantCode>\n" +
                "  <beneficiaryName>DUPONT-CLAIRE</beneficiaryName>\n" +
                "  <beneficiaryAddress/>\n" +
                "  <nationalityCode>CI</nationalityCode>\n" +
                "  <resident>yes</resident>\n" +
                "  <operType>EA001</operType>\n" +
                "  <operTypeName>IMPORTATION EFFECTIVE</operTypeName>\n" +
                "  <currencyCode>EUR</currencyCode>\n" +
                "  <currencyName>Euro</currencyName>\n" +
                "  <currencyRate>655.957</currencyRate>\n" +
                "  <currencyPayCode/>\n" +
                "  <currencyPayName/>\n" +
                "  <currencyPayRate>655.957</currencyPayRate>\n" +
                "  <isFinalAmount>false</isFinalAmount>\n" +
                "  <finalAmountInDevise/>\n" +
                "  <finalAmount/>\n" +
                "  <goodsValuesInXOF/>\n" +
                "  <exFeesPaidByExpInCIinXOF/>\n" +
                "  <exFeesPaidByExpInAbroadinXOF/>\n" +
                "  <amountMentionedCurrency>1</amountMentionedCurrency>\n" +
                "  <amountNationalCurrency>655.95</amountNationalCurrency>\n" +
                "  <balanceAs>1</balanceAs>\n" +
                "  <countryProvenanceDestinationCode>CF</countryProvenanceDestinationCode>\n" +
                "  <provenanceDestinationBank>xxxxx</provenanceDestinationBank>\n" +
                "  <bankAccountNocreditedDebited>xxx</bankAccountNocreditedDebited>\n" +
                "  <exportationTitleNo/>\n" +
                "  <accountNumberBeneficiary>xxxx</accountNumberBeneficiary>\n" +
                "  <attachedDocs>\n" +
                "    <attachment>\n" +
                "      <rank>1</rank>\n" +
                "      <docType>0007</docType>\n" +
                "      <docTypeName>FACTURE</docTypeName>\n" +
                "      <docRef>dddd</docRef>\n" +
                "      <docDate>13/11/2020</docDate>\n" +
                "      <attachedFile>com.webbfontaine.efem.attachedDoc.AttachedFile : 3357</attachedFile>\n" +
                "    </attachment>\n" +
                "  </attachedDocs>\n" +
                "</exchange>\n"
    }

}
