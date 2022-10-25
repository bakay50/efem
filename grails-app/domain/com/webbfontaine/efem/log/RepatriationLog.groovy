package com.webbfontaine.efem.log

import com.webbfontaine.efem.repatriation.Repatriation
import org.joda.time.LocalDateTime
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RepatriationLog implements Comparable{

    String initialStatus
    String operation
    String endStatus
    LocalDateTime operationDate
    String userLogin
    String logMessage

    static belongsTo = [repatriation: Repatriation]

    static mapping = {
        table "REP_TRANSACTION_RECORD"
        logMessage sqlType: 'clob'
        repatriation column: 'DOCUMENT_ID'
    }

    @Override
    String toString() {
        return "${this.class.name}{" +
                "id=" + id +
                ", initialStatus='" + initialStatus + '\'' +
                ", operation='" + operation + '\'' +
                ", endStatus='" + endStatus + '\'' +
                ", operationDate=" + operationDate +
                ", userLogin='" + userLogin + '\'' +
                ", logMessage='" + logMessage + '\'' +
                '}'
    }

    @Override
    int compareTo(Object other) {
        if (operationDate.isAfter(other.operationDate)) {
            return 1
        } else if ((operationDate.isBefore(other.operationDate))) {
            return -1
        } else {
            return 0
        }
    }
}
