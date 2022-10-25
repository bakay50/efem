package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.command.ExchangeSearchCommand
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.search.ExchangeSearchService
import com.webbfontaine.grails.plugins.search.QueryBuilder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Ignore
/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 8/23/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExchangeSearchServiceSpec extends Specification implements ServiceUnitTest<ExchangeSearchService>, DataTest{



    void setup(){
        mockDomain(Exchange, [
                [executions : [new Execution(executingBankCode: 'AIB')]]
        ])
    }

    @Ignore
    void "test getSearchResult method should not return any result when wrong executingBankCode is used"(){
        given:'SearchCommand and map of params'
        GroovyMock(UserUtils, global: true)
        ExchangeSearchCommand searchCommand = new ExchangeSearchCommand(executingBankCode: 'AIB')
        def params = [test : 'test'] as Map
        service.searchService = [getSearchOffset : { a -> 1}]
        def max = 10

        when:
        def result = service.getSearchResult(new QueryBuilder(searchCommand), max, params, searchCommand)

        then:
        result == []
    }
}
