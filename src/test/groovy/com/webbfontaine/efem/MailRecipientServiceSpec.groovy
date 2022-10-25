package com.webbfontaine.efem

import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.constants.ExchangeRequestType
import static com.webbfontaine.efem.constants.Statuses.*
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.security.Roles
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.rimm.cmp.Company
import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.datastore.mapping.core.connections.ConnectionSource
import org.grails.datastore.mapping.simple.SimpleMapDatastore
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/14/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class MailRecipientServiceSpec extends Specification implements ServiceUnitTest<MailRecipientService>, DataTest {

    @Shared
    @AutoCleanup
    SimpleMapDatastore dataStore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "rimm"], Company, Declarant, Bank)

    Class<?>[] getDomainClassesToMock() {
        return [Exchange, TransferOrder] as Class[]
    }

    void setupSpec() {
        new Company(code: '0001', email: 'comp@test.com', description: 'test name').save(flush: true)
        new Declarant(code: '1234', email: 'dec@test.com', description: 'test name').save(flush: true)
        new Bank(code: 'bank1', email: 'bank@test.com', description: 'test1 name').save(flush: true)
        new Bank(code: 'bank2', email: 'bank2@test.com', description: 'test2 name').save(flush: true)
        new Bank(code: 'bank3', email: 'bank3@test.com', description: 'test3 name').save(flush: true)
    }

    @Unroll
    void "On request operation and Exchange end status is : #exchangeEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Exchange exchange = new Exchange(requestType : requestType, status : exchangeEndStatus, declarantCode : '1234', importerCode : '0001', bankCode: bankCode,domiciliationBankCode: domiciliationBank, domiciliationNumber : '12345', departmentInCharge : departmentInCharge)
        GroovyMock(UserUtils, global: true)
        UserUtils.isDeclarant() >> true
        UserUtils.isTrader() >> true

        when:
        def result = service.resolveRequestOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus     | mailsSent | domiciliationBank | requestType            | departmentInCharge                     | bankCode
        ST_STORED             | 0         | 'bank1'           | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | 'bank1'
        ST_REQUESTED          | 2         | 'bank2'           | ExchangeRequestType.EC | null                                   | 'bank1'
        ST_REQUESTED          | 1         | 'bank1'           | ExchangeRequestType.EC | null                                   | 'bank1'
        ST_REQUESTED          | 1         | 'bank1'           | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | 'bank1'
        ST_REQUESTED          | 2         | 'bank1'           | ExchangeRequestType.EA | ExchangeRequestType.departmentInCharg  | 'bank1'
        ST_REQUESTED          | 3         | 'bank2'           | ExchangeRequestType.EA | null                                   | 'bank1'
        ST_REQUESTED          | 2         | null              | ExchangeRequestType.EA | ExchangeRequestType.departmentInCharg  | 'bank1'
        ST_REQUESTED          | 1         | null              | ExchangeRequestType.EA | ExchangeRequestType.departmentInCharg  |  null

    }

    @Unroll
    void "On reject operation and Exchange end status is : #exchan geEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Exchange exchange = new Exchange(status : exchangeEndStatus, declarantCode : '1234', importerCode : '0001', requestedBy : mockRole)
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        UserUtils.isGovOfficer() >> true

        when:
        def result = service.resolveRejectOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus     | mailsSent | mockRole
        ST_REJECTED           | 1         | Roles.DECLARANT.authority
        ST_REQUESTED          | 0         | Roles.TRADER.authority
        ST_REJECTED           | 1         | Roles.TRADER.authority
    }

    @Unroll
    void "On query operation and Exchange end status is : #exchangeEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Exchange exchange = new Exchange(status : exchangeEndStatus, declarantCode : declarantCode, importerCode : '0001')
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        UserUtils.isGovOfficer() >> true

        when:
        def result = service.resolveQueryOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus    | mailsSent | declarantCode
        ST_QUERIED           | 1         |  '1234'
        ST_REJECTED          | 0         |  null
        ST_QUERIED           | 0         |  null
    }

    @Unroll
    void "On cancel operation and Exchange end status is : #exchangeEndStatus number of notification to be sent is #mailsSent"() {
        given:
        GroovyMock(UserUtils, global: true)
        GroovyMock(WebRequestUtils, global: true)
        Exchange exchange = new Exchange(requestType : requestType, status : exchangeEndStatus, declarantCode : '1234', importerCode : '0001', bankCode : 'bank1', departmentInCharge: departmentInCharge)
        UserUtils.isBankAgent() >> true
        UserUtils.isGovOfficer() >> true
        WebRequestUtils.getParams() >> new GrailsParameterMap([initialStatus: initialStatus], null)

        when:
        def result = service.resolveCancelOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus     | mailsSent | requestType            | departmentInCharge                     | initialStatus
        ST_CANCELLED          | 1         | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | ST_QUERIED
        ST_CANCELLED          | 1         | ExchangeRequestType.EC | null                                   | ST_QUERIED
        ST_CANCELLED          | 1         | ExchangeRequestType.EA | ExchangeRequestType.departmentInCharg  | ST_QUERIED
        ST_CANCELLED          | 1         | ExchangeRequestType.EA | null                                   | ST_QUERIED
        ST_CANCELLED          | 2         | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | ST_APPROVED
        ST_CANCELLED          | 2         | ExchangeRequestType.EC | null                                   | ST_APPROVED
        ST_CANCELLED          | 0         | ExchangeRequestType.EA | ExchangeRequestType.departmentInCharg  | ST_APPROVED
        ST_CANCELLED          | 0         | ExchangeRequestType.EA | null                                   | ST_APPROVED
    }

    @Unroll
    void "On approve operation and Exchange end status is : #exchangeEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Exchange exchange = new Exchange(requestType : requestType, status : exchangeEndStatus, declarantCode : '1234', importerCode : '0001', bankCode: 'bank1', departmentInCharge : departmentInCharge, authorizedBy: authorizedBy)
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        UserUtils.isGovOfficer() >> true

        when:
        def result = service.resolveApproveOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus     | mailsSent | domiciliationBank | requestType            | departmentInCharge                     | authorizedBy
        ST_PARTIALLY_APPROVED | 0         | 'bank1'           | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | null
        ST_PARTIALLY_APPROVED | 1         | 'bank2'           | ExchangeRequestType.EA | null                                   | null
        ST_APPROVED           | 2         | 'bank1'           | ExchangeRequestType.EC | null                                   | null
        ST_APPROVED           | 2         | 'bank1'           | ExchangeRequestType.EC | ExchangeRequestType.departmentInCharg  | null
        ST_APPROVED           | 2         |  null             | ExchangeRequestType.EA | null                                   | ExchangeRequestType.authorizedBy
        ST_APPROVED           | 3         |  null             | ExchangeRequestType.EA | null                                   | null
    }

    @Unroll
    void "On update operation and Exchange end status is : #exchangeEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Exchange exchange = new Exchange(status : exchangeEndStatus, declarantCode : '1234', importerCode : '0001', basedOn : basedOn, requestedBy:  mockRole,requestType : requestType, isFinalAmount : isFinalAmount, geoArea : geoArea, bankCode : 'bank1')
        exchange.addAttDoc(new AttachedDoc(docType: '0008', docRef : "002020"))
        exchange.addAttDoc(new AttachedDoc(docType: '0007', docRef : "002030"))
        GroovyMock(UserUtils, global: true)
        GroovyMock(WebRequestUtils, global: true)
        UserUtils.isBankAgent() >> true
        UserUtils.isTrader() >> true
        UserUtils.isDeclarant() >> true
        service.tvfService = [loadTvf : { a, b -> new Exchange(tvf: new Tvf(impEmail: 'mail@test.com'))}]
        WebRequestUtils.getParams() >> new GrailsParameterMap([operationsStarted: operationsStarted], null)
        when:
            def result = service.resolveUpdateOperationRecipients(exchange)

        then:
        result.size() == mailsSent

        where:
        exchangeEndStatus    | requestType             |basedOn                          | mailsSent | mockRole                    | operationsStarted           |  isFinalAmount   |  geoArea
        ST_EXECUTED          |  ExchangeRequestType.EA |ExchangeRequestType.BASE_ON_TVF  | 0         | Roles.TRADER.authority      | Operation.UPDATE_EXECUTED   |   true           |  ExchangeRequestType.AREA_002
        ST_EXECUTED          |  ExchangeRequestType.EA |ExchangeRequestType.BASE_ON_SAD  | 0         | Roles.TRADER.authority      | Operation.UPDATE_EXECUTED   |   true           |  ExchangeRequestType.AREA_002
        ST_QUERIED           |  ExchangeRequestType.EA |ExchangeRequestType.BASE_ON_TVF  | 0         | Roles.TRADER.authority      | Operation.UPDATE_QUERIED    |   true           |  ExchangeRequestType.AREA_002
        ST_EXECUTED          |  ExchangeRequestType.EA |ExchangeRequestType.BASE_ON_TVF  | 0         | Roles.DECLARANT.authority   | Operation.UPDATE_EXECUTED   |   true           |  ExchangeRequestType.AREA_002
        ST_EXECUTED          |  ExchangeRequestType.EA |ExchangeRequestType.BASE_ON_SAD  | 0         | Roles.DECLARANT.authority   | Operation.UPDATE_EXECUTED   |   true           |  ExchangeRequestType.AREA_002
        ST_APPROVED          |  ExchangeRequestType.EC | null                            | 1         | Roles.DECLARANT.authority   | Operation.UPDATE_APPROVED   |   true           |  ExchangeRequestType.AREA_002
        ST_EXECUTED          |  ExchangeRequestType.EC | null                            | 0         | Roles.DECLARANT.authority   | Operation.UPDATE_QUERIED    |   true           |  ExchangeRequestType.AREA_001
        ST_EXECUTED          |  ExchangeRequestType.EC | null                            | 0         | Roles.TRADER.authority      | Operation.UPDATE_EXECUTED   |   false          |  ExchangeRequestType.AREA_003
    }

    @Unroll
    void "On request operation done by Trader and TransferOrder end status is : #transferEndStatus number of notification to be sent is #mailsSent"() {
        given:
        TransferOrder transferOrder = new TransferOrder(status : transferEndStatus, importerCode : '0001', bankCode: 'bank1')
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        UserUtils.isBankAgent() >> true
        when:
        def resultRequested = service.resolveOrderRequestedOperationRecipients(transferOrder)

        and:
        assert resultRequested[0].mailType == 'REQBANK'
        assert resultRequested[0].recipientEmail == 'bank@test.com'

        then:
        resultRequested.size() == mailsSent

        where:
        transferEndStatus     | mailsSent
        ST_REQUESTED          | 1
    }

    @Unroll
    void "On request operation done by bank Agent and TransferOrder end status is : #transferEndStatus number of notification to be sent is #mailsSent"() {
        given:
        TransferOrder transferOrder = new TransferOrder(status : transferEndStatus, importerCode : '0001', bankCode: 'bank1')
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        UserUtils.isBankAgent() >> true
        when:
        def resultValidated = service.resolveOrderRequestedOperationRecipients(transferOrder)

        and:
        assert resultValidated[0].mailType == 'REQVALIMP'
        assert resultValidated[0].recipientEmail == 'comp@test.com'

        then:
        resultValidated.size() == mailsSent

        where:
        transferEndStatus     | mailsSent
        ST_VALIDATED          | 1
    }

    @Unroll
    void "On Query operation and Transfer end status is : #transferEndStatus number of notification to be sent is #mailsSent"() {
        given:
        TransferOrder transferOrder = new TransferOrder(status : transferEndStatus, importerCode : '0001', bankCode: 'bank1', requestedBy : mockRole)
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true

        when:
        def resultQueried = service.resolveOrderQueriedOperationRecipients(transferOrder)

        and:
        assert resultQueried[0].mailType == 'QUEIMP'
        assert resultQueried[0].recipientEmail == 'comp@test.com'

        then:
        resultQueried.size() == mailsSent

        where:
        transferEndStatus     | mailsSent | mockRole
        ST_QUERIED            | 1         | Roles.BANK_AGENT.authority

    }

    @Unroll
    void "On Declare operation and Repatriation end status is : #repatriationEndStatus , repatriation bank is #repatriationBank number of notification to be sent is #mailsSent"() {
        given:
        Repatriation repatriation = new Repatriation(status : repatriationEndStatus, code : '0001',repatriationBankCode : repatriationBank)
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test1 name', ecReference: "EA0001"))
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test2 name', ecReference: "EA0002"))
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true



        when:
        def result = service.resolveRepatriationDeclareOperationRecipients(repatriation)

        then:
        result.size() == mailsSent

        where:
        repatriationEndStatus      | mailsSent | repatriationBank
        ST_CONFIRMED               | 2         | 'bank1'
        ST_CONFIRMED               | 3         | 'bank3'
        ST_DECLARED                | 1         | 'bank3'
        ST_REJECTED                | 0         | 'bank3'

    }

    @Unroll
    void "On Confirm operation and Repatriation end status is : #repatriationEndStatus , repatriation bank is #repatriationBank number of notification to be sent is #mailsSent"() {
        given:
        Repatriation repatriation = new Repatriation(status : repatriationEndStatus, code : '0001',repatriationBankCode : repatriationBank)
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test1 name', ecReference: "EA0001"))
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test2 name', ecReference: "EA0002"))
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true



        when:
        def result = service.resolveRepatriationConfirmOperationRecipients(repatriation)

        then:
        result.size() == mailsSent

        where:
        repatriationEndStatus      | mailsSent | repatriationBank
        ST_CONFIRMED               | 3         | 'bank1'
        ST_CONFIRMED               | 3         | 'bank3'
        ST_DECLARED                | 0         | 'bank3'
        ST_REJECTED                | 0         | 'bank3'

    }

    @Unroll
    void "On Query operation and Repatriation end status is : #repatriationEndStatus number of notification to be sent is #mailsSent"() {
        given:
        Repatriation repatriation = new Repatriation(status : repatriationEndStatus, code : '0001')
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true



        when:
        def result = service.resolveRepatriationQueryOperationRecipients(repatriation)

        then:
        result.size() == mailsSent

        where:
        repatriationEndStatus      | mailsSent
        ST_QUERIED                 | 1
        ST_CONFIRMED               | 0
        ST_DECLARED                | 0
        ST_REJECTED                | 0

    }

    @Unroll
    void "On Cancel operation and Repatriation end status is : #repatriationEndStatus , begin status is #repatriationBeginStatus number of notification to be sent is #mailsSent"() {
        given:
        Repatriation repatriation = new Repatriation(status : repatriationEndStatus, code : '0001')
        GroovyMock(UserUtils, global: true)
        GroovyMock(WebRequestUtils, global: true)
        UserUtils.isBankAgent() >> true
        WebRequestUtils.getParams() >> new GrailsParameterMap([initialStatus: repatriationBeginStatus], null)


        when:
        def result = service.resolveRepatriationCancelOperationRecipients(repatriation)

        then:
        result.size() == mailsSent

        where:
        repatriationEndStatus      | mailsSent | repatriationBeginStatus
        ST_CANCELLED               | 0         | ST_QUERIED
        ST_CANCELLED               | 1         | ST_CONFIRMED
        ST_QUERIED                 | 0         | ST_CANCELLED
        ST_REJECTED                | 0         | ST_CONFIRMED

    }

    @Unroll
    void "On Update operation and Repatriation end status is : #repatriationEndStatus , begin status is #repatriationBeginStatus number of notification to be sent is #mailsSent"() {
        given:
        Repatriation repatriation = new Repatriation(status : repatriationEndStatus, code : '0001',repatriationBankCode : 'bank1')
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test1 name', ecReference: "EA0001"))
        repatriation.addClearanceOfDom(new ClearanceOfDom(domiciliaryBank: 'test2 name', ecReference: "EA0002"))
        GroovyMock(UserUtils, global: true)
        GroovyMock(WebRequestUtils, global: true)
        UserUtils.isBankAgent() >> bankConnect
        UserUtils.isTrader() >> traderConnect


        when:
        def result = service.resolveRepatriationUpdateOperationRecipients(repatriation)

        then:
        result.size() == mailsSent

        where:
        repatriationEndStatus      | mailsSent | traderConnect   | bankConnect
        ST_CONFIRMED               | 1         | false           | true
        ST_CONFIRMED               | 3         | true            | false

    }

}
