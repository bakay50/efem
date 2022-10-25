package com.webbfontaine.efem

import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.attachment.AttachmentService
import com.webbfontaine.efem.constants.DocumentTypes
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class AttachmentServiceSpec extends Specification implements ServiceUnitTest<AttachmentService>, DataTest {
   def setup() {
       mockDomains(CurrencyTransfer)
   }

    def "test getNewInstance"() {
        given:
        def domain = new Exchange()
        when:
        def result = service.getNewInstance(domain)
        then:
        result instanceof Exchange
    }

    def "test get domain type name"() {
        given:
        def domain = new CurrencyTransfer()
        when:
        def domainType = service.getDomainTypeName(domain)
        then:
        domainType == DocumentTypes.CURRENCY_TRANSFER.getLabel()
    }

    def "test get domain instance"() {
        given:
        def attDoc = new CurrencyTransferAttachedDoc()
        attDoc.currencyTransfer = new CurrencyTransfer()
        when:
        def typeReturn = service.getDomainInstance(attDoc)
        then:
        typeReturn instanceof CurrencyTransfer
    }

}
