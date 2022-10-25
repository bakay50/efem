package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckBalanceAndFinalAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchange.requestType == ExchangeRequestType.EC && exchange?.balanceAs != exchange?.finalAmountInDevise) {
            exchange.errors.rejectValue("balanceAs", "exchange.balance.error", "the field Balance and Invoice Final Amount In Currency field should have the same value.")
        }
    }

}
