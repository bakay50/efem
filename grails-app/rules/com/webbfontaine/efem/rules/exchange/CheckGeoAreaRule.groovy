package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.workflow.Operation.*

class CheckGeoAreaRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange)
        checkIfGeoAreaAndCtryOfDestIsCompatible(exchange)
    }

    def checkIfGeoAreaAndCtryOfDestIsCompatible(Exchange exchangeInstance) {
        List listCountryOfDest = AppConfig.listCountryOfDestination
        listCountryOfDest.remove(ExchangeRequestType.CI)
        if(exchangeInstance.requestType == ExchangeRequestType.EC_FROM_SAD){
            if (exchangeInstance.startedOperation in [CREATE, REQUEST_QUERIED, UPDATE_QUERIED, REQUEST_STORED]){
                if((exchangeInstance?.countryOfDestinationCode in listCountryOfDest)){
                    if(exchangeInstance?.geoArea == ExchangeRequestType.AREA_001 || exchangeInstance?.geoArea == ExchangeRequestType.AREA_002){
                        exchangeInstance.departmentInCharge = ExchangeRequestType.departmentInCharg
                    }
                    if (exchangeInstance?.geoArea == ExchangeRequestType.AREA_001 && exchangeInstance.areaPartyCode == ExchangeRequestType.AREA_003) {
                        exchangeInstance.departmentInCharge = exchangeInstance.bankCode
                    }
                } else {
                    if ((exchangeInstance?.geoArea == ExchangeRequestType.AREA_003 && exchangeInstance.areaPartyCode != ExchangeRequestType.AREA_001)) {
                        exchangeInstance.departmentInCharge = exchangeInstance.bankCode
                    } else if (exchangeInstance?.geoArea == ExchangeRequestType.AREA_002 ||
                            (exchangeInstance?.geoArea == ExchangeRequestType.AREA_003 && exchangeInstance?.areaPartyCode == ExchangeRequestType.AREA_001)
                    ) {
                        exchangeInstance.departmentInCharge = ExchangeRequestType.departmentInCharg
                    }
                    else {
                        exchangeInstance.errors.rejectValue('geoArea', 'exchange.errors.geoAreaIsNotCompatible', 'The Geographical Area and the Destination Country are not Compatible.')
                    }
                }
                exchangeInstance
            }
        }
    }

}
