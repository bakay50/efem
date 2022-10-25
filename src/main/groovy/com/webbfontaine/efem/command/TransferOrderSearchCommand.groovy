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
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrderSearchCommand extends AbstractSearchCommand implements SearchResultOptions, Validateable, Serializable {
    static ArrayList<Field> resultFields
    static HashMap<String, ResultField> results
    static {
        results = SearchUtils.getResults(this)
        resultFields = SearchUtils.getResultFields(this)
    }

    @Override
    Map getSearchParams(Object inputBean) {
        Map searchParams = [:]
        inputBean.eaReference ? searchParams.put('eaReference', inputBean.eaReference) : ''
        inputBean.bankCode ? searchParams.put('bankCode', inputBean.bankCode) : ''
        inputBean.executionRef ? searchParams.put('executionRef', inputBean.executionRef) : ''
        inputBean.requestDate ? searchParams.put('requestDate', inputBean.requestDate) : ''
        inputBean.importerCode ? searchParams.put('importerCode', inputBean.importerCode) : ''
        inputBean.requestDateTo ? searchParams.put('requestDateTo', inputBean.requestDateTo) : ''
        inputBean.requestNo ? searchParams.put('requestNo', inputBean.requestNo) : ''
        inputBean.op_requestDate ? searchParams.put('op_requestDate', inputBean.op_requestDate) : ''
        inputBean.op_executionDate ? searchParams.put('op_executionDate', inputBean.op_executionDate) : ''
        inputBean.executionDate ? searchParams.put('executionDate', inputBean.executionDate) : ''
        inputBean.currencyPayCode ? searchParams.put('currencyPayCode', inputBean.currencyPayCode) : ''
        inputBean.executionDateTo ? searchParams.put('executionDateTo', inputBean.executionDateTo) : ''
        inputBean.status ? searchParams.put('status', inputBean.status) : ''
        return searchParams
    }

    @CriteriaField(operator = "equals")
    @FieldStyle(fieldClass = {"status"} )
    @ResultField(displayName = "transferOrder.status.label", pattern = "translatable")
    String status

    @CriteriaField(operator = "equals")
    @FieldStyle(fieldClass = {"requestNo"} )
    @ResultField(displayName = "transferOrder.requestNo.label")
    String requestNo

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    @FieldStyle(fieldClass = {"requestDate"} )
    @ResultField(displayName = "transferOrder.requestDate.label")
    LocalDate requestDate
    LocalDate requestDateTo
    String op_requestDate


    @FieldStyle(fieldClass = {"importerCode"} )
    @ResultField(displayName = "transferOrder.importerCode.label")
    String importerCode

    @FieldStyle(fieldClass = {"bankCode"} )
    @ResultField(displayName = "transferOrder.bankName.label")
    String bankCode

    @FieldStyle(fieldClass = {"transferAmntRequested"} )
    @ResultField(displayName = "transferOrder.transferAmntRequested.label")
    String transferAmntRequested

    @FieldStyle(fieldClass = {"sumOfClearanceOfDoms"} )
    @ResultField(displayName = "transferOrder.amntSetInMentionedCurr.label")
    BigDecimal sumOfClearanceOfDoms

    @CriteriaField(operator = "equals")
    String executionRef

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate executionDate
    LocalDate executionDateTo
    String op_executionDate

    @FieldStyle(fieldClass = {"currencyPayCode"} )
    @ResultField(displayName = "transferOrder.search.currencyCodePay.label")
    String currencyPayCode

    String eaReference

    @Override
    HashMap<String, ResultField> getResults() {
        if (!results) {
            results = SearchUtils.getResults(this)
        }
        return results
    }

    @Override
    ArrayList<Field> getResultFields() {
        if (!resultFields) {
            resultFields = SearchUtils.getResultFields(this)
        }
        return resultFields
    }

}
