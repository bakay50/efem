package com.webbfontaine.autocomplete

import com.webbfontaine.efem.constants.ExchangeRequestType
import grails.gorm.transactions.Transactional

@Transactional
class RimmService {

    def beanDataLoadService

    def findBank(params) {
        List<Map> results = beanDataLoadService.doLoadData(params)
        List<Map> listOfBanks = results.findAll { it?.code?.length() <= 5 && it?.email }
        return listOfBanks
    }

    def findCountry(params) {
        List<Map> results = beanDataLoadService.doLoadData(params)
        List<Map> listOfCountries = results.findAll { it?.code != ExchangeRequestType.CI }
        return listOfCountries
    }
}
