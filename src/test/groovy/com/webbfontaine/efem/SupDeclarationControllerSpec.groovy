package com.webbfontaine.efem

import com.webbfontaine.grails.plugins.validation.rules.DocVerificationService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import org.joda.time.LocalDate
import spock.lang.Specification



class SupDeclarationControllerSpec extends Specification implements ControllerUnitTest<SupDeclarationController>, DataTest {

    def setup() {
        mockDomain Exchange
        mockDomain SupDeclaration
    }

    def "test of addSupDeclaration()"() {
        given:
        def data = [clearanceOfficeCode: "CIAB1", declarationSerial: "C", declarationNumber: 396, declarationDate: new LocalDate()]
        controller.sadService = [retrieveExchangeFromSad: { e -> new Exchange() }]
        controller.exchangeService = [findFromSessionStore: { exc -> new Exchange() }]
        controller.supDeclarationService = Stub(SupDeclarationService) {
            setDeclarationCifAmount(*_) >> new  SupDeclaration(data)
            setDeclarationRemainingBalance(*_) >> new SupDeclaration(data)
            setRemainingBalanceEADeclaredAmount(*_) >> new SupDeclaration(data)
            setRemainingBalanceTransferDoneAmount(*_) >> new SupDeclaration(data)
        }
        controller.docVerificationService = Mock(DocVerificationService) {
            deepVerify(_) >> true
        }
        controller.params.conversationId = 'test'

        when:
        controller.addSupDeclaration()

        then:
        controller.response.contentType == "application/json;charset=UTF-8"
        controller.response.status == 200
        controller.response.text.contains('"template":')

    }

}