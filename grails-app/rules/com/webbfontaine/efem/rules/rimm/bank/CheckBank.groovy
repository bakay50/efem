package com.webbfontaine.efem.rules.rimm.bank

import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckBank implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("In apply of ${CheckBank}")
        Bank bank = ruleContext.getTargetAs(Bank) as Bank
        checkBankCode(bank)
    }

    static def checkBankCode(bank){
        Bank foundbank = Bank.findByCode(bank?.code)
        if(foundbank && foundbank?.id != bank?.id){
            bank.errors.rejectValue('code', 'bank.code.error', 'The Bank code already exists.')
        }
    }
}
