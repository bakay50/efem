package com.webbfontaine.efem.print

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import com.webbfontaine.sw.rimm.RimmSadDetails
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

class PrintParametersServiceSpec extends Specification implements ServiceUnitTest<PrintParametersService> , DataTest{

    def setup() {
        mockDomains(Exchange)
    }

    @Unroll
    def "test setupSadGoodsParams() method"() {
        given:

        PrintParametersService.metaClass.retrieveExchangeSadArticlesWithAttachedDocs = {
            return listItems
        }

        Exchange exchange = new Exchange(clearanceOfficeCode: 'CIABE', declarationSerial: 'E', declarationNumber: '474', declarationDate: new  LocalDate(),registrationNumberBank:'ECO1', departmentInCharge: "FINEX2")

        when:
        def result = service.setupSadGoodsParams(exchange,isProvisory,exchangeParams)
        then:
        result.size() == paramsNumber

        where:
        isProvisory | exchangeParams | paramsNumber | listItems
        true        | [:]            | 0            | [["17503", "1", "CIABE", "BUREAU SECTION EXPORT", "2020-11-30", "EX", 2020, "E", "447", "L", "441", "2020-11-30", "0402990000", "5247656", "24000", "-- Lait", "LAIT CONCENTRE", "5247656", "VD"]]
        false       | [:]            | 3            | [["17503", "1", "CIABE", "BUREAU SECTION EXPORT", "2020-11-30", "EX", 2020, "E", "447", "L", "441", "2020-11-30", "0402990000", "5247656", "24000", "-- Lait", "LAIT CONCENTRE", "5247656", "VD"]]

    }
    def "test setSadInformationsInParams() method"() {
        given:

        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            retrieveOnesad(*_) >> sad

        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService
        Exchange exchange = new Exchange(clearanceOfficeCode:'CIABE',declarationSerial:'E',declarationNumber:474 ,declarationDate: new LocalDate(),requestNo: requestNo, attachedDocs:attachedDocs,executions: executions)


        when:
        def result = service.setSadInformationsInParams(exchange,exchangeParams)
        then:
        result.size() == paramsNumber

        where:
        exchangeParams | requestNo | attachedDocs                                       | executions                                    | paramsNumber | sad
        [:]            | '1234'    | new AttachedDoc(docType: '0008', docRef: "123455") | new Execution(executionDate: new LocalDate()) | 7            | new RimmSadDetails(id: "17503", office_code: "CIABE", office_name: "BUREAU SECTION EXPORT", declaration_date: "2020-11-30", declaration_year: 2020)
        [:]            | '1234'    | []                                                 | []                                            | 5            | new RimmSadDetails(id: "17503", office_code: "CIABE", office_name: "BUREAU SECTION EXPORT", declaration_date: "2020-11-30", declaration_year: 2020)
    }
}
