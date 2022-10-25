package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import groovy.json.JsonBuilder
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import response.AjaxPostResponse
import spock.lang.Specification
import spock.lang.Unroll

class CheckAmountInDeviseFromTvfRuleSpec extends Specification implements ServiceUnitTest<TvfService>, DataTest{
    def setup() {
        defineBeans {
            tvfService(TvfService)
            exchangeService(ExchangeService)
        }
        mockDomain(Exchange)
    }

    @Unroll
    def "test checkSumOfAllAmount for EA based on TVF"() {
        Exchange exchange = new Exchange(requestNo: "001", amountMentionedCurrency: amountMentionedCurrency, tvfNumber: 419, tvfDate: LocalDate.now(),
                requestType: ExchangeRequestType.EA,basedOn: ExchangeRequestType.BASE_ON_TVF)
        exchange.startedOperation = Operation.CREATE

        List<Exchange> listExchange = [new Exchange(requestNo: "EA1", amountMentionedCurrency: 1000, tvfNumber: 1, tvfDate: LocalDate.now())
                                       , new Exchange(requestNo: "EA2", amountMentionedCurrency: 3000, tvfNumber: 1, tvfDate: LocalDate.now())
                                       , new Exchange(requestNo: "EA3", amountMentionedCurrency: 1500, tvfNumber: 1, tvfDate: LocalDate.now())]

        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [id: 1, tot_ivn_amt: new BigDecimal("10000.00"), transaction_date: "2020-12-07"]
        postResponse.data_segment_general = result_general_segment
        def result = new JsonBuilder(postResponse)
        service.exchangeService = Mock(ExchangeService)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> result
        }

        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        Exchange.metaClass.static.findAllByTvfNumberAndTvfDateAndStatusInListAndRequestNoIsNotNull = {
            tvfNumber, tvfDate, listOfStatus -> return listExchange
        }


        when:
        Rule rule = new CheckAmountInDeviseFromTvfRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        expectedResult | amountMentionedCurrency
        false          | new BigDecimal("4500.00")
        true           | new BigDecimal("5000.00")
    }
}
