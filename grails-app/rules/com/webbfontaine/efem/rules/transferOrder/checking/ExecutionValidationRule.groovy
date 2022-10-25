package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.transferOrder.TransferOrderService
import com.webbfontaine.wfutils.AppContextUtils
import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 19/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExecutionValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        TransferOrderService transferOrderService = AppContextUtils.getBean(TransferOrderService)
        if (transferInstance?.startedOperation in [REQUEST, REQUEST_QUERIED]){
             if (transferOrderService.checkExecutionRef(transferInstance) && transferInstance?.executionRef) {
                transferInstance.errors.rejectValue("executionRef", "transferOrder.executionRef.unique.error", "This execution reference has already been used this year in another transfer order document")
            }
        }
    }

}