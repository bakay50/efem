package com.webbfontaine.efem.rules.transferOrder

import com.webbfontaine.efem.rules.transferOrder.checking.AmountValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.BankAndImporterValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckClearanceEaReferenceRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckConnectedUserRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckExecutionReferenceRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckTransferAmountRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckTransferApproveRule
import com.webbfontaine.efem.rules.transferOrder.checking.CurrencyCodeValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.ExecutionDateValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.ExecutionValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.ImporterValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.OneEaAtLeastValidationRule
import com.webbfontaine.efem.rules.transferOrder.checking.CheckTransferAmountNegativeRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.UserUtils.isSuperAdministrator

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class TransferBusinessLogicRule implements Rule {

    private static final VALIDATION_RULES = [
            new OneEaAtLeastValidationRule(),
            new ImporterValidationRule(),
            new CurrencyCodeValidationRule(),
            new ExecutionValidationRule(),
            new BankAndImporterValidationRule(),
            new CheckExecutionReferenceRule(),
            new ExecutionDateValidationRule(),
            new CheckTransferAmountNegativeRule(),
            new AmountValidationRule(),
            new CheckTransferAmountRule(),
            new CheckTransferApproveRule(),
            new CheckClearanceEaReferenceRule(),
            new CheckConnectedUserRule()
    ]

    @Override
    void apply(RuleContext ruleContext) {
        if(!isSuperAdministrator()){
            executeSetOfRules(VALIDATION_RULES, ruleContext)
        }
    }

    static void executeSetOfRules(Collection setOfRules, RuleContext ruleContext) {
        setOfRules?.each { Rule rule ->
            LOGGER.info("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }

}
