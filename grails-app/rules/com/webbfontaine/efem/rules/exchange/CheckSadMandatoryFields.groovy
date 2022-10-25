package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED

class CheckSadMandatoryFields implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        checkIfSadMandatoryFields(exchange)
    }

    def checkIfSadMandatoryFields(Exchange exchange){
            if ((exchange.requestType == ExchangeRequestType.EC) && (exchange?.startedOperation in [UPDATE_APPROVED, UPDATE_EXECUTED])){
                if (isNullOrEmpty(exchange.clearanceOfficeCode)){
                    exchange.errors.rejectValue('clearanceOfficeCode', 'exchange.errors.clearanceOfficeCode_complete', 'The fields is mandatory.')
                }
                if (isNullOrEmpty(exchange.declarationNumber)){
                    exchange.errors.rejectValue('declarationNumber', 'exchange.errors.declarationNumber_complete', 'The fields is mandatory.')
                }
                if (isNullOrEmpty(exchange.declarationDate)){
                    exchange.errors.rejectValue('declarationDate', 'exchange.errors.declarationDate_complete', 'The fields is mandatory.')
                }
            }
    }

    private isNullOrEmpty(def value) {
        return value == null || value == ""
    }
}
