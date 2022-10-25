package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.json.JsonBuilder
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import response.AjaxPostResponse
import spock.lang.Specification
import spock.lang.Unroll

class CheckDeclarantFromTVForSADRuleSpec extends Specification implements ServiceUnitTest<TvfService>, DataTest{

    @Unroll
    def "test CheckDeclarantFromTVForSADRule"() {

        WebRequestUtils.getParams() >> new GrailsParameterMap([clearanceOfficeCode: "CIAB1", clearanceOfficeName: "ABIDJAN-PORT", declarationSerial: "C", declarationNumber: "360", declarationDate: "02/10/2020"], null)
        Exchange exchange = new Exchange(requestNo: "001", tvfNumber: 419, tvfDate: LocalDate.now(),requestType: ExchangeRequestType.EA,basedOn: ExchangeRequestType.BASE_ON_TVF, declarantCode: declarantCode)
        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [id: 1, declarant_code:"003T", transaction_date: "2020-12-07"]
        postResponse.data_segment_general = result_general_segment
        def result = new JsonBuilder(postResponse)
        service.exchangeService = Mock(ExchangeService)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> result
        }
        CheckDeclarantFromTVForSADRule.metaClass.getSadDetails = { [data: [declarant_code: "003T"]] }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        when:
        Rule rule = new CheckDeclarantFromTVForSADRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        expectedResult | declarantCode | baseOn
        false          | "003T"        | ExchangeRequestType.BASE_ON_TVF
        true           | "003Q"        | ExchangeRequestType.BASE_ON_SAD
        false          | "003T"        | ExchangeRequestType.BASE_ON_SAD
        true           | "003Q"        | ExchangeRequestType.BASE_ON_TVF
    }
}
