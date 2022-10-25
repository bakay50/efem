package com.webbfontaine.efem

import com.webbfontaine.currencyTransfer.CurrencyTransferSearchService
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import grails.gorm.transactions.Transactional
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.joda.time.LocalDateTime
import spock.lang.Specification

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 11/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Integration
@Rollback
@Transactional
class CurrencyTransferSearchServiceIntegrationSpec extends Specification {

    CurrencyTransferSearchService currencyTransferSearchService

    void "test getSearchResults "() {
        given:
        createCurrenciesTransfer()
        def searchCommand = new CurrencyTransferSearchCommand()
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovSupervisor() >> isGovSupervisor

        when:
        Map result = currencyTransferSearchService.getSearchResults(searchCommand, [:])

        then:
        result.resultList?.size() == totalResult
        def requestNo
        result.resultList.each {
            requestNo = it.requestNo
            it.requestNo == expectedRequestNo
            it.status == expectedStatus
        }

        where:
        testCase                                        | isGovSupervisor | totalResult | expectedRequestNo          | expectedStatus
        'GovSupervisor user can not view status stored' | true            | 2           | "CT001234"                 | "Transferred"
        'GovSupervisor user can not view status stored' | true            | 2           | "CT001235"                 | "Transferred"
        'Admin user can view all status'                | false           | 5           | "CT001231"                 | "Stored"
        'Admin user can view all status'                | false           | 5           | "CT001232"                 | "Stored"
        'Admin user can view all status'                | false           | 5           | "CT001233"                 | "Stored"
        'Admin user can view all status'                | false           | 5           | "CT001234"                 | "Transferred"
        'Admin user can view all status'                | false           | 5           | "CT001235"                 | "Transferred"
    }

    void createCurrenciesTransfer() {
        (1..3).each { idx ->
            CurrencyTransfer currencyTransfer = new CurrencyTransfer(requestNumberSequence: "${idx}"
                    , requestYear: '2020'
                    , requestNo: "CT00123${idx}"
                    , requestDate: LocalDateTime.now().plusDays(idx)
                    , amountTransferred: 40000000
                    , amountTransferredNat: 40000000
                    , currencyTransferDate: LocalDateTime.now().plusDays(idx)
                    , userOwner: 'LANFIA'
                    , bankCode: 'AZ563BC'
                    , bankName: 'BNI'
                    , currencyCode: 'XOF'
                    , currencyName: 'FCFA'
                    , status: "Stored")
            currencyTransfer.save(validate: false, failOnError: true, flush: true)
        }
        (4..5).each { idx ->
            CurrencyTransfer currencyTransfer = new CurrencyTransfer(requestNumberSequence: "${idx}"
                    , requestYear: '2020'
                    , requestNo: "CT00123${idx}"
                    , requestDate: LocalDateTime.now().plusDays(idx)
                    , amountTransferred: 40000000
                    , amountTransferredNat: 40000000
                    , currencyTransferDate: LocalDateTime.now().plusDays(idx)
                    , userOwner: 'LANFIA'
                    , bankCode: 'AZ563BC'
                    , bankName: 'BNI'
                    , currencyCode: 'XOF'
                    , currencyName: 'FCFA'
                    , status: "Transferred")
            currencyTransfer.save(validate: false, failOnError: true, flush: true)
        }
    }
}