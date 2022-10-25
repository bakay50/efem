package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckConnectedUserRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferOrder = ruleContext.getTargetAs(TransferOrder) as TransferOrder

        if (UserUtils.isBankAgent() && !(UserUtils.inUserProperty(transferOrder.bankCode, UserProperties.ADB))) {
            transferOrder.errors.rejectValue("bankCode", "transferOrder.errors.bankCode", "You can not submit a Transfer File for the mentionned Bank")
        }

        if (UserUtils.isTrader() && !(UserUtils.inUserProperty(transferOrder.importerCode, UserProperties.TIN))) {
            transferOrder.errors.rejectValue("importerCode", "transferOrder.errors.importerCode", "You cannot submit a Transfer file for the listed importer.")
        }

    }
}
