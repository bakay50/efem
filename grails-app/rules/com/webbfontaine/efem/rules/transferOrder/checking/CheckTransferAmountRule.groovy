package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_VALIDATED

class CheckTransferAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        if(!(WebRequestUtils.params.commitOperation in [UPDATE_VALIDATED, CANCEL_QUERIED, CANCEL_VALIDATED])){
            TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
            checkSumOfExchangeAmounts(transferInstance)
        }
    }

    def checkSumOfExchangeAmounts(TransferOrder transferInstance) {
        transferInstance.allOrderClearanceOfDoms.each { OrderClearanceOfDom clearanceOfDom->
            Exchange exchangeInstance = Exchange.findByRequestNo(clearanceOfDom.eaReference)
            if (exchangeInstance.balanceAs < clearanceOfDom.amountSettledMentionedCurrency) {
                transferInstance.errors.rejectValue('orderClearanceOfDoms', 'transferOrder.clearance.amountSettled.sum.exceed', [clearanceOfDom.eaReference, exchangeInstance.balanceAs, transferInstance.currencyPayCode] as Object[], "The sum of the amounts transferred for the Exchange Authorisation ${clearanceOfDom.eaReference} may not exceed its balance ${exchangeInstance.balanceAs} ${transferInstance.currencyPayCode} ")
            }
        }
    }
}