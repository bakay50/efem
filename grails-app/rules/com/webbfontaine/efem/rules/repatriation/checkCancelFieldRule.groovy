package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.Repatriation
import static com.webbfontaine.efem.workflow.Operation.CANCEL_CONFIRMED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.workflow.Operation.QUERY_DECLARED

class checkCancelFieldRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriationInstance = ruleContext.getTargetAs(Repatriation) as Repatriation
        if (repatriationInstance?.startedOperation in [CANCEL_CONFIRMED, CANCEL_QUERIED, QUERY_DECLARED] && !repatriationInstance?.comments) {
            repatriationInstance.errors.rejectValue("comments", "client.transfer.queryMandatoryMessage", "Provide reason for this operation please.");
        }
    }
}
