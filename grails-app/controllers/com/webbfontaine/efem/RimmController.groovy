package com.webbfontaine.efem

import grails.converters.JSON

class RimmController {

    def rimmService

    def bankDataLoad() {
        render rimmService.findBank(params) as JSON
    }

    def countryDataLoad() {
        render rimmService.findCountry(params) as JSON
    }
}
