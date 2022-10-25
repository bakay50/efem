package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import org.slf4j.LoggerFactory

class CheckAttachedInvoiceRule implements Rule{
    private static final LOGGER = LoggerFactory.getLogger(CheckAttachedInvoiceRule)
    @Override
    void apply(RuleContext ruleContext){
        LOGGER.warn("checkInvoice")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        def attDocList = exchangeInstance?.attachedDocs
        def invoice_existe = 'false'
        if(attDocList){
            attDocList.any {
                if (it?.docType && it?.docType in ExchangeRequestType.invoiceCodes) {
                    invoice_existe = 'true'
                }
            }
            if (invoice_existe == 'false') {
                LOGGER.debug("Provide at least one INVOICE please")
                if(exchangeInstance.requestType == ExchangeRequestType.EA_FROM_TVF && exchangeInstance.basedOn == ExchangeRequestType.BASE_ON_TVF){
                    exchangeInstance.errors.rejectValue('attachedDocs', 'exchange.errors.invoice', 'Provide at least one INVOICE please')
                }else if(exchangeInstance.requestType == ExchangeRequestType.EC_FROM_SAD){
                    exchangeInstance.errors.rejectValue('attachedDocs', 'exchange.errors.invoice', 'Provide at least one INVOICE please')
                }else{
                    exchangeInstance.errors.rejectValue('attachedDocs', 'exchange.errors.invoice', 'Provide at least one INVOICE please')
                }
            }
        }else{
            LOGGER.debug("Provide at least one INVOICE please")
            if(exchangeInstance.requestType == ExchangeRequestType.EA_FROM_TVF && exchangeInstance.basedOn == ExchangeRequestType.BASE_ON_TVF){
                exchangeInstance.errors.rejectValue('attachedDocs', 'exchange.errors.invoice', 'Provide at least one INVOICE please')
            }else{
                exchangeInstance.errors.rejectValue('attachedDocs', 'exchange.errors.invoice', 'Provide at least one INVOICE please')
            }
        }
        LOGGER.debug("invoice_existe ={}",invoice_existe)
    }
}
