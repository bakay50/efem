package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckAmountInDeviseFromTvfOrSadRuleSpec extends Specification implements ServiceUnitTest<SadService>, DataTest {
    def setup() {
        defineBeans {
            sadService(SadService)
            exchangeService(ExchangeService)
        }
        mockDomain(Exchange)
    }

    @Unroll
    def "test checkSumOfAllAmount for EA based on SAD"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([clearanceOfficeCode: "CIAB1", declarationSerial: "E", declarationNumber: "377", declarationDate: "02/10/2020"], null)

        Exchange exchange = new Exchange(requestNo: "002", amountMentionedCurrency: amountMentionedCurrency, clearanceOfficeCode: "CIAB1",
                declarationSerial: "E", declarationNumber: "377", declarationDate: new LocalDate(), requestType: ExchangeRequestType.EA,
                basedOn: ExchangeRequestType.BASE_ON_SAD)
        exchange.setStartedOperation(Operation.CREATE)

        List<Exchange> listExchange = [new Exchange(requestNo: "EA1", amountMentionedCurrency: 1000, clearanceOfficeCode: "CIAB1",
                declarationSerial: "E", declarationNumber: "377", declarationDate: new LocalDate())
                                       , new Exchange(requestNo: "EA2", amountMentionedCurrency: 3000, clearanceOfficeCode: "CIAB1",
                declarationSerial: "E", declarationNumber: "377", declarationDate: new LocalDate())
                                       , new Exchange(requestNo: "EA3", amountMentionedCurrency: 1500, clearanceOfficeCode: "CIAB1",
                declarationSerial: "E", declarationNumber: "377", declarationDate: new LocalDate())]

        def mockrimmLoadSadTvfService = Stub(RimmLoadSadTvfService) {
            retrieveOnesad(*_) >> [id: 1, declarant_code: "00267T", vgs_inv_amt_fcx: new BigDecimal("10000.00")]
        }
        service.rimmLoadSadTvfService = mockrimmLoadSadTvfService

        Exchange.metaClass.static.findAllByClearanceOfficeCodeAndDeclarationDateAndDeclarationSerialAndDeclarationNumberAndStatusInListAndRequestNoIsNotNull = {
            clearanceOfficeCode, declarationDate, declarationSerial, declarationNumber, listOfStatus -> return listExchange
        }

        when:
        Rule rule = new CheckAmountInDeviseFromSadRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange?.hasErrors() == expectedResult

        where:
        expectedResult | amountMentionedCurrency
        false          | new BigDecimal("4500.00")
        true           | new BigDecimal("5000.00")
    }
}
