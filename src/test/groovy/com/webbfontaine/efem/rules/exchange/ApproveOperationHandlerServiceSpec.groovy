package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.operations.ApproveOperationHandlerService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.workflow.Operation.*

@ConfineMetaClassChanges([WebRequestUtils, UserUtils, AppConfig])
class ApproveOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<ApproveOperationHandlerService>, DataTest {

    def setup() {
        mockDomains(Exchange)
        defineBeans {
            springSecurityService(SpringSecurityService)
        }
    }

    @Unroll
    def "Should call when operation leads to APPROVED status : #op"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([commitOperation: op], null)
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovOfficer() >> true
        UserUtils.isBankAgent() >> false
        Exchange exchange = new Exchange(registrationDateBank: LocalDate.now())

        when:
        service.setAuthorizationForEA(exchange)

        then:
        exchange.authorizationDate == authDate
        exchange.expirationDate == expDate
        exchange.authorizedBy == authBy
        exchange.year == exchange.registrationDateBank.getYear()

        where:
        op                           | authDate        | expDate                      | authBy
        "APPROVE_PARTIALLY_APPROVED" | LocalDate.now() | LocalDate.now().plusDays(90) | "LES FINEX"
        "UPDATE_APPROVED"            | null            | null                         | null

    }

    @Unroll
    def "operation #op should set ' #expected ' to authrozied by when bank prop is #prop"() {
        given:
        Exchange exchange = new Exchange()
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        WebRequestUtils.getParams() >> new GrailsParameterMap([commitOperation: op], null)
        UserUtils.isGovOfficer() >> isGov
        UserUtils.isBankAgent() >> isBank
        UserUtils.userPropertyValueAsList(_) >> prop

        when:
        service.setAuthorizedBy(exchange)

        then:
        exchange.authorizedBy == expected

        where:
        isGov | isBank | op                           | prop            | expected
        false | true   | "APPROVE_PARTIALLY_APPROVED" | ["LMAO"]        | "LMAO"
        false | true   | "APPROVE_PARTIALLY_APPROVED" | []              | null
        false | true   | "APPROVE_PARTIALLY_APPROVED" | ["LMAO", "LUL"] | null
        true  | false  | "APPROVE_REQUESTED"          | ["LMAO"]        | "LES FINEX"
    }

    @Unroll
    def "test #testCases"() {
        given:
        Exchange exchange1 = new Exchange(requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType?.BASE_ON_TVF, tvfInstanceId: "001", id: 1, amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"),
                balanceAs: new BigDecimal("5000.00"), treatmentLevel: treatmentLevel, status: docStatus).save()
        Operation commitOperation = APPROVE_REQUESTED
        def commitOperationName = APPROVE_REQUESTED.humanName()
        def params = new HashMap<String, String>()
        grailsApplication.config.com.webbfontaine.efem.maxApprovalConfig = maxApproval
        service.springSecurityService = [principal: [userProperties: [LVL: LVL]]]
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBankOfficer

        when:
        service.setCommitOperAndTreatmentLvl(exchange1, commitOperation, commitOperationName, params)

        then:
        assert params.treatmentLevel == expected
        assert params.commitOperation == operation

        where:
        testCases                                        | treatmentLevel | docStatus                      | maxApproval | expected | operation                  | LVL   | isBankOfficer
        "for document that is Requested Status"          | 1              | Statuses.ST_REQUESTED          | 1           | 1        | APPROVE_REQUESTED          | "ALL" | true
        "for document that is Partially Approved Status" | 4              | Statuses.ST_PARTIALLY_APPROVED | 3           | 5        | APPROVE_PARTIALLY_APPROVED | 4     | false
        "for treatment level is less than maxApproval"   | 1              | Statuses.ST_REQUESTED          | 3           | 2        | PARTIALLY_APPROVED         | 1     | false
        "for treatment level is equal to maxApproval"    | 2              | Statuses.ST_PARTIALLY_APPROVED | 3           | 3        | PARTIALLY_APPROVED         | 2     | false
    }

    @Ignore
    def "test for finexApproval"() {
        given:
        Exchange exchange = new Exchange(requestType: ExchangeRequestType.EA, basedOn: basedOn, sadInstanceId: "001", id: 1, amountNationalCurrency: amountNationalCurrency, amountMentionedCurrency: new BigDecimal("5000000.00"),
                balanceAs: new BigDecimal("50000.00"), status: status).save()

        Operation commitOperation = APPROVE_REQUESTED
        def commitOperationName = APPROVE_REQUESTED.humanName()
        def params = new HashMap<String, String>()
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> isBankAgent
        grailsApplication.config.com.webbfontaine.efem.maxApprovalConfig = 2
        params.finexApproval = testCase
        when:
        service.setCommitOperAndTreatmentLvl(exchange, commitOperation, commitOperationName, params)

        then:
        params.commitOperation == operation

        where:
        testCase | basedOn                         | isBankAgent | amountNationalCurrency       | operation          | status
        true     | ExchangeRequestType.BASE_ON_SAD | true        | new BigDecimal("5000000.00") | PARTIALLY_APPROVED | Statuses.ST_REQUESTED
        false    | ExchangeRequestType.BASE_ON_SAD | true        | new BigDecimal("5000000.00") | APPROVE_REQUESTED  | Statuses.ST_REQUESTED
        true     | ExchangeRequestType.BASE_ON_TVF | true        | new BigDecimal("400000.00")  | APPROVE_REQUESTED  | Statuses.ST_REQUESTED
        true     | ExchangeRequestType.BASE_ON_TVF | true        | new BigDecimal("6000000.00") | PARTIALLY_APPROVED | Statuses.ST_REQUESTED
        true     | ExchangeRequestType.BASE_ON_TVF | true        | new BigDecimal("6000000.00") | PARTIALLY_APPROVED | Statuses.ST_PARTIALLY_APPROVED
        true     | ExchangeRequestType.BASE_ON_TVF | false       | new BigDecimal("6000000.00") | PARTIALLY_APPROVED | Statuses.ST_REQUESTED
    }

    def "test case set registrationNumberBank and registrationDateBank if is bank agent"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.commitOperation = APPROVE_REQUESTED
        webRequest.params.commitOperationName = APPROVE_REQUESTED.name()

        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> configUser
        RequestContextHolder.setRequestAttributes(webRequest)
        service.exchangeService = Mock(ExchangeService)
        Exchange exchange = new Exchange(exchangeMap)
        def commitOp = APPROVE_REQUESTED

        when:
        service.beforePersist(exchange, commitOp)

        then:
        exchange.registrationNumberBank == regNum
        exchange.registrationDateBank == regDate

        where:
        configUser | regDate         | regNum  | exchangeMap
        true       | new LocalDate() | "test1" | [amountNationalCurrency: new BigDecimal(600000), registrationNumberBank: "test1", registrationDateBank: new LocalDate()]
        false      | null            | null    | [amountNationalCurrency: new BigDecimal(500000), registrationNumberBank: null, registrationDateBank: null]


    }

}