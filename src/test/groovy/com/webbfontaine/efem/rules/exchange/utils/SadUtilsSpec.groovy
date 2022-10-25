package com.webbfontaine.efem.rules.exchange.utils

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadUtils
import org.apache.http.HttpStatus
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification
import spock.lang.Unroll
import static com.webbfontaine.efem.constants.Statuses.*

class SadUtilsSpec extends Specification implements GrailsUnitTest {

    @Unroll
    void "test checkSadResponse when  statusCode is #statusCode and SAD status is #status then exchangeInstance error is #expected"() {
        given:
        Exchange exchangeInstance = new Exchange()
        def responseResult = [statusCode: statusCode, data: [status: status]]
        when:
        SadUtils.checkSadResponse(responseResult, exchangeInstance)
        then:
        exchangeInstance.hasErrors() == expected
        where:
        status       | statusCode              | expected
        null         | HttpStatus.SC_NOT_FOUND | true
        ST_ASSESSED  | HttpStatus.SC_OK        | false
        ST_PAID      | HttpStatus.SC_OK        | false
        ST_EXITED    | HttpStatus.SC_OK        | false
        ST_REQUESTED | HttpStatus.SC_OK        | true

    }

    @Unroll
    void "test checkSadResponse when data status is #statusCode and SAD status is #status then Document Validity is #isDocumentValid"() {
        given:
        Exchange exchangeInstance = new Exchange()
        def responseResult = [data: [status: status]]

        when:
        SadUtils.checkSadResponse(responseResult, exchangeInstance)

        then:
        exchangeInstance.isDocumentValid == isDocumentValid

        where:
        status       | statusCode       | isDocumentValid
        ST_REQUESTED | HttpStatus.SC_OK | false
        ST_ASSESSED  | HttpStatus.SC_OK | true
        ST_PAID      | HttpStatus.SC_OK | true
        ST_APPROVED  | HttpStatus.SC_OK | false
        ST_EXITED    | HttpStatus.SC_OK | true
    }


}
