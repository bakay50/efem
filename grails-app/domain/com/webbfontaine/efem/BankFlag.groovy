package com.webbfontaine.efem

import com.webbfontaine.grails.plugins.rimm.annotations.Historized
import com.webbfontaine.grails.plugins.rimm.utils.Utils

@Historized
class BankFlag implements Serializable {

    String code
    String description
    Boolean isBankAgent
    Date dov
    Date eov

    static mapping = {
        version false
        datasource "rimm"
        cache usage: Utils.getCachePolicy()
        table "RIMM_BNK_FLG"
        id composite: ["code", "dov"]
        code column: "COD"
        description column: "NAM"
        isBankAgent column: "IS_BANK_AGENT"
    }
}
