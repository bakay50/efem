package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_TVF
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA006_TRANSACTION

@Slf4j("LOGGER")
class CheckEATransactionFromTvfRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckEATransactionFromTvfRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkTransaction(exchangeInstance)
    }

    static def checkTransaction(Exchange exchange) {
        if (exchange.basedOn == BASE_ON_TVF && exchange.requestType == EA) {
            if (exchange.operType == EA006_TRANSACTION) {
                exchange.errors.rejectValue('operType', 'exchange.errors.ea006', 'This type of transaction Apurement de dettes fournisseurs can not be requested for this transaction.')
            }
        }
    }
}
