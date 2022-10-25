package com.webbfontaine.efem.log

import com.webbfontaine.efem.Exchange
import org.joda.time.LocalDateTime

class ExchangeLog implements Comparable{

    String initialStatus
    String operation
    String endStatus
    LocalDateTime operationDate
    String userLogin
    String logMessage

    static belongsTo = [exchange: Exchange]

    static mapping = {
        table "TRANSACTION_RECORD"
        logMessage sqlType: 'clob'
        exchange column: 'document_id'
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
