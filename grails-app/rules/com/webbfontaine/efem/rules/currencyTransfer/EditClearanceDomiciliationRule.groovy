package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constant.CurrencyTransferConstants
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 24/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class EditClearanceDomiciliationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        CurrencyTransfer currencyTransferInstance = ruleContext.getTargetAs(CurrencyTransfer) as CurrencyTransfer
        def params = WebRequestUtils.getParams()
        def paramsRank = params?.rank ?: 0
        def ecReference = params?.ecReference ?: ''
        def amountTransferredInCurr = params?.amountTransferredInCurr ?: ''
        ClearanceDomiciliation domiciliation = currencyTransferInstance.getClearanceDomiciliation(paramsRank as Integer, ecReference)
        if (domiciliation) {
            checkAmountTransferredInCurrZero(domiciliation, amountTransferredInCurr)
            checkEcReference(domiciliation, currencyTransferInstance, ecReference)
            int result = checkAmountTransferredInCurr(domiciliation, params, currencyTransferInstance)
            if (result == 1 && domiciliation.errors.errorCount == 0) {
                if (currencyTransferInstance?.clearanceDomiciliations?.size() > 1) {
                    domiciliation.errors.rejectValue(CurrencyTransferConstants.amountTransferredInCurr,
                            'clerance.errors.amountTransferredInCurr.sum',
                            'The sum of the amounts transferred in foreign currency for this EC reference ' +
                                    'foreign exchange liability must not exceed the amount transferred.')
                } else {
                    domiciliation.errors.rejectValue(CurrencyTransferConstants.amountTransferredInCurr,
                            'clerance.errors.amountTransferredInCurr.Great',
                            'The amount may not exceed the amount of the foreign exchange commitment.')
                }
            }
        }
        domiciliation
    }

    ClearanceDomiciliation checkAmountTransferredInCurrZero(ClearanceDomiciliation domiciliation, def amountTransferredInCurr) {
        if (amountTransferredInCurr in CurrencyTransferConstants.ZERO || !amountTransferredInCurr) {
            domiciliation.errors.rejectValue('amountTransferredInCurr',
                    'clerance.errors.amountTransferredInCurr.zero',
                    'he Amount transferred in Currency must be greater than zero.')
        }
        domiciliation
    }

    int checkAmountTransferredInCurr(ClearanceDomiciliation domiciliation, def params, CurrencyTransfer currencyTransferInstance) {
        updateParamsFields(params)
        def allPrevAmount = currencyTransferInstance?.clearanceDomiciliations?.findAll {
            it?.ecReference == domiciliation?.ecReference && !it?.id
        }?.amountTransferredInCurr?.sum()
        def totalSumRep = allPrevAmount ?: BigDecimal.ZERO
        def total
        def newAmountTransferredInCurr = TypeCastUtils.parseStringToBigDecimal(params?.amountTransferredInCurr, false)
        if (currencyTransferInstance?.clearanceDomiciliations?.size() > 1) {
            total = (totalSumRep - domiciliation?.amountTransferredInCurr) + newAmountTransferredInCurr
        } else {
            total = newAmountTransferredInCurr
        }
        total?.compareTo(currencyTransferInstance?.amountTransferred)
    }

    ClearanceDomiciliation checkEcReference(ClearanceDomiciliation domiciliation,
                                            CurrencyTransfer currencyTransferInstance,
                                            def ecReference) {
        if ((ecReference && ecReference != domiciliation?.ecReference) && currencyTransferInstance?.clearanceDomiciliations?.findAll {
            it?.ecReference?.equalsIgnoreCase(domiciliation?.ecReference) && !it?.id
        }?.size() == 1) {
            domiciliation.errors.rejectValue('ecReference',
                    'clerance.errors.ecReference.checking',
                    'You are not allowed to add the same EC reference multiple times in a current session.')
        }
        domiciliation
    }

    def updateParamsFields(Map params) {
        String local = LocaleContextHolder.getLocale().toString()
        if (local in CurrencyTransferConstants.ENGLISH_FIELDS) {
            return null
        } else {
            CurrencyTransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS.each {
                def refValue
                if (params."${it}") {
                    if (it in CurrencyTransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS) {
                        refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${it}", true)
                        params."${it}" = refValue?.toBigDecimal()
                    } else {
                        refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${it}", false)
                        params."${it}" = refValue?.toBigDecimal()
                    }
                }
            }
        }
    }

}
