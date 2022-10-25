package com.webbfontaine.efem.rules.execution


import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class ExecutionRule implements Rule {

    def exchangeService = AppContextUtils.getBean(ExchangeService)

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${ExecutionRule}")

        Execution executionInstance = ruleContext.getTargetAs(Execution) as Execution
        checkAmountSettledAgainstNationalCurrency(executionInstance)
    }

    private void checkAmountSettledAgainstNationalCurrency(Execution executionInstance){

        if(executionInstance?.amountSettledMentionedCurrency > executionInstance?.amountMentionedExCurrency) {
            executionInstance.errors.rejectValue('amountSettledMentionedCurrency', "execution.errors.amountSettledMentioned", "Settled Amount can not exceed the Amount of the Transfer")
        }
    }
}
