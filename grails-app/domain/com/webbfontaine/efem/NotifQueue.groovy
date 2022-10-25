package com.webbfontaine.efem

import org.joda.time.LocalDate

class NotifQueue {

    int retries = 0
    LocalDate dateQueued = LocalDate.now()
    String modName
    Integer id
    String type
    String corpsus
    String typeError

    static constraints = {
        type  nullable: false
        modName  nullable: false
        retries  nullable: false
        dateQueued nullable: false
        typeError nullable: false
    }

    static mapping = {
        datasource('wf_rimm')
        version(false)
        table 'NOTIF_QUEUE'
        modName column: 'MOD_NAME'
        dateQueued column: 'TRYDATE'
        type column: 'TYPE'
        typeError column: 'TYP_ERROR'
        corpsus column: 'CORPUS', sqlType: 'CLOB'
        retries column: 'NBRE_RETRY'
        id generator: 'sequence', params: [sequence: 'SEQ_NOTIF_QUEUE']
    }
}