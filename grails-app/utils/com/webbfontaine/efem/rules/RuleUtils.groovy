package com.webbfontaine.efem.rules


import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class RuleUtils {

    static getBeanDataService() {
        Holders.applicationContext.getBean("beanDataService")
    }

    static getBeanDataLoadService() {
        Holders.applicationContext.getBean("beanDataLoadService")
    }

    static void executeSetOfRules(setOfRules, ruleContext) {
        setOfRules.each { rule ->
            LOGGER.trace("applying rule {}", rule.getClass().getName())
            rule.apply(ruleContext)
        }
    }

    static errorAlreadyExist(Object domainInstance, String errorCode) {
        return domainInstance?.errors?.allErrors?.codes?.flatten()?.contains(errorCode)
    }

    static getTvfService() {
        Holders.applicationContext.getBean("tvfService")
    }

    static getErcService() {
        Holders.applicationContext.getBean("ercService")

    }

    static reject(def domainInstance, String message, String code) {
        domainInstance.errors.reject(message,[code] as Object[],  "You are not authorized to do this operation")
    }

}
