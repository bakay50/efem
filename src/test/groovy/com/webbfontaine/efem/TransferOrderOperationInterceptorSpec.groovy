package com.webbfontaine.efem

import com.webbfontaine.transferOrder.TransferOrderService
import grails.testing.gorm.DataTest
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 21/12/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrderOperationInterceptorSpec extends Specification implements InterceptorUnitTest<TransferOrderOperationInterceptor>, DataTest {

    void "Test TransferOrderOperationInterceptor "() {
        setup:
        interceptor.transferOrderService = Stub(TransferOrderService) {
            isTransferEnabled(_) >> false
        }

        when:
        interceptor.before()

        then:
        response.redirectedUrl == '/access-denied'

    }
}
