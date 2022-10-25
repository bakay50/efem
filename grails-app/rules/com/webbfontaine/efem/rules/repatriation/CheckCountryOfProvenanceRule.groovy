package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.constants.RepatriationRequestType
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

class CheckCountryOfProvenanceRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriation = ruleContext.getTargetAs(Repatriation) as Repatriation
        if (repatriation.countryOfOriginCode == RepatriationRequestType.CI) {
            repatriation.errors.rejectValue('countryOfOriginCode', 'repatriation.errors.countryOfOrigin.invalid', 'Country of Provenance/Destination cannot be Ivory Coast')
        }
    }

}
