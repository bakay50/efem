package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class OrderClearanceAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        OrderClearanceOfDom order = ruleContext.getTargetAs(OrderClearanceOfDom) as OrderClearanceOfDom
        checkIfAmountSettledIsValid(order)
        checkIfAmountToBeSettledIsValid(order)
    }

    static def checkIfAmountSettledIsValid(OrderClearanceOfDom clearance){
        if (clearance?.amountSettledMentionedCurrency > clearance?.amountRequestedMentionedCurrency) {
            clearance.errors.rejectValue("eaReference", "orderClearanceOfDom.amountSettledMentionedCurrency.greater.error", "The Amount settled can not be greater than the amount requested.")
        }
    }

    static def checkIfAmountToBeSettledIsValid(OrderClearanceOfDom clearance){
        if (clearance.amountRequestedMentionedCurrency  &&  clearance.amountToBeSettledMentionedCurrency < clearance.amountRequestedMentionedCurrency) {
            clearance.errors.rejectValue("eaReference", "orderClearanceOfDom.amountToBeSettledMentionedCurrency.greater.error", "The Amount requested can not be greater than the amount to be settled.")
        }
    }
}