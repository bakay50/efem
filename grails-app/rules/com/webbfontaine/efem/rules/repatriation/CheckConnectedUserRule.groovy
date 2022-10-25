package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckConnectedUserRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriation = ruleContext.getTargetAs(Repatriation) as Repatriation

        if (UserUtils.isBankAgent() && !(UserUtils.inUserProperty(repatriation.repatriationBankCode, UserProperties.ADB))) {
            repatriation.errors.rejectValue("repatriationBankCode", "repatriation.errors.repatriationBankCode", "You can not submit a Repatriation File for the mentionned Bank")
        }
        if (UserUtils.isTrader() && !(UserUtils.inUserProperty(repatriation.code, UserProperties.TIN))) {
            repatriation.errors.rejectValue("code", "repatriation.errors.code", "You can not submit a Repatriation File for the mentionned Exporter")
        }
    }
}
