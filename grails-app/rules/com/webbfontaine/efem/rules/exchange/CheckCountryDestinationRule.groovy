package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckCountryDestinationRule implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchange.requestType == ExchangeRequestType.EC && exchange.countryOfDestinationCode == ExchangeRequestType.CI) {
                exchange.errors.rejectValue('countryOfDestinationCode', 'client.exchange.errors.countryProvenanceDestinationCode.invalid', ' Invalid Country of Destination Code')
        }
        if (exchange.requestType == ExchangeRequestType.EC && exchange.countryPartyCode == ExchangeRequestType.CI) {
                exchange.errors.rejectValue('countryPartyCode', 'client.exchange.errors.countryProvenanceDestinationCode.invalid', ' Invalid Country of Destination Code')
        }
    }

}
