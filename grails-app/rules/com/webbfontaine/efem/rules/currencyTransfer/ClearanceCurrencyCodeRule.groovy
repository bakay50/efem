package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.grails.datastore.mapping.query.api.BuildableCriteria

import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.Statuses.ST_CEDED
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 17/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ClearanceCurrencyCodeRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {

        ClearanceDomiciliation clearanceDomiciliation = ruleContext.getTargetAs(ClearanceDomiciliation) as ClearanceDomiciliation
        List connectedBankCode = userPropertyValueAsList(UserProperties.ADB)
        def params = WebRequestUtils.getParams()
        Exchange exchange = Exchange?.findByRequestNo(clearanceDomiciliation?.ecReference)

        if (!exchange?.currencyCode?.equalsIgnoreCase(params.currencyCodeTransfer)) {
            clearanceDomiciliation.errors.rejectValue("ecReference", "currencyTransfer.errors.currency.code.label", "The Transfer Currency must be identical to the Exchange Commitment Currency to be cleared")
        }
        BuildableCriteria criteria = ClearanceOfDom.createCriteria()
        List<ClearanceOfDom> clearanceRepatriation = criteria.list {
            eq("ecReference", clearanceDomiciliation.ecReference)
            projections {
                repats {
                    'in'("repatriationBankCode", connectedBankCode)
                    'in'("status", [ST_CONFIRMED, ST_CEDED])
                }
            }
        } as List<ClearanceOfDom>
        int repatsCount = clearanceRepatriation?.size()
        if( repatsCount < 1 ){
            clearanceDomiciliation.errors.rejectValue("ecReference", "currencyTransfer.errors.repatriation.bank", "The Exchange Commitment has not been repatriated to your bank.")
        }

    }

}
