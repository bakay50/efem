package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import static com.webbfontaine.efem.constants.TvfConstants.IM_FLOW

class CheckSadRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange)
        if ((exchange.basedOn == ExchangeRequestType.BASE_ON_SAD || exchange.requestType == ExchangeRequestType.EC) && !exchange.sadInstanceId) {
            checkSadInfos(exchange)
        }
    }

    def checkSadInfos(Exchange exchange) {
        def params = WebRequestUtils.params
        Exchange exchangeInstance = new Exchange()
        if(checkSadInformations(params)){
            exchangeInstance = getExchangeFromSad(params)
        }
        exchange.errors.addAllErrors(exchangeInstance?.errors)
        if (exchangeInstance?.sadTypeOfDeclaration == IM_FLOW && exchange.requestType == ExchangeRequestType.EC) {
            exchange.errors.rejectValue("sadTypeOfDeclaration","load.sad.declaration.ex.error", "Export exchange document cannot be linked to an import declaration")
        }
        if (exchangeInstance?.exporterCode != exchange.exporterCode && exchange.requestType == ExchangeRequestType.EC) {
            exchange.errors.rejectValue("exporterCode","load.sad.exporterCode.error", "The declaration refers to a different exporter of that mentioned on the exchange document")
        }
    }

    Exchange getExchangeFromSad(params){
        SadService sadService = Holders.applicationContext.getBean("sadService")
        sadService.retrieveExchangeFromSad(params)
    }

    def checkSadInformations(params){
        params?.clearanceOfficeCode && params?.declarationNumber && params?.declarationSerial && params?.declarationDate
    }
}
