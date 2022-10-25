package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.testing.gorm.DataTest
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.CurrencyTransferRequestType.getDOC_SWIFT_MESSAGE

class ValidationCurrencyTransferRuleSpec extends Specification implements  DataTest {

    def setup() {
        mockDomain(CurrencyTransfer)
    }

    @Unroll
    void "test for ExecutionValidationRule should expected error message"() {
        given:
        GroovyMock(WebRequestUtils, global: true)
        GroovyMock(UserUtils, global: true)
        UserUtils.userPropertyValueAsList(UserProperties.ADB) >> bank
        CurrencyTransfer currencyTransferInstance = new CurrencyTransfer(requestNo: "1",
                currencyCode: currencyCode, amountTransferred: amountTransferred, bankCode: "BNK1", attachedDocs: [attDocs])

        currencyTransferInstance.addClearanceDomiciliation(new ClearanceDomiciliation(ecReference: "EC01", amountTransferredInCurr: amountTransferredInCurr1))
        currencyTransferInstance.addClearanceDomiciliation(new ClearanceDomiciliation(ecReference: "EC01", amountTransferredInCurr: amountTransferredInCurr2))
        Exchange.metaClass.static.findByRequestNo = {
            new Exchange(requestNo: "EC01", currencyCode:"EUR")
        }

        Repatriation.metaClass.static.findByRequestNo = {
            new Repatriation(requestNo: "R01", natureOfFund: natureOfFund)
        }

        when:
        Rule rule = new ValidationCurrencyTransferRule()
        rule.apply(new RuleContext(currencyTransferInstance, currencyTransferInstance.errors as Errors))

        then:
        currencyTransferInstance?.errors.allErrors?.code[0] == expectedError
        where:
        currencyCode | amountTransferred | amountTransferredInCurr1 | amountTransferredInCurr2 | expectedError                                         | bank     | natureOfFund            | attDocs
        "EUR"        | 500               | 150                      | 300                      | null                                                  | ["BNK1"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "EUR"        | 500               | 150                      | 300                      | "currencyTransfer.errors.repatriation.bank"           | ["BNK2"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "EUR"        | 400               | 150                      | 300                      | "currencyTransfer.errors.amountTransferredInCurr.sum" | ["BNK1"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "USD"        | 500               | 150                      | 300                      | "currencyTransfer.errors.currency.code.label"         | ["BNK1"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "EUR"        | 500               | 0                        | 0                        | "clerance.errors.amountTransferred.zero"              | ["BNK1"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "EUR"        | 500               | 150                      | 0                        | "clerance.errors.amountTransferred.zero"              | ["BNK1"] | NatureOfFund.NOF_REP    | new CurrencyTransferAttachedDoc(docType: DOC_SWIFT_MESSAGE)
        "EUR"        | 500               | 0                        | 0                        | "currencyTransfer.errors.swiftMessage"                | ["BNK1"] | NatureOfFund.NOF_PRE    | new CurrencyTransferAttachedDoc(docType: "WF09")

    }
}
