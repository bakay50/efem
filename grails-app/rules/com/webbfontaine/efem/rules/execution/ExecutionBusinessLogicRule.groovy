package com.webbfontaine.efem.rules.execution

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.rimm.country.Country
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.grails.plugins.validation.rules.ref.setter.RefValueSetterRule
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class ExecutionBusinessLogicRule implements Rule{

    private static def COMMON_EXECUTION_RULES = [
            new ExecutionRule()
    ]

    private static final SETTER_RULES = [
            new RefValueSetterRule(Country, 'countryProvenanceDestinationExCode', true, false, 'countryProvenanceDestinationExName'),
            new RefValueSetterRule(Bank, 'executingBankCode', true, false, 'executingBankName')
    ]

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${ExecutionBusinessLogicRule}")

        Execution execution = ruleContext.getTargetAs(Execution)
        Exchange exchangeInstance = execution.exchange
        def startTime = System.currentTimeMillis()
        def params = WebRequestUtils.getParams()
        def conversationId = params.conversationId

        LOGGER.debug("id = {}, cid = {} Execution # {}. Started. ", exchangeInstance?.id, conversationId, execution?.rank)
        RuleUtils.executeSetOfRules(COMMON_EXECUTION_RULES, ruleContext)
        LOGGER.debug("id = {}, cid = {} Execution # {}. ExecutionBusinessLogicRule execution took {}ms", exchangeInstance?.id, conversationId, execution?.rank, (System.currentTimeMillis() - startTime))
        RuleUtils.executeSetOfRules(SETTER_RULES, ruleContext)
    }
}
