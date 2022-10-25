package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckTransferOrderRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckTransferOrderRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        CheckTransferOrder(exchangeInstance)
    }

    static def CheckTransferOrder(Exchange exchange) {
        List<OrderClearanceOfDom> orders = OrderClearanceOfDom.findAllByEaReference(exchange.requestNo)
        orders.each {
            if (it.transfer.status in [Statuses.ST_REQUESTED, Statuses.ST_VALIDATED, Statuses.ST_QUERIED]) {
                exchange.errors.reject('exchange.cancel.transferOrder', [it.transfer.requestNo] as Object[], "This exchange authorization cannot be cancelled, as it is linked to the transfer file ${it.transfer.requestNo}.")
            }
        }
    }
}
