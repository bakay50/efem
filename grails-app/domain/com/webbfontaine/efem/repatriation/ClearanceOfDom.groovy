package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.rules.repatriation.clearanceOfDom.ClearanceOfDomBusinessLogicRule
import grails.databinding.BindingFormat
import org.joda.time.LocalDate

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 14/04/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ClearanceOfDom {
    Integer rank
    String ecReference
    @BindingFormat("dd/mm/yyyy")
    LocalDate ecDate
    String domiciliaryBank
    String domiciliationNo
    @BindingFormat("dd/mm/yyyy")
    LocalDate domiciliationDate
    BigDecimal domAmtInCurr
    BigDecimal invFinalAmtInCurr
    BigDecimal repatriatedAmtInCurr
    String currencyCodeEC
    @BindingFormat("dd/mm/yyyy")
    LocalDate dateOfBoarding
    String bankCode
    Boolean status = true
    Boolean ceded = false
    static belongsTo = [repats:Repatriation]
    static rules = {[ClearanceOfDomBusinessLogicRule]}
    static mapping = {
        table 'CLEARANCE'
        domiciliaryBank column: 'DOM_BANK'
        domiciliationNo column: 'DOM_NO'
        domiciliationDate column: 'DOM_DAT'
        domAmtInCurr column: 'DOM_CUR'
        invFinalAmtInCurr column: 'INV_AMT_CURR'
        repatriatedAmtInCurr column: 'REPA_AMT_CURR'
        currencyCodeEC column: 'CUR_COD_EC'
        dateOfBoarding column: 'DATE_BOARDING'
        bankCode column: 'BANK_CODE'
        status defaultValue: 1
    }
    static constraints = {
        bankCode(nullable:true,blank:true)
    }
}
