package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.constants.NatureOfFund
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.workflow.Operation.UPDATE_CONFIRMED

@Slf4j("LOGGER")
class AtleastOneEcValidationRule implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriationInstance = ruleContext.getTargetAs(Repatriation) as Repatriation
        LOGGER.debug("in apply() of ${AtleastOneEcValidationRule} Repatriation ID : ${repatriationInstance?.id}")
        boolean isPrefinancingUpdateConfirmed = repatriationInstance?.natureOfFund == NatureOfFund.NOF_PRE && repatriationInstance?.startedOperation == UPDATE_CONFIRMED
        if ((!repatriationInstance?.clearances || repatriationInstance?.clearances.findAll{it.status == true}?.size() == 0) && (repatriationInstance?.natureOfFund != NatureOfFund.NOF_PRE || isPrefinancingUpdateConfirmed)) {
            repatriationInstance.errors.rejectValue("clearances", "repatriation.clearances.oneAtLeast.error", "Please add at least one Exchange Commitment Referencee or the Statement of Account Please.")
        }
    }
}
