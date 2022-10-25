package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckEAFromSadAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkSumOfAllAmount(exchangeInstance)
    }

    def checkSumOfAllAmount(Exchange exchangeInstance) {
        def params = WebRequestUtils.params
        def sadResult = getSadDatas(params)
        BigDecimal totalAmountSadinDevise = new BigDecimal(sadResult?.data?.totalAmountOfCostInsuranceFreight?: BigDecimal.ZERO) + 1
        LOGGER.debug("In Apply of ${CheckEAFromSadAmountRule} : totalAmountOfCostInsuranceFreight : ${totalAmountSadinDevise}")
        BigDecimal amountNationalCurrencySum = getListOfExchange(exchangeInstance)
        LOGGER.debug("In Apply of ${CheckEAFromSadAmountRule} : Sum of Amount National in Currency of Other Exchange : ${amountNationalCurrencySum}")
        LOGGER.debug("In Apply of ${CheckEAFromSadAmountRule} : Exchange amount National Currency : ${exchangeInstance?.amountNationalCurrency}")
        BigDecimal ttlAmtInDevise = amountNationalCurrencySum + (exchangeInstance?.amountNationalCurrency ?: BigDecimal.ZERO)
        LOGGER.debug("In Apply of ${CheckEAFromSadAmountRule} : Sum of  Amount in Currency : ${ttlAmtInDevise}")

        if(exchangeInstance?.currencyCode != exchangeInstance?.currencyPayCode && exchangeInstance?.clearanceOfficeCode in AppConfig.getXofOfficeCode()){
            BigDecimal currencyRate = new BigDecimal(getCurrencyRate(exchangeInstance?.currencyPayCode))
            BigDecimal sadCurrencyRate = new BigDecimal(getCurrencyRate(sadResult?.data?.invoiceCurrencyCode))
            BigDecimal ttlAmtNationalCurr = ttlAmtInDevise * currencyRate
            BigDecimal ttlAmtNationalCurrSad = totalAmountSadinDevise * sadCurrencyRate
            compareSadAndExchangesAmount(exchangeInstance, ttlAmtNationalCurrSad, ttlAmtNationalCurr)
        }else {
            compareSadAndExchangesAmount(exchangeInstance, totalAmountSadinDevise, ttlAmtInDevise)
        }

    }

    def compareSadAndExchangesAmount(Exchange exchangeInstance, BigDecimal totalSadAmount, BigDecimal totalExchangeAmount){
        if (totalExchangeAmount > totalSadAmount) {
            exchangeInstance.errors.rejectValue('amountNationalCurrency', 'exchange.errors.allAmount', 'The sum of the amounts of all requests linked to SAD should NOT be more than the declared value in SAD.')
        }
    }

    def getSadDatas(params) {
        SadService sadService = Holders.applicationContext.getBean("sadService")
        sadService.retrieveSad(params)
    }

    static BigDecimal getListOfExchange(Exchange exchange) {
        ExchangeService service = Holders.applicationContext.getBean("exchangeService")
        BigDecimal sumOfamountMentionedCurrency = service.doSumOfExchangeAmount(exchange, Statuses.CHECKED_STATUS_FOR_AMOUNT_RULE, true, ExchangeRequestType.AMOUNT_IN_NATIONAL_CURRENCY, true, true)
        sumOfamountMentionedCurrency
    }

    def getCurrencyRate(currencyCode) {
        def criteria = [code: currencyCode]
        def htDate = new Date().clearTime()
        BeanDataLoadService beanDataLoadService = AppContextUtils.getBean(BeanDataLoadService)
        beanDataLoadService.loadFullBeanData("HT_RATE", criteria, "get", "", htDate)?.exchangeRate?: BigDecimal.ZERO
    }

}
