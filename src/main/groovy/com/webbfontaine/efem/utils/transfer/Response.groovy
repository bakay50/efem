package com.webbfontaine.efem.utils.transfer

import org.joda.time.LocalDate

class Response implements Serializable {
    LocalDate authorizationDate
    String bankName
    String registrationNumberBank
    LocalDate registrationDateBank
    BigDecimal balance
    String bankCode
}
