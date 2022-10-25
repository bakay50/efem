package com.webbfontaine.efem.log

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import org.joda.time.LocalDateTime

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady Diarra.
 * Date: 15/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferLog implements Comparable {
    String initialStatus
    String operation
    String endStatus
    LocalDateTime operationDate
    String userLogin
    String logMessage

    static belongsTo = [currencyTransfer: CurrencyTransfer]

    static mapping = {
        table "CURRENCY_TRANSFER_RECORD"
        logMessage sqlType: 'clob'
        currencyTransfer column: 'DOCUMENT_ID'
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
