package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.constants.TvfConstants.INVALID_DATE
import static com.webbfontaine.efem.constants.TvfConstants.INVALID_STATUS
import static com.webbfontaine.efem.rules.RuleUtils.*

class CheckTvfValidityRule implements Rule{

    @Override
    void apply(RuleContext ruleContext){
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        String tvfXmlData
        String tvfDate = exchangeInstance?.tvfDate?.getDayOfMonth()+"-"+exchangeInstance.tvfDate.getMonthOfYear()+"-"+exchangeInstance.tvfDate.getYear()
        tvfXmlData = tvfService.getTvfResponse(exchangeInstance,exchangeInstance.tvfNumber.toString(), tvfDate);
        if(tvfXmlData?.contains(INVALID_STATUS)){
            exchangeInstance.errors.rejectValue("tvfNumber", "efem.errors.tvf.invalidStatus", "The TVF doesn't have a valid status.");
        } else if(tvfXmlData?.contains(INVALID_DATE)){
            exchangeInstance.errors.rejectValue("tvfNumber", "exchange.errors.tvf.expire_date", "The TVF has expired");
        }
    }
}
