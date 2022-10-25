package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckFinalInvoiceAmountRuleSpec extends Specification implements DataTest {

    @Unroll
    def "test CheckFinalInvoiceAmountRule"(){
        given:
        mockDomain(Exchange)
        Exchange exchange = new Exchange(id: 1,requestType: ExchangeRequestType.EC, isFinalAmount: isFinalAmount, finalAmountInDevise: finalAmountInDevise, finalAmount: new BigDecimal(12000), requestNo: "EC001")
        Exchange persistexchange = new Exchange(id: 1, isFinalAmount: true, finalAmountInDevise: new BigDecimal(12000), finalAmount: new BigDecimal(12000), requestNo: "EC001")
        Exchange.metaClass.static.findById = {
            id -> return persistexchange
        }
        ClearanceOfDom.metaClass.static.findAllByEcReference = {
            ecReference -> return [new ClearanceOfDom(id:1, repats: new Repatriation(id:1,status: repatstatus))]
        }

        when:
        Rule rule = new CheckFinalInvoiceAmountRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | repatstatus | isFinalAmount | finalAmountInDevise
        false    | "Stored"    | true          | new BigDecimal(12000)
        true     | "Declared"  | false         | new BigDecimal(12000)
    }
}
