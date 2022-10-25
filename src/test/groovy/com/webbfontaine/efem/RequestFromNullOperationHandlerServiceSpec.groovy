package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.operations.RequestFromNullOperationHandlerService
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/15/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class RequestFromNullOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<RequestFromNullOperationHandlerService> {

    @Unroll
    void "test after persist when domain hasErrors = #mockError"() {
        given:
        Exchange exchange1 = new Exchange()
        Exchange exchange2 = new Exchange()
        def hasErrors = mockErrors
        def commitOperation = REQUEST_QUERIED
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
