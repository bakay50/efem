package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class CheckClearanceEcCurrencyCodeRule implements Rule{
    @Override
    void apply(RuleContext ruleContext)
    {
        LOGGER.debug("in apply() of ${CheckClearanceEcCurrencyCodeRule}")
        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom) as ClearanceOfDom
        def params = WebRequestUtils.getParams()
        checkRepatriationCurrencyEqualsEcCurrencyCode(clearanceOfDom, params)
    }

    static def checkRepatriationCurrencyEqualsEcCurrencyCode(ClearanceOfDom clearanceOfDom, params)
    {
        Exchange exchange = Exchange.findByRequestNo(clearanceOfDom?.ecReference)
        if (exchange?.currencyCode != params.currencyCode)
        {
            clearanceOfDom.errors.rejectValue('currencyCodeEC', 'clearanceDom.currencyCodeEC.error')
        }
        clearanceOfDom
    }
}
