package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.ExchangeRequestType.EA005_TRANSACTION
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.CODE_AUTHORIZATION_LETTER_6057

@Slf4j("LOGGER")
class CheckAutorizationLetterForEARule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckAutorizationLetterForEARule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkTransaction(exchangeInstance)
    }

    static def checkTransaction(Exchange exchange) {
        List<AttachedDoc> attachements = exchange?.attachedDocs
        if (exchange.requestType == EA) {
            if (exchange.operType == EA005_TRANSACTION && !attachements.any {it.docType == CODE_AUTHORIZATION_LETTER_6057}) {
                exchange.errors.rejectValue('operType', 'exchange.errors.ea005', 'For this transaction, please attach the type of document : 6057 - Lettre d\'autorisation de détention du compte de résident à l\'étranger.')
            }
        }
    }
}
