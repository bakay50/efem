package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.grails.plugins.rimm.country.Country
import com.webbfontaine.grails.plugins.rimm.currency.Currency
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.ref.setter.RefValueSetterRule

import static com.webbfontaine.efem.UserUtils.isSuperAdministrator
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.workflow.Operation.CANCEL_CONFIRMED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CONFIRM
import static com.webbfontaine.efem.workflow.Operation.CONFIRM_DECLARED
import static com.webbfontaine.efem.workflow.Operation.CONFIRM_STORED
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.DECLARE
import static com.webbfontaine.efem.workflow.Operation.DECLARE_QUERIED
import static com.webbfontaine.efem.workflow.Operation.DECLARE_STORED
import static com.webbfontaine.efem.workflow.Operation.QUERY_DECLARED
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_CONFIRMED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_QUERIED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED
import static com.webbfontaine.efem.workflow.Operation.VERIFY

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class RepatriationBusinessLogicRule implements Rule {
    private static final VALIDATION_RULES = [
            new AtleastOneEcValidationRule(),
            new RepatriationAttachedFile(),
            new CheckCountryOfProvenanceRule(),
            new CheckUniqueReferenceExecutionRepatriationDocRule(),
            new CheckRepatriationAmountNegativeRule(),
            new CheckConnectedUserRule()
            //new CheckUniqueEcReferenceRule()
    ]

    private static final CANCEL_RULES = [
            new checkCancelFieldRule()
    ]

    private static final SUBMIT_RULES = [
            new CheckAmountRepatriatedRule()
    ]

    private static final SETTER_RULES = [
            new RefValueSetterRule(Currency, 'currencyCode', true, false, 'currencyName'),
            new RefValueSetterRule(Country, 'countryOfOriginCode', true, false, 'countryOfOriginName')
    ]

    @Override
    void apply(RuleContext ruleContext) {
        executeSetOfRules(SETTER_RULES, ruleContext)
        if (!isSuperAdministrator() && !isCancelOperation()) {
            executeSetOfRules(VALIDATION_RULES, ruleContext)
        }
        if (isSubmitOperation()) {
            executeSetOfRules(SUBMIT_RULES, ruleContext)
        }
        if (isCancelOperation()) {
            executeSetOfRules(CANCEL_RULES, ruleContext)
        }
    }

    static void executeSetOfRules(Collection setOfRules, RuleContext ruleContext) {
        setOfRules?.each { Rule rule ->
            LOGGER.info("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }

    private static boolean isCancelOperation() {
        WebRequestUtils.params.commitOperation in [CANCEL_QUERIED, CANCEL_CONFIRMED, QUERY_DECLARED]
    }

    private static boolean isSubmitOperation() {
        WebRequestUtils.params.commitOperation in [VERIFY, CREATE, STORE, DECLARE, DECLARE_QUERIED, DECLARE_STORED, CONFIRM, CONFIRM_DECLARED, CONFIRM_STORED, UPDATE_CONFIRMED, UPDATE_STORED, UPDATE_QUERIED]
    }

}
