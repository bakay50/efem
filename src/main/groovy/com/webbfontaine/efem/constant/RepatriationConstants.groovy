package com.webbfontaine.efem.constant

class RepatriationConstants {
    static final EXCEPTION_CLEARANCE_FIELD_PARAMS = ["domAmtInCurr","invFinalAmtInCurr","repatriatedAmtInCurr"]
    static final ENGLISH_FIELDS=["en","en_EN","EN","us_En","US"]
    static String ALL = "ALL"
    static String TIN = "TIN"
    static String ADB = "ADB"
    static String BNK = "BNK"
    static String TRADER = "trader"
    static String BANK = "bank"
    static final ZERO = ["0,00","0.00",BigDecimal.ZERO,0,00,0.00]
    static def ALLOWED_AREA=["003"]
    static def ALLOWED_CURRENCYCODE=["XOF"]
    static def REQUEST_TYPE_EC="EC"
    static def XOF_CODE="XOF"
    static String CODE_CREDIT_NOTICE_OR_STATEMENT_OF_ACCOUNT = "WF08"
    static String CLEARANCE_ADD_ACTION = "addClearanceDom"
}

