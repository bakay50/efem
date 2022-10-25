package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.rules.exchange.TvfRule
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.efem.tvf.TvfUtils
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.util.Holders
import grails.web.context.ServletContextHolder
import groovy.json.JsonBuilder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import response.AjaxPostResponse
import spock.lang.Specification
import spock.lang.Unroll

class TvfServiceSpec extends Specification implements ServiceUnitTest<TvfService>, DataTest {

    def setup() {
        defineBeans {
            tvfRule(TvfRule)
            springSecurityService(SpringSecurityService)
            tvfService(TvfService)
        }
    }

    def "test setTvfError"() {
        given:
        Tvf tvfInstance
        when:
        tvfInstance = service.setTvfError(error)
        then:
        tvfInstance.errors.getAllErrors().first().code == messageCodeError
        where:
        error                             | messageCodeError
        "Erreur_tvf_status"               | "exchange.errors.tvf_status"
        "Erreur_tvf_existe"               | "exchange.errors.tvf_existe"
        "Erreur_tvf_date_expiration_null" | "exchange.errors.tvf.expiration_date_null"
        "Erreur_tvf_expiration"           | "exchange.errors.tvf.expire_date"
        "Erreur_Serveur"                  | "exchange.errors.serveur"
        "Erreur_tvf_flows"                | "exchange.errors.tvf_flow"
        "other_error"                     | "exchange.errors.serveur"
    }

    @Unroll
    def "test loadTvf"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> ["ALL"]
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> ["ALL"]
        mockDomains(Exchange)
        setRequestParams()
        Holders.config.rest?.isWebService = isWebService
        TvfService.metaClass.loadExchangeFromTvfByView = {
            return new Exchange(tvfNumber: tvfNumber, tvfDate: tvfDate)
        }
        TvfService.metaClass.loadExchangeFromTvfByUrl = {
            return new Exchange(tvfNumber: tvfNumber, tvfDate: tvfDate)
        }
        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [
                domiciliation_ref    : "WKDJJCSLM", domiciliation_date: "2020-10-28", bank_code: "BIC1", bank_name: "B.I.C.I.C.I.", exporter_name: "TRANSMARE CHEMIE NV\nDE KEYSERLEI 58 2018 ANTWERPEN BELGIUM",
                imp_taxpayer_account : "1331590S", importer_name: "WEBB FONTAINE CÔTE D'IVOIRE\n04 BP 225 ABIDJAN 04\nBIETRY-BVD VGE\nRCI", invoice_cur_code: "EUR", invoice_cur_name: "Euro", transaction_status: "VALIDATED",
                transaction_number   : 1, transaction_date: "2020-10-28", declarant_code: "00267T", tot_ivn_amt: "21142", tot_ivn_nat_amt: "13868242.89", invoice_reference: "840.4237", invoice_date: "2020-08-11",
                incoterm_code        : "CFR", incoterm_name: "Cout et FRET", tot_fob_fgn_cur: "21142", tot_frg_fgn_cur: "1100", tot_cif_fgn_cur: "22242", cuo_code: "CIAB1", cuo_name: "ABIDJAN-PORT",
                importer_address     : "04 BP 225 ABIDJAN 04\nBIETRY-BVD VGE\nRCI", importer_phone: "0022521212399", exporter_address: "DE KEYSERLEI 58 2018 ANTWERPEN BELGIUM",
                exporter_country_code: "BE", exporter_country_name: "Belgique", exporter_email: "jerape@transmare.com", exporter_phone: "003232060823", countryOfExportCode: "BE", countryOfExportName: "Belgique"
        ]
        postResponse.data_segment_general = result_general_segment
        def rep = new JsonBuilder(postResponse)
        service.exchangeService = Mock(ExchangeService)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> rep
        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        when:
        Exchange exchangeInstance = service.loadTvf(tvfNumber, tvfDate)

        then:
        exchangeInstance?.tvfNumber == expectedNumber
        exchangeInstance?.tvf?.status == expectedStatus

        where:
        tvfNumber | tvfDate      | expectedNumber | expectedStatus | isWebService
        "1"       | "28/10/2020" | 1              | "VALIDATED"    | false
        "1"       | "28/10/2020" | null           | null           | true
    }

    def "test retrieveTvf"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> ["ALL"]
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> ["ALL"]
        mockDomains(Exchange)
        setRequestParams()
        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [
                domiciliation_ref    : "WKDJJCSLM", domiciliation_date: "2020-10-28", bank_code: "BIC1", bank_name: "B.I.C.I.C.I.", exporter_name: "TRANSMARE CHEMIE NV\nDE KEYSERLEI 58 2018 ANTWERPEN BELGIUM",
                imp_taxpayer_account : "1331590S", importer_name: "WEBB FONTAINE CÔTE D'IVOIRE\n04 BP 225 ABIDJAN 04\nBIETRY-BVD VGE\nRCI", invoice_cur_code: "EUR", invoice_cur_name: "Euro", transaction_status: "VALIDATED",
                transaction_number   : 1, transaction_date: "2020-10-28", declarant_code: "00267T", tot_ivn_amt: "21142", tot_ivn_nat_amt: "13868242.89", invoice_reference: "840.4237", invoice_date: "2020-08-11",
                incoterm_code        : "CFR", incoterm_name: "Cout et FRET", tot_fob_fgn_cur: "21142", tot_frg_fgn_cur: "1100", tot_cif_fgn_cur: "22242", cuo_code: "CIAB1", cuo_name: "ABIDJAN-PORT",
                importer_address     : "04 BP 225 ABIDJAN 04\nBIETRY-BVD VGE\nRCI", importer_phone: "0022521212399", exporter_address: "DE KEYSERLEI 58 2018 ANTWERPEN BELGIUM",
                exporter_country_code: "BE", exporter_country_name: "Belgique", exporter_email: "jerape@transmare.com", exporter_phone: "003232060823", countryOfExportCode: "BE", countryOfExportName: "Belgique"
        ]
        postResponse.data_segment_general = result_general_segment
        def rep = new JsonBuilder(postResponse)
        service.exchangeService = Mock(ExchangeService)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> rep
        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        when:
        Tvf tvf = service.retrieveTvf("1", "28/10/2020")

        then:
        tvf.status == "VALIDATED"

    }

    void "test setExchangeWithTvfInstance" (){
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> ["ALL"]
        Tvf tvfInstance = new Tvf(impTaxPayerAcc: 'imp01', expTaxPayerAcc: 'exp01', decCode: 'dec1')
        Exchange exchangeInstance = new Exchange(tvfInstanceId: '001', tvf: tvfInstance)
        GroovyMock(TvfUtils, global: true)

        when:
        service.setExchangeWithTvfInstance(exchangeInstance, tvfInstance,  "1",  LocalDate.now())

        then:
        exchangeInstance.tvfNumber == 1
        exchangeInstance.tvf.decCode == "dec1"

    }

    private void setRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        RequestContextHolder.setRequestAttributes(webRequest)
    }
}