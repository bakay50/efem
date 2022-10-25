package com.webbfontaine.efem.currencyTransfer

import grails.databinding.BindingFormat
import org.joda.time.LocalDate

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady Diarra.
 * Date: 15/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ClearanceDomiciliation {

    Integer rank
    String ecReference
    @BindingFormat("dd/mm/yyyy")
    LocalDate ecDate
    String ecExporterName
    String domiciliationCodeBank
    String domiciliationNo
    @BindingFormat("dd/mm/yyyy")
    LocalDate domiciliationDate
    BigDecimal domiciliatedAmounttInCurr
    BigDecimal invoiceFinalAmountInCurr
    BigDecimal repatriatedAmountToBank
    BigDecimal amountTransferredInCurr
    Long repatClearance
    static belongsTo = [currencyTransfer: CurrencyTransfer]

    static mapping = {
        table 'CLEARANCE_DOMICILIATION'
        ecReference maxSize: 11, column: 'EC_REF'
        domiciliationCodeBank maxSize: 5, column: 'DOM_CODE_BANK'
        domiciliationNo maxSize: 35, column: 'DOM_NO'
        domiciliationDate column: 'DOM_DAT'
        domiciliatedAmounttInCurr column: 'DOM_CUR'
        invoiceFinalAmountInCurr column: 'INV_AMT_CURR'
        repatriatedAmountToBank column: 'REPA_AMT_CURR'
        amountTransferredInCurr column: 'AMT_TRANSF_CURR'
        repatClearance column: 'REPAT_CLEARANCE'
    }

}
