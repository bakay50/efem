package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.rimm.RimmGeoArea
import com.webbfontaine.efem.rimm.RimmOpt
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.rimm.country.Country
import com.webbfontaine.grails.plugins.rimm.cuo.CustomsOffice
import com.webbfontaine.grails.plugins.rimm.currency.Currency
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.grails.plugins.validation.rules.ref.setter.RefValueSetterRule
import grails.util.Holders
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.ExchangeRequestType.*
import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class ExchangeBusinessLogicRule implements Rule {

    private static final SETTER_RULES = [
            new RefValueSetterRule(Country, 'countryOfExportCode', true, false, 'countryOfExportName'),
            new RefValueSetterRule(Country, 'nationalityCode', true, false, 'nationalityName'),
            new RefValueSetterRule(RimmOpt, 'operType', true, false, 'operTypeName'),
            new RefValueSetterRule(Country, 'countryProvenanceDestinationCode', true, false, 'countryProvenanceDestinationName'),
            new RefValueSetterRule(CustomsOffice, 'clearanceOfficeCode', true, false, 'clearanceOfficeName'),
            new RefValueSetterRule(Bank, 'bankCode', true, false, 'bankName'),
            new RefValueSetterRule(Currency, 'currencyCode', true, false, 'currencyName'),
            new RefValueSetterRule(Currency, 'currencyPayCode', true, false, 'currencyPayName'),
            new RefValueSetterRule(Country, 'countryOfDestinationCode', true, false, 'countryOfDestinationName'),
            new RefValueSetterRule(RimmGeoArea, 'geoArea', true, false, 'geoAreaName'),
            new RefValueSetterRule(RimmGeoArea, 'areaPartyCode', true, false, 'areaPartyName'),
            new RefValueSetterRule(Country, 'countryPartyCode', true, false, 'countryPartyName'),
            new SetExecutionRatesRule()
    ]

    private static final VALIDATION_RULES = [
            new MandatoryFieldRule(),
            new ChkAttachmentRule(),
            new ChkNamesAndPartiesRule(),
            new CheckSadMandatoryFields(),
            new CheckCountryDestinationRule(),
            new CheckCountryProvenanceRule(),
            new CheckEATransactionFromTvfRule(),
            new CheckAutorizationLetterForEARule(),
            new CheckEATransactionFromSadRule(),
            new CheckRegistrationNumberBankRule(),
            new CheckSadAttachedDocumentForECRule(),
            new CheckDeclarantCodeForECRule(),
            new CheckOperationTypeCompatibilityRule(),
            new CheckNamesAndPartiesFromSADRule(),
            new CheckAmountMentionnedInCurrRule(),
            new CheckDeclarantFromTVForSADRule(),
            new CheckMandatoryRegistrationNumberBankRule()
    ]

    private static final TVF_RULES = [
            new CheckUnclearedEaRule(),
            new CheckNamesAndPartiesFromTVFRule(),
            new CheckTVFAmountRule()
    ]

    private static final TVF_RULES_BJ = [
            new ChkTotalAmountInCurrencyRule()
    ]

    private static final STORE_RULES = [
            new ChkCurrencyAndCountryOfExportRule(),
            new CheckWAEMUCountryExportAndCurrencyCodeRule(),
    ]

    private static final REQUEST_RULES = [
            new ChkCurrencyAndCountryOfExportRule(),
            new CheckOperationTypeCompatibilityRule(),
            new CheckAttachedInvoiceRule(),
            new CheckGeoAreaRule(),
            new CheckWAEMUCountryExportAndCurrencyCodeRule(),
            new CheckForeignAccountRule(),
            new CheckExporterECRule(),
            new CheckAmountNegativeRule()
    ]

    private static final TVF_QUERIED_RULES = [
            new TvfRule()
    ]

    private static final SAD_UPDATED_RULES = [
            new CheckCurrencyCodeSadAndEcDocumentRule()
    ]

    private static final BALANCE_UPDATED_RULES = [
            new CheckBalanceAndFinalAmountRule(),
            new CheckSadRule(),
            new CheckFinalInvoiceAttachedFiles(),
            new CheckFinalInvoiceAmountRule()
    ]

    private static final SAD_RULES = [
            new CheckSadDeclaration()
    ]

    private static final CANCEL_RULES = [
            new CheckTransferOrderRule()
    ]

    private static final SAD_AMOUNT_RULES = [
            new ChkSadAmountRule(),
            new CheckTotalAmountRule(),
            new CheckEAFromSadAmountRule()
    ]

    private static final MANDATORY_FIELDS_RULE = [
            new MandatoryFieldRule(),
    ]

    @Override
    void apply(RuleContext ruleContext) {

        def commitOp = WebRequestUtils.params.commitOperation
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        boolean isWebService = Holders.config.rest?.isWebService

        if (BusinessLogicUtils.isLoadedDocumentValid(exchangeInstance) && !isRejectOrCancelOperation()) {
            executeSetOfRules(VALIDATION_RULES, ruleContext)
        }

        if (isRequestOperation()) {
            executeSetOfRules(REQUEST_RULES, ruleContext)
        }

        if (commitOp in [STORE, UPDATE_STORED]) {
            executeSetOfRules(STORE_RULES, ruleContext)
        }

        if (commitOp in [CANCEL_APPROVED]) {
            executeSetOfRules(CANCEL_RULES, ruleContext)
        }

        if (commitOp in [UPDATE_QUERIED, REQUEST_QUERIED] &&
                BASE_ON_TVF.equals(exchangeInstance?.basedOn)) {
            executeSetOfRules(TVF_QUERIED_RULES, ruleContext)
        }

        if (commitOp in [UPDATE_APPROVED, UPDATE_EXECUTED]) {
            executeSetOfRules(BALANCE_UPDATED_RULES, ruleContext)
            if (EC.equals(exchangeInstance.requestType)) {
                executeSetOfRules(SAD_UPDATED_RULES, ruleContext)
            }
        }
        if (commitOp in [UPDATE_APPROVED] && exchangeInstance.requestType == EC && exchangeInstance.hasErrors() && BusinessLogicUtils.checkExchangeFieldsErrors(exchangeInstance)) {
            exchangeInstance.sadInstanceId = null
        }

        if (isRequestOperation() && BASE_ON_TVF.equals(exchangeInstance?.basedOn)) {
            executeSetOfRules(TVF_RULES, ruleContext)
            if (isWebService)
                executeSetOfRules(TVF_RULES_BJ, ruleContext)
        }

        if (isRequestOperation() && BASE_ON_SAD.equals(exchangeInstance?.basedOn) && EA == exchangeInstance?.requestType) {
            executeSetOfRules(SAD_RULES, ruleContext)
            if (!hasSomeIncoterm(exchangeInstance)) {
                executeSetOfRules(SAD_AMOUNT_RULES, ruleContext)
            }
        }
        if (isRejectOrCancelOperation()) {
            executeSetOfRules(MANDATORY_FIELDS_RULE, ruleContext)
        }

        if (!isRejectOrCancelOperation()) {
            executeSetOfRules(SETTER_RULES, ruleContext)
        }

    }

    static void executeSetOfRules(Collection setOfRules, RuleContext ruleContext) {
        setOfRules?.each { Rule rule ->
            LOGGER.info("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }

    private static boolean isRequestOperation() {
        def commitOperation = WebRequestUtils.params.commitOperation
        List operations = [VERIFY, REQUEST, REQUEST_STORED, REQUEST_QUERIED, UPDATE_QUERIED]
        operations.contains(commitOperation)
    }

    static boolean hasSomeIncoterm(Exchange exchange) {
        return exchange?.sadIncoterm in ExchangeRequestType.INCOTERMS_AUTHORIZED
    }

    private static boolean isRejectOrCancelOperation() {
        return WebRequestUtils.params.commitOperation in [QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED, REJECT_REQUESTED, REJECT_PARTIALLY_APPROVED, CANCEL_APPROVED, CANCEL_QUERIED]
    }

}
