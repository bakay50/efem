package com.webbfontaine.efem

import com.webbfontaine.efem.print.PrintResultService
import com.webbfontaine.efem.print.PrintService
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.util.Holders
import org.springframework.context.i18n.LocaleContextHolder
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

class PrintServiceSpec extends Specification implements ServiceUnitTest<PrintService>, DataTest {

    def setup() {
        mockDomains(Exchange)
        defineBeans {
            tvfService(TvfService)
            printResultService(PrintResultService)
        }

    }

    void setupSpec() {
        Holders.config.efemciApplicationConfig?.displayBeneficiary = false
        Holders.config.efemciApplicationConfig?.decimalNumberFormat = "#,##0.00"
    }

    @Unroll
    void "test #testCase"() {
        given:
        Tvf tvfInstance = new Tvf(impName: "impname1", impAddress: "Importer Address", impPhone: phone, impEmail: email, decCode: 'dec1',
                expName: "expname1", expCtyName: "China", expAddress: "China Address")
        Exchange exchange = new Exchange(tvf: tvfInstance, beneficiaryAddress: "beneficiary address", beneficiaryName: "beneficiaryName", basedOn: "TVF")
        service.tvfService = Stub(TvfService) {
            getExchangeTvfFromTvfService(exchange) >> tvfInstance
        }

        when:
        def result = service.getTvfDetails(exchange)

        then:
        result[key] == expected

        where:
        testCase             | phone | email        | key              | expected
        "for applicant name" | "123" | null         | "applicant_name" | "impname1"
        "for recipient name" | "456" | "test@g.com" | "recipient_name" | "beneficiaryName"
        "for recipient pays" | null  | "test@g.com" | "recipient_pays" | null
    }

    @Unroll
    void "test for decimalFormatter where amountNationalCurrency = #amount and formatted to #expected"() {
        given:
        Exchange exchange = new Exchange(beneficiaryAddress: "beneficiary address", beneficiaryName: "beneficiaryName", basedOn: "TVF", amountNationalCurrency: amount)

        when:
        def result = service.decimalFormatter(exchange)

        then:
        assert result == expected

        where:
        expected | amount
        "500,50" | new BigDecimal("500.500")
        "589,55" | new BigDecimal("589.553")
    }

    @ConfineMetaClassChanges(LocaleContextHolder)
    @Unroll
    void "test setupResidentValue() case language:#language get result :#result"() {
        given:
        Exchange exchange = new Exchange(resident: "yes")
        def exchangeParams = [:]
        LocaleContextHolder.metaClass.static.getLocale = { ->
            new Locale(language)
        }
        when:
        service.setupResidentValue(exchange, exchangeParams)
        then:
        assert exchangeParams.resident_local == result

        where:
        language | result
        "fr"     | "oui"
        "en"     | "yes"
    }

}

