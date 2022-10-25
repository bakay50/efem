package com.webbfontaine.efem.workflow.currencyTransfer.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification
import static com.webbfontaine.efem.workflow.Operation.STORE

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 03/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class StoreCurrencyTransferOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<StoreCurrencyTransferOperationHandlerService>, DataTest {
    void setupSpec() {
        mockDomain(CurrencyTransfer)
    }

    def "test beforePersist() "() {

        given:
        CurrencyTransfer currencyTransfer = new CurrencyTransfer()
        def commitOperation = STORE
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true

        when:
        service.beforePersist(currencyTransfer, commitOperation)

        then:
        assert currencyTransfer?.lastTransactionDate?.toLocalDate() == LocalDate.now()
        assert currencyTransfer?.requestDate == LocalDate.now()

    }

}
