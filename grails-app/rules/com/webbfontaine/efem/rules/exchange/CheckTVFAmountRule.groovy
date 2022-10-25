package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckTVFAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange)
        Tvf tvfDetails = getTvfDetails(exchangeInstance?.tvfNumber?.toString(), FormattingUtils.fromDate(exchangeInstance?.tvfDate))
        if(!hasSomeIncoterm(tvfDetails)){
            checkSumOfAllAmount(exchangeInstance, tvfDetails)
        }
    }

    def checkSumOfAllAmount(Exchange exchangeInstance, Tvf tvfDetails) {
        BigDecimal currentExchangeAmount = exchangeInstance?.amountMentionedCurrency?:BigDecimal.ZERO
        BigDecimal ttlAmtNationalDevise = getListOfExchange(exchangeInstance) + currentExchangeAmount ?: BigDecimal.ZERO
        LOGGER.debug("in CheckTVFAmountRule Total amount National Devise ${ttlAmtNationalDevise}")
        BigDecimal currencyRate = new BigDecimal(getCurrencyRate(tvfDetails?.invCurCode))
        LOGGER.debug("in CheckTVFAmountRule currency Rate ${currencyRate}")
        BigDecimal ttlAmtNationalCurr = ttlAmtNationalDevise * currencyRate
        BigDecimal totCifInFgn = tvfDetails?.totCifInFgn?:BigDecimal.ZERO
        LOGGER.debug("in CheckTVFAmountRule valeur CAF ${totCifInFgn}")
        BigDecimal totalAmountTvf = totCifInFgn * currencyRate
        BigDecimal increasedTotalAmountTvf = totalAmountTvf + (totalAmountTvf * AppConfig.getTvfVarianceRate())
        BigDecimal amountVariation = ttlAmtNationalCurr - totalAmountTvf
        BigDecimal increasedVariation = ttlAmtNationalCurr - increasedTotalAmountTvf

        if (amountVariation.compareTo(AppConfig.getTvfVarianceAmount()) == 1){
            exchangeInstance.errors.rejectValue('amountMentionedCurrency', 'exchange.errors.amountVariation', 'The variation of the amount can not exceed 10,000,000 FCFA.')
        }

        if(increasedVariation > 0){
            exchangeInstance.errors.rejectValue('amountMentionedCurrency', 'exchange.errors.increasedAmount', 'The amount of the exchange authorization may not exceed the amount of the TVF less than 10%.')
        }

    }

    Tvf getTvfDetails( tvfNumber,  tvfDate) {
        TvfService tvfService = AppContextUtils.getBean(TvfService)
        tvfService.retrieveTvf(tvfNumber, tvfDate)
    }

    static BigDecimal getListOfExchange(Exchange exchange) {
        ExchangeService service = Holders.applicationContext.getBean("exchangeService")
        BigDecimal sumOfamountMentionedCurrency = service.doSumOfExchangeAmount(exchange, Statuses.CHECKED_STATUS_FOR_AMOUNT_RULE, true, ExchangeRequestType.AMOUNT_IN_MENTIONNED_CURRENCY, true, true)
        sumOfamountMentionedCurrency
    }

    def getCurrencyRate(currencyCode) {
        def criteria = [code: currencyCode]
        def htDate = new Date().clearTime()
        BeanDataLoadService beanDataLoadService = AppContextUtils.getBean(BeanDataLoadService)
        beanDataLoadService.loadFullBeanData("HT_RATE", criteria, "get", "", htDate)?.exchangeRate?: BigDecimal.ZERO
    }

    static Boolean hasSomeIncoterm(Tvf tvfDetails) {
        return tvfDetails?.incCode in ExchangeRequestType.INCOTERMS_AUTHORIZED
    }
}
