package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.grails.plugins.rimm.cmp.Company
import grails.gorm.transactions.Transactional

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 7/19/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Transactional(value = 'rimm', readOnly = true)
class TaxpayerService {

    def setImporterDetails(Exchange exchange) {
        def importer = getCompany()
        if (importer) {
            exchange.with {
                importerCode = importer.code
                importerNameAddress = importer.description + "\n" + BusinessLogicUtils.concatenateAddresses(importer.address1, importer.address2,
                        importer.address3, importer.address4)
            }
        }
    }

    def setExporterDetails(Exchange exchange) {
        def exporter = getCompany()
        if (exporter) {
            exchange.with {
                exporterCode = exporter.code
                exporterNameAddress = exporter.description + "\n" + BusinessLogicUtils.concatenateAddresses(exporter.address1, exporter.address2,
                        exporter.address3, exporter.address4)
            }
        }
    }

    def getCompany(){
        Company.withNewSession {
            Company.findByCodeAndEov(BusinessLogicUtils.getUserProp(UserProperties.TIN), null)
        }
    }
}
