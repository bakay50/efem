package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckRepatriatedAmountRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom) as ClearanceOfDom
        checkSumOfRepatriatedAmounts(clearanceOfDom)
    }

    def checkSumOfRepatriatedAmounts(ClearanceOfDom clearanceOfDom){

        if (clearanceOfDom?.repats){
            def clearances = clearanceOfDom?.repats?.clearances
            def currentClearancesList = clearances.findAll {it?.ecReference == clearanceOfDom?.ecReference && it.status == true}
                def totalRepatriatedAmount = currentClearancesList*.repatriatedAmtInCurr?.sum() ?: BigDecimal.ZERO
                if(totalRepatriatedAmount > clearanceOfDom?.domAmtInCurr){
                    clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.sum', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Domiciliated Amount in Currency')
                }
            }

        }
}
