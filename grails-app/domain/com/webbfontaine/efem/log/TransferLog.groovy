package com.webbfontaine.efem.log

import com.webbfontaine.efem.transferOrder.TransferOrder
import org.joda.time.LocalDateTime

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferLog implements Comparable {
    String initialStatus
    String operation
    String endStatus
    LocalDateTime operationDate
    String userLogin
    String logMessage

    static belongsTo = [transfer: TransferOrder]

    static mapping = {
        table "TRANSFER_TRANSACT_RECORD"
        logMessage sqlType: 'clob'
        transfer column: 'DOCUMENT_ID'
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
