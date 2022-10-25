package com.webbfontaine.efem.supportInfo

import com.webbfontaine.efem.BusinessLogicUtils
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class SupportInformationUtils {

    private static def dateTimeFormats = [:]

    static{
        dateTimeFormats << ["fr": ["date": "dd/MM/yyyy", "time": "HH:mm:ss"]]
        dateTimeFormats << ["en": ["date": "MM/dd/yyyy", "time": "hh:mm:ss a"]]
    }

    public static def constructLogMessage(transactionRecord) {
        DateTimeFormatter timeFormatter = BusinessLogicUtils.getLocale().toString().equalsIgnoreCase("fr") ?
                DateTimeFormat.forPattern(dateTimeFormats.fr.time.toString()) :
                DateTimeFormat.forPattern(dateTimeFormats.en.time.toString())
        String transactionTime = timeFormatter.print(transactionRecord.operationDate)
        def logMsg = [logTime  : transactionTime,
                      userLogin: transactionRecord.userLogin]
        if (transactionRecord?.logMessage) {
            logMsg.put('logMessage', transactionRecord.logMessage)
        } else {
            logMsg.put('commitOperation', transactionRecord.operation)
            logMsg.put('newStatus', transactionRecord.endStatus)
        }
        logMsg
    }

    public static def constructLogDate(LocalDateTime date){
        DateTimeFormatter dateFormatter = BusinessLogicUtils.getLocale().toString().equalsIgnoreCase("fr") ?
                DateTimeFormat.forPattern(dateTimeFormats.fr.date.toString()) :
                DateTimeFormat.forPattern(dateTimeFormats.en.date.toString())
        String transactionDate = dateFormatter?.print(date)
        transactionDate
    }
}
