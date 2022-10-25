package com.webbfontaine.efem

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification


/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Edilbert D. Trizona
 * Date: 12/5/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class BusinessLogicServiceSpec extends Specification implements ServiceUnitTest<BusinessLogicService>, DataTest {

    def "test for splitBankCodeList should return the expected size"() {
        given:
        def bankCodeList = ["ALL","AIB:TP","BOA:ALL"]

        when:
        def result = service.splitBankCodeList(bankCodeList)

        then:
        result.size() == 7
        
    }
}
