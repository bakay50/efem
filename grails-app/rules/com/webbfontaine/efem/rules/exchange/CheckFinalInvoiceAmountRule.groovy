package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckFinalInvoiceAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange

        if(exchangeInstance?.requestType == ExchangeRequestType.EC){
            checkFinalInvoiceAmount(exchangeInstance)
        }
    }

    def checkFinalInvoiceAmount(Exchange exchangeInstance) {

        Exchange persistExchange = Exchange.findById(exchangeInstance?.id)
        int repatCount = ClearanceOfDom.findAllByEcReference(persistExchange?.requestNo).count{it.repats.status in Statuses.CHECKED_STATUS_FOR_FINAL_AMOUNT}

        if(repatCount > 0){
            exchangeInstance.errors.rejectValue('finalAmountInDevise', 'exchange.errors.usingExchangeInRepat', 'Exchange has been already linked to a repatriation file')
        }

        if(persistExchange?.isFinalAmount && (persistExchange?.finalAmountInDevise != exchangeInstance?.finalAmountInDevise
                        || persistExchange?.finalAmount != exchangeInstance?.finalAmount
                || persistExchange?.isFinalAmount != exchangeInstance?.isFinalAmount)){
            exchangeInstance.errors.rejectValue('finalAmountInDevise', 'exchange.errors.defineFinalAmount', 'The invoice final amount has been already define ')
        }
    }
}
