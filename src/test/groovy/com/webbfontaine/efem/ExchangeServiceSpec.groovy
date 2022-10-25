package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.execution.Execution
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.security.Roles.*
import static com.webbfontaine.efem.workflow.Operation.APPROVE_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.DOMICILIATE

class ExchangeServiceSpec extends Specification implements ServiceUnitTest<ExchangeService>, DataTest {

    def setup() {
        mockDomains(Exchange)
    }

    def "test for supDeclarationChecking where Status is invalid because isDocumentValid is false"() {
        given:
        Exchange exchange = new Exchange()
        exchange.setIsDocumentValid(false)
        SupDeclaration supDeclaration = new SupDeclaration()

        when:
        service.supDeclarationChecking(exchange, supDeclaration)

        then:
        assert supDeclaration.hasErrors() == true
        supDeclaration.errors.allErrors.code.each { errors ->
            assert errors in ["load.sad.status.error"]
        }
    }

    def "test for supDeclarationChecking where Status is invalid because the load Sad is not in Paid Status"() {
        given:
        Exchange exchange = new Exchange(sadInstanceId: 1, sadStatus: Statuses.ST_ASSESSED)
        SupDeclaration supDeclaration = new SupDeclaration()

        when:
        service.supDeclarationChecking(exchange, supDeclaration)

        then:
        assert supDeclaration.hasErrors() == true
        supDeclaration.errors.allErrors.code.each { errors ->
            assert errors in ["load.sad.status.error"]
        }
    }

    def "test for supDeclarationChecking where declaration is not connected to the user"() {
        given:
        Exchange exchange = new Exchange()
        exchange.setIsSadOwner(false)
        SupDeclaration supDeclaration = new SupDeclaration()

        when:
        service.supDeclarationChecking(exchange, supDeclaration)

        then:
        assert supDeclaration.hasErrors() == true
        supDeclaration.errors.allErrors.code.each { errors ->
            assert errors in ["load.sad.ownership.error"]
        }
    }

    def "test for supDeclarationChecking where declaration is not existing"() {
        given:
        Exchange exchange = new Exchange(sadInstanceId: null, isDocumentValid: true, isSadOwner: true)
        SupDeclaration supDeclaration = new SupDeclaration()

        when:
        service.supDeclarationChecking(exchange, supDeclaration)

        then:
        assert supDeclaration.hasErrors() == true
        supDeclaration.errors.allErrors.code.each { errors ->
            assert errors in ["exchange.errors.existe.declarationNumber"]
        }
    }

    def "test for setInstanceRegistrationDefaultDate "() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Exchange exchange = new Exchange()
        exchange.startedOperation = startedOperation

        when:
        service.setInstanceRegistrationDefaultDate(exchange)

        then:
        exchange.registrationDateBank == expected

        where:
        startedOperation   | expected
        APPROVE_REQUESTED  | LocalDate.now()
        DOMICILIATE        | LocalDate.now()
    }

    @Unroll
    def "#testCase"() {
        given:
        Exchange exchange = new Exchange(amountMentionedCurrency: new BigDecimal(10.00), balanceAs: new BigDecimal(10.00))
        exchange.addExecution(new Execution(amountMentionedExCurrency: amountMention))

        when:
        service.computationOfBalanceAs(exchange)

        then:
        exchange.balanceAs == updatedBalance

        where:
        testCase                                            | amountMention         | updatedBalance
        "Exchange has no execution"                         | new BigDecimal(0)     | new BigDecimal(10.00)
        "Amount < Amount of Transfer in mentioned Currency" | new BigDecimal(3.00)  | new BigDecimal(7.00)
        "Amount > Amount of Transfer in mentioned Currency" | new BigDecimal(13.00) | new BigDecimal(-3.00)

    }

    void generateExchange() {
        Exchange exchange1 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 1, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")).save()
        Exchange exchange2 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 2, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_CANCELLED).save()
        Exchange exchange5 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "003", id: 3, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")).save()

        Exchange exchange3 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", id: 7, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_REJECTED).save()
        Exchange exchange4 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", id: 9, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")).save()
        Exchange exchange6 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "005", id: 8, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")).save()

    }

    @Ignore
    @Unroll
    def "test method getSumOfAmountNationalCurrency"() {
        given:
        generateExchange()
        Exchange exchange = new Exchange(requestType: ExchangeRequestType.EA, requestNo: "001", requestDate: new LocalDate())

        service.metaClass.static.doSumOfAmountNationalCurrency = {
            a, b -> return sumOfAmountNationalCurrency
        }
        when:
        def result = service.getSumOfAmountNationalCurrency(exchange)

        then:
        assert result == expected

        where:
        sumOfAmountNationalCurrency  | expected
        ["10"]                       | new BigDecimal("10")
        null                         | BigDecimal.ZERO
        ["0"]                        | BigDecimal.ZERO
    }

    @Unroll
    def "test for conversion of CIF value that is equal to #cifAmount and Currency Rate is #currency"() {
        given:
        generateExchange()
        Exchange exchange = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5.00"), balanceAs: new BigDecimal("5000.00"),
                totalAmountOfCif: new BigDecimal(cifAmount), currencyRate: new BigDecimal(currency),
                clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_REQUESTED)

        when:
        service.getConvertedCIF(exchange)

        then:
        exchange.convertedCif == expectedResult

        where:
        cifAmount | currency | expectedResult
        "5000"    | "5"      | 1000
        "5000"    | "10"     | 500
    }

    @Unroll
    def "test for generateRegistrationNumber with generatedRegistrationNumber = #expected "() {
        given:
        generateExchange()
        Exchange exchange = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_SAD, sadInstanceId: "004", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5.00"), balanceAs: new BigDecimal("5000.00"),
                totalAmountOfCif: new BigDecimal(cifAmount), currencyRate: new BigDecimal(currency), registrationNumberSequence: regSeq, bankCode: bank, requestDate: dateReq,
                clearanceOfficeCode: "BJA02", declarationSerial: "S", declarationNumber: "1", declarationDate: LocalDate.now(), status: Statuses.ST_REQUESTED)

        when:
        def generated = service.generateRegistrationNumber(exchange)

        then:
        generated == expected

        where:
        cifAmount | currency | expectedResult | regSeq | bank  | dateReq         | expected
        "5000"    | "5"      | 1000           | 1      | "AB"  | LocalDate.now() | "AB${LocalDate.now().getYear()}000001"
        "5000"    | "10"     | 500            | 2      | "AIB" | LocalDate.now() | "AIB${LocalDate.now().getYear()}000002"
    }

    @Unroll
    def "test for getRequestedBy should return correct ROLE when isDeclarant is = #mockIsDeclarant "() {
        setup:
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> mockIsDeclarant

        when:
        def result = service.getRequestedBy()

        then:
        result == expected

        where:
        mockIsDeclarant | expected
        true            | DECLARANT.authority
        false           | TRADER.authority
    }
}
