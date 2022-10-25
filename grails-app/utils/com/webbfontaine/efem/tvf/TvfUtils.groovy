package com.webbfontaine.efem.tvf

import com.webbfontaine.efem.Exchange
import grails.util.Holders
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.joda.time.LocalDate
import org.springframework.context.i18n.LocaleContextHolder

import static com.webbfontaine.efem.constants.TvfConstants.*

@Slf4j('log')
class TvfUtils {

    public static def checkTvfResponse(String tvfXmlResponse, Exchange exchangeInstance){
        log.debug("in checkTvfResponse() of ${TvfUtils}");

        boolean isValidTvf = false
        String errorMsg = ""

        if(tvfXmlResponse?.contains(TVF_XML_NAME)){
            isValidTvf = true
        }else if(tvfXmlResponse?.contains(INVALID_STATUS)){
            exchangeInstance.isDocumentValid = false
            errorMsg = "license.errors.tvf.invalidStatus"
            exchangeInstance.errors.rejectValue("tvfNumber", "efem.errors.tvf.invalidStatus", "The TVF doesn't have a valid status.");
        }else if(tvfXmlResponse?.equals("")){
            exchangeInstance.isDocumentValid = false
            errorMsg = "license.errors.tvf.nonexistent"
            exchangeInstance.errors.rejectValue("tvfNumber", "efem.errors.tvf.nonexistent", "TVF does not exist.");
        }else if(tvfXmlResponse?.contains(INVALID_DATE)){
            exchangeInstance.isDocumentValid = false
            errorMsg = "license.errors.tvf.expired"
            exchangeInstance.errors.rejectValue("tvfNumber", "exchange.errors.tvf.expire_date", "The TVF has expired");
        }else if(tvfXmlResponse?.contains("503")){
            exchangeInstance.isDocumentValid = false
            errorMsg = "license.errors.tvf.error.load"
            exchangeInstance.errors.rejectValue("tvfNumber", "efem.errors.tvf.error.load", "Error encountered during load TVF.");
        }
        return [isValidTvf : isValidTvf, errorMsg : errorMsg]
    }

    public static GPathResult parseXml(String xml){
        GPathResult tvfData = new XmlSlurper().parseText(xml)
        return tvfData
    }

    public static def setValueToTargetObject(sourceObject, sourceToTargetFieldMapping, targetObject){
        //TODO : add handling if target & sourceObj is of different type
        sourceToTargetFieldMapping.each{key,value ->
            if(sourceObject["$value"]){
                if(key.contains(message("utils.nameAddress"))){
                    targetObject["$key"] = sourceObject["$value"] + '\n' + (sourceObject[value.substring(0,3)+"Address"]?: '')
                }else{
                    targetObject["$key"] = sourceObject["$value"]
                }
            }
        }
        sourceObject
    }

    public static String formatDate(String dateValue) {
        if (dateValue) {
            if(dateValue.count("/") == 2){
                return dateValue.replaceAll("/", "-")
            }else{
                return dateValue
            }
        }
        return null
    }

    public static LocalDate gPathToLocalDate(String xmlValue) {
        if (!xmlValue.equals("")) {
            int dash = xmlValue.indexOf('-');
            boolean hasTwoDash = dash != -1 && xmlValue.indexOf('-', dash + 1) != -1;
            try {
                if (hasTwoDash) {
                    return new LocalDate(xmlValue)
                }else{
                    // can convert string(dd/mm/yyyy) to LocalDate with format: yyyy/month/day
                    int slash = xmlValue.indexOf('/');
                    boolean hasTwoSlash = slash != -1 && xmlValue.indexOf('/', slash + 1) != -1;
                    if(hasTwoSlash){
                        int year = Integer.parseInt(xmlValue.substring(6,10))
                        int day = Integer.parseInt(xmlValue.substring(0,2))
                        int month = Integer.parseInt(xmlValue.substring(3,5))
                        return new LocalDate(year, month, day)
                    }
                }
            } catch (IllegalArgumentException ex) {
                log.error("Unable to convert TR Date to LocalDate")
                return null
            }
        }
        return null
    }


    private static String message(String messageCode) {
        def messageSource = Holders.applicationContext.messageSource
        messageSource.getMessage(messageCode, null, 'NameAddress', LocaleContextHolder.getLocale())
    }

}
