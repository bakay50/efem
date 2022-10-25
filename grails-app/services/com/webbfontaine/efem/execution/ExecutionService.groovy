package com.webbfontaine.efem.execution

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.context.i18n.LocaleContextHolder

import java.text.DecimalFormat

@Slf4j("LOGGER")
@Transactional
class ExecutionService {

    def exchangeService
    def convertDigitsToLetterService

    def findExecution(params){
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)
        exchangeInstance.getExecution(Integer.parseInt(params.rank))
    }

    def initAddExecutions(Exchange exchangeInstance, Execution executionInstance){
        executionInstance.currencyExCode = exchangeInstance.currencyCode ?: null
        executionInstance.currencyExName = exchangeInstance.currencyName ?: null
        executionInstance.currencyExRate = exchangeInstance.currencyRate ?: null
        executionInstance.executingBankCode = exchangeInstance.bankCode ?: null
        executionInstance.executingBankName = exchangeInstance.bankName ?: null
        executionInstance.countryProvenanceDestinationExCode = exchangeInstance.countryProvenanceDestinationCode ?: null
        executionInstance.countryProvenanceDestinationExName = exchangeInstance.countryProvenanceDestinationName ?: null
        executionInstance.provenanceDestinationExBank = exchangeInstance.provenanceDestinationBank ?: null
        executionInstance.bankAccountNumberCreditedDebited = exchangeInstance.bankAccountNocreditedDebited ?: null
        executionInstance.accountExBeneficiary = exchangeInstance.accountNumberBeneficiary ?: null
    }

    def formatDecimalNumber(params){
        def decimal_format = AppConfig.decimalNumberFormat
        LOGGER.debug("Decimal Format = {} ",decimal_format);

        DecimalFormat decimalFormat = new DecimalFormat(decimal_format);
        decimalFormat.setParseBigDecimal(true);
        BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(params.amount);

        bigDecimal
    }

    boolean deleteExecution(Exchange exchangeInstance, params) {
        LOGGER.debug("in deleteExecution() of ${ExecutionService}");
        int executionCount = exchangeInstance.executions.size()
        Integer rank = 0
        if (params.rank) {
            rank = Integer.parseInt(params.rank)
        }
        Execution executionInstance = exchangeInstance.getExecution(rank)
        if (executionInstance) {
            exchangeInstance.removeExecution(executionInstance)
        }
        return exchangeInstance.executions.size() < executionCount
    }

    def setExchangeAmountSettledFields(Exchange exchange){
        def decimal_format = Holders.config.numberFormatConfig.decimalNumberFormat
        if(exchange?.executions){
            exchange?.totalAmountSettledInCurrency = getTotalAmountSettledInCurrency(exchange?.executions)
            exchange?.totalAmountSettled = exchange?.totalAmountSettledInCurrency?.multiply(exchange?.currencyRate)
            exchange?.totalAmountSettledInLetter = convertDigitsToLetterService.retrieveAndConvertNumberToLetter(exchange?.totalAmountSettled, LocaleContextHolder.locale.toString(), decimal_format)
        }
    }

    BigDecimal getTotalAmountSettledInCurrency(List<Execution> executions){
        BigDecimal totalAmountSettledInCurrency = 0
        executions.each {
            if(it.state == null || it.state == ExchangeRequestType.EXECUTION_INITIAL_STATE){
                totalAmountSettledInCurrency += it.amountSettledMentionedCurrency
            }
        }
        return totalAmountSettledInCurrency
    }

}
