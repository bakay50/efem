package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.AppConfig.*
import static com.webbfontaine.efem.rules.RuleUtils.reject

@Slf4j("LOGGER")
class CheckExchangeAuthorizedUserRule implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckExchangeAuthorizedUserRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        Operation commitOperation = WebRequestUtils.params.commitOperation
        checkUserAccess(exchangeInstance,commitOperation?.humanName())
    }

    static def checkUserAccess(Exchange exchange, commitOperation) {
        if (isBlockUserEnabled &&  exchange?.declarantCode && (exchange?.declarantCode in blockDecCodeList) && commitOperation in blockOperationsList ){
            LOGGER.debug("Exchange : Block  declarantCode ${exchange?.declarantCode} , commitOperation human name ${commitOperation}")
           reject(exchange,"users.blocked.label", exchange?.declarantCode)
        }
        if (isBlockUserEnabled && exchange?.importerCode &&  (exchange?.importerCode in blockNccList) && commitOperation in blockOperationsList ){
            LOGGER.debug("Exchange : Block  importerCode ${exchange?.importerCode} , commitOperation human name ${commitOperation}")
            reject(exchange,"users.blocked.label", exchange?.importerCode)
        }

        if (isBlockUserEnabled && exchange?.exporterCode &&  (exchange?.exporterCode in blockNccList) && commitOperation in blockOperationsList ){
            LOGGER.debug("Exchange : Block  exporterCode ${exchange?.exporterCode} , commitOperation human name ${commitOperation}")
            reject(exchange,"users.blocked.label", exchange?.exporterCode)
        }
    }
}
