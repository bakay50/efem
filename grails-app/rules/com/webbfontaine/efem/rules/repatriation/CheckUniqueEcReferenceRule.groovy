package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class CheckUniqueEcReferenceRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckUniqueEcReferenceRule}")
        Repatriation repatriation = ruleContext.getTargetAs(Repatriation) as Repatriation
            for (clearance in repatriation?.clearances) {
                if (EcReferenceChecking(repatriation, clearance)) {
                    repatriation.errors.reject('clerance.errors.ecReference.checking',[clearance?.ecReference] as Object[], "${clearance?.ecReference} : You are not allowed to add the same EC reference multiple times in a current session.")
                    break
                }
            }
    }

    static def EcReferenceChecking(Repatriation repatriation, ClearanceOfDom clearance) {
        return repatriation?.clearances?.any {it.ecReference == clearance.ecReference && it.rank != clearance.rank && it.status == true}.booleanValue()
    }


}
