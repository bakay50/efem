package com.webbfontaine.efem.constant

class CurrencyTransferConstants {
    static String REQUEST_TYPE_EC = "EC"
    static final ENGLISH_FIELDS=["en","en_EN","EN","us_En","US"]
    static final EXCEPTION_CLEARANCE_FIELD_PARAMS = ["domiciliatedAmounttInCurr","invoiceFinalAmountInCurr","amountTransferredInCurr"]
    static def ALLOWED_CURRENCYCODE=["XOF"]
    static def ALLOWED_AREA=["003"]
    static final ZERO = ["0,00","0.00",BigDecimal.ZERO,0,00,0.00]
    static def amountTransferredInCurr = "amountTransferredInCurr"
    static String joinAssociation = "join"
}
