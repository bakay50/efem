package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CancelTransferFieldRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CancelTransferFieldRule}");
        CurrencyTransfer currencyTransferInstance = ruleContext.getTargetAs(CurrencyTransfer) as CurrencyTransfer
        def params = WebRequestUtils.getParams()
        if (isNullOrEmpty(params?.comments)) {
            currencyTransferInstance.errors.rejectValue("comments", "currencyTransfer.comment.required", "Provide reason for this operation please.");
        }
    }

    def isNullOrEmpty(def value) {
        return value == null || value == ""
    }
}
