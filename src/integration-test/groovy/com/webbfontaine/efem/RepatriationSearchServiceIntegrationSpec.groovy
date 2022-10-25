package com.webbfontaine.efem

import com.webbfontaine.repatriation.search.RepatriationSearchService
import com.webbfontaine.efem.command.RepatriationSearchCommand
import com.webbfontaine.efem.repatriation.Repatriation
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
class RepatriationSearchServiceIntegrationSpec extends Specification {

    RepatriationSearchService repatriationSearchService

    void "test getSearchResults "() {
        given:
        createRepatriation()
        def searchCommand = new RepatriationSearchCommand()

        when:
        Map result = repatriationSearchService.getSearchResults(searchCommand, [:])

        then:
        result.resultList?.size() == 5

    }

    void createRepatriation() {
        (1..3).each { idx ->
            Repatriation repatriation = new Repatriation(requestNumberSequence: "${idx}"
                    , requestYear: '2020'
                    , requestNo: "CT00123${idx}"
                    , requestDate: LocalDateTime.now().plusDays(idx)
                    , repatriationBankCode: 'AZ563'
                    , repatriationBankName: 'BNI'
                    , status: "Stored")
            repatriation.save(validate: false, failOnError: true, flush: true)
        }
        (4..5).each { idx ->
            Repatriation repatriation = new Repatriation(requestNumberSequence: "${idx}"
                    , requestYear: '2020'
                    , requestNo: "CT00123${idx}"
                    , requestDate: LocalDateTime.now().plusDays(idx)
                    , repatriationBankCode: 'AZ563'
                    , repatriationBankName: 'BNI'
                    , status: "Declared")
            repatriation.save(validate: false, failOnError: true, flush: true)
        }
    }
}