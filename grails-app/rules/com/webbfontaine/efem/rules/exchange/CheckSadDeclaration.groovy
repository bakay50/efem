package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.constants.TvfConstants.*
import static com.webbfontaine.efem.constants.ExchangeRequestType.*

class CheckSadDeclaration implements Rule {


    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange)

        checkIfTypeIsCompatible(exchange)

    }

    def checkIfTypeIsCompatible(Exchange exchange){
        if(exchange.sadTypeOfDeclaration == EX_FLOW && exchange.requestType == EA){
            exchange.errors.reject("load.sad.declaration.error","Import exchange document cannot be linked to an export declaration")
        }else if(exchange.sadTypeOfDeclaration == EC && exchange.requestType == EC){
            exchange.errors.reject("load.sad.declaration.ex.error","Export exchange document cannot be linked to an import declaration")
        }
    }
}
