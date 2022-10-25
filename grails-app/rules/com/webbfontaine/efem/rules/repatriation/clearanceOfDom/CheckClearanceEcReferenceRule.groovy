package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.RepatriationService
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class CheckClearanceEcReferenceRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckClearanceEcReferenceRule}")
        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom) as ClearanceOfDom
        Repatriation repatriationInstance = getRepatriation()
        ecReferenceChecking(clearanceOfDom, repatriationInstance)
    }

    static def ecReferenceChecking(ClearanceOfDom clearanceOfDom, Repatriation repatriation) {
        def result = repatriation?.clearances?.any {it.ecReference == clearanceOfDom.ecReference && it.rank != clearanceOfDom.rank && it.status == true}
        if (result) {
            clearanceOfDom.errors.rejectValue('ecReference', 'clerance.errors.ecReference.checking', 'You are not allowed to add the same EC reference multiple times in a current session.')
        }
    }

    static def getRepatriation() {
        def params = WebRequestUtils.getParams()
        RepatriationService  service = Holders.applicationContext.getBean("repatriationService")
        Repatriation repatriation = service.findFromSessionStore(params?.conversationId)
        repatriation
    }
}
