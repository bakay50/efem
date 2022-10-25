package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.workflow.Operation
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class StoreRepOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<StoreRepOperationHandlerService>, DataTest {

    def setup() {
        mockDomain(Repatriation)
    }

    @Unroll
    void " Test BeforePersist when store document"() {
        given:
        GroovyMock(UserUtils, global: true)
        Repatriation repatriation = new Repatriation()

        def commitOperation = operation

        UserUtils.isBankAgent() >> userRoleIsBank

        when:
        service.beforePersist(repatriation, commitOperation)

        then:
        repatriation.storedOwner == owner

        where:
        operation               | owner                        | userRoleIsBank
        Operation.STORE         | RepatriationConstants.TRADER | false
        Operation.UPDATE_STORED | RepatriationConstants.TRADER | false
        Operation.STORE         | RepatriationConstants.BANK   | true
        Operation.UPDATE_STORED | RepatriationConstants.BANK   | true
    }
}
