package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.UtilConstants.XOF

class CheckWAEMUCountryExportAndCurrencyCodeRule implements Rule{
    
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkRule(exchangeInstance)
    }

    static def checkRule(Exchange exchange)
    {
        List waemuCountries = AppConfig.getListCountryOfDestination()
        if (exchange.requestType == EA && exchange?.countryOfExportCode?.toUpperCase() in waemuCountries && exchange.currencyCode == XOF)
        {
            exchange.errors.rejectValue('countryOfExportCode', 'exchange.errors.countryListOfUEMOA', 'No Exchange Authorization (AC) is permitted for this transaction')
        }

        if (exchange.requestType == EC && exchange?.countryPartyCode?.toUpperCase() in waemuCountries && exchange.currencyCode != XOF)
        {
            exchange.errors.rejectValue('currencyCode', 'exchange.errors.waemuCurrencyCode', [exchange.currencyCode] as Object[], "The ${exchange.currencyCode} currency code is not compatible with your EC request")
        }
    }
}
