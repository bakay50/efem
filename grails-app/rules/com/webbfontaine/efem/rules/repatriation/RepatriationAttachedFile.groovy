package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import static com.webbfontaine.efem.workflow.Operation.CREATE


class RepatriationAttachedFile implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriation = ruleContext.getTargetAs(Repatriation) as Repatriation
        checkIfCreditNoticeOrTheStatementOfAccountIsAttached(repatriation)
    }
    def checkIfCreditNoticeOrTheStatementOfAccountIsAttached(Repatriation repatriation){
        def attachedDocs = repatriation?.attachedDocs
        if (repatriation?.startedOperation == CREATE ){
            if (!attachedDocs?.any {it?.docType ==  RepatriationConstants.CODE_CREDIT_NOTICE_OR_STATEMENT_OF_ACCOUNT}){
                repatriation.errors.reject( 'attachedDoc.error.creditNotice', 'Provide the Credit Notice or the Statement of Account Please.')
            }
        }

    }
}
