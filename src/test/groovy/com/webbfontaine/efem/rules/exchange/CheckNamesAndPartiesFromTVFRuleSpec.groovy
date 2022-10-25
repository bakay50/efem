package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import groovy.json.JsonBuilder
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import response.AjaxPostResponse
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.ExchangeRequestType.EA


class CheckNamesAndPartiesFromTVFRuleSpec extends Specification implements ServiceUnitTest<TvfService>, DataTest {

    def setup() {
        mockDomain Exchange
        defineBeans() {
            tvfService(TvfService)
            springSecurityService(SpringSecurityService)
            tvfRule(TvfRule)
        }
    }

    @Unroll
    void "test CheckNamesAndPartiesFromTVFRule"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> ["ALL"]
        Exchange exchange = new Exchange()
        exchange.requestType = EA
        exchange.importerCode = importerCode
        exchange.exporterCode = exporterCode
        exchange.tvfNumber = 668
        exchange.tvfDate = new LocalDate()
        service.exchangeService = Mock(ExchangeService)
        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [
                transaction_number  : 1, transaction_date: "2020-10-28", invoice_cur_code: "EUR",
                imp_taxpayer_account: tvfData.tvf.impTaxPayerAcc, importer_address: "CI", exporter_address: "CI",
                exp_taxpayer_account: tvfData.tvf.expTaxPayerAcc
        ]
        postResponse.data_segment_general = result_general_segment
        def rep = new JsonBuilder(postResponse)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> rep
        }

        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        when:
        Rule rule = new CheckNamesAndPartiesFromTVFRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | tvfData                                                                     | importerCode | exporterCode
        true     | new Exchange(tvf: new Tvf(impTaxPayerAcc: "IMP96", expTaxPayerAcc: "EX75")) | "IMP452"     | "EX741"
        true     | new Exchange(tvf: new Tvf(impTaxPayerAcc: "IMP96", expTaxPayerAcc: "EX75")) | "IMP96"      | "EX741"
        false    | new Exchange(tvf: new Tvf(impTaxPayerAcc: "IMP96", expTaxPayerAcc: "EX75")) | null         | null
        false    | new Exchange(tvf: new Tvf(impTaxPayerAcc: "IMP96", expTaxPayerAcc: "EX75")) | "IMP96"      | "EX75"
    }
}
