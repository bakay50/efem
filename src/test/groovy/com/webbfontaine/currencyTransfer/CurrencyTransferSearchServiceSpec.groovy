package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.search.SearchService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 11/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferSearchServiceSpec extends Specification implements ServiceUnitTest<CurrencyTransferSearchService>, DataTest {

    void setup() {
        mockDomain(CurrencyTransfer)
    }

    @Ignore
    void "test getSearchResults "() {
        given:
        def mockSearchService = Mock(SearchService)
        def search = new CurrencyTransferSearchCommand()
        service.searchService = mockSearchService
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovSupervisor() >> isGovSupervisor

        when:
        Map result = service.getSearchResults(search, [:])

        then:
        result.resultList == expected

        where:
        testCase                    | isGovSupervisor | expected
        'GovSupervisor user result' | true            | []
        'Other user result'         | false           | []
    }
}