package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.ReferenceConstants
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.rimm.RimmGeoArea
import com.webbfontaine.efem.rimm.RimmOpt
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.rimm.cmp.Company
import com.webbfontaine.grails.plugins.rimm.country.Country
import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import com.webbfontaine.grails.plugins.rimm.currency.Currency
import com.webbfontaine.grails.plugins.rimm.hist.HistorizationSupport
import com.webbfontaine.grails.plugins.search.utils.MessageSourceUtils
import com.webbfontaine.grails.plugins.utils.TypesCastUtils
import grails.util.Environment
import grails.util.Holders
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 7/15/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class ReferenceUtils {

    static getDeclarantFields(userPropValues) {
        return getFieldsPerUserProp(Declarant, userPropValues, ReferenceConstants.DECLARANT_SELECT_FIELDS)
    }

    static getCompanyFields(userPropValues) {
        return getFieldsPerUserProp(Company, userPropValues, ReferenceConstants.COMPANY_SELECT_FIELDS)
    }

    static getBankFields(userPropValues) {
        return getFieldsPerUserProp(Bank, userPropValues, ReferenceConstants.BANK_SELECT_FIELDS)
    }

    static getCurrencyCodeFieldsWithNotIn(currCodeValue) {
        return Currency.withNewSession {
            Currency.findAllByCodeNotInList(currCodeValue)
        }
    }

    static getFieldsPerUserProp(domain, def userPropValues, String[] fieldsMap) {
        List newDataList = getFieldsFromRimmData(domain, userPropValues)
        convertToMaps(newDataList, fieldsMap)
    }

    static getFieldsFromRimmData(domain, def userPropValues) {
        List newDataList = domain.withNewSession {
            domain.where { criteria ->
                'in'("code", userPropValues)
                HistorizationSupport.withHistorizedCriteria(getHtDate(), criteria)
            }.list()
        }
        newDataList
    }

    private static getHtDate() {
        return new Date().clearTime()
    }

    static List convertToMaps(List source, String[] mapElements) {
        List retVal = new ArrayList(source.size())
        for (int i = 0; i < source.size(); i++) {
            Object sourceObject = source.get(i)

            Map dataMap = convertToMap(sourceObject, mapElements)
            retVal.add(dataMap)
        }
        return retVal
    }

    static Map convertToMap(sourceObject, String[] mapElements) {
        Map dataMap = new HashMap()
        for (int j = 0; j < mapElements.length; j++) {
            String mapElement = mapElements[j]

            def sourceObjectFieldValue = mapElement.contains(".") ?
                    sourceObject[mapElement.split("\\.")[0]][[mapElement.split("\\.")[1]]] : sourceObject[mapElement]
            dataMap.put(mapElement, sourceObjectFieldValue)
        }
        dataMap
    }

    static LocalDate gPathToLocalDate(String xmlValue) {
        if (!xmlValue.equals("")) {
            int dash = xmlValue.indexOf('/');
            boolean hasTwoDash = dash != -1 && xmlValue.indexOf('/', dash + 1) != -1;
            if (!hasTwoDash) {
                return null
            }
            try {
                return TypesCastUtils.toDate(xmlValue)
            } catch (IllegalArgumentException exception) {
                return null
            }
        }
        return null
    }

    static def getBankCodes() {
        return businessLogicService.getUsersADB()
    }

    static getBusinessLogicService() {
        Holders?.applicationContext?.getBean("businessLogicService")
    }

    static isProduction() {
        return Environment.current == Environment.PRODUCTION
    }

    static Map currentRequestParams() {
        GrailsWebRequest.lookup().params
    }

    static getCountryFieldsWithNotIn(codeCountry) {
        return Country.withNewSession {
            Country.findAllByCodeNotInList(codeCountry)
        }
    }

    static getOperationTypeInList(operationType) {
        return RimmOpt.withNewSession {
            RimmOpt.findAllByCodeInList(operationType)
        }
    }

    static getGeoAreaNotEqual(geoArea) {
        String locale = LocaleContextHolder.locale
        List<RimmGeoArea> out = RimmGeoArea.withNewSession {
            RimmGeoArea.findAllByCodeNotInList([geoArea])
        }
        if (locale == UtilConstants.LOCALE_FR) {
            out.collect { element -> element.description = element.description_fr }
        }
        return out
    }

    static getNameAndPartiesArea() {
        String locale = LocaleContextHolder.locale
        List<RimmGeoArea> out = RimmGeoArea.withNewSession { RimmGeoArea.findAllByCodeNotEqual(ExchangeRequestType.AREA_002) }
        if (locale == UtilConstants.LOCALE_FR) {
            out.collect { element -> element.description = element.description_fr }
        }
        return out
    }

    static def createNotificationMessage(props) {
        try {
            getMessage("message.notification", [props] as Object[])
        } catch (IllegalArgumentException e) {
            return "Nombre de dossier : ${props}"
        }
    }

    static String getMessage(String messageCode, Object[] args) {
        MessageSourceUtils.getMessageValue(messageCode, args)
    }

}
