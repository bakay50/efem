package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import grails.testing.gorm.DataTest
import grails.util.Holders
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*

@ConfineMetaClassChanges([ExchangeOperationHandlerUtils, UserUtils])
class BusinessLogicUtilsSpec extends Specification implements DataTest {

    @Unroll
    def "Check if status: #stat in list #statList, expected: #expected"() {
        given:
        Exchange exchange = new Exchange(status: stat)

        when:
        def result = BusinessLogicUtils.checkIfStatusInList(exchange, statList)

        then:
        result == expected

        where:
        stat         | statList                    | expected
        ST_APPROVED  | [ST_CANCELLED, ST_APPROVED] | true
        ST_STORED    | [ST_APPROVED, ST_CANCELLED] | false
        ST_REQUESTED | []                          | false
    }

    @Unroll
    def "on operation #operation attachment should be editable expected: #isAtteditable"() {
        given:
        Exchange exchange = new Exchange()
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.getOp() >> operation

        when:
        BusinessLogicUtils.setAttachmentAccess(exchange)

        then:
        exchange.isAttachmentEditable == isAttEditable

        where:
        operation                  | isAttEditable
        APPROVE_REQUESTED          | false
        APPROVE_PARTIALLY_APPROVED | false
        REJECT_PARTIALLY_APPROVED  | false
        QUERY_PARTIALLY_APPROVED   | false
        CREATE                     | true
        REQUEST_QUERIED            | true
        UPDATE_QUERIED             | true
        UPDATE_APPROVED            | true
        UPDATE_EXECUTED            | true
    }

    @Unroll
    def "test for Field Configuration if field is #field an EA status is #stat"() {
        given:
        Exchange exchange = new Exchange(status: stat)
        GroovyMock(ExchangeOperationHandlerUtils, global: true)
        ExchangeOperationHandlerUtils.getOp() >> operation

        when:
        boolean result = BusinessLogicUtils.isEAFieldEditable(field, exchange)

        then:
        assert result == expectedResult

        where:
        operation         | stat         | field                 | expectedResult
        APPROVE_REQUESTED | ST_REQUESTED | "exporterCode"        | true
        REQUEST           | null         | "declarantCode"       | false
        UPDATE_QUERIED    | ST_QUERIED   | "currencyCode"        | false
        UPDATE_STORED     | ST_STORED    | "currencyCode"        | false
        UPDATE_QUERIED    | ST_QUERIED   | "countryOfExportCode" | false
    }

    @Unroll
    def "test for isCommentDisabled if operation is #statartedOp"() {
        given:
        Exchange exchange = new Exchange()
        exchange.setStartedOperation(statartedOp)
        when:
        boolean isDisabled = BusinessLogicUtils.isCommentDisabled(exchange)

        then:
        assert isDisabled == excpectedResult

        where:
        statartedOp       | excpectedResult
        APPROVE_REQUESTED | false
        REQUEST_QUERIED   | true
        QUERY_REQUESTED   | false
        REJECT_REQUESTED  | false
        CANCEL_APPROVED   | false
        CANCEL_QUERIED    | false
    }

    @Unroll
    void "test requireDomiciliationFields - #testCase"() {
        given:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBankAgent

        when:
        boolean result = BusinessLogicUtils.requireDomiciliationFields(operation)

        then:
        result == expected

        where:
        testCase                                    | isBankAgent | operation       | expected
        'Bank Agent and Operation Update Approved'  | true        | UPDATE_APPROVED | true
        'Bank Agent and Operation Update Executed'  | true        | UPDATE_EXECUTED | true
        'Not B.Agent and Operation Update Approved' | false       | UPDATE_APPROVED | false
        'Not B.Agent and Operation Update Approved' | false       | UPDATE_EXECUTED | false
        'Bank Agent and Operation Request'          | true        | REQUEST         | false
        'Bank Agent and Operation Query Requested'  | true        | QUERY_REQUESTED | false
    }

    @Unroll
    void "test isDomiciliationRequired if Amount = #value"() {
        given:
        Exchange exchange = new Exchange(amountNationalCurrency: value)

        when:
        boolean result = BusinessLogicUtils.isDomiciliationRequired(exchange?.amountNationalCurrency)

        then:
        result == expected

        where:
        value                       | expected
        new BigDecimal(550000.00)   | false
        new BigDecimal(11000000.00) | true
        new BigDecimal(10000000.00) | true

    }

    @Unroll
    void "test isVerifyAvailable if status is #status"() {
        given:
        Exchange exchange = new Exchange(status: status)

        when:
        boolean result = BusinessLogicUtils.isVerifyAvailable(exchange)

        then:
        result == expected

        where:
        status      | expected
        ST_STORED   | true
        null        | true
        ST_APPROVED | false


    }

    @Unroll
    void "test canDisplayDeclarationSection"() {
        given:
        Exchange exchange = new Exchange(requestType: requestType, status: status)

        when:
        boolean result = BusinessLogicUtils.canDisplayDeclarationSection(exchange)

        then:
        result == expected

        where:
        requestType            | status     | expected
        ExchangeRequestType.EC | ST_QUERIED | true
        ExchangeRequestType.EA | null       | true
    }

    @Unroll
    def "test for Field Configuration if field is #field a transferOrder for operation #operation"() {
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> declarantConnect
        UserUtils.isTrader() >> traderConnect
        UserUtils.isBankAgent() >> bankAgentConnect

        when:
        boolean result = BusinessLogicUtils.isFieldEditableTransferOrder(field, operation)

        then:
        assert result == expectedResult

        where:
        operation | declarantConnect | traderConnect | bankAgentConnect | field           | expectedResult
        REQUEST   | true             | false         | false            | 'executionRef'  | false
        REQUEST   | false            | true          | false            | 'executionRef'  | false
        REQUEST   | false            | false         | true             | 'executionRef'  | true
        REQUEST   | true             | false         | false            | 'executionDate' | false
        REQUEST   | false            | true          | false            | 'executionDate' | false
        REQUEST   | false            | false         | true             | 'executionDate' | true
    }

    @Unroll
    void "test isPrintExchange"() {
        given:
        Exchange exchange = new Exchange(requestType: requestType, status: status)

        when:
        boolean result = BusinessLogicUtils.isPrintExchange(exchange)

        then:
        result == expected

        where:
        requestType            | status       | expected
        ExchangeRequestType.EC | ST_QUERIED   | false
        ExchangeRequestType.EC | null         | false
        ExchangeRequestType.EA | ST_REQUESTED | true
        ExchangeRequestType.EC | ST_REQUESTED | false
    }


    @Unroll
    void "test traderOrDeclarantFieldsProhibited"() {
        given:
        Exchange exchange = new Exchange(requestType: requestType)
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> isDeclarant
        UserUtils.isTrader() >> isTrader
        UserUtils.isBankAgent() >> isBankAgent
        exchange.startedOperation = startedOperation
        when:
        boolean result = BusinessLogicUtils.traderOrDeclarantFieldsProhibited(exchange)
        then:
        result == expected

        where:
        requestType            | startedOperation | isDeclarant | isTrader | isBankAgent | expected
        ExchangeRequestType.EC | CREATE           | true        | true     | false       | true
        ExchangeRequestType.EC | UPDATE_APPROVED  | false       | false    | false       | false
        ExchangeRequestType.EC | UPDATE_EXECUTED  | false       | false    | true        | false
        ExchangeRequestType.EC | UPDATE_QUERIED   | true        | false    | false       | true

    }

    @Unroll
    def "test enableCurrencyPayCodeByOperation"() {
        given:
        Holders.config.efemAllowedOfficeCode.code_office = ["CIAB6", "CIAB4", "CIYKP", "CIB41", "CIRSY", "CIAB3"]
        Exchange exchange = new Exchange(requestType: requestType, clearanceOfficeCode: clearanceOfficeCode)

        when:
        boolean result = BusinessLogicUtils.enableCurrencyPayCodeByOperation(exchange, op)

        then:
        result == expected

        where:
        requestType | clearanceOfficeCode | op              | expected
        "EA"        | "CIAB6"             | REQUEST_STORED  | true
        "EA"        | "CIAB6"             | REQUEST_QUERIED | true
        "EA"        | "CIB41"             | null            | true
        "EA"        | "CIAB1"             | REQUEST_QUERIED | false
        "EC"        | "CIAB6"             | REQUEST_QUERIED | false
    }

    @Unroll
    def "test canEnableDomiciliationDetails"() {
        given:
        Exchange exchange = new Exchange(status: status, requestType: requestType)
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> declarantConnect
        UserUtils.isTrader() >> traderConnect
        UserUtils.isBankAgent() >> bankAgentConnect

        when:
        boolean result = BusinessLogicUtils.canEnableDomiciliationDetails(exchange)

        then:
        assert result == expectedResult

        where:
        declarantConnect | traderConnect | bankAgentConnect | expectedResult | status                | requestType
        true             | false         | false            | false          | Statuses.ST_REQUESTED | ExchangeRequestType.EC
        false            | true          | false            | false          | Statuses.ST_REQUESTED | ExchangeRequestType.EA
        false            | false         | true             | true           | Statuses.ST_REQUESTED | ExchangeRequestType.EA
        true             | false         | false            | false          | Statuses.ST_REQUESTED | ExchangeRequestType.EA
        false            | true          | false            | false          | Statuses.ST_REQUESTED | ExchangeRequestType.EA
        false            | false         | true             | false          | Statuses.ST_CANCELLED | ExchangeRequestType.EA
    }

    @Unroll
    def "test checkIfDisplayExchangeImportExportXml"() {
        given:
        Exchange exchange = new Exchange(requestType: requestType)

        when:
        boolean result = BusinessLogicUtils.canDisplayExchangeImportExportXml(exchange)

        then:
        result == expected

        where:
        requestType            | expected
        ExchangeRequestType.EA | false
        ExchangeRequestType.EC | true

    }

    @Unroll
    def "test for Field Configuration if field is #field a repatriation for operation #operation"() {
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> declarantConnect
        UserUtils.isTrader() >> traderConnect
        UserUtils.isBankAgent() >> bankAgentConnect

        when:
        boolean result = BusinessLogicUtils.isFieldEditableRepatriation(field, operation)

        then:
        assert result == expectedResult

        where:
        operation        | declarantConnect | traderConnect | bankAgentConnect | field           | expectedResult
        CREATE           | true             | false         | false            | 'executionRef'  | false
        CREATE           | false            | true          | false            | 'executionRef'  | false
        CREATE           | false            | false         | true             | 'executionRef'  | true
        UPDATE_CONFIRMED | false            | false         | true             | 'executionRef'  | false
        DECLARE          | true             | false         | false            | 'executionDate' | false
        DECLARE          | false            | true          | false            | 'executionDate' | false
        DECLARE          | false            | false         | true             | 'executionDate' | true
        UPDATE_CONFIRMED | false            | false         | true             | 'executionDate' | false
    }

    @Unroll
    void "test isRepatriationFieldReadonly()"() {
        given:
        Repatriation repatriation = new Repatriation()
        repatriation.startedOperation = operation

        when:
        boolean result = BusinessLogicUtils.isRepatriationFieldReadonly(repatriation, field)

        then:
        result == excepted
        where:
        excepted << [false, true]
        operation << [CREATE, UPDATE_CONFIRMED]
        field << ["currencyCode", "currencyCode"]
    }

    @Unroll
    void "Test isRepatriationFieldMandatory()"() {
        given:
        Repatriation repatriation = new Repatriation()
        repatriation.startedOperation = operation

        when:
        boolean result = BusinessLogicUtils.isRepatriationFieldMandatory(repatriation, field)

        then:
        result == excepted

        where:
        excepted << [true, false]
        operation << [CREATE, UPDATE_CONFIRMED]
        field << ["currencyCode", "currencyCode"]

    }
}
