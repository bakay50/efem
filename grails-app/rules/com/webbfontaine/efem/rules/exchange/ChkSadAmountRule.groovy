package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest

import static com.webbfontaine.efem.constants.Statuses.*

@Slf4j("LOGGER")
class ChkSadAmountRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange

        checkInvoiceAmountAndTotalAmount(exchangeInstance)

    }

    def checkInvoiceAmountAndTotalAmount(Exchange exchangeInstance) {

        BigDecimal invAmount = exchangeInstance.finalAmountInDevise
        BigDecimal totAmount = exchangeInstance.amountMentionedCurrency

        LOGGER.debug("checking comparison between Total Exchange Amount Mentioned in Currency ${totAmount} and Invoice Final Amount ${invAmount}")
        if (invAmount > totAmount) {
            exchangeInstance.errors.rejectValue('finalAmountInDevise', 'exchange.errors.sadTotAmountExceeds', 'The Invoice Final Amount should not exceed the total Amount in Currency of exchange document')
        }
    }

    def checkTotalExchangeAmount(Exchange exchangeInstance) {

        def sadAmount = GrailsWebRequest.lookup().params?.sadInvoiceAmountInForeignCurrency
        BigDecimal currentExchangeAmount = exchangeInstance?.amountMentionedCurrency ?: new BigDecimal('0.00')
        BigDecimal sadInvAmount = sadAmount ? new BigDecimal(sadAmount) : new BigDecimal('0.00')

        BigDecimal totalExchangeSadAmount = Exchange.where {
            id != exchangeInstance?.id &&
                    clearanceOfficeCode == exchangeInstance.clearanceOfficeCode &&
                    declarationDate == exchangeInstance.declarationDate &&
                    declarationNumber == exchangeInstance.declarationNumber &&
                    declarationSerial == exchangeInstance.declarationSerial &&
                    status in [ST_REQUESTED, ST_QUERIED, ST_PARTIALLY_APPROVED, ST_APPROVED, ST_EXECUTED]

        }?.list()?.amountMentionedCurrency?.sum() ?: new BigDecimal('0.00')

        totalExchangeSadAmount = totalExchangeSadAmount?.plus(currentExchangeAmount)

        LOGGER.debug("checking comparison between SAD Total CIF Amount ${sadInvAmount} and Total Exchange Amount Mentioned in Currency ${totalExchangeSadAmount}")
        if (totalExchangeSadAmount > sadInvAmount.plus(BigDecimal.ONE)) {
            exchangeInstance.errors.rejectValue('amountMentionedCurrency', 'exchange.errors.sadamount', 'The total Amount in Currency of exchange documents linked to a SAD should not exceed the Invoice Amount of the used document')
        }
    }
}
