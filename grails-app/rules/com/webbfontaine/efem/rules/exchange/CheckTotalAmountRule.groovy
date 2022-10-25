package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import groovy.util.logging.Slf4j

import java.math.RoundingMode

import static com.webbfontaine.efem.rules.RuleUtils.*

@Slf4j("LOGGER")
class CheckTotalAmountRule implements Rule {


    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        if (AppConfig.isRuleEnabled("CheckTotalAmountRule")) {
            checkConvertedCifValueToSumOfAllAmount(exchangeInstance)
        }
    }

    def checkConvertedCifValueToSumOfAllAmount(Exchange exchangeInstance) {
        BigDecimal sadCifValue = exchangeInstance?.totalAmountOfCif ?: BigDecimal.ZERO

        BigDecimal ttlAmtNationalCurr = getListOfExchange(exchangeInstance) + exchangeInstance.amountNationalCurrency ?: BigDecimal.ZERO

        if (exchangeInstance?.basedOn == ExchangeRequestType.BASE_ON_SAD) {
            def result = ercService?.getAvdDetails(exchangeInstance?.attachedDocReferenceSad)
            def avdTotCifVal = result ? new BigDecimal(result?.totCIFValD) : BigDecimal.ZERO
            BigDecimal localAvdCifValue = (avdTotCifVal == BigDecimal.ZERO) ? BigDecimal.ZERO : new BigDecimal(avdTotCifVal * result?.invCurRat)?.setScale(2, RoundingMode.HALF_UP)

            LOGGER.debug("CheckTotalAmountRule: EA Local Currency: ${ttlAmtNationalCurr} where SAD Cif Value: ${sadCifValue} and local AVD CIF Value: ${localAvdCifValue}")
            if (ttlAmtNationalCurr > sadCifValue && exchangeInstance.attachedDocCodeSad == ExchangeRequestType.AVD_EXIST_COD && ttlAmtNationalCurr > localAvdCifValue) {
                hasSomeIncoterm(exchangeInstance)
            } else if (ttlAmtNationalCurr > sadCifValue && exchangeInstance.attachedDocCodeSad != ExchangeRequestType.AVD_EXIST_COD) {
                hasSomeIncoterm(exchangeInstance)
            }
        }
    }

    static errorMessage(Exchange exchange) {
        exchange.errors.rejectValue('amountNationalCurrency', 'exchange.errors.allAmount', 'The sum of the amounts of all requests linked to SAD should NOT be more than the declared value in SAD.')
    }

    static BigDecimal getListOfExchange(Exchange exchange) {
        List includedStatus = [Statuses.ST_APPROVED, Statuses.ST_EXECUTED]
        ExchangeService service = Holders.applicationContext.getBean("exchangeService")
        BigDecimal sumOfamountNationalCurrency = service.doSumOfExchangeAmount(exchange, includedStatus, true, ExchangeRequestType.AMOUNT_IN_NATIONAL_CURRENCY, false, false)
        sumOfamountNationalCurrency
    }

    static hasSomeIncoterm(Exchange exchange) {
        if ((exchange?.sadIncoterm != null) && !(exchange?.sadIncoterm in ExchangeRequestType.INCOTERMS_AUTHORIZED)) {
            errorMessage(exchange)
        }
    }
}
