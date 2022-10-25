package com.webbfontaine.efem.command

import com.webbfontaine.grails.plugins.search.SearchResultOptions
import com.webbfontaine.grails.plugins.search.SearchUtils
import com.webbfontaine.grails.plugins.search.annotations.CriteriaField
import com.webbfontaine.grails.plugins.search.annotations.FieldStyle
import com.webbfontaine.grails.plugins.search.annotations.ResultField
import com.webbfontaine.grails.plugins.search.command.AbstractSearchCommand
import grails.databinding.BindingFormat
import grails.validation.Validateable
import org.joda.time.LocalDate
import java.lang.reflect.Field

/**
 * Copyrights 2002-2016 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 10/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferSearchCommand extends AbstractSearchCommand implements SearchResultOptions, Validateable, Serializable {
    static ArrayList<Field> resultFields
    static HashMap<String, ResultField> results
    static {
        results = SearchUtils.getResults(this)
        resultFields = SearchUtils.getResultFields(this)
    }

    @Override
    Map getSearchParams(Object inputBean) {
        Map searchParams = [:]
        inputBean.status ? searchParams.put('status', inputBean.status) : ''
        inputBean.ecReference ? searchParams.put('ecReference', inputBean.ecReference) : ''
        inputBean.requestNo ? searchParams.put('requestNo', inputBean.requestNo) : ''
        inputBean.requestDate ? searchParams.put('requestDate', inputBean.requestDate) : ''
        inputBean.requestDateTo ? searchParams.put('requestDateTo', inputBean.requestDateTo) : ''
        inputBean.op_requestDate ? searchParams.put('op_requestDate', inputBean.op_requestDate) : ''
        inputBean.ecDate ? searchParams.put('ecDate', inputBean.ecDate) : ''
        inputBean.ecDateTo ? searchParams.put('ecDateTo', inputBean.ecDateTo) : ''
        inputBean.op_ecDate ? searchParams.put('op_ecDate', inputBean.op_ecDate) : ''
        inputBean.bankCode ? searchParams.put('bankCode', inputBean.bankCode) : ''
        inputBean.currencyCode ? searchParams.put('currencyCode', inputBean.currencyCode) : ''
        inputBean.currencyTransferDate ? searchParams.put('currencyTransferDate', inputBean.currencyTransferDate) : ''
        inputBean.currencyTransferDateTo ? searchParams.put('currencyTransferDateTo', inputBean.currencyTransferDateTo) : ''
        inputBean.op_currencyTransferDate ? searchParams.put('op_currencyTransferDate', inputBean.op_currencyTransferDate) : ''
        return searchParams
    }

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "currencyTransfer.status.label", pattern = "translatable")
    @FieldStyle(fieldClass = {"status"} )
    String status

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "currencyTransfer.requestNo.label")
    @FieldStyle(fieldClass = {"requestNo"} )
    String requestNo

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    @ResultField(displayName = "currencyTransfer.requestDate.label")
    @FieldStyle(fieldClass = {"requestDate"} )
    LocalDate requestDate
    LocalDate requestDateTo
    String op_requestDate

    @ResultField(displayName = "currencyTransfer.bankName.label")
    @FieldStyle(fieldClass = {"bankCode"} )
    String bankCode

    @BindingFormat("dd/MM/yyyy")
    @ResultField(displayName = "currencyTransfer.transferDate.label")
    @CriteriaField(operator = "")
    @FieldStyle(fieldClass = {"currencyTransferDate"} )
    LocalDate currencyTransferDate
    LocalDate currencyTransferDateTo
    String op_currencyTransferDate

    @BindingFormat("dd/MM/yyyy")
    LocalDate ecDate
    LocalDate ecDateTo
    String op_ecDate
    String ecReference

    @ResultField(displayName = "currencyTransfer.amountRepatriated.label")
    @FieldStyle(fieldClass = {"amountRepatriated"} )
    BigDecimal amountRepatriated

    @ResultField(displayName = "currencyTransfer.amountTransferred.label")
    @FieldStyle(fieldClass = {"amountTransferred"} )
    BigDecimal amountTransferred

    @ResultField(displayName = "currencyTransfer.transferRate.label")
    @FieldStyle(fieldClass = {"transferRate"} )
    BigDecimal transferRate

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "currencyTransfer.currencyCode.label")
    @FieldStyle(fieldClass = {"currencyCode"} )
    String currencyCode

    @ResultField(displayName = "currencyTransfer.amountTransferredNat.label")
    @FieldStyle(fieldClass = {"amountTransferredNat"} )
    BigDecimal amountTransferredNat

    @Override
    ArrayList<Field> getResultFields() {
        if (!resultFields) {
            resultFields = SearchUtils.getResultFields(this)
        }
        return resultFields
    }

    @Override
    HashMap<String, ResultField> getResults() {
        if (!results) {
            results = SearchUtils.getResults(this)
        }
        return results
    }
}
