package com.webbfontaine.efem.rest

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.command.RetrieveExchangeCommand
import grails.testing.mixin.integration.Integration
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

@Integration
class RestEfemServiceIntegrationSpec extends Specification {

    RestEfemService restEfemService

    @Unroll
    void "test getAllEADocsViaTVF should return expected result"() {
        given:
        Exchange.withTransaction {
            new Exchange(status: testStatus, tvfNumber: 1, tvfDate: new LocalDate(2020, 03, 05),
                    amountMentionedCurrency: BigDecimal.ZERO, balanceAs: BigDecimal.ZERO,
                    amountNationalCurrency: BigDecimal.ZERO).save(flush: true)
        }
        RetrieveExchangeCommand retrieveExchangeCommand = new RetrieveExchangeCommand(tvfNumber: testTvfNumber, tvfDate: '2020-03-05')
        when:
        def result = restEfemService.getAllEADocsViaTVF(retrieveExchangeCommand)
        then:
        result.exchange?.size() == expectedSize
        result.exchange[0]?.status == expectedStatus
        where:
        testTvfNumber | testStatus  | expectedSize | expectedStatus
        1             | 'Requested' | 1            | 'Requested'
        2             | null        | 0            | null

    }
}
