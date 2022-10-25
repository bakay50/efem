package com.webbfontaine.efem.rimm

class RimmOpt {

    String code
    String description

    static mapping = {
        datasource "rimm"
        table "RIMM_OPT"
        version false

        id generator: 'assigned', column: 'ID'
        code column: "COD"
        description column: "DSC"
    }

    static constraints = {
        code nullable: false, maxSize: 5
        description nullable: false, maxSize: 255
    }
}
