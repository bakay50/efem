package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import static com.webbfontaine.efem.constants.Statuses.ST_CANCELLED
import static com.webbfontaine.efem.workflow.Operation.APPROVE_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.DOMICILIATE


@Slf4j("LOGGER")
class CheckRegistrationNumberBankRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkRule(exchangeInstance)
    }

    static def checkRule(Exchange exchange) {
        if (isBankAgentOperations(exchange)) {
            int currentYear = new LocalDate().year
            String bankCode = exchange.bankCode
            String registrationNoBank = exchange.registrationNumberBank
            Exchange foundExchange = Exchange.findByRequestYearAndBankCodeAndRegistrationNumberBankAndStatusNotEqual(currentYear, bankCode, registrationNoBank, ST_CANCELLED)
            LOGGER.debug("Current year : {}, bankCode : {}, registrationNoBank : {} and Exchange founds data : {} ",currentYear, bankCode, registrationNoBank, foundExchange)
            registrationNumberBankError(exchange, foundExchange)
        }
    }

    private static boolean isBankAgentOperations(Exchange exchange) {
        exchange.startedOperation in [DOMICILIATE, APPROVE_REQUESTED] && UserUtils.isBankAgent()
    }

    private static void registrationNumberBankError(Exchange exchange, Exchange foundExchange) {
        if (foundExchange && foundExchange?.id != exchange?.id && !(foundExchange?.status in [ST_CANCELLED]) && foundExchange?.registrationNumberBank == exchange.registrationNumberBank){
            exchange.errors.rejectValue('registrationNumberBank', 'exchange.errors.registrationNumberBank', 'The registration number must be unique per bank per year.')
        }
    }
}
