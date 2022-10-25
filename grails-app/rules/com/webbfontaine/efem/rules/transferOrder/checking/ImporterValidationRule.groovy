package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.Exchange
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
class ImporterValidationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        transferInstance.orderClearanceOfDoms.each() {
            if (it.eaReference) {
                Exchange exchange = Exchange.findByRequestNo(it.eaReference)
                if (exchange && exchange?.importerCode != transferInstance?.importerCode) {
                    transferInstance.errors.rejectValue("importerCode", "transferOrder.importerCode.same.error", [it?.eaReference] as Object[], "Importer non consistent with that of the EA document(s).")
                }
            }
        }

    }

}