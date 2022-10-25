package com.webbfontaine.efem.currencyTransfer

import com.webbfontaine.efem.AttachedDocContainer
import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.LoggerInstance
import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.log.CurrencyTransferLog
import com.webbfontaine.efem.rules.currencyTransfer.CurrencyTransferBusinessLogicRule
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import com.webbfontaine.grails.plugins.utils.Utils
import grails.databinding.BindingFormat
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA.
 * Date: 15/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class CurrencyTransfer implements ConfigurableFields, Serializable, AttachedDocContainer, LoggerInstance {

    Integer requestNumberSequence
    Integer requestYear
    String requestNo
    @BindingFormat("dd/mm/yyyy")
    LocalDate requestDate
    String bankCode
    String bankName
    String currencyCode
    String currencyName
    BigDecimal currencyRate
    BigDecimal amountTransferred
    BigDecimal amountTransferredNat
    BigDecimal amountRepatriated
    BigDecimal amountRepatriatedNat
    @BindingFormat("dd/mm/yyyy")
    LocalDate currencyTransferDate
    String status
    String userOwner
    List<ClearanceDomiciliation> clearanceDomiciliations
    Collection<Operation> operations
    Operation startedOperation
    SortedSet<CurrencyTransferLog> logs
    LocalDateTime lastTransactionDate
    String comments
    String repatriationNo
    LocalDate repatriationDate
    BigDecimal transferRate

    static hasMany = [clearanceDomiciliations: ClearanceDomiciliation, attachedDocs: CurrencyTransferAttachedDoc, operations: Operation, logs: CurrencyTransferLog]

    boolean isDocumentEditable = false
    boolean isAttachmentEditable = false
    boolean isPrefinancingWithoutEC = false
    boolean addAttachedDocs
    boolean editAttachedDocs
    List<CurrencyTransferAttachedDoc> attachedDocs
    String initialStatus
    static rules = {[CurrencyTransferBusinessLogicRule]}
	
    static fetchMode = [attachedDocs: 'join', logs: 'join', clearanceDomiciliations: 'join']

    static transients = ['operations', 'startedOperation', 'initialStatus', 'isDocumentEditable', 'isExecutionsEditable',
                         'isDocumentValid', 'addAttachedDocs', 'editAttachedDocs', 'isAttachmentEditable']

    static constraints = {

        currencyRate(nullable: true, scale: 3)
        amountTransferred(nullable: true, scale: 2)
        amountTransferredNat(nullable: true, scale: 2)
        amountRepatriated(nullable: true, scale: 2)
        amountRepatriatedNat(nullable: true, scale: 2)
        repatriationNo(nullable: true)
        repatriationDate(nullable: true)
        requestNo(nullable: true)
        requestDate(nullable: true)
        currencyCode(nullable: true)
        currencyName(nullable: true)
        currencyTransferDate(nullable: true)
        bankCode(nullable: true)
        bankName(nullable: true)
        requestNumberSequence(nullable: true, min: 1)
        requestYear(nullable: true)
        attachedDocs(nullable: true, blank: true)
        transferRate(nullable: true, scale: 3)

    }

    static mapping = {
        table 'CURRENCY_TRANSFER'
        requestNumberSequence column: 'REQ_SEQ'
        requestYear column: 'REQ_YER'
        requestNo maxSize: 11, column: 'REQUEST_NO'
        requestDate column: 'REQUEST_DATE'
        currencyCode maxSize: 3
        currencyName maxSize: 35, column: 'CUR_NAME'
        currencyRate column: 'CUR_RATE'
        currencyTransferDate column: 'CUR_TR_DAT'
        bankCode maxSize: 7, column: 'BANK_CODE'
        bankName maxSize: 35, column: 'BANK_NAME'
        amountTransferred column: 'AMOUN_TR_DAT'
        amountTransferredNat column: 'AMOUN_TRAN'
        amountRepatriated column: 'AMOUNT_REPAT'
        amountRepatriatedNat column: 'AMOUNT_REPAT_NAT'
        status maxSize: 30
        repatriationNo maxSize: 11, column: 'REPAT_NO'
        repatriationDate column: 'REPAT_DATE'
        comments sqlType: 'clob'
        clearanceDomiciliations cascade: "all-delete-orphan", fetch: 'join'
        attachedDocs cascade: "all-delete-orphan", fetch: 'join'
        transferRate column: 'TRANSFER_RATE'
        isPrefinancingWithoutEC defaultValue: 0

    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return FieldsConfiguration.isCurrencyTransferFieldsMandatory(fieldName, this?.startedOperation)
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        def result = false
        if (!FieldsConfiguration.isCurrencyTransferFieldsProhibited(fieldName, this?.startedOperation)) {
            result = !FieldsConfiguration.isCurrencyTransferFieldsCustom(fieldName, this?.startedOperation)
        }
        return result
    }

    @Override
    AbstractAttachedDoc createAttachedDoc(Object params) {
        return new CurrencyTransferAttachedDoc(params as Map)
    }

    @Override
    CurrencyTransferLog instanceLog() {
        return new CurrencyTransferLog()
    }

    void setIsDocumentEditable(boolean editable) {
        this.isDocumentEditable = editable
    }

    boolean isDocumentEditable() {
        return this.isDocumentEditable

    }

    void setIsAttachmentEditable(boolean editable) {
        this.isAttachmentEditable = editable
    }

    boolean isAttachmentEditable() {
        return this.isAttachmentEditable
    }

    CurrencyTransferAttachedDoc addAttDoc(CurrencyTransferAttachedDoc attDoc) {
        attDoc.currencyTransfer = this
        Utils.addToCollection('attachedDocs', this, attDoc)
        attDoc
    }

    CurrencyTransferAttachedDoc getAttDoc(Integer rank) {
        attachedDocs?.find { it.rank == rank }
    }

    CurrencyTransferAttachedDoc getAttDoc(String docTypName) {
        attachedDocs?.find {
            it.docTypName == docTypName
        }
    }

    Integer getNumberAtt() {
        numberAtt = attachedDocs?.size() ? attachedDocs?.size() : 0
    }

    void setAddAttachedDocs(boolean addAttachedDocs) {
        this.addAttachedDocs = addAttachedDocs
    }

    boolean editAttachedDocs() {
        return editAttachedDocs
    }

    void setEditAttachedDocs(boolean editAttachedDocs) {
        this.editAttachedDocs = editAttachedDocs
    }

    List removeFromList(List list, domain) {
        Utils.removeFromCollection(list, domain)
    }

    ClearanceDomiciliation getClearanceDomiciliation(Integer rank, String ecReference) {
        clearanceDomiciliations?.find {
            it.ecReference == ecReference && it.rank == rank
        }
    }

    ClearanceDomiciliation addClearanceDomiciliation(ClearanceDomiciliation clearanceDomiciliation) {
        clearanceDomiciliation.currencyTransfer = this
        Utils.addToCollection("clearanceDomiciliations", this, clearanceDomiciliation)
        clearanceDomiciliation
    }

    List<ClearanceDomiciliation> removeClearanceDomiciliation(ClearanceDomiciliation clearanceDomiciliation) {
        Utils.removeFromCollection(this.clearanceDomiciliations, clearanceDomiciliation)
        clearanceDomiciliations
    }

}
