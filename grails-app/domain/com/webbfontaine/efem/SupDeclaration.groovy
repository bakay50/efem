package com.webbfontaine.efem

import com.webbfontaine.efem.rules.exchange.declaration.SupDeclarationBusinessLogicRule
import org.joda.time.LocalDate

class SupDeclaration {

    Integer rank
    String clearanceOfficeCode
    String clearanceOfficeName
    String declarationSerial
    Integer declarationNumber
    LocalDate declarationDate
    BigDecimal declarationCifAmount
    BigDecimal declarationRemainingBalance
    BigDecimal declarationAmountWriteOff

    static belongsTo = [exchange:Exchange]

    static rules = {
        [SupDeclarationBusinessLogicRule]
    }
}
