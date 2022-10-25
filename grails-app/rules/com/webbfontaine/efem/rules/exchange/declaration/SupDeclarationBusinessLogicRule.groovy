package com.webbfontaine.efem.rules.exchange.declaration

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.UtilConstants.SUPDECLARATION

@Slf4j("LOGGER")
class SupDeclarationBusinessLogicRule implements Rule {

    private static def COMMON_RULES = [
            new CheckSadDeclarationRule()
    ]

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("apply() of ${SupDeclarationBusinessLogicRule}")
        def params = WebRequestUtils.getParams()
        if (params?.controller == SUPDECLARATION) {
            SupDeclaration supDeclaration = ruleContext.getTargetAs(SupDeclaration)
            Exchange exchange = supDeclaration.exchange
            def conversationId = params.conversationId
            LOGGER.debug("id = {}, cid = {} declaration # {}. Started. ", exchange?.id, conversationId, supDeclaration?.rank)
            RuleUtils.executeSetOfRules(COMMON_RULES, ruleContext)
        }
    }

}
