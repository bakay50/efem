package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckMandatoryRegistrationNumberBankRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkRegistrationNumber(exchangeInstance)
    }

    void checkRegistrationNumber(exchange) {
        if (UserUtils.isBankAgent() && exchange?.startedOperation == Operation.DOMICILIATE && !exchange?.registrationNumberBank) {
            exchange.errors.rejectValue('registrationNumberBank', 'exchange.mandatory.registrationNumberBank', 'The registration number is mandatory.')
        }
    }
}
