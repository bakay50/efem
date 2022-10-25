package com.webbfontaine.efem

import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.execution.ExecutionService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

class ExecutionServiceSpec extends Specification implements ServiceUnitTest<ExecutionService>, DataTest {
    def setup() {
        defineBeans {
            convertDigitsToLetterService(ConvertDigitsToLetterService)
        }
        mockDomain(Exchange)
    }

    @Unroll
    void "test for initAddExecutions - #testCase"() {
        when:
        Execution executionInstance = new  Execution()
        service.initAddExecutions(exchangeInstance, executionInstance)

        then:
        executionInstance.currencyExCode == exchangeInstance.currencyCode
        executionInstance.currencyExName == exchangeInstance.currencyName
        executionInstance.currencyExRate == exchangeInstance.currencyRate
        executionInstance.executingBankCode == exchangeInstance.bankCode
        executionInstance.executingBankName == exchangeInstance.bankName
        executionInstance.countryProvenanceDestinationExCode == exchangeInstance.countryProvenanceDestinationCode
        executionInstance.countryProvenanceDestinationExName == exchangeInstance.countryProvenanceDestinationName
        executionInstance.provenanceDestinationExBank == exchangeInstance.provenanceDestinationBank
        executionInstance.bankAccountNumberCreditedDebited == exchangeInstance.bankAccountNocreditedDebited
        executionInstance.accountExBeneficiary == exchangeInstance.accountNumberBeneficiary

        where:
        testCase                 | exchangeInstance
        "null exchange instance" | new Exchange()
        "with value"             | new Exchange(currencyCode: 'COD', currencyName: 'COD-DESC', currencyRate: new BigDecimal('10'),
                                                bankCode: 'BNK-COD', bankName: 'BNK-NAME', countryProvenanceDestinationCode: 'ben-cod',
                                                countryProvenanceDestinationName: 'ben-name', provenanceDestinationBank:'dest-bank',
                                                bankAccountNocreditedDebited: '54321', accountNumberBeneficiary: '12345')
    }

    @Unroll
    void "test for setExchangeAmountSettledFields" (){
        when:
        Exchange exchange = new Exchange(requestNo: "REQ-01", requestDate: new LocalDate(), currencyCode: "EUR", currencyRate: new BigDecimal(500))
        exchange?.addToExecutions(new Execution(amountSettledMentionedCurrency : new BigDecimal(300), state: 0))
        exchange?.addToExecutions(new Execution(amountSettledMentionedCurrency : new BigDecimal(1200), state: 0))

        service.setExchangeAmountSettledFields(exchange)

        then:
        exchange?.totalAmountSettledInCurrency == new BigDecimal(1500)
        exchange?.totalAmountSettled == new BigDecimal(750000)
    }

    @Unroll
    void "test for getTotalAmountSettledInCurrency" (){
        when:
        Exchange exchange = new Exchange(requestNo: "REQ-01", requestDate: new LocalDate(), currencyCode: "EUR", currencyRate: new BigDecimal(500))
        exchange?.addToExecutions(new Execution(amountSettledMentionedCurrency : new BigDecimal(300), state: state))
        exchange?.addToExecutions(new Execution(amountSettledMentionedCurrency : new BigDecimal(1200), state: 0))

        BigDecimal result = service.getTotalAmountSettledInCurrency(exchange.executions)

        then:
        result == totalAmountSettledInCurrency

        where:
        state | totalAmountSettledInCurrency
        null  | new BigDecimal(1500)
        0     | new BigDecimal(1500)
        1     | new BigDecimal(1200)
    }

}
