package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.constants.Statuses.ST_CANCELLED

@Slf4j("LOGGER")
class ChkRegistrationNumberRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        checkUniqueRegNumber(exchange)
    }

    private void checkUniqueRegNumber(Exchange exchange) {
        Integer currentYear = exchange.registrationDateBank?.getYear()
        Exchange foundExchange = Exchange.findByYearAndBankCodeAndRegistrationNumberBankAndIdNotEqual(currentYear, exchange.bankCode, exchange.registrationNumberBank, exchange.id)

        LOGGER.warn("Exchange found status in checkRegistrationNumberBank :${foundExchange?.status}")
        if (foundExchange && !(foundExchange.status in [ST_CANCELLED])) {
            exchange.errors.rejectValue("registrationNumberBank", 'custom.checkRegistrationNumberBank.message', 'The registration number must be unique per bankper year.');
        }
    }

}
