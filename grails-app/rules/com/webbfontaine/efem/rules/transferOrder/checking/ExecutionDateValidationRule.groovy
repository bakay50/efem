package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.joda.time.LocalDate

class ExecutionDateValidationRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferOrderInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        LocalDate currentDate = new LocalDate()
        if(transferOrderInstance?.executionDate > currentDate){
            transferOrderInstance.errors.rejectValue("executionDate", "transferOrder.executionDate.greater.error", "Execution Date must not be greater than current date.")
        }
    }
}
