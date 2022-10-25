package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j('LOGGER')
class ClearanceOfDomBusinessLogicRule implements Rule {

    private static def COMMON_CLEARANCEOFDOM_RULES = [
            new CheckRepatriatedAmountRule(),
            new CheckClearanceEcCurrencyCodeRule(),
            new CheckClearanceRepatriatedAmtInCurrNotNullRule(),
            new CheckClearanceEcReferenceRule(),
            new CheckAmountReceivedRule()
    ]

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${ClearanceOfDomBusinessLogicRule}")

        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom)
        Repatriation domainInstance = clearanceOfDom.repats
        def params = WebRequestUtils.getParams()
        def conversationId = params.conversationId

        LOGGER.debug("id = {}, cid = {} ClearanceOfDom # {}. Started. ", domainInstance?.id, conversationId, clearanceOfDom?.rank)
        if(params?.action?.toString()?.equalsIgnoreCase(RepatriationConstants.CLEARANCE_ADD_ACTION)){
            RuleUtils.executeSetOfRules(COMMON_CLEARANCEOFDOM_RULES, ruleContext)
        }

    }
}
