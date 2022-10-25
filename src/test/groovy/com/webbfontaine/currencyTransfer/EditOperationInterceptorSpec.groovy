package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.currencyTransfer.EditOperationInterceptor
import com.webbfontaine.efem.workflow.CurrencyTransferWorkflowService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

/**
 * Copyright 2019 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 05/08/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class EditOperationInterceptorSpec extends Specification implements InterceptorUnitTest<EditOperationInterceptor>, DataTest {

    void setupSpec() {
        mockDomain(CurrencyTransfer)
    }

    void "Test EditOperationInterceptor"() {
        setup:

        CurrencyTransfer currencyTransfer = new CurrencyTransfer(id: 1)
        def mockCurrencyTransferWorkflowService = Mock(CurrencyTransferWorkflowService)
        mockCurrencyTransferWorkflowService.initOperations(currencyTransfer)

        when:
        def result = interceptor.before()

        then:
        result == true

    }
}
