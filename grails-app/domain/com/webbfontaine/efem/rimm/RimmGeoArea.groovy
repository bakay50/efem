package com.webbfontaine.efem.rimm

class RimmGeoArea {

    String code
    String description
    String description_fr

    static mapping = {
        datasource "rimm"
        table "RIMM_GEOARE"
        version false

        id generator: 'assigned', column: 'ID'
        code column: "COD"
        description column: "DSC"
        description_fr column: "DSC_FR"
    }

    static constraints = {
        code nullable: false, maxSize: 5
        description nullable: false, maxSize: 255
        description_fr nullable: false, maxSize: 255
    }
}
