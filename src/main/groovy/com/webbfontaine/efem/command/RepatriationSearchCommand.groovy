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
 * Developer: Yacouba SYLLA
 * Date: 22/04/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RepatriationSearchCommand extends AbstractSearchCommand implements SearchResultOptions, Validateable, Serializable {
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
        inputBean.natureOfFund ? searchParams.put('natureOfFund', inputBean.natureOfFund) : ''
        inputBean.requestNo ? searchParams.put('requestNo', inputBean.requestNo) : ''
        inputBean.requestDate ? searchParams.put('requestDate', inputBean.requestDate) : ''
        inputBean.requestDateTo ? searchParams.put('requestDateTo', inputBean.requestDateTo) : ''
        inputBean.op_requestDate ? searchParams.put('op_requestDate', inputBean.op_requestDate) : ''
        inputBean.repatriationBankCode ? searchParams.put('repatriationBankCode', inputBean.repatriationBankCode) : ''
        inputBean.code ? searchParams.put('code', inputBean.code) : ''
        inputBean.receivedAmount ? searchParams.put('receivedAmount', inputBean.receivedAmount) : ''
        inputBean.currencyCode ? searchParams.put('currencyCode', inputBean.currencyCode) : ''
        inputBean.receptionDate ? searchParams.put('receptionDate', inputBean.receptionDate) : ''
        inputBean.receptionDateTo ? searchParams.put('receptionDateTo', inputBean.receptionDateTo) : ''
        inputBean.op_receptionDate ? searchParams.put('op_receptionDate', inputBean.op_receptionDate) : ''
        return searchParams
    }

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "repatriation.status.label", pattern = "translatable")
    @FieldStyle(fieldClass = {"status"} )
    String status

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "repatriation.natureOfFund.label", pattern = "translatable")
    @FieldStyle(fieldClass = {"natureOfFund"} )
    String natureOfFund

    String ecReference

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "repatriation.requestNo.label")
    @FieldStyle(fieldClass = {"requestNo"} )
    String requestNo

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    @ResultField(displayName = "transferOrder.requestDate.label")
    LocalDate requestDate
    LocalDate requestDateTo
    String op_requestDate

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "repatriation.receivedAmount.label")
    @FieldStyle(fieldClass = {"receivedAmount"} )
    String receivedAmount

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.currencyCode.label")
    @FieldStyle(fieldClass = {"currencyCode"} )
    String currencyCode

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate receptionDate
    LocalDate receptionDateTo
    String op_receptionDate

    @ResultField(displayName = "repatriation.code.label")
    @FieldStyle(fieldClass = {"code"} )
    String code

    @ResultField(displayName = "repatriation.repatriationBankCodeSearch.label")
    @FieldStyle(fieldClass = {"repatriationBankCode"} )
    String repatriationBankCode

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
