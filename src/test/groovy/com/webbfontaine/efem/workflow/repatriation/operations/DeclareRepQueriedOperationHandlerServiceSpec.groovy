package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.MessagingNotifService
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.RepatriationService
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.workflow.Operation.DECLARE

class DeclareRepQueriedOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<DeclareRepQueriedOperationHandlerService>{

    @Unroll
    void "test after persist when domain hasErrors = #mockError"() {
        given:
        Repatriation repatriation1 = new Repatriation()
        Repatriation repatriation2 = new Repatriation()

        service.repatriationService = Mock(RepatriationService)
        service.messagingNotifService = Mock(MessagingNotifService)
        def hasErrors = mockErrors
        def commitOperation = DECLARE

        when:
        service.afterPersist(repatriation1, repatriation2, hasErrors, commitOperation)

        then:
        called * service.messagingNotifService.sendAfterCommit(repatriation2,_)

        where:
        mockErrors | called
        true       | 0
        false      | 1


    }
}
