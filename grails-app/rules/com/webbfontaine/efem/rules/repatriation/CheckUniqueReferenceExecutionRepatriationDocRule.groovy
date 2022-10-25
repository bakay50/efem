package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate

import static com.webbfontaine.efem.workflow.Operation.DECLARE_QUERIED
import static com.webbfontaine.efem.workflow.Operation.CONFIRM_DECLARED
import static com.webbfontaine.efem.workflow.Operation.CREATE

@Slf4j("LOGGER")
class CheckUniqueReferenceExecutionRepatriationDocRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Repatriation repatriation = ruleContext.getTargetAs(Repatriation) as Repatriation
        checkRule(repatriation)
    }

    static def checkRule(Repatriation repatriation) {
        if (handleBankAgentOperation(repatriation)) {
            int currentYear = new LocalDate().year
            String bankCode = repatriation.repatriationBankCode
            String executionReference = repatriation.executionRef
            Repatriation foundRepatriation = Repatriation.findByRequestYearAndRepatriationBankCodeAndExecutionRef(currentYear, bankCode, executionReference)
            LOGGER.debug("Current year : {}, bankCode : {}, executionReference : {} and Repatriation founds data : {} ", currentYear, bankCode, executionReference, foundRepatriation)
            handleExecutionReferenceError(repatriation, foundRepatriation)
        }
    }

    private static boolean handleBankAgentOperation(Repatriation repatriation) {
        return repatriation.startedOperation in [CREATE,DECLARE_QUERIED,CONFIRM_DECLARED] && UserUtils.isBankAgent()
    }

    private static def handleExecutionReferenceError(Repatriation repatriation, Repatriation repatriationFounded) {
        if (repatriationFounded && repatriationFounded?.id != repatriation?.id) {
            repatriation.errors.rejectValue('executionRef', 'repatriation.executionRef.error', 'This execution reference has already been used this year in another rapatriation document')
        }
    }
}
