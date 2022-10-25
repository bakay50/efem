package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckTransferAmountNegativeRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        List<String> fields = ["transferAmntRequested","transferNatAmntRequest"]
        fields.each {field->
            if(transferInstance?."${field}" && transferInstance?."${field}" < BigDecimal.ZERO){
                transferInstance.errors.rejectValue("${field}", "common.errors.negativeAmount", [transferInstance."${field}"] as Object[], "a negative amount is not allowed [ " + transferInstance."${field}" + " ]")
            }
        }
    }
}
