package com.webbfontaine.efem.command

import com.webbfontaine.efem.constant.SearchTab
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
 * Developer: A.Bilalang
 * Date: 20/07/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ExchangeSearchCommand extends AbstractSearchCommand implements SearchResultOptions, Validateable, Serializable {

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
        inputBean.requestType ? searchParams.put('requestType', inputBean.requestType) : ''
        inputBean.basedOn ? searchParams.put('basedOn', inputBean.basedOn) : ''
        inputBean.tvfNumber ? searchParams.put('tvfNumber', inputBean.tvfNumber) : ''
        inputBean.declarationNumber ? searchParams.put('declarationNumber', inputBean.declarationNumber) : ''
        inputBean.domiciliationBankCode ? searchParams.put('domiciliationBankCode', inputBean.domiciliationBankCode) : ''
        inputBean.departmentInCharge ? searchParams.put('departmentInCharge', inputBean.departmentInCharge) : ''
        inputBean.geoArea ? searchParams.put('geoArea', inputBean.geoArea) : ''
        inputBean.registrationNumberBank ? searchParams.put('registrationNumberBank', inputBean.registrationNumberBank) : ''
        inputBean.domiciliationNumber ? searchParams.put('domiciliationNumber', inputBean.domiciliationNumber) : ''
        inputBean.bankName ? searchParams.put('bankName', inputBean.bankName) : ''
        inputBean.bankCode ? searchParams.put('bankCode', inputBean.bankCode) : ''
        inputBean.declarantNameAddress ? searchParams.put('declarantNameAddress', inputBean.declarantNameAddress) : ''
        inputBean.exporterNameAddress ? searchParams.put('exporterNameAddress', inputBean.exporterNameAddress) : ''
        inputBean.importerNameAddress ? searchParams.put('importerNameAddress', inputBean.importerNameAddress) : ''
        inputBean.countryProvenanceDestinationCode ? searchParams.put('countryProvenanceDestinationCode', inputBean.countryProvenanceDestinationCode) : ''
        inputBean.balanceAs ? searchParams.put('balanceAs', inputBean.balanceAs) : ''
        inputBean.declarantCode ? searchParams.put('declarantCode', inputBean.declarantCode) : ''
        inputBean.declarationSerial ? searchParams.put('declarationSerial', inputBean.declarationSerial) : ''
        inputBean.treatmentLevel ? searchParams.put('treatmentLevel', inputBean.treatmentLevel) : ''
        inputBean.importerCode ? searchParams.put('importerCode', inputBean.importerCode) : ''
        inputBean.exporterCode ? searchParams.put('exporterCode', inputBean.exporterCode) : ''
        inputBean.operType ? searchParams.put('operType', inputBean.operType) : ''
        inputBean.searchTab ? searchParams.put('searchTab', inputBean.searchTab) : ''
        inputBean.executingBankCode ? searchParams.put('executingBankCode', inputBean.executingBankCode) : ''
        inputBean.clearanceOfficeCode ? searchParams.put('clearanceOfficeCode', inputBean.clearanceOfficeCode) : ''
        inputBean.requestNo ? searchParams.put('requestNo', inputBean.requestNo) : ''
        inputBean.amountNationalCurrency ? searchParams.put('amountNationalCurrency', inputBean.amountNationalCurrency) : ''
        inputBean.amountNationalCurrencyTo ? searchParams.put('amountNationalCurrencyTo', inputBean.amountNationalCurrencyTo) : ''
        inputBean.op_amountNationalCurrency ? searchParams.put('op_amountNationalCurrency', inputBean.op_amountNationalCurrency) : ''
        inputBean.declarationDate ? searchParams.put('declarationDate', inputBean.declarationDate) : ''
        inputBean.declarationDateTo ? searchParams.put('declarationDateTo', inputBean.declarationDateTo) : ''
        inputBean.op_declarationDate ? searchParams.put('op_declarationDate', inputBean.op_declarationDate) : ''
        inputBean.registrationDateBank ? searchParams.put('registrationDateBank', inputBean.registrationDateBank) : ''
        inputBean.registrationDateBankTo ? searchParams.put('registrationDateBankTo', inputBean.registrationDateBankTo) : ''
        inputBean.op_registrationDateBank ? searchParams.put('op_registrationDateBank', inputBean.op_registrationDateBank) : ''
        inputBean.domiciliationDate ? searchParams.put('domiciliationDate', inputBean.domiciliationDate) : ''
        inputBean.domiciliationDateTo ? searchParams.put('domiciliationDateTo', inputBean.domiciliationDateTo) : ''
        inputBean.op_domiciliationDate ? searchParams.put('op_domiciliationDate', inputBean.op_domiciliationDate) : ''
        inputBean.tvfDate ? searchParams.put('tvfDate', inputBean.tvfDate) : ''
        inputBean.tvfDateTo ? searchParams.put('tvfDateTo', inputBean.tvfDateTo) : ''
        inputBean.op_tvfDate ? searchParams.put('op_tvfDate', inputBean.op_tvfDate) : ''
        inputBean.authorizationDate ? searchParams.put('authorizationDate', inputBean.authorizationDate) : ''
        inputBean.authorizationDateTo ? searchParams.put('authorizationDateTo', inputBean.authorizationDateTo) : ''
        inputBean.op_authorizationDate ? searchParams.put('op_authorizationDate', inputBean.op_authorizationDate) : ''
        inputBean.requestDate ? searchParams.put('requestDate', inputBean.requestDate) : ''
        inputBean.requestDateTo ? searchParams.put('requestDateTo', inputBean.requestDateTo) : ''
        inputBean.op_requestDate ? searchParams.put('op_requestDate', inputBean.op_requestDate) : ''
        inputBean.importerCode ? searchParams.put('importerCode', inputBean.importerCode) : ''
        inputBean.bankCode ? searchParams.put('bankCode', inputBean.bankCode) : ''
        inputBean.currencyCode ? searchParams.put('currencyCode', inputBean.currencyCode) : ''
        inputBean.bankApprovalDate ? searchParams.put('bankApprovalDate', inputBean.bankApprovalDate) : ''
        inputBean.bankApprovalDateTo ? searchParams.put('bankApprovalDateTo', inputBean.bankApprovalDateTo) : ''
        inputBean.op_bankApprovalDate ? searchParams.put('op_bankApprovalDate', inputBean.op_bankApprovalDate) : ''
        inputBean.docRef ? searchParams.put('docRef', inputBean.docRef) : ''
        return searchParams
    }

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.status.label", pattern = "translatable")
    @FieldStyle(fieldClass = {"status"} )
    String status

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.requestType.label")
    @FieldStyle(fieldClass = {"requestType"} )
    String requestType
    
    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.basedOn.label")
    @FieldStyle(fieldClass = {"basedOn"} )
    String basedOn

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.requestNo.label")
    @FieldStyle(fieldClass = {"requestNo"} )
    String requestNo

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    @ResultField(displayName = "exchange.requestDate.label")
    @FieldStyle(fieldClass = {"requestDate"} )
    LocalDate requestDate
    LocalDate requestDateTo
    String op_requestDate

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.tvfNumber.label")
    @FieldStyle(fieldClass = {"tvfNumber"} )
    Integer tvfNumber

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.declarationNumber.label")
    @FieldStyle(fieldClass = {"declarationNumber"} )
    String declarationNumber

    @CriteriaField(operator = "equals")
    String domiciliationBankCode

    @CriteriaField(operator = "equals")
    String departmentInCharge

    @CriteriaField(operator = "equals")
    String geoArea

    @CriteriaField(operator = "equals")
    String registrationNumberBank

    @CriteriaField(operator = "equals")
    String domiciliationNumber

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.bankName.label")
    @FieldStyle(fieldClass = {"bankName"} )
    String bankName

    String bankCode

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    @ResultField(displayName = "exchange.bankApprovalDate.label")
    @FieldStyle(fieldClass = {"bankApprovalDate"} )
    LocalDate bankApprovalDate
    LocalDate bankApprovalDateTo
    String op_bankApprovalDate

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.declarantNameAddress.label")
    @FieldStyle(fieldClass = {"declarantNameAddress"} )
    String declarantNameAddress

    @ResultField(displayName = "exchange.importerNameAddress.label")
    @FieldStyle(fieldClass = {"importerNameAddress"} )
    String importerNameAddress

    @ResultField(displayName = "exchange.exporterNameAndAddress.label")
    @FieldStyle(fieldClass = {"exporterNameAddress"} )
    String exporterNameAddress

    @CriteriaField(operator = "")
    @ResultField(displayName = "exchange.srch.amountNationalCurrency.label")
    @FieldStyle(fieldClass = {"amountNationalCurrency"} )
    BigDecimal amountNationalCurrency
    BigDecimal amountNationalCurrencyTo
    String op_amountNationalCurrency

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "exchange.srch.countryProvenanceDestinationCode.label")
    @FieldStyle(fieldClass = {"countryProvenanceDestinationCode"} )
    String countryProvenanceDestinationCode

    @CriteriaField(operator = "")
    @ResultField(displayName = "exchange.balanceAs.label")
    @FieldStyle(fieldClass = {"balanceAs"} )
    String balanceAs

    String declarantCode

    @CriteriaField(operator = "equals")
    String declarationSerial

    @CriteriaField(operator = "equals")
    String currencyCode

    @CriteriaField(operator = "equals")
    Integer treatmentLevel

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate declarationDate
    LocalDate declarationDateTo
    String op_declarationDate


    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate registrationDateBank
    LocalDate registrationDateBankTo
    String op_registrationDateBank

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate domiciliationDate
    LocalDate domiciliationDateTo
    String op_domiciliationDate

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate tvfDate
    LocalDate tvfDateTo
    String op_tvfDate

    @BindingFormat("dd/MM/yyyy")
    @CriteriaField(operator = "")
    LocalDate authorizationDate
    LocalDate authorizationDateTo
    String op_authorizationDate

    String importerCode
    String exporterCode

    @CriteriaField(operator = "equals")
    String operType
    SearchTab searchTab
    String executingBankCode

    @CriteriaField(operator = "equals")
    String clearanceOfficeCode

    String docRef

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
