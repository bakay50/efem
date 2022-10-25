package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders

import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_001
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_003
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_002
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC

class CheckCurrencyCodeSadAndEcDocumentRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        handleRule(exchangeInstance)
    }

    static def handleRule(Exchange exchange) {
        if (!exchange.sadInstanceId) {
            def params = WebRequestUtils.params
            def sadData = retrieveSad(params)
            setSadInstanceIdForEc(sadData, exchange)
            if (handleCheckAreaAndRequestType(exchange, AREA_001) && exchange?.countryOfDestinationCode in AppConfig.listCountryOfDestination) {
                handleCheckCurrencyCode(exchange, sadData)
            }
            if (handleCheckAreaAndRequestType(exchange, AREA_003) && !AppConfig.listCountryOfDestination.contains(exchange?.countryOfDestinationCode)) {
                handleCheckCurrencyCode(exchange, sadData)
            }
            if (handleCheckAreaAndRequestType(exchange, AREA_002)) {
                handleCheckCurrencyCode(exchange, sadData)
            }
        }

    }

    private static void setSadInstanceIdForEc(sadData, Exchange exchange) {
        exchange.sadInstanceId = sadData?.data?.id
    }

    static boolean handleCheckAreaAndRequestType(Exchange exchange, area) {
        exchange?.requestType == EC && exchange?.geoArea == area
    }

    static def handleCheckCurrencyCode(Exchange exchange, LinkedHashMap<Object, Object> sadData) {
        if (sadData && exchange?.currencyCode != sadData?.data?.invoiceCurrencyCode) {
            exchange.errors.rejectValue('declarationNumber', 'exchange.errors.currencyCode.sad.ec.notEquals', 'The currency code of the SAD does not conform to your EC request.')
        }
    }

    static def retrieveSad(params) {
        SadService service = Holders.applicationContext.getBean("sadService")
        def sadDocument = service.retrieveSad(params)
        sadDocument
    }
}

