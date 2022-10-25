package com.webbfontaine.efem.workflow.currencyTransfer.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.currencyTransfer.CurrencyTransferService
import com.webbfontaine.efem.sequence.SequenceGenerator
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import static com.webbfontaine.efem.workflow.Operation.TRANSFER

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 03/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferCurrencyTransferOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<TransferCurrencyTransferOperationHandlerService>, DataTest {
    void setupSpec() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    @ConfineMetaClassChanges([CurrencyTransferService])
    def "test beforePersist() when domain has no errors"() {

        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        def commitOperation = TRANSFER
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        service.currencyTransferService = new CurrencyTransferService()
        def year = LocalDate.now()?.getYear()

        when:
        service.beforePersist(currencyTransfer, commitOperation)

        then:
        assert currencyTransfer?.lastTransactionDate?.toLocalDate() == LocalDate.now()
        assert currencyTransfer?.requestNumberSequence == 1
        assert currencyTransfer?.requestYear == year
        assert currencyTransfer?.requestDate == LocalDate.now()
        assert currencyTransfer?.requestNo == "${year}000001"

    }

    def "test beforePersist() when currency transfer domain has no errors with sequence generator"() {
        given:
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer()
        def commitOperation = TRANSFER
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        service.currencyTransferService = Mock(CurrencyTransferService)
        service.sequenceGenerator = Mock(SequenceGenerator)
        when:
        service.beforePersist(currencyTransferInstance, commitOperation)
        then:
        assert currencyTransferInstance?.lastTransactionDate?.toLocalDate() == LocalDate.now()
        assert currencyTransferInstance?.requestDate == LocalDate.now()
    }

}
