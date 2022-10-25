package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import grails.gorm.transactions.Transactional

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 7/19/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class DeclarantService {

    @Transactional(value = 'rimm', readOnly = true)
    def setDeclarantDetails(Exchange exchange) {
        def userPropertyDec = BusinessLogicUtils.getUserProp(UserProperties.DEC)
        def declarant = Declarant.withNewSession {
         return Declarant.findByCode(userPropertyDec?:exchange?.declarantCode)
        }
        if (declarant) {
            exchange.with {
                declarantCode = declarant.code
                declarantNameAddress = declarant.description + "\n" + BusinessLogicUtils.concatenateAddresses(declarant.address1, declarant.address2,
                        declarant.address3, declarant.address4) + "\n" + getDeclarantPhoneNumber(declarant.phoneNumber)  + getDeclarantFax(declarant.fax) + getDeclarantEmail(declarant.email)
            }
        }
    }

    private String getDeclarantPhoneNumber(String phoneNumber){
        phoneNumber?phoneNumber + "\n":""
    }

    private String getDeclarantFax(String fax){
        fax?fax + "\n":""
    }

    private String getDeclarantEmail(String email){
        email?email+ "\n":""
    }

}
