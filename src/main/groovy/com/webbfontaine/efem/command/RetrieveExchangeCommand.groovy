package com.webbfontaine.efem.command

import grails.validation.Validateable

class RetrieveExchangeCommand implements Validateable {

    Integer tvfNumber
    String tvfDate

    static constraints = {
        tvfNumber nullable: false
        tvfDate nullable: false
    }

}
