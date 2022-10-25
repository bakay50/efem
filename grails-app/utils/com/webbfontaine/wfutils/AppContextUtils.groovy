package com.webbfontaine.wfutils

import grails.util.Holders

final class AppContextUtils {

    static def getBean(Class clazz) {
        def beanInstance = Holders.applicationContext.getBean(clazz)
        beanInstance
    }

    static def getConfig(String configPropertyName) {
        return Holders.config."$configPropertyName"
    }


}
