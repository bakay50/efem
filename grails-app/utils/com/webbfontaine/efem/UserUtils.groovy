package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.security.Roles
import com.webbfontaine.grails.plugins.security.DocAccessConstants
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Holders
import org.grails.datastore.mapping.query.api.Criteria
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

class UserUtils {

    static Authentication authenticate(roleNames) {
        def authorities = SpringSecurityUtils.parseAuthoritiesString(roleNames)
        def principal = new User('username', 'password', true, true, true, true, authorities)
        def authentication = new TestingAuthenticationToken(principal, null, authorities)
        authentication.authenticated = true
        SecurityContextHolder.context.authentication = authentication
    }

    static String getCurrentUsername(){
        String currentUserName = null
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName()
        }

        return currentUserName
    }

    static String getUserProperty(String propertyName) {
        SpringSecurityService springSecurityService = Holders.applicationContext.getBean(SpringSecurityService)
        String userPropValue = springSecurityService?.authentication?.principal?.getAt(propertyName)
        userPropValue
    }

    static Boolean  inUserProperty(String propertyVal, String userPropertyName) {
        String userPropertyVal = getUserProperty(userPropertyName)
        if ("".equals(userPropertyVal) || userPropertyVal == null) {
            return false
        }
        if (UserProperties.ALL.equals(userPropertyVal)) {
            return true
        }
        def propertyValues = userPropertyVal.split(":") as List

        return (propertyVal in propertyValues)
    }

    static List userPropertyValueAsList(String userProperty) {
        String userPropertyVal = getUserProperty(userProperty)
        def result = []
        if (userPropertyVal && userPropertyVal != UserProperties.ALL) {
            def values = userPropertyVal.split("\\s*:\\s*") as List
            switch (userProperty) {
                case UserProperties.MIN:
                    result = values
                    break
                case UserProperties.DEP:
                    result = values
                    break
                default:
                    result.addAll(values)
                    break
            }
        }
        result
    }

    static Boolean checkForSingleProp(String userProperty) {
        String userPropertyVal = getUserProperty(userProperty)

        if (userPropertyVal) {
            def values = userPropertyVal?.split(":") as List
            if(values.size() == 1 && !values.contains("ALL")){
                return true
            } else {
                return false
            }
        } else {
            false
        }
    }

    static hasUserProperties(String userProp) {
        List userPropValues = userPropertyValueAsList(userProp)
        boolean hasUserProperties = userPropValues
        boolean hasDefaultProperty = hasUserProperties && userPropValues?.size() == 1
        return [hasUserProperties: hasUserProperties, userPropValues: userPropValues, hasDefaultProperty: hasDefaultProperty]
    }


    static boolean isUserpropertyIsNotMultiple(String userProperty){
        return userPropertyValueAsList(userProperty).size()==1
    }

    static boolean isValidLvlUserProp(String userLvlPropValue) {
        boolean valid = true
        if (DocAccessConstants.ALL.equals(userLvlPropValue)) {
            return valid
        } else {
            try {
                valid = Integer.parseInt(userLvlPropValue) instanceof Integer
            } catch (NumberFormatException ignore) {
                valid = false
            }
        }
        return valid
    }

    static boolean isDeclarant() {
        SpringSecurityUtils.ifAllGranted(Roles.DECLARANT.getAuthority())
    }

    static boolean isTrader() {
        SpringSecurityUtils.ifAllGranted(Roles.TRADER.getAuthority())
    }

    static boolean isGovOfficer() {
        SpringSecurityUtils.ifAllGranted(Roles.GOVT_OFFICER.getAuthority())
    }
    static boolean isGovSupervisor() {
        SpringSecurityUtils.ifAllGranted(Roles.GOVT_SUPERVISOR.getAuthority())
    }

    static boolean isBankAgent() {
        SpringSecurityUtils.ifAllGranted(Roles.BANK_AGENT.getAuthority())
    }

    static boolean isAdministrator() {
        SpringSecurityUtils.ifAllGranted(Roles.ADMIN.getAuthority())
    }

    static boolean isSuperAdministrator() {
        SpringSecurityUtils.ifAllGranted(Roles.SUPER_ADMINISTRATOR.getAuthority())
    }

    static Criteria searchForUndefinedCode(Criteria criteria, List connectedCode, String codeProperty, String combinedCodeProperty = "") {
        criteria.or {
            connectedCode.each {
                criteria.eq(codeProperty, it)
                combinedCodeProperty.equals("")?:criteria.eq(combinedCodeProperty, it)
            }
        }
    }

    static Criteria searchForDefinedCode(String codeValue, List connectedCode, Criteria criteria, String codeProperty, String combinedCodeProperty = "") {
        if (codeValue in connectedCode) {
            criteria.or {
                criteria.eq(codeProperty, codeValue)
                combinedCodeProperty.equals("")?:criteria.eq(combinedCodeProperty, codeValue)
            }
        } else {
            searchForUndefinedCode(criteria, connectedCode, codeProperty, combinedCodeProperty)
        }
    }

    static isNullOrEmpty(def value) {
        return value == null || value == ""
    }

    static boolean roleHasAccess(){
        SpringSecurityService springSecurityService = Holders.applicationContext.getBean(SpringSecurityService)
        springSecurityService?.authentication?.authorities?.any { Roles.values().collect {it.authority}.contains(it.authority) }
    }
}
