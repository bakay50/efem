package com.webbfontaine.efem

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 16/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@TestMixin(GrailsUnitTestMixin)
class TypeCastUtilsSpec extends Specification {
    def setup() {
        grailsApplication.config.numberFormatConfig.exchangeRateFormat = "#,###.####"
        grailsApplication.config.numberFormatConfig.decimalNumberFormat = "#,##0.00"
    }

    @Unroll
    def "Test for method parseCurrencyApplyPattern"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.characterEncoding = 'UTF-8'

        def params = new HashMap<String, String>()
        params.amountToBeSettledMentionedCurrency = "1 500,00"
        params.amountSettledMentionedCurrency = "55 441"

        when:
        def result = TypeCastUtils.parseCurrencyApplyPattern(params.amountSettledMentionedCurrency, true)

        then:
        result == "55"
    }

    @Unroll
    def "Test for method parseStringToBigDecimal"() {
        given:

        def stringValue = "1,313,001.00"

        when:
        BigDecimal result = TypeCastUtils.parseStringToBigDecimal(stringValue, rate)

        then:
        result == expected

        where :
        rate            | expected
        false           | BigDecimal.valueOf(1313001.00)
        true            | BigDecimal.valueOf(1313001.00)
    }

}