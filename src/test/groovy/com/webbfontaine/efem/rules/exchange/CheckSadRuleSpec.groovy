package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.ExchangeRequestType.getEA
import static com.webbfontaine.efem.constants.ExchangeRequestType.getEC
import static com.webbfontaine.efem.constants.TvfConstants.EX_FLOW
import static com.webbfontaine.efem.constants.TvfConstants.IM_FLOW


class CheckSadRuleSpec extends Specification implements DataTest  {

    @Unroll
    def "test CheckSadRule"(){
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([clearanceOfficeCode: "CIAB1", clearanceOfficeName: "ABIDJAN-PORT", declarationSerial: "C", declarationNumber: "360", declarationDate: "02/10/2020", basedOn: "SAD", requestType: "EA"], null)

        Exchange exchange = new Exchange(requestNo: "EA202012", status: "Approved", declarantCode: '1234', importerCode: '0001',
                requestType: reqType,exporterCode:'001',
                currencyCode: "XOF", bankCode: "SGBCI",
                amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(10.00),
                year: LocalDate.now().year, registrationNumberBank: "bank123", registrationDateBank: LocalDate.now())

        CheckSadRule.metaClass.getExchangeFromSad = {
            return new Exchange(requestNo: "EA202012", status: "Approved",sadTypeOfDeclaration: decType, declarantCode: '1234', importerCode: '0001', exporterCode:exporterCode)
        }

        when:
        Rule rule = new CheckSadRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | reqType | decType    | exporterCode
        true     | EC      | IM_FLOW    | '001'
        false    | EA      | IM_FLOW    | '001'
        true     | EC      | IM_FLOW    | '002'
        false    | EC      | EX_FLOW    | '001'
    }

}