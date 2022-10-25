package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j

@Slf4j
class CheckDeclarantFromTVForSADRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        log.debug("in apply of ${CheckDeclarantFromTVForSADRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchangeInstance?.basedOn == ExchangeRequestType.BASE_ON_TVF) {
            checkTvfDeclarantCode(exchangeInstance)
        }
    }


    def checkTvfDeclarantCode(Exchange exchangeInstance) {
        Tvf tvfDetails = getTvfDetails(exchangeInstance?.tvfNumber?.toString(), FormattingUtils.fromDate(exchangeInstance?.tvfDate))
        if (tvfDetails?.decCode && !exchangeInstance?.declarantCode.equals(tvfDetails?.decCode)) {
            exchangeInstance.errors.rejectValue("declarantCode", "exchange.errors.tvf.declarantCode.error", "Declarant code non consistent with that of the TVF")
        }
    }

    Tvf getTvfDetails(tvfNumber, tvfDate) {
        TvfService tvfService = AppContextUtils.getBean(TvfService)
        tvfService.retrieveTvf(tvfNumber, tvfDate)
    }

}
