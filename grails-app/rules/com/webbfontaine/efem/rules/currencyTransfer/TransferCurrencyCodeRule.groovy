package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 17/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferCurrencyCodeRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {

        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        def connectedBankCode = userPropertyValueAsList(UserProperties.ADB)
        def params = WebRequestUtils.getParams()
        if (!exchange?.currencyCode?.equalsIgnoreCase(params.currencyCodeTransfer)) {
            exchange.errors.rejectValue("currencyCode", "currencyTransfer.errors.currency.code.label", "The Transfer Currency must be identical to the Exchange Commitment Currency to be cleared")
        }
        List<ClearanceOfDom> clearanceRepatriation = ClearanceOfDom.findAllByEcReferenceAndBankCodeInList(exchange?.requestNo, connectedBankCode)
        int repatsCount = clearanceRepatriation.repats.findAll {it.status.equalsIgnoreCase(ST_CONFIRMED)}.size()
        if( repatsCount < 1 ){
            exchange.errors.rejectValue("requestNo", "currencyTransfer.errors.repatriation.status", "The repatriation document for this Exchange commitment is not in the correct status")
        }
    }

}
