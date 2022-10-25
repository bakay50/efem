package com.webbfontaine.efem

import com.webbfontaine.efem.workflow.operations.AfterApproveAndRequestOperationHandlerService
import grails.testing.services.ServiceUnitTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.constants.Statuses.*

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/14/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class AfterApproveAndRequestOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<AfterApproveAndRequestOperationHandlerService> {

    @Unroll
    def "test after persist should call the method notifyAboutRequestOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Exchange(), new Exchange(), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp        | called
        REQUEST_QUERIED | 1
        REQUEST         | 0

    }

    @Unroll
    def "test after persist should call the method notifyAboutRejectOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Exchange(), new Exchange(), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp                  | called
        REJECT_REQUESTED          | 1
        REQUEST                   | 0
        REJECT_PARTIALLY_APPROVED | 1
    }

    @Unroll
    def "test after persist should call the method notifyAboutQueryOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Exchange(), new Exchange(), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp                 | called
        QUERY_REQUESTED          | 1
        REQUEST                  | 0
        QUERY_PARTIALLY_APPROVED | 1
    }

    @Unroll
    def "test after persist should call the method notifyAboutCancelOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Exchange(), new Exchange(), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp                 | called
        CANCEL_APPROVED          | 1
        QUERY_PARTIALLY_APPROVED | 1
        CANCEL_QUERIED           | 1
    }

    @Unroll
    def "test after persist should call the method notifyAboutApproveOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Exchange(), new Exchange(status: stat), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp                   | called | stat
        APPROVE_REQUESTED          | 1      | ST_PARTIALLY_APPROVED
        CANCEL_QUERIED             | 1      | ST_CANCELLED
        APPROVE_PARTIALLY_APPROVED | 1      | ST_APPROVED
        PARTIALLY_APPROVED         | 1      | ST_APPROVED
    }
}
