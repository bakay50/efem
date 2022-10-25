package com.webbfontaine.efem.signature

import grails.util.Holders

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: Alvin Goya
 * Date: 6/4/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class SignatureUtils {
    static final String GUCE_DOCUMENT = "GUCE DOCUMENT"
    static final boolean enableDigitalSignature = getEnableDigitalSignature()
    static final boolean showDigitalSignature = getShowDigitalSignature()

    static boolean getEnableDigitalSignature(){
        return Boolean.valueOf(Holders.config.com.webbfontaine.efemci.enableDigitalSignature as String)
    }

    static boolean getShowDigitalSignature(){
        return Boolean.valueOf(Holders.config.com.webbfontaine.efemci.showDigitalSignature as String)
    }

    static String getXRight(){
        return Holders.config.com.webbfontaine.efemci.digitalSignatureAdditionalDetails.rectangle.urx as String
    }

    static String getXLeft(){
        return Holders.config.com.webbfontaine.efemci.digitalSignatureAdditionalDetails.rectangle.llx as String
    }

    static String getYTop(){
        return Holders.config.com.webbfontaine.efemci.digitalSignatureAdditionalDetails.rectangle.ury as String
    }

    static String getYBottom(){
        return Holders.config.com.webbfontaine.efemci.digitalSignatureAdditionalDetails.rectangle.lly as String
    }

    static String getDocumentLocation(){
        return Holders.config.com.webbfontaine.efemci.digitalSignatureAdditionalDetails.location as String
    }

    static String getGwsRequestUrl(){
        return Holders.config.com.webbfontaine.efemci.gws.sign.url as String
    }

    static String getGwsSignAuthentication(){
        String username = Holders.config.com.webbfontaine.efemci.gws.sign.username as String
        String password = Holders.config.com.webbfontaine.efemci.gws.sign.password as String
        String usernameAndPassword = "$username:$password"
        return usernameAndPassword
    }
}