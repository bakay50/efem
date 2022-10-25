package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckAmountNegativeRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        List<String> fields = ["amountMentionedCurrency","amountNationalCurrency","finalAmount","finalAmountInDevise" ]
        fields.each {field->
            if(exchangeInstance?."${field}" && exchangeInstance?."${field}" < BigDecimal.ZERO){
                exchangeInstance.errors.rejectValue("${field}", "common.errors.negativeAmount", [exchangeInstance."${field}"] as Object[], "a negative amount is not allowed [ " + exchangeInstance."${field}" + " ]")
            }
        }
    }
}
