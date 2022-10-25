package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import static com.webbfontaine.efem.workflow.Operation.*
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckOperationTypeCompatibilityRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkOperationCompatibility(exchangeInstance)
    }

    def checkOperationCompatibility(exchangeInstance) {
        if (exchangeInstance?.startedOperation in [CREATE, REQUEST, REQUEST_QUERIED, UPDATE_QUERIED]) {
            if (exchangeInstance?.requestType == ExchangeRequestType.EA) {
                checkOperationtype(exchangeInstance, ExchangeRequestType.operationCodes_EC)
            } else if (exchangeInstance?.requestType == ExchangeRequestType.EC) {
                checkOperationtype(exchangeInstance, ExchangeRequestType.operationCodes_EA)
            }
        }
    }

    static void checkOperationtype(exchangeInstance, listOfOperations) {
        if (exchangeInstance.operType in listOfOperations) {
            exchangeInstance.errors.rejectValue('operType', 'exchange.errors.operType.errors', 'Operation type and type of request are not compatible')
        }
    }
}
