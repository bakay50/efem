package com.webbfontaine.autocomplete

import com.webbfontaine.efem.rimm.RimmOpt
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.taglibs.AutocompleteDataSource
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import grails.testing.services.ServiceUnitTest
import org.grails.datastore.mapping.core.connections.ConnectionSource
import org.grails.datastore.mapping.simple.SimpleMapDatastore
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

class RimmServiceSpec extends Specification implements ServiceUnitTest<RimmService>{

   @Unroll("test custom rimm data load")
    void "test findBank"() {
        given:
        SimpleMapDatastore dataStore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "rimm"], Bank)
        def mockBeanDataLoadService = Stub(BeanDataLoadService) {
            doLoadData(*_) >> {
                return resultData
            }
        }
        service.beanDataLoadService = mockBeanDataLoadService
        Map params = paramsData

        when:
        def result  = service.findBank(params)

        then:
        result == resultData
        where:
        paramsData | resultData
        [beanClass: 'com.webbfontaine.grails.plugins.rimm.bnk.Bank', htDate: LocalDate.now().toDate().clearTime(), selectFields: 'code,description,email', asc: 'code', controller: 'rim', action: 'bankDataLoad', criteriaFields: 'code'] | [[code: "SGB1", description: "S.G.B.C.I",email: 'test@gmail.com']]

    }
}
