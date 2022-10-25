package com.webbfontaine.efem.rimm

import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.rules.rimm.bank.BankBusinessLogicRule
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import org.joda.time.LocalDateTime

class Bank implements ConfigurableFields, Serializable {
    Long id
    String code
    LocalDateTime dateOfValidity
    String emailEA
    String emailEC
    String status
    Operation startedOperation
    LinkedHashSet<Operation> operations
    boolean isDocumentEditable = false

    static hasMany = [operations: Operation]

    static transients = ['startedOperation', 'isDocumentEditable', 'operations']

    static constraints = {
        code maxSize: 35, unique: true
        emailEA maxSize: 128
        emailEC maxSize: 128
    }

    static rules = { [BankBusinessLogicRule] }

    static mapping = {
        table 'RIMM_BNK'
        id column: 'ID', generator: 'sequence', params: [sequence: 'RIMM_BNK_SEQ']
        code maxSize: 11, column: 'COD'
        dateOfValidity column: 'DOV'
        emailEA maxSize: 128, column: 'EML'
        emailEC maxSize: 128, column: 'EML_EC'
        status column: 'STATUS'
        version(false)
    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return FieldsConfiguration.isMandatory(fieldName, this?.startedOperation, UtilConstants.BANK)
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        return (this.isDocumentEditable && !FieldsConfiguration.isProhibited(fieldName, this?.startedOperation, UtilConstants.BANK))
    }

    void setIsDocumentEditable(boolean editable) {
        this.isDocumentEditable = editable
    }

    boolean isDocumentEditable() {
        return this.isDocumentEditable
    }
}
