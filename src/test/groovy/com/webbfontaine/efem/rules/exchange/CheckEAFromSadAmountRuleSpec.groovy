package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import com.webbfontaine.sw.rimm.RimmSadDetails
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.validation.Errors
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class CheckEAFromSadAmountRuleSpec extends Specification implements ServiceUnitTest<SadService>, DataTest {
    @Ignore
    @Unroll
    def "test checkSumOfAllAmount"(){
        given:
        defineBeans {
            sadService(SadService)
            beanDataLoadService(BeanDataLoadService)
        }
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([clearanceOfficeCode: "CIAB1", clearanceOfficeName: "ABIDJAN-PORT", declarationSerial: "C", declarationNumber: "360", declarationDate: "02/10/2020", basedOn: "SAD", requestType: "EA"], null)
        Exchange exchange = new Exchange(clearanceOfficeCode: 'CIAB1',currencyCode: currencyCode, currencyPayCode: currencyPayCode,amountMentionedCurrency: amountMentionedCurrency)

        List<Exchange> listExchange = [new Exchange(requestNo: "EA1", amountMentionedCurrency: new BigDecimal("100"))
                                       , new Exchange(requestNo: "EA2", amountMentionedCurrency: new BigDecimal("300"))
                                       , new Exchange(requestNo: "EA3", amountMentionedCurrency: new BigDecimal("150"))]

        RimmSadDetails sadResult = new RimmSadDetails()
        sadResult.vgs_inv_amt_fcx = new BigDecimal("1000")
        sadResult.invoice_currency_code = "USD"
        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            retrieveOnesad(*_) >> sadResult
        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        Exchange.metaClass.static.findAllByClearanceOfficeCodeAndDeclarationDateAndDeclarationSerialAndDeclarationNumberAndStatusInListAndRequestNoIsNotNull = {
            clearanceOfficeCode, declarationDate, declarationSerial, declarationNumber, listOfStatus -> return listExchange
        }

        CheckSadRule.metaClass.getSadDatas = {
            return sadResult
        }

        when:
        Rule rule = new CheckEAFromSadAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | currencyCode | currencyPayCode | amountMentionedCurrency
        true     | "XOF"        | "XOF"           | new BigDecimal(1000)
        false    | "XOF"        | "USD"           | new BigDecimal(10)
    }
}
