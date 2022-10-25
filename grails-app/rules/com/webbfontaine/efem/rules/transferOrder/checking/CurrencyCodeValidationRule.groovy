package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 13/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyCodeValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        checkIfCurrencyCodeIsXof(transferInstance)
        checkClearanceOfDomCurrencyCode(transferInstance)
    }

    def checkIfCurrencyCodeIsXof(transferInstance){
        if(transferInstance?.currencyPayCode?.equalsIgnoreCase(UtilConstants.XOF)){
            transferInstance.errors.rejectValue("currencyPayCode", "transferOrder.currencyPayCode.xof.error", "The currency can not be XOF.")
        }
    }

    def checkClearanceOfDomCurrencyCode(TransferOrder transferInstance){
        def listOfEa = checkCurrencyCode(transferInstance)
        if(listOfEa){
            transferInstance.errors.rejectValue("currencyPayCode", "transferOrder.currencyPayCode.same.error", [listOfEa?.join(", ")] as Object[] , "The currency code should be the same to that of EA document(s) : ${listOfEa?.join(",")}.")
        }
    }

    def checkCurrencyCode(transferInstance){
        def listOfEa = []
        transferInstance?.orderClearanceOfDoms.each(){
            if(it.eaReference){
                Exchange exchange = Exchange.findByRequestNo(it.eaReference)
                if(exchange && exchange?.currencyCode != transferInstance?.currencyPayCode){
                    listOfEa.add(it.eaReference)
                }
            }
        }
        listOfEa
    }
}
