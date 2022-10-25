package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckAmountRepatriatedRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriationInstance = ruleContext.getTargetAs(Repatriation) as Repatriation
        checkSumOfClearanceAndRepatrietedAmounts(repatriationInstance)
    }

    def checkSumOfClearanceAndRepatrietedAmounts(Repatriation repatriationInstance) {

        List<ClearanceOfDom> clearances = repatriationInstance?.clearances.findAll {it.status == true}
        BigDecimal sumOfClearanceAmount = clearances*.repatriatedAmtInCurr?.sum() ?: BigDecimal.ZERO
        if (sumOfClearanceAmount > repatriationInstance.receivedAmount?:BigDecimal.ZERO) {
            repatriationInstance.errors.rejectValue('receivedAmount', 'clearance.errors.repatriatedAmtInCurr.sum', 'Sum of Repatriated Amount by Exchange Commitment can not exceed the Received Amount Transferred')
        }
    }
}
