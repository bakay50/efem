package com.webbfontaine.transferOrder.search

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.search.QueryBuilder
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrderSearchServiceSpec extends Specification implements DataTest, ServiceUnitTest<TransferOrderSearchService> {

    def setupSpec() {
        mockDomain(TransferOrder)
    }

    @Unroll
    def "test for getTotalCount method should return all docs excepted Stored for govSupervisor"() {
        given:
        transferOrderList()
        GroovyMock(UserUtils, global: true)
        UserUtils.isGovSupervisor() >> isGovSupervisor
        UserUtils.isBankAgent() >> false
        UserUtils.isTrader() >> false
        QueryBuilder queryBuilder = Mock(QueryBuilder)
        TransferOrderSearchCommand searchCommand = new TransferOrderSearchCommand()
        when:
        def result = service.getTotalCount(queryBuilder,searchCommand)

        then:
        result == totalCount

        where:
        isGovSupervisor | totalCount
        true            | 3
        false           | 4
    }

    void transferOrderList() {
        TransferOrder.withNewTransaction {
            new TransferOrder(id: 1, status: Statuses.ST_STORED).save(flush: true)
            new TransferOrder(id: 2, status: Statuses.ST_REQUESTED).save(flush: true)
            new TransferOrder(id: 3, status: Statuses.ST_VALIDATED).save(flush: true)
            new TransferOrder(id: 4, status: Statuses.ST_CANCELLED).save(flush: true)
        }
    }

}
