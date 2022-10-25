package com.webbfontaine.efem

import com.webbfontaine.grails.plugins.layout.MessageTagLib
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ReferenceServiceSpec extends Specification implements ServiceUnitTest<ReferenceService>, DataTest {

    void "Number of values in the list of Departments in Charge and checking of values"(){
        given:
        service.wf_message = Stub(MessageTagLib)

        when:
        def result = service.getDepartmentInCharge()

        then:
        assert result.size() == 2
        assert result.get(0).get("key") == "FINEX"
        assert result.get(1).get("key") == "BANK"
    }
}
