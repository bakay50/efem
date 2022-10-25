package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class CheckAmountMentionnedInCurrRule implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        LOGGER.debug("in apply() of ${CheckAmountMentionnedInCurrRule} on Exchange id ${exchangeInstance.id}")

        if(exchangeInstance?.startedOperation in [CREATE, REQUEST] && isNull(exchangeInstance?.amountMentionedCurrency)){
            exchangeInstance.errors.rejectValue("amountMentionedCurrency", "execution.errors.amountMentionedExCurrency", "Amount in the indicated currency must be greater than zero.")
        }
    }

    static def isNull(value){
        return value == null || value in BigDecimal.ZERO
    }
}
