package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.joda.time.LocalDate

import static com.webbfontaine.efem.constants.ExchangeRequestType.UNCLEARED_OPERTYPE

class CheckUnclearedEaRule implements Rule {

    @Override
    void apply(RuleContext ruleContext){
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if (AppConfig.isRuleEnabled("CheckUnclearedEaRule")) {
            List<Exchange> listUnclearedEA = getUnclearedEA(exchange)
            listUnclearedEA.each { unclearedEa ->
                def reqNum = unclearedEa.requestNo
                if (!unclearedEa.supDeclarations?.size()){
                    exchange.errors.reject("exchange.errors.tvf.unclearedEa.error", [reqNum] as Object[], "The exchange authorization ${reqNum} is not cleared with a customs declaration")
                }
            }
        }
    }

    def getUnclearedEA(Exchange exchange){
        LocalDate currentDate = new LocalDate()
        List<Exchange> notCleared = Exchange.list()?.findAll {
            it?.status == Statuses.ST_EXECUTED &&
            it?.importerCode == exchange?.importerCode &&
            it?.operType != UNCLEARED_OPERTYPE &&
            it?.expirationDate < currentDate
        }

        notCleared
    }
}
