package com.webbfontaine.efem.rules

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.TvfConstants
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.rules.exchange.TvfRule
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.efem.tvf.TvfUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.util.Holders
import org.joda.time.LocalDate
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

class TvfRuleSpec extends Specification implements DataTest{

    def setup(){
        mockDomains(Exchange)
        defineBeans {
            tvfService(TvfService)
        }

    }

    @Unroll
    void "test #testCase" (){
        given:
        Holders.config.rest?.isWebService = true
        Tvf tvfInstance = new Tvf(flow: tvfFlow, impTaxPayerAcc: 'imp01', expTaxPayerAcc: 'exp01', decCode: 'dec1')
        Exchange exchangeInstance = new Exchange(tvfNumber: 01, tvfInstanceId: '001', tvf: tvfInstance, tvfDate: LocalDate.now())
        exchangeInstance.save(flush : true, validate : false, failOnError : false)
        GroovyMock(UserUtils, global: true)
        GroovyMock(TvfUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.DEC) >> depProp
        UserUtils.userPropertyValueAsList(UserProperties.TIN) >> tinProp


        when:
        Rule rule = new TvfRule()
        rule.apply(new RuleContext(exchangeInstance, exchangeInstance.errors as Errors))

        then:
        assert exchangeInstance?.errors?.allErrors?.code[index] == expectedError


        where:
        testCase                                | depProp       |   tinProp     |   tvfFlow              | index  |  expectedError
        'for no TVF load'                       | ["dec1"]      |   []          |   TvfConstants.IM_FLOW |  0     | "load.tvf.error"
        'for TVF flow is not compatible'        | ["dec1"]      |   ["tin1"]    |   TvfConstants.EX_FLOW |  1     | "exchange.loadTvf.flow.invalid.error"
    }
}
