package com.webbfontaine.efem

import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import grails.gorm.transactions.Rollback
import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
@Transactional
class DeclarantServiceIntegrationSpec extends Specification {

    DeclarantService declarantService

    void cleanup() {
        Declarant.withNewTransaction {
            Declarant.deleteAll(Declarant.list())
        }
    }

    void "test setDeclarantDetails "() {
        given:

        Declarant.withNewTransaction {
            new Declarant(code: 'DEC1', description: 'Declarant test', phoneNumber: '123123', email: 'declarant@test.com', dov: new Date().clearTime()).save(flush: true)
        }
        Exchange exchange = new Exchange(declarantCode: 'DEC1')

        when:
        def expected = declarantService.setDeclarantDetails(exchange)

        then:
        expected == "Declarant test\n" +
                "\n" +
                "123123\n" +
                "declarant@test.com\n"

    }
}