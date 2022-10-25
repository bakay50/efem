package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class AmountValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        transferInstance.orderClearanceOfDoms.each() {
            checkIfAmountSettledIsValid(it, transferInstance)
            checkIfAmountSettledIsGreaterThanZero(it, transferInstance)
            checkIfAmountToBeSettledIsValid(it, transferInstance)
            checkIfAmountRequestedIsGreaterThanZero(it, transferInstance)
        }
    }

    def checkIfAmountSettledIsValid(OrderClearanceOfDom clearance, TransferOrder transferInstance){
        if (clearance.amountSettledMentionedCurrency && clearance.amountSettledMentionedCurrency > clearance.amountRequestedMentionedCurrency) {
            transferInstance.errors.rejectValue("orderClearanceOfDoms", "orderClearanceOfDom.amountSettledMentionedCurrency.greater.error", "The Amount settled can not be greater than the amount requested.")
        }
    }

    def checkIfAmountSettledIsGreaterThanZero(OrderClearanceOfDom clearance, transferInstance){
        if(UserUtils.isBankAgent() && transferInstance?.startedOperation in [Operation.VALIDATE] && clearance.amountSettledMentionedCurrency <= BigDecimal.ZERO){
            transferInstance.errors.rejectValue("orderClearanceOfDoms", "execution.errors.amountSettledMentionedCurrency", "Amount to be paid in mentioned currency must be greater than zero.")
        }
    }

    def checkIfAmountToBeSettledIsValid(OrderClearanceOfDom clearance, transferInstance){
        if (clearance.amountRequestedMentionedCurrency  &&  clearance.amountToBeSettledMentionedCurrency < clearance.amountRequestedMentionedCurrency) {
            transferInstance.errors.rejectValue("orderClearanceOfDoms", "orderClearanceOfDom.amountToBeSettledMentionedCurrency.greater.error", "The Amount requested can not be greater than the amount to be settled.")
        }
    }

    def checkIfAmountRequestedIsGreaterThanZero(OrderClearanceOfDom clearance, transferInstance){
        if (clearance?.amountRequestedMentionedCurrency <= BigDecimal.ZERO) {
            transferInstance.errors.rejectValue("orderClearanceOfDoms", "orderClearanceOfDom.amountRequestedMentionedCurrency.zero.error", "The amount requested in mentioned currency must be greater than Zero.")
        }
    }
}