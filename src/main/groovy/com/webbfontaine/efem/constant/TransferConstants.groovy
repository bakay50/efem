package com.webbfontaine.efem.constant

import com.webbfontaine.efem.constants.Statuses

class TransferConstants {
    static final EXCEPTION_CLEARANCE_FIELD_PARAMS = ["amountToBeSettledMentionedCurrency","amountRequestedMentionedCurrency","amountSettledMentionedCurrency"]
    static final ENGLISH_FIELDS=["en","en_EN","EN","us_En","US"]
    static String ALL = "ALL"
    static String TIN = "TIN"
    static String ADB = "ADB"
    static String TRADER = "trader"
    static final ZERO = ["0,00","0.00",BigDecimal.ZERO,0,00,0.00]
    static def ALLOWED_CURRENCYCODE=["XOF"]
    static def REQUEST_TYPE_EA="EA"
    static  final TRANSFER_STATUS = [Statuses.ST_APPROVED,Statuses.ST_EXECUTED]
    static CLEARANCE_DOMICILIATION_FIELDS = ["amountSettledMentionedCurrency"]
}
