package com.webbfontaine.efem.command

import com.webbfontaine.grails.plugins.search.SearchResultOptions
import com.webbfontaine.grails.plugins.search.SearchUtils
import com.webbfontaine.grails.plugins.search.annotations.CriteriaField
import com.webbfontaine.grails.plugins.search.annotations.ResultField
import com.webbfontaine.grails.plugins.search.command.AbstractSearchCommand
import grails.databinding.BindingFormat
import grails.validation.Validateable
import org.joda.time.LocalDateTime

import java.lang.reflect.Field

class BankSearchCommand extends AbstractSearchCommand implements SearchResultOptions, Validateable, Serializable{
    static ArrayList<Field> resultFields
    static HashMap<String, ResultField> results
    static {
        results = SearchUtils.getResults(this)
        resultFields = SearchUtils.getResultFields(this)
    }

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "bank.status.label", pattern = "translatable")
    String status

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "bank.code.label")
    String code

    @BindingFormat("dd/MM/yyyy HH:mm:ss")
    @CriteriaField(operator = "")
    @ResultField(displayName = "bank.dateOfValidity.label")
    LocalDateTime dateOfValidity
    LocalDateTime dovTo
    String op_dov

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "bank.emailEA.label")
    String emailEA

    @CriteriaField(operator = "equals")
    @ResultField(displayName = "bank.emailEC.label")
    String emailEC


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
