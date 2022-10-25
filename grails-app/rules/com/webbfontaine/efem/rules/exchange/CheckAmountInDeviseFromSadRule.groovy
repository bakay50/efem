package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import com.webbfontaine.wfutils.AppContextUtils
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.REQUEST

@Slf4j("LOGGER")
class CheckAmountInDeviseFromSadRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        LOGGER.debug("in apply() of ${CheckAmountInDeviseFromSadRule} on Exchange id ${exchangeInstance.id}")
        if (exchangeInstance?.startedOperation in [CREATE, REQUEST] && (exchangeInstance?.requestType == ExchangeRequestType.EA)) {
            if (exchangeInstance?.basedOn == ExchangeRequestType.BASE_ON_SAD) {
                checkAmountFromSad(exchangeInstance)
            }
        }
    }

    static def checkAmountFromSad(Exchange exchange) {
        def sadDetails = getSadDetails()
        BigDecimal totalAmountSad = sadDetails?.data.invoiceAmountInForeignCurrency
        checkAmountinvoice(exchange, totalAmountSad)
    }

    static def getSadDetails() {
        SadService service = AppContextUtils.getBean(SadService)
        def params = WebRequestUtils.params
        return service.retrieveSad(params)
    }

    static def checkAmountinvoice(exchange, totalAmount) {
        def totalAmountExchangeinCurrency = getTotalAmountExchangeinCurrency(exchange)
        if (totalAmountExchangeinCurrency > totalAmount) {
            exchange.errors.rejectValue('amountMentionedCurrency', 'exchange.errors.totAmountExceeds', 'The total Amount in Currency of exchange documents linked to a TVF/SAD should not exceed the Invoice Amount of the used document.')
        }
    }

    static def getTotalAmountExchangeinCurrency(Exchange exchange) {
        ExchangeService exchangeService = AppContextUtils.getBean(ExchangeService)
        List<Exchange> exchangeList = exchangeService.findExchangeByTvfOrSad(exchange)
        return (exchangeList*.amountMentionedCurrency?.sum() ?: BigDecimal.ZERO) + exchange?.amountMentionedCurrency ?: BigDecimal.ZERO
    }
}