package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 19/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class BankAndImporterValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        transferInstance?.orderClearanceOfDoms.each() {
            if (it.eaReference) {
                Exchange exchange = Exchange.findByRequestNo(it.eaReference)
                checkBankCodeUniqueness(exchange, transferInstance, it?.eaReference)
                checkImporterCodeUniqueness(exchange, transferInstance, it?.eaReference)
            }
        }
    }

    private def checkImporterCodeUniqueness(exchange, transferInstance, eaReference) {
        if (exchange && exchange?.importerCode != transferInstance?.importerCode) {
            transferInstance.errors.rejectValue("importerCode", "transferOrder.importerCode.same.inList.error", [eaReference] as Object[], "Le(s) document(s) AC n'est(sont) pas utilisable(s) par l'utilisateur connecté.")
            return
        }
    }

    private def checkBankCodeUniqueness(exchange, transferInstance, eaReference) {
        if (exchange && exchange?.bankCode != transferInstance?.bankCode) {
            transferInstance.errors.rejectValue("bankCode", "transferOrder.bankCode.same.inList.error", [eaReference, transferInstance?.bankCode, exchange?.bankCode] as Object[], "Le(s) document(s) AC n'est(sont) pas utilisable(s) par l'utilisateur connecté.")
            return
        }
    }

}
