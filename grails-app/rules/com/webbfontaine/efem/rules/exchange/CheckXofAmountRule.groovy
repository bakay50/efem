package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckXofAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        def xofAmount = exchangeInstance.amountNationalCurrency
        if( xofAmount && xofAmount < ExchangeRequestType.MINIMUN_AMOUNT ){
            exchangeInstance.errors.rejectValue("amountNationalCurrency", "exchange.errors.xofAmount", "The EA is only required for transactions with a minimum amount of 500,000 XOF")
        }
    }
}
