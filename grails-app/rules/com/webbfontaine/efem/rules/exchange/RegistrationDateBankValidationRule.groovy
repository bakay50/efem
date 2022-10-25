package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class RegistrationDateBankValidationRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if(exchange?.registrationDateBank < exchange?.requestDate){
            exchange.errors.rejectValue("registrationDateBank", "exchange.errors.registrationDate.error", "Registration Date at Bank must be greater than Request Date.")
        }
    }
}
