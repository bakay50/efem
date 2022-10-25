package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.constants.ExchangeRequestType.UNCLEARED_OPERTYPE

@ConfineMetaClassChanges(AppConfig)
class CheckUnclearedEaRuleSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain(Exchange)
    }

    @Unroll
    def "test for CheckUnclearedEaRule #testCase"() {
        given:
        GroovyMock(AppConfig, global: true)
        AppConfig.isRuleEnabled("CheckUnclearedEaRule") >> true
        genertedEA()
        Exchange exchange = new Exchange(id: 4, tvfNumber: declaredTVF, importerCode: impCode, tvfDate: new LocalDate().minusDays(3))
        when:
        Rule rule = new CheckUnclearedEaRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        assert exchange?.hasErrors() == expectedResult
        assert exchange?.errors?.errorCount == numOfErr

        where:
        testCase          | numOfErr | expectedResult | declaredTVF | impCode
        "if has no error" | 0        | false          | 123         | "imp1"
        "if has  error"   | 1        | true           | 124         | "imp2"
    }

    void genertedEA() {
        Exchange exchange1 = new Exchange(id: 1, tvfNumber: 123, tvfDate: new LocalDate().minusDays(3), importerCode: "imp1", operType: UNCLEARED_OPERTYPE, requestNo: "test1", requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_EXECUTED, expirationDate: new LocalDate().minusDays(2))
        exchange1.save()
        Exchange exchange2 = new Exchange(id: 2, tvfNumber: 123, tvfDate: new LocalDate().minusDays(3), importerCode: "imp2", operType: "EA002", requestNo: "test2", requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("10.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_EXECUTED, expirationDate: new LocalDate().minusDays(2))
        exchange2.save()
        Exchange exchange4 = new Exchange(id: 2, tvfNumber: 124, tvfDate: new LocalDate().minusDays(3), importerCode: "imp1", operType: UNCLEARED_OPERTYPE, requestNo: "test2", requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("10.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_EXECUTED, expirationDate: new LocalDate().minusDays(2))
        exchange4.save()
        Exchange exchange3 = new Exchange(id: 3, tvfNumber: 124, tvfDate: new LocalDate().minusDays(3), importerCode: "imp2", operType: UNCLEARED_OPERTYPE, requestNo: "test3", requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "003", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("15.00"), balanceAs: new BigDecimal("5000.00"), status: Statuses.ST_EXECUTED, expirationDate: new LocalDate().minusDays(2))
        SupDeclaration supDeclaration3 = new SupDeclaration(exchange: exchange3, id: 3)
        exchange3.addToSupDeclarations(supDeclaration3)
        exchange3.save()
    }
}
