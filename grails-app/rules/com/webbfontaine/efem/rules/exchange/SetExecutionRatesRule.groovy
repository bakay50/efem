package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.grails.web.servlet.mvc.GrailsWebRequest

class SetExecutionRatesRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        def params = GrailsWebRequest.lookup().params
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange.class)

        exchangeInstance.currencyRate = TypeCastUtils.parseCurrencyApplyPattern(params.currencyRate, true) as BigDecimal
        exchangeInstance.currencyPayRate = TypeCastUtils.parseCurrencyApplyPattern(params.currencyPayRate, true) as BigDecimal
    }
}
