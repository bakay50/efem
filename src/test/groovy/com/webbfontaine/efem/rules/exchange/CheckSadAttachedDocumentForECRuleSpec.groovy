package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmSadAttdocs
import grails.testing.gorm.DataTest
import org.grails.datastore.mapping.core.connections.ConnectionSource
import org.grails.datastore.mapping.simple.SimpleMapDatastore
import grails.plugins.jodatime.simpledatastore.SimpleMapJodaTimeMarshaller
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_003
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED


class CheckSadAttachedDocumentForECRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomains Exchange
        defineBeans() {
            sadService(SadService)
        }
    }

    @Unroll
    void "test CheckSadAttachedDocumentForECRule()"() {
        given:
        SimpleMapDatastore dataStore = new SimpleMapDatastore([ConnectionSource.DEFAULT, "sydamviews"], RimmSadAttdocs)
        dataStore.mappingContext.mappingFactory.registerCustomType(new SimpleMapJodaTimeMarshaller(LocalDate))
        Exchange exchange = new Exchange()
        exchange.requestType = requestType
        exchange.geoArea = area
        exchange.startedOperation = operation as Operation
        exchange.registrationNumberBank = registrationNoBank
        exchange.clearanceOfficeCode = "CIAB1"
        exchange.declarationDate = new LocalDate()
        exchange.declarationSerial = "C"
        exchange.declarationNumber = "389"
        CheckSadAttachedDocumentForECRule.metaClass.static.getSadAttachedDocument = { return sadAttachedDoc }
        when:
        Rule rule = new CheckSadAttachedDocumentForECRule()
        rule.apply(new RuleContext(exchange, exchange.errors as Errors))

        then:
        exchange.hasErrors() == expected

        where:
        requestType | area     | operation       | sadAttachedDoc | registrationNoBank | expected
        EC          | AREA_003 | UPDATE_APPROVED | ["SGB-23"]     | "sgb-23"           | false
        EC          | AREA_003 | UPDATE_EXECUTED | ["SGB-23"]     | "sgb-23"           | false
        EC          | AREA_003 | UPDATE_APPROVED | ["SGB-120"]    | "sgb-23"           | true
        EC          | AREA_003 | UPDATE_EXECUTED | ["SGB-120"]    | "sgb-23"           | true
        EC          | AREA_003 | UPDATE_EXECUTED | []             | "sgb-23"           | true
    }
}
