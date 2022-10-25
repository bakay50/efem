package com.webbfontaine.efem.sad

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.grails.plugins.utils.TypesCastUtils
import org.apache.http.HttpStatus

import static com.webbfontaine.efem.constants.Statuses.*

class SadUtils {

    public static def checkSadResponse(sadXMLresponse, Exchange exchangeInstance){
        boolean isValidSad = false
        List validSadStatus = [ST_ASSESSED,ST_PAID,ST_EXITED, ST_TOTALLYEXITED]
        if(HttpStatus.SC_NOT_FOUND.equals(sadXMLresponse?.statusCode)){
            exchangeInstance.isDocumentValid = false
            exchangeInstance.errors.reject("load.sad.error", "The SAD doesn't exist.")
        }else if(!validSadStatus.contains(sadXMLresponse?.data?.declaration?.status ?: sadXMLresponse?.data?.status )) {
            exchangeInstance.isDocumentValid = false
            exchangeInstance.errors.reject("load.sad.status.error", "The declaration doesn’t have valid status")
        }else if(HttpStatus.SC_OK.equals(sadXMLresponse?.statusCode)){
            isValidSad = true
        }
        return [isValidSad : isValidSad]
    }

    static def setValueToTargetObject(sourceObject, sourceToTargetFieldMapping, targetObject){
        sourceToTargetFieldMapping.each{key,value ->
            if (value == "items" && AppConfig.resolevRoleName() != "EFEMCI") {
                def attachments = sourceObject["$value"][0]."attachedDocuments"
                def avdDetails = attachments.find{it."attachedDocCode" == "900"}
                avdDetails.each {attKey, attVal ->
                    if (attKey.equals("attachedDocReference") && attVal.contains("-")){
                        targetObject["$attKey"+"Sad"] = attVal?.split('\\-')[0]
                    } else {
                        targetObject["$attKey"+"Sad"] = (attKey == "date" && attVal != null) ? TypesCastUtils.toDate([value]) : attVal
                    }
                }
            }
            else if(getExchangeFieldType("$key") in BigDecimal){
                    targetObject["$key"] = sourceObject["$value"] as BigDecimal
            }
            else{
                    targetObject["$key"] = sourceObject["$value"]
                }
            }

        sourceObject
    }

    static def getExchangeFieldType(field){
        return Exchange.getDeclaredField(field).getType()
    }
}
