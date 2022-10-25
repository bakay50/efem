package com.webbfontaine.efem

import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.command.ExchangeSearchCommand
import com.webbfontaine.efem.search.ExchangeSearchService
import com.webbfontaine.grails.plugins.search.QueryBuilder
import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import org.joda.time.LocalDateTime
import spock.lang.Specification

@Integration
@Rollback
@Transactional
class ExchangeSearchServiceIntegrationSpec extends Specification {

    ExchangeSearchService exchangeSearchService

    void "test getSearchResult "() {
        given:
        createExchanges()
        ExchangeSearchCommand searchCommand = new ExchangeSearchCommand()
        QueryBuilder queryBuilder = Mock(QueryBuilder)
        def max = 10
        boolean mustInitAfterSearch = false

        when:
        def result = exchangeSearchService.getSearchResult(queryBuilder, max, [:], searchCommand, mustInitAfterSearch)

        then:
        result.resultList?.size() == 2
    }

    void createExchanges() {
        Exchange firstExchange = new Exchange(requestNumberSequence: 1
                , requestYear: 2020
                , requestType: 'EC'
                , basedOn: 'TVF'
                , requestNo: "EC20211"
                , requestDate: LocalDateTime.now().plusDays(1)
                , amountMentionedCurrency:100
                , declarationNumber:200
                , bankName:'SCB'
                , bankApprovalDate:LocalDateTime.now().plusDays(2)
                , declarantNameAddress:'BOLLORE'
                , registrationNumberBank:"SCB-1"
                , domiciliationNumber:'A10'
                , importerNameAddress:'PROLINE'
                , exporterNameAddress:'TRANSIT'
                , clearanceOfficeCode:'CIAB6'
                , declarationDate:LocalDateTime.now().plusDays(3)
                , declarationSerial:'C'
                , countryProvenanceDestinationCode:'FR'
                , tvfNumber:'147'
                , balanceAs:105
                , amountNationalCurrency:15
                , attachedDocs : new AttachedDoc(docType: '0008', docRef : "002022}")
                , status: "Stored")
        firstExchange .save(validate: false, failOnError: true, flush: true)
        Exchange secondExchange = new Exchange(requestNumberSequence: 2
                , requestYear: 2020
                , requestType: 'AC'
                , basedOn: 'SAD'
                , requestNo: "AC20211"
                , requestDate: LocalDateTime.now().plusDays(1)
                , amountMentionedCurrency:200
                , declarationNumber:'56'
                , bankName:"ECO"
                , bankApprovalDate:LocalDateTime.now().plusDays(2)
                , declarantNameAddress:'DECLARANT'
                , registrationNumberBank:"ECO-R1"
                , domiciliationNumber:'22'
                , importerNameAddress:'IMPORTER'
                , exporterNameAddress:'EXPOTER'
                , clearanceOfficeCode:'CIAB1'
                , declarationDate:LocalDateTime.now().plusDays(-1)
                , declarationSerial:'I'
                , countryProvenanceDestinationCode:'SN'
                , tvfNumber:47
                , balanceAs:109
                , attachedDocs : new AttachedDoc(docType: '0008', docRef : "0020211")
                , amountNationalCurrency:10
                , status: "Queried")
        secondExchange.save(validate: false, failOnError: true, flush: true)

    }
}
