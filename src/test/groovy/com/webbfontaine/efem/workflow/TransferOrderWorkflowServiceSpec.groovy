package com.webbfontaine.efem.workflow

import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.gorm.Domain
import spock.lang.Specification
import com.webbfontaine.efem.UserUtils
import static com.webbfontaine.efem.constants.Statuses.ST_STORED
import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED

/**
 * Copyrights 2002-2016 Webb Fontaine
 * Developer: Bakayoko Abdoulaye
 * Date: 19/09/2017
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@TestFor(TransferOrderWorkflowService)
@Mock(SpringSecurityService)
@Domain(TransferOrder)
class TransferOrderWorkflowServiceSpec extends Specification {


    def setup() {
        defineBeans() {
            transferOrderWorkflow(TransferOrderWorkflow)
        }
    }

    def "test initOperation"() {
        given:
        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }

        TransferOrder transferOrder = new TransferOrder()
        transferOrder.status = status

        when:
        service.initOperations(transferOrder)

        then:
        transferOrder.operations.find { domain ->
            domain.id == operation.name()
        }

        where:
        role                    | status         | operation
        "ROLE_EFEMCI_TRADER"    | null           | Operation.STORE
        "ROLE_EFEMCI_TRADER"    | ST_STORED      | Operation.UPDATE_STORED
    }

    def "test Request Operation"() {
        given:
        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }

        TransferOrder transferOrder = new TransferOrder()
        transferOrder.status = status

        when:
        service.initOperationsForEdit(transferOrder,[op: Operation.REQUEST_STORED])

        then:
        transferOrder.operations.find { domain ->
            domain.id == operation.name()
        }

        where:
        role                         | status          | operation
        "ROLE_EFEMCI_TRADER"         | ST_STORED       | Operation.REQUEST_STORED
    }

    def "test Validate Operation" (){
        given:

        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }

        TransferOrder transferOrder = new TransferOrder()
        transferOrder.status = status

        when:
        service.initOperationsForEdit(transferOrder,[op: Operation.VALIDATE])

        then:
        transferOrder.operations.find { ops ->
            ops.id == operation.name()
        }

        where:
        role                                | status            | operation
        "ROLE_EFEMCI_BANK_AGENT"            | ST_REQUESTED      | Operation.VALIDATE
    }

    def "test view access operation for Trader role"() {
        given:

        UserUtils.authenticate(role)
        service.springSecurityService.metaClass.isLoggedIn { return true }

        TransferOrder transferOrder = new TransferOrder()
        transferOrder.status = status

        when:
        service.initOperationsForEdit(transferOrder,[operation: Operation.VIEW_STORED])

        then:
        transferOrder?.operations?.size() == 0

        where:
        role                       | status            | operation
        "ROLE_EFEMCI_BANK_AGENT"   | ST_STORED         | Operation.VIEW_STORED
    }

}
