package com.webbfontaine.efem.rules.rimm.bank

import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class BankBusinessLogicRule implements Rule{

    private static final VALIDATION_RULES = [
        new CheckBank()
    ]
    @Override
    void apply(RuleContext ruleContext) {
        executeSetOfRules(VALIDATION_RULES, ruleContext)
    }

    static void executeSetOfRules(Collection setOfRules, RuleContext ruleContext) {
        setOfRules?.each { Rule rule ->
            LOGGER.info("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }
}
