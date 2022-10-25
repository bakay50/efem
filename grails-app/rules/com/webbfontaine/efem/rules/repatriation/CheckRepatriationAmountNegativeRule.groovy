package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckRepatriationAmountNegativeRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriationInstance = ruleContext.getTargetAs(Repatriation) as Repatriation
        List<String> fields = ["receivedAmount","receivedAmountNat"]
        fields.each {field->
            if(repatriationInstance?."${field}" && repatriationInstance?."${field}" < BigDecimal.ZERO){
                repatriationInstance.errors.rejectValue("${field}", "common.errors.negativeAmount", [repatriationInstance."${field}"] as Object[], "a negative amount is not allowed [ " + repatriationInstance."${field}" + " ]")
            }
        }
    }
}
