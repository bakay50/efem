package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class ChkTotalAmountInCurrencyRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        def tvf = exchangeInstance?.tvf ?: getExchangeTvfFromTvfService(exchangeInstance)
        BigDecimal tvfTotCifValue = tvf?.totCifInFgn
        BigDecimal exTotAmtInCur = exchangeInstance?.amountMentionedCurrency ?: BigDecimal.ZERO
        exTotAmtInCur = exTotAmtInCur + getListOfExchange(exchangeInstance)

        LOGGER.debug("checking comparison between TVF Total CIF Amount ${tvfTotCifValue} and Total Exchange Amount Mentioned in Currency ${exTotAmtInCur}")

        if (tvf != null && exTotAmtInCur > tvfTotCifValue) {
            hasSomeIncoterm(exchangeInstance)
        }
    }

    def getExchangeTvfFromTvfService(Exchange exchangeInstance) {
        TvfService service = Holders.applicationContext.getBean("tvfService")
        Exchange exchange = service.loadTvf(exchangeInstance?.tvfNumber?.toString(), FormattingUtils.fromDate(exchangeInstance?.tvfDate))
        exchange?.tvf
    }

    static BigDecimal getListOfExchange(Exchange exchange) {
        List notIncludedStatus = [Statuses.ST_CANCELLED, Statuses.ST_REJECTED, Statuses.ST_STORED, Statuses.ST_QUERIED]
        ExchangeService service = Holders.applicationContext.getBean("exchangeService")
        BigDecimal sumOfamountMentionedCurrency = service.doSumOfExchangeAmount(exchange, notIncludedStatus, false, ExchangeRequestType.AMOUNT_IN_MENTIONNED_CURRENCY, false, false)
        sumOfamountMentionedCurrency
    }

    static hasSomeIncoterm(Exchange exchangeInstance) {
        def tvfIncoterm = exchangeInstance?.tvf?.incCode
        if ((tvfIncoterm != null) && !(tvfIncoterm in ExchangeRequestType.INCOTERMS_AUTHORIZED)) {
            exchangeInstance.errors.rejectValue("amountMentionedCurrency", "exchange.errors.allAmount.TVF", "The sum of the amounts of all requests linked to TVF should NOT be more than the declared value in TVF")
        }
    }
}
