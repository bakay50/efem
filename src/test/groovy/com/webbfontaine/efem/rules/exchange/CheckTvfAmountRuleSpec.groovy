package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import groovy.json.JsonBuilder
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import response.AjaxPostResponse
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class CheckTvfAmountRuleSpec extends Specification implements ServiceUnitTest<TvfService>, DataTest {
    @Ignore
    @Unroll
    def "test checkSumOfAllAmount"() {
        given:

        defineBeans {
            tvfService(TvfService)
            beanDataLoadService(BeanDataLoadService)
        }

        Exchange exchange = new Exchange(requestNo: 1, amountMentionedCurrency: amountMentionedCurrency, tvfNumber: 1, tvfDate: LocalDate.now())

        def postResponse = new AjaxPostResponse()
        postResponse.message = null
        postResponse.success = "true"
        def result_general_segment = [
                transaction_number: 1, transaction_date: "2020-10-28", invoice_cur_code: "EUR", tot_cif_fgn_cur: totInvoiceAmount
        ]
        postResponse.data_segment_general = result_general_segment
        def rep = new JsonBuilder(postResponse)
        service.exchangeService = Mock(ExchangeService)
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            RetrieveInfoTvf(*_) >> rep
        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        List<Exchange> listExchange = [new Exchange(requestNo: "EA1", amountMentionedCurrency: 100, tvfNumber: 1, tvfDate: LocalDate.now())
                                       , new Exchange(requestNo: "EA2", amountMentionedCurrency: 300, tvfNumber: 1, tvfDate: LocalDate.now())
                                       , new Exchange(requestNo: "EA3", amountMentionedCurrency: 150, tvfNumber: 1, tvfDate: LocalDate.now())]


        Exchange.metaClass.static.findAllByTvfDateAndTvfNumberAndStatusInListAndRequestNoIsNotNull = {
            tvfDate, tvfNumber, listOfStatus -> return listExchange
        }

        CheckTVFAmountRule.metaClass.getCurrencyRate= {
            return "655"
        }

        when:
        Rule rule = new CheckTVFAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        expectedResult | totInvoiceAmount         | amountMentionedCurrency
        true           | new BigDecimal("500.00") | new BigDecimal("550.00")
        false          | new BigDecimal("1000.00")| new BigDecimal("5.00")

    }
}
