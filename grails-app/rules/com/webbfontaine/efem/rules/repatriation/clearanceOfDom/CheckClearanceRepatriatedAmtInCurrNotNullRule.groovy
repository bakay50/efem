package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class CheckClearanceRepatriatedAmtInCurrNotNullRule implements Rule
{
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckClearanceRepatriatedAmtInCurrNotNullRule}")
        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom) as ClearanceOfDom
        checkIfClearanceRepatriatedAmtInCurrIsNotNull(clearanceOfDom)
    }

    static def checkIfClearanceRepatriatedAmtInCurrIsNotNull(ClearanceOfDom clearanceOfDom)
    {
        if (clearanceOfDom?.repatriatedAmtInCurr?.equals(BigDecimal.ZERO) || !clearanceOfDom?.repatriatedAmtInCurr)
        {
            clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.zero', 'The Repatriated Amount in Currency must be greater than zero.')
        }
    }
}
