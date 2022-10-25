package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.MessagingNotifService
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.workflow.Operation.*

class CancelRepOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<CancelRepOperationHandlerService>, DataTest {
    @Unroll
    def "test after persist should call the method notifyAboutRepatriationCancelOperation when commit operation is #commitOp"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        service.repatriationService = Mock(RepatriationService)
        service.messagingNotifService = Mock(MessagingNotifService)

        when:
        service.afterPersist(new Repatriation(), new Repatriation(), false, commitOp)

        then:
        called * service.messagingNotifService.sendAfterCommit(_,_)

        where:
        commitOp         | called
        CANCEL_CONFIRMED | 1
        CANCEL_QUERIED   | 1
        REQUEST_QUERIED  | 0

    }
}
