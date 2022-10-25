package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.constants.UserProperties.ALL
import static com.webbfontaine.efem.constants.UserProperties.TIN

class CheckExporterECRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {

        def tin = UserUtils.userPropertyValueAsList(TIN) ?: UserUtils.getUserProperty(TIN)
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchange.requestType == ExchangeRequestType.EC && !(exchange?.exporterCode in tin || tin == ALL)) {
            exchange.errors.rejectValue('exporterCode', 'exchange.checkTinPropertyForEC.message', 'You can not submit a Exchange Commitment for the mentionned Exporter ')
        }
    }
}


