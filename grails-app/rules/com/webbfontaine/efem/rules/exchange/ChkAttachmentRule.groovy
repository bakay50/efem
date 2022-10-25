package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class ChkAttachmentRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        def attDocsType = exchangeInstance?.attachedDocs?.docType
        def requiredFacture = AppConfig.requiredAttachments

        if (AppConfig.isRuleEnabled("ChkAttachmentRule")) {
            if(requiredFacture.size() > 0 && (exchangeInstance?.attachedDocs?.size() < 1 || !attDocsType?.intersect(requiredFacture))){
                exchangeInstance.errors.rejectValue("attachedDocs", "exchange.errors.notUsingFacture", [requiredFacture] as Object[],"Invoice attachment is required")
            }
        }
    }
}
