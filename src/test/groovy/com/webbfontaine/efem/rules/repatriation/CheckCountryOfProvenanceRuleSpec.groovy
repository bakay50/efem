package com.webbfontaine.efem.rules.repatriation

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class CheckCountryOfProvenanceRuleSpec extends Specification implements DataTest {
    def setup() {
        mockDomain(Repatriation)
    }

    @Unroll
    void "test country provenance rule"() {
        given:
        Repatriation repatriation = new Repatriation(countryOfOriginCode:countryOfOriginCode)

        when:
        Rule rule = new CheckCountryOfProvenanceRule()
        rule.apply(new RuleContext(repatriation, repatriation.errors as Errors))

        then:
        repatriation.hasErrors() == expected

        where:
        countryOfOriginCode | expected
        "FR"                | false
        "CI"                | true
        "ML"                | false
    }
}
