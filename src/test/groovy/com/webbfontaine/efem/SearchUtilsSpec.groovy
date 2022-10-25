package com.webbfontaine.efem

import com.webbfontaine.efem.command.ExchangeSearchCommand
import grails.testing.gorm.DataTest
import spock.lang.Specification
import spock.lang.Unroll

class SearchUtilsSpec extends Specification implements DataTest {


    @Unroll
    def "Test modifySearchCommand method"() {
        given:
        ExchangeSearchCommand searchCommand = new ExchangeSearchCommand(departmentInCharge: departmentInCharge)
        boolean mustInitAfterSearch = false

        when:
        (searchCommand, mustInitAfterSearch) = SearchUtils.modifySearchCommand(searchCommand, mustInitAfterSearch)

        then:
        searchCommand?.departmentInCharge == expectedDepartment
        mustInitAfterSearch == expected

        where:
        departmentInCharge      | expectedDepartment  | expected
        "FINEX"                 | "FINEX"             | false
        null                    | null                | false
        "BANK"                  | null                | true
    }

    @Unroll
    def "Test setDefaultBankDepartmentInCharge method"() {
        given:
        ExchangeSearchCommand searchCommand = new ExchangeSearchCommand(departmentInCharge: "FINEX")

        when:
        searchCommand = SearchUtils.setDefaultBankDepartmentInCharge(searchCommand, mustInitAfterSearch)

        then:
        searchCommand?.departmentInCharge == expected

        where:
        mustInitAfterSearch     | expected
        false                   | "FINEX"
        true                    | "BANK"
    }
}
