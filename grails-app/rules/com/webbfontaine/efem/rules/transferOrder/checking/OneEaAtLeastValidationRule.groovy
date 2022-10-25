package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 13/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class OneEaAtLeastValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        if (!transferInstance?.orderClearanceOfDoms || transferInstance?.orderClearanceOfDoms?.size() == 0) {
            transferInstance.errors.rejectValue("orderClearanceOfDoms", "transferOrder.orderClearanceOfDoms.oneAtLeast.error", "Please add at least one foreign exchange authorization to the transfer order document.")
        }
    }

}


