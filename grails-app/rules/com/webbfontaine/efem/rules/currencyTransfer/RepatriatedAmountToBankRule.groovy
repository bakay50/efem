package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.CurrencyTransferConstants
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.grails.datastore.mapping.query.api.BuildableCriteria

class RepatriatedAmountToBankRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        ClearanceDomiciliation clearanceDomiciliation = ruleContext.getTargetAs(ClearanceDomiciliation) as ClearanceDomiciliation
        handleRule(clearanceDomiciliation)
    }

    static def handleRule(ClearanceDomiciliation clearanceDomiciliation) {
        String bankCode = UserUtils.getUserProperty(UserProperties.BNK) ?: UserUtils.getUserProperty(UserProperties.ADB)
        BuildableCriteria criteria = ClearanceOfDom.createCriteria()
        List<ClearanceOfDom> domList = criteria.list {
            eq("ecReference", clearanceDomiciliation.ecReference)
            projections {
                repats {
                    eq("repatriationBankCode", bankCode)
                    'in'("status", [Statuses.ST_CONFIRMED, Statuses.ST_CEDED])
                }
            }
        } as List<ClearanceOfDom>
        BigDecimal allRepatriatedAmtInCurr = domList*.repatriatedAmtInCurr?.sum() as BigDecimal
        BigDecimal totalAmount = allRepatriatedAmtInCurr ?: BigDecimal.ZERO
        if (totalAmount < clearanceDomiciliation.amountTransferredInCurr && clearanceDomiciliation.amountTransferredInCurr) {
            clearanceDomiciliation.errors.reject('clearanceDom.repatriatedAmountToBank.error')
        }
        if(clearanceDomiciliation.repatriatedAmountToBank < clearanceDomiciliation.amountTransferredInCurr){
            clearanceDomiciliation.errors.reject(
                    'clerance.errors.amountTransferredInCurr.repatriatedAmountToBank',
                    'The amount transferred in currency may not exceed the amount repatriated to bank.')
        }
    }
}
