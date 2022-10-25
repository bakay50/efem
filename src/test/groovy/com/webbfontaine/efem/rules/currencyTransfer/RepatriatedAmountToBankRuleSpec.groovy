package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class RepatriatedAmountToBankRuleSpec extends Specification implements DataTest {

    def setup() {
        mockDomain ClearanceDomiciliation
    }

    @Unroll
    def "test RepatriatedAmountToBankRule()"() {
        given:

        GroovyMock(UserUtils, global: true)
        UserUtils.getUserProperty(UserProperties.ADB) >> bank
        ClearanceDomiciliation clearanceDomiciliation = new ClearanceDomiciliation()
        clearanceDomiciliation.amountTransferredInCurr = amount
        clearanceDomiciliation.repatriatedAmountToBank = bankAmount
        ClearanceOfDom.metaClass.static.findAllByEcReferenceAndBankCode = { ecRef, bankCode -> domList }

        when:

        Rule rule = new RepatriatedAmountToBankRule()
        rule.apply(new RuleContext(clearanceDomiciliation, clearanceDomiciliation.errors as Errors))

        then:

        assert clearanceDomiciliation.hasErrors() == excepted

        where:
        excepted | amount                | bank   | bankAmount            | domList
        true     | new BigDecimal("250") | "SGB1" | new BigDecimal("200") | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"),), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"))]
        true     | new BigDecimal("250") | "SGB1" | new BigDecimal("250") | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"),), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"))]
        true     | new BigDecimal("250") | "SGB1" | new BigDecimal("250") | null
        false    | null                  | "SGB1" | new BigDecimal("250") | null
        false    | null                  | "SGB1" | new BigDecimal("250") | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100")), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"))]
        true     | new BigDecimal("300") | "SGB1" | new BigDecimal("250") | [new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100")), new ClearanceOfDom(repatriatedAmtInCurr: new BigDecimal("100"))]
    }
}
