package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.rimm.RimmOpt
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.sw.rimm.RimmSadGeneralSegement
import org.grails.datastore.mapping.core.connections.ConnectionSource
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import com.webbfontaine.sw.rimm.RimmSadDetails
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.util.Holders
import grails.web.context.ServletContextHolder
import org.grails.datastore.mapping.simple.SimpleMapDatastore
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Unroll

class SadServiceSpec extends Specification implements ServiceUnitTest<SadService>, DataTest {

    def setup() {
        mockDomain Exchange
    }

    @Unroll
    def "#testCase"() {
        given:
        Exchange exchange = new Exchange()
        def sadData = [consigneeCode: "CC1", declarantCode: "DC2"]
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> depProp
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> tinProp
        UserUtils.getUserProperty(UserProperties.DEC) >> UserProperties.ALL
        UserUtils.getUserProperty(UserProperties.TIN) >> UserProperties.ALL

        when:
        def result = service.checkSadOwner(exchange, sadData)

        then:
        result == expected
        exchange.isDocumentValid == expected

        where:
        testCase                                      | expected | depProp        | tinProp
        "sad is owned by user"                        | true     | ["DC1", "DC2"] | ["CC1", "CC2"]
        "sad is owned by user"                        | false    | ["DC1", "DC3"] | ["CC3", "CC2"]
        "User has correct DEC but TIN is not in list" | false    | ["DC2"]        | ["CC3"]
        "User has correct TIN but DEC is not in list" | false    | ["DC4"]        | ["CC1"]
        "Dec and Tin is incorrect"                    | false    | ["DC8"]        | ["CC8"]
        "DEC is ALL and Tin is ALL"                   | true     | []             | []
        "DEC is ALL and tin is correct"               | true     | []             | ["CC1"]
        "DEC is ALL and tin is correct"               | false    | []             | ["CC2"]
        "DEC is ALL but tin is not in list"           | false    | []             | ["CC5"]
        "Tin is ALL and Dec is correct"               | true     | ["DC2"]        | []
        "Tin is all but dec is not in list"           | false    | ["DC5"]        | []
    }

    @Unroll
    def "Check if eforex can load sad by type of declaration when declaration is #decl"() {
        given:
        Exchange exchange = new Exchange()
        def sadData = [typeOfDeclaration: decl]

        when:
        def result = service.checkSadForEA(exchange, sadData, "EA")

        then:
        result == expected
        exchange.isDocumentValid == expected

        where:
        decl | expected
        "IM" | true
        "EX" | false
    }

    def "test retrieveExchangeFromSad"() {
        given:
        SimpleMapDatastore sydamviewsStore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "sydamviews"], RimmSadDetails)
        SimpleMapDatastore rimmStore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "rimm"], RimmOpt)
        GroovyMock(UserUtils, global: true)
        Holders.config.rest?.isWebService = false
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> ["00267T"]
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> ["1331590S"]
        mockDomains(Exchange)
        defineBeans {
            springSecurityService(SpringSecurityService)
            sadService(SadService)
        }
        setRequestParams()

        def result = ["statusCode": 200, data: ["id"                           : "17181", "invoiceAmountInNationalCurrency": "9839355", "countryOfExportName": "Belgique", "insuranceAmountInNationalCurrency": "131546", "totalAssessedAmount": "3286767", "declarantName": "CI LOGISTIQUE 18 BP 1395 ABIDJAN 18"
                                                , "declarantReferenceYear"     : "2020", "totalAmountOfCostInsuranceFreight": "11545198", "consigneeName": "WEBB FONTAINE CÔTE D'IVOIRE  04 BP 225 ABIDJAN 04 BIETRY-BVD VGE"
                                                , "officeOfDispatchExportName" : "ABIDJAN-PORT", "externalFreightAmountInNationalCurrency": "1574297", "declarantCode": "00267T", "officeOfDispatchExportCode": "CIAB1",
                                                "typeOfDeclaration"            : "IM", "invoiceCurrencyCode": "EUR", "countryOfExportCode": "BE", "invoiceAmountInForeignCurrency": "15000", "consigneeCode": "1331590S", "exporterName": "VMD HOGE MAUW 900 2370 ARENDONK BELGIUM Belgique"
                                                , "invoiceCurrencyExchangeRate": "655.957", "status": "Paid", "deliveryTermsCode": "FCA"]]
        SadService.metaClass.retrieveFromRimmview = {
            return result
        }
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            retrieveOnesad(*_) >> result

        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService
        Map params = [clearanceOfficeCode: "CIAB1", clearanceOfficeName: "ABIDJAN-PORT", declarationSerial: "C", declarationNumber: "360", declarationDate: "02/10/2020", basedOn: "SAD", requestType: "EA"]

        when:
        Exchange exchangeInstance = service.retrieveExchangeFromSad(params)

        then:
        exchangeInstance?.sadInstanceId == "17181"
        exchangeInstance?.sadStatus == "Paid"

    }

    private void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        RequestContextHolder.setRequestAttributes(webRequest)
    }

    @Unroll
    def "test checkPrintAE"() {
        given:
        SimpleMapDatastore sydamstore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "sydamviews"], RimmSadGeneralSegement)

        SadService.metaClass.static.RetrieveSADInformation = {
            return resultSAD
        }

        Exchange exchangeInstance = new Exchange()
        exchangeInstance.requestType = requestType
        exchangeInstance.status = status
        exchangeInstance.clearanceOfficeCode = clearanceOfficeCode
        exchangeInstance.declarationNumber = declarationNumber
        exchangeInstance.declarationSerial = declarationSerial
        exchangeInstance.declarationDate = declarationDate
        exchangeInstance.isReleaseOrder = releaseOrder
        exchangeInstance.sadInstanceId = sadId

        when:
        boolean result = service.checkPrintAE(exchangeInstance)

        then:
        result == expectResult

        where:
        expectResult << [true, false, false, false]
        releaseOrder << [false, false, false, false]
        sadId << ["sad896", "sad485", "sad789", null]
        requestType << [ExchangeRequestType.EC, ExchangeRequestType.EC, ExchangeRequestType.EC, ExchangeRequestType.EC]
        status << [Statuses.ST_EXECUTED, Statuses.ST_EXECUTED, Statuses.ST_APPROVED, Statuses.ST_APPROVED]
        clearanceOfficeCode << ["CIAB1", "CIAB1", "CIAB1", "CIABE"]
        declarationNumber << ["380", "402", "456", "138"]
        declarationSerial << ["E", "E", "E", "E"]
        declarationDate << [TypeCastUtils.toLocalDate("26/11/2020"), TypeCastUtils.toLocalDate("11/11/2020"), TypeCastUtils.toLocalDate("02/01/2022"), TypeCastUtils.toLocalDate("14/01/2022")]
        resultSAD << [
                [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Paid"]],
                [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Print Release Order", id: 2, status: "Paid"], [op_name: "Deposer (DPOD)", id: 3, status: "Cancelled"]],
                [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Cancelled"]],
                null
        ]

    }

    @Unroll
    def "test verifyReleaseOrder"() {
        when:
        def result = service.verifyReleaseOrder(resultSAD)

        then:
        result == expectResult

        where:
        expectResult | resultSAD
        false        | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Paid", id: 2, status: "Paid"], [op_name: "Deposer (DPOD)", id: 3, status: "Cancelled"]]
        true         | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Paid"]]
        true         | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Cancelled"]]
    }

    @Unroll
    def "test verifyIfSADIsNotCancelled"() {
        when:
        def result = service.verifyIfSADIsNotCancelled(resultSAD)

        then:
        result == expectResult

        where:
        expectResult | resultSAD
        true         | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Paid", id: 2, status: "Cancelled"], [op_name: "Deposer (DPOD)", id: 3, status: "Paid"]]
        true         | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Paid"]]
        false        | [[op_name: "Assess Stored", id: 1, status: "Assessed"], [op_name: "Payment", id: 2, status: "Paid"], [op_name: "Print Release Order", id: 3, status: "Paid"], [op_name: "Payment", id: 4, status: "Cancelled"]]
    }

}