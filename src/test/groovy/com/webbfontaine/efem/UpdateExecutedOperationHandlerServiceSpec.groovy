package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.operations.UpdateExecutedOperationHandlerService
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/16/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class UpdateExecutedOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<UpdateExecutedOperationHandlerService> {


    @Unroll
    void "test after persist when domain hasErrors = #mockError"() {
        given:
        Exchange exchange1 = new Exchange()
        Exchange exchange2 = new Exchange()
        def hasErrors = mockErrors
        def commitOperation = UPDATE_EXECUTED
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(exchange1, exchange2, hasErrors, commitOperation)

        then:
        called * service.messagingNotifService.sendAfterCommit(exchange2,_)

        where:
        mockErrors | called
        true       | 0
        false      | 1
    }

}
