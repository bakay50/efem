package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_SAD
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA006_TRANSACTION
import static com.webbfontaine.efem.constants.ExchangeRequestType.CODE_OTHERS_6058

@Slf4j("LOGGER")
class CheckEATransactionFromSadRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckEATransactionFromSadRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkTransaction(exchangeInstance)
    }

    static def checkTransaction(Exchange exchange) {
        List<AttachedDoc> attachements = exchange?.attachedDocs
        checkAttachementAndOperType(exchange, attachements)
    }

    private static void checkAttachementAndOperType(Exchange exchange, List<AttachedDoc> attachements) {
        if (exchange.basedOn == BASE_ON_SAD && exchange.requestType == EA && checkDocTypeAndOperType(exchange, attachements)) {
            exchange.errors.rejectValue('operType', 'exchange.errors.ea006.attachments', 'For this transaction, please attach the type of document : 6058 - Autres.')
        }
    }

    private static boolean checkDocTypeAndOperType(Exchange exchange, List<AttachedDoc> attachements) {
        exchange.operType == EA006_TRANSACTION && !attachements.any { it.docType == CODE_OTHERS_6058 }
    }
}
