package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.constants.ExchangeRequestType.CODE_AUTHORIZATION_LETTER_6059
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.CI_CURRENCY
import static com.webbfontaine.efem.constants.ExchangeRequestType.EXCEPTION_COUNTRY
import static com.webbfontaine.efem.constants.ExchangeRequestType.EXCEPTION_CURRENCY
import static com.webbfontaine.efem.constants.ExchangeRequestType.invoiceCodes

class CheckForeignAccountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {

        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange.class)
        List<AttachedDoc> attachements = exchangeInstance?.attachedDocs
        if (!checkAttachements(attachements)
                && exchangeInstance.requestType == EA
                && !(exchangeInstance?.currencyPayCode in EXCEPTION_CURRENCY)
                && ((exchangeInstance?.countryProvenanceDestinationCode in EXCEPTION_COUNTRY)
                || (exchangeInstance?.countryProvenanceDestinationCode in CI_CURRENCY))) {
            exchangeInstance.errors.rejectValue("attachedDocs", "default.invoiceautorisation.message", "Veuillez joindre la Lettre d'autorisation de détention de compte étranger en devise (code 6059) au dossier SVP");
        }
    }

    boolean checkAttachements(List<AttachedDoc> attachements){
        boolean att =  attachements?.any{ it?.docType in invoiceCodes } && attachements?.any { it?.docType == CODE_AUTHORIZATION_LETTER_6059 }
        return att
    }
}
