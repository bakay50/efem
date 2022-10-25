package com.webbfontaine.efem.workflow.transferOrder.operations

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.sequence.SequenceGenerator
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.context.ServletContextHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.joda.time.LocalDate
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification
import spock.lang.Unroll

import static com.webbfontaine.efem.constants.Statuses.ST_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.VALIDATE

class RequestStoredTransferOperationHandlerServiceSpec extends Specification implements ServiceUnitTest<RequestStoredTransferOperationHandlerService>, DataTest {
    void setupSpec() {
        mockDomain(TransferOrder)
    }

    @Unroll
    def "test beforePersist() when domain has no errors"() {

        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), ServletContextHolder.servletContext)
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest)
        webRequest.params.commitOperation = REQUEST
        webRequest.params.commitOperationName = REQUEST.name()
        TransferOrder transfer = new TransferOrder()
        def commitOperation = REQUEST
        GroovyMock(UserUtils, global: true)
        UserUtils.isTrader() >> true
        RequestContextHolder.setRequestAttributes(webRequest)
        service.transferOrderService = Mock(TransferOrderService)
        service.sequenceGenerator = Mock(SequenceGenerator)

        when:
        service.beforePersist(transfer, commitOperation)

        then:
        assert transfer?.lastTransactionDate?.toLocalDate() == LocalDate.now()
        assert transfer?.requestDate == LocalDate.now()

    }

    def "test handleValidateRequestForBankAgent()"() {
        given:
        GroovyMock(UserUtils, global: true)
        TransferOrder transferOrder = new TransferOrder()
        def commitOperation = VALIDATE
        def commitOperationName
        def params = new HashMap<String, String>()
        UserUtils.isBankAgent() >> true
        when:
        service.handleValidateRequestForBankAgent(transferOrder, commitOperation, commitOperationName, params)

        then:
        assert transferOrder.status == ST_REQUESTED
        assert params.commitOperation == VALIDATE
        assert params.commitOperationName == VALIDATE.humanName()
    }
}
