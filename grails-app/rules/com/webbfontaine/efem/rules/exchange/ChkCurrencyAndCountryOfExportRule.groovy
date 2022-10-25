package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.constants.ExchangeRequestType.DEVICE_XOF
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA

class ChkCurrencyAndCountryOfExportRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {

        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange.class)
        List listCountryOfDest = AppConfig.listCountryOfDestination
        listCountryOfDest.remove(ExchangeRequestType.CI)
        if (exchangeInstance?.requestType == EA && exchangeInstance?.currencyPayCode == DEVICE_XOF && !(exchangeInstance?.countryOfExportCode in listCountryOfDest)){
            exchangeInstance.errors.rejectValue('currencyPayCode', 'exchange.errors.countryOfExportCode',
                    'This request can not be made on the basis of CFA francs (XOF).')
        }
    }

}
