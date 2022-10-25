package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.UserUtils.getUserProperty
import static com.webbfontaine.efem.UserUtils.isDeclarant
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.UserProperties.ALL
import static com.webbfontaine.efem.constants.UserProperties.DEC

@Slf4j("LOGGER")
class CheckDeclarantCodeForECRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckDeclarantCodeForECRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkDeclarantCode(exchangeInstance)
    }

    def checkDeclarantCode(Exchange exchangeInstance){
        if(isDeclarant() && exchangeInstance?.requestType == ExchangeRequestType.EC && exchangeInstance?.startedOperation == Operation.CREATE && exchangeInstance?.status == null){
            def currentDeclarant = userPropertyValueAsList(DEC) ?: getUserProperty(DEC)
            if(currentDeclarant != ALL){
                if(!(exchangeInstance?.declarantCode in currentDeclarant)){
                    exchangeInstance.errors.rejectValue("declarantCode", "exchange.checkDecPropertyForEC.message", "You can not submit a Exchange Commitment with this Declarant Code ")
                }
            }
        }
    }
}
