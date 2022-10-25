package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.repatriation.Repatriation
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED

class RepatriationWorkflowServiceSpec extends Specification implements ServiceUnitTest<RepatriationWorkflowService>, DataTest {

    def setup() {
        defineBeans() {
            repatriationWorkflow(RepatriationWorkflow)
            springSecurityService(SpringSecurityService)
        }
        mockDomain(Repatriation)
    }

    def "test requestCurrencyTransfer()"() {
        given:
        UserUtils.authenticate("ROLE_EFEMCI_BANK_AGENT")
        service.springSecurityService.metaClass.isLoggedIn { return true }
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        Repatriation repatriation = new Repatriation()
        repatriation.status = ST_CONFIRMED
        service.initOperations(repatriation)
        when:
        service.requestCurrencyTransfer(repatriation)
        then:
        repatriation.operations?.each { it.name == Operation.REQUEST_CURRENCY_TRANSFER }
    }
}
