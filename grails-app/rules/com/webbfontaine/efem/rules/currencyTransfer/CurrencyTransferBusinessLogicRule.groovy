package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.grails.plugins.rimm.currency.Currency
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.grails.plugins.validation.rules.ref.setter.RefValueSetterRule
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.workflow.Operation.CANCEL_TRANSFERRED
import static com.webbfontaine.efem.workflow.Operation.TRANSFER
import static com.webbfontaine.efem.workflow.Operation.TRANSFER_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_TRANSFERRED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 17/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class CurrencyTransferBusinessLogicRule implements Rule {

    private static final TRANSFER_CURRENCY_RULES = [
            new ValidationCurrencyTransferRule(),
    ]

    private static final CANCEL_TRANSFER_RULES = [
            new CancelTransferFieldRule(),
    ]

    private static final SETTER_RULES = [
            new RefValueSetterRule(Currency, 'currencyCode', true, false, 'currencyName')
    ]

    @Override
    void apply(RuleContext ruleContext) {
        def commitOp = WebRequestUtils.params.commitOperation
        executeSetOfRules(SETTER_RULES, ruleContext)
        if(commitOp == CANCEL_TRANSFERRED ) {
            executeSetOfRules(CANCEL_TRANSFER_RULES, ruleContext)
        }
        if(commitOp in [TRANSFER_STORED,TRANSFER, UPDATE_TRANSFERRED]  ) {
            executeSetOfRules(TRANSFER_CURRENCY_RULES, ruleContext)
        }
    }

    static void executeSetOfRules(Collection setOfRules, RuleContext ruleContext) {
        setOfRules?.each { Rule rule ->
            LOGGER.info("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }

}
