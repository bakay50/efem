package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_SAD
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED


class CheckNamesAndPartiesFromSADRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain Exchange
        defineBeans() {
            sadService(SadService)
        }
    }

    @Unroll
    void "test CheckNamesAndPartiesFromSADRule"() {
        given:
        Exchange exchange = new Exchange()
        exchange.clearanceOfficeCode = "CIAB"
        exchange.declarationSerial = "E"
        exchange.declarationNumber = "001"
        exchange.declarationDate = new LocalDate()
        exchange.basedOn = baseOn
        exchange.requestType = requestType
        exchange.importerCode = importerCode
        exchange.exporterCode = exporterCode
        exchange.startedOperation = startOperation
        exchange.sadInstanceId = sadId
        CheckNamesAndPartiesFromSADRule.metaClass.static.retrieveSadDocument = { sadData }

        when:
        Rule rule = new CheckNamesAndPartiesFromSADRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        expected | exporterCode | importerCode | sadData                                                            | startOperation  | requestType | baseOn      | sadId
        true     | "EX256"      | "IMP6328"    | [data: [consigneeCode: "IMP6325", exporterCode: "EX254", id: "1"]] | UPDATE_APPROVED | EC          | null        | "sad852"
        true     | "EX256"      | "IMP6328"    | [data: [consigneeCode: "IMP6325", exporterCode: "EX256", id: "1"]] | UPDATE_APPROVED | EC          | null        | "sad852"
        false    | null         | null         | [data: [consigneeCode: "IMP6325", exporterCode: "EX256", id: "1"]] | UPDATE_APPROVED | EC          | null        | "sad852"
        false    | "EX256"      | "IMP6325"    | [data: [consigneeCode: "IMP6325", exporterCode: "EX256", id: "1"]] | UPDATE_APPROVED | EC          | null        | "sad852"
        true     | "EX123"      | "IMP456"     | [data: [consigneeCode: "IMP6325", exporterCode: "EX256", id: "1"]] | CREATE          | EA          | BASE_ON_SAD | "sad852"
        true     | "EX123"      | "IMP456"     | [data: [consigneeCode: "IMP6325", exporterCode: "EX123", id: "1"]] | CREATE          | EA          | BASE_ON_SAD | "sad852"
        false    | null         | null         | [data: [consigneeCode: "IMP6325", exporterCode: "EX123", id: "1"]] | CREATE          | EA          | BASE_ON_SAD | "sad852"
        false    | "EX123"      | "IMP6325"    | [data: [consigneeCode: "IMP6325", exporterCode: "EX123", id: "1"]] | CREATE          | EA          | BASE_ON_SAD | "sad852"

    }

    @Unroll
    void "test checkSadDeclarantCode"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([declarantCode: decCodeParams], null)
        Exchange exchange = new Exchange()
        exchange.clearanceOfficeCode = "CIAB"
        exchange.declarationSerial = "E"
        exchange.declarationNumber = "001"
        exchange.declarationDate = new LocalDate()
        exchange.sadInstanceId = sadId
        exchange.declarantCode = decCode
        exchange.basedOn = baseOn
        exchange.requestType = requestType
        exchange.startedOperation = startOperation
        CheckNamesAndPartiesFromSADRule.metaClass.static.retrieveSadDocument = { sadData }

        when:
        Rule rule = new CheckNamesAndPartiesFromSADRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.errors.hasFieldErrors("declarantCode") == expected

        where:
        expected << [true, false, true, false, false, false]
        baseOn << [BASE_ON_SAD, BASE_ON_SAD, null, null, BASE_ON_SAD, null]
        requestType << [EA, EA, EC, EC, EA, EC]
        decCode << ["80001K", "00331B", "92542C", "00267T", "00267T", "92542C"]
        decCodeParams << ["80001K", "00331B", "92542C", "00267T", null, null]
        sadId << ["sad789", "sad745", "sad452", "sad85", "sad459", "sad742"]
        startOperation << [CREATE, CREATE, UPDATE_APPROVED, UPDATE_APPROVED, CREATE, UPDATE_APPROVED]
        sadData << [
                [data: [declarantCode: "00267T", id: "41"]],
                [data: [declarantCode: "00331B", id: "75"]],
                [data: [declarantCode: "00267T", id: "63"]],
                [data: [declarantCode: "00267T", id: "12"]],
                [data: [declarantCode: "00331B", id: "11"]],
                [data: [declarantCode: "80001K", id: "5"]],
        ]
    }
}
