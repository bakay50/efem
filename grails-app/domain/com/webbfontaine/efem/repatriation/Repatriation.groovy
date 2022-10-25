package com.webbfontaine.efem.repatriation

import com.webbfontaine.efem.AttachedDocContainer
import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.LoggerInstance
import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachedDoc.RepAttachedDoc
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.log.RepatriationLog
import com.webbfontaine.efem.rules.repatriation.RepatriationBusinessLogicRule
import com.webbfontaine.grails.plugins.utils.Utils
import grails.databinding.BindingFormat
import org.joda.time.LocalDate
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.UserUtils.isBankAgent
import static com.webbfontaine.efem.UserUtils.isDeclarant
import static com.webbfontaine.efem.UserUtils.isTrader
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 14/04/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class Repatriation implements ConfigurableFields, Serializable, AttachedDocContainer, LoggerInstance {

    Integer requestNumberSequence
    Integer requestYear
    Integer year
    String requestNo
    @BindingFormat("dd/mm/yyyy")
    LocalDate requestDate
    String natureOfFund
    String code
    String nameAndAddress
    String repatriationBankCode
    String repatriationBankName
    String currencyCode
    BigDecimal currencyRate
    BigDecimal receivedAmountNat
    String currencyName
    BigDecimal receivedAmount
    @BindingFormat("dd/mm/yyyy")
    LocalDate receptionDate
    String countryOfOriginCode
    String countryOfOriginName
    String bankOfOriginCode
    String bankOfOriginName
    @BindingFormat("dd/mm/yyyy")
    LocalDate bankNotificationDate

    String executionRef
    @BindingFormat("dd/mm/yyyy")
    LocalDate executionDate
    @BindingFormat("dd/mm/yyyy")
    LocalDate currencyTransfertDate
    BigDecimal amountTransferred
    BigDecimal amountRemaining
    BigDecimal amountTransferredNat
    BigDecimal amountRemainingNat
    LocalDateTime lastTransactionDate
    boolean isDocumentEditable = false
    boolean isAttachmentEditable = false
    String status
    String userOwner
    String userGroup
    String storedOwner
    boolean addAttachedDocs
    boolean editAttachedDocs
    boolean isQueryOrReject
    List<RepAttachedDoc> attachedDocs
    List<ClearanceOfDom> clearances
    Integer numberAtt;
    SortedSet<RepatriationLog> logs
    Collection<Operation> operations
    Operation startedOperation
    String initialStatus
    String comments
    List<String> ecRefToBeDeleted = []
    List<LinkedHashMap> ecChanged = []

    static hasMany = [clearances: ClearanceOfDom, attachedDocs: RepAttachedDoc, operations: Operation, logs: RepatriationLog]
    static fetchMode = [attachedDocs: 'join', logs: 'join', clearances: 'join']

    static transients = ['operations', 'startedOperation', 'initialStatus', 'isDocumentEditable', 'isExecutionsEditable',
                         'isDocumentValid', 'addAttachedDocs', 'editAttachedDocs',
                         'isAssignableToFinex', 'isSelected', 'hasExecutionReference', 'isQueryOrReject', 'isAttachmentEditable', 'ecRefToBeDeleted','ecChanged']
    static rules = { [RepatriationBusinessLogicRule] }

    static constraints = {
        currencyRate(nullable: true, scale: 3)
        receivedAmount(nullable: true, scale: 2)
        receivedAmountNat(nullable: true, scale: 2)
        amountTransferred(nullable: true, scale: 2)
        amountRemaining(nullable: true, scale: 2)
        amountTransferredNat(nullable: true, scale: 2)
        amountRemainingNat(nullable: true, scale: 2)
        requestNo(nullable: true)
        requestDate(nullable: true)
        natureOfFund(nullable: true)
        code(nullable: true)
        nameAndAddress(nullable: true)
        repatriationBankCode(nullable: true)
        repatriationBankName(nullable: true)
        currencyCode(nullable: true)
        currencyName(nullable: true)
        receptionDate(nullable: true)
        countryOfOriginCode(nullable: true)
        countryOfOriginName(nullable: true)
        bankOfOriginCode(nullable: true)
        bankOfOriginName(nullable: true)
        bankNotificationDate(nullable: true)
        executionRef(nullable: true)
        executionDate(nullable: true)
        currencyTransfertDate(nullable: true)
        amountTransferred(nullable: true)
        amountRemaining(nullable: true)
        amountTransferredNat(nullable: true)
        amountRemainingNat(nullable: true)
        userGroup(nullable: true)
        storedOwner(nullable: true, blank: true)
        requestNumberSequence(nullable: true, min: 1)
        requestYear(nullable: true)
        attachedDocs(nullable: true, blank: true)
    }

    static mapping = {
        table 'REPATRIATION'
        requestNumberSequence column: 'REQ_SEQ'
        requestYear column: 'REQ_YER'
        requestNo maxSize: 11, column: 'REQUEST_NO'
        requestDate column: 'REQUEST_DATE'
        natureOfFund maxSize: 20, column: 'NAT_OF_FUND'
        code maxSize: 15
        nameAndAddress maxSize: 255
        repatriationBankCode maxSize: 5
        repatriationBankName maxSize: 35
        currencyCode maxSize: 3
        status maxSize: 20
        clearances cascade: "all-delete-orphan", fetch: 'join'
        attachedDocs cascade: "all-delete-orphan", fetch: 'join'
        receivedAmountNat column: 'RE_AMOUN_NAT'
        currencyName column: 'CUR_NAME'
        receivedAmount column: 'REC_AMOUN'
        receptionDate column: 'REC_DATE'
        countryOfOriginCode column: 'CTY_ORIG_CODE'
        countryOfOriginName column: 'CTY_ORIG_NAME'
        bankOfOriginCode column: 'BANK_ORIG_CODE'
        bankOfOriginName column: 'BANK_ORIG_NAME'
        bankNotificationDate column: 'BANK_NOTIF_DAT'
        executionRef column: 'EXEC_REF'
        executionDate column: 'EXEC_DAT'
        currencyTransfertDate column: 'CUR_TR_DAT'
        amountTransferred column: 'AMOUN_TR_DAT'
        amountRemaining column: 'AMOUN_REM'
        amountTransferredNat column: 'AMOUN_TRAN'
        amountRemainingNat column: 'AMOUN_REM_NAT'
        repatriationBankCode column: 'REP_BANK_CODE'
        repatriationBankName column: 'REP_BANK_NAME'
        comments sqlType: 'clob'
    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return FieldsConfiguration.isMandatory(fieldName, this?.startedOperation, UtilConstants.REPATRIATION)
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        def result = false
        if (!FieldsConfiguration.isProhibited(fieldName, this?.startedOperation, UtilConstants.REPATRIATION)) {
            if (FieldsConfiguration.isCustom(fieldName, this?.startedOperation, UtilConstants.REPATRIATION)) {
                result = BusinessLogicUtils.isFieldEditableRepatriation(fieldName, this?.startedOperation)
            } else {
                result = true
            }
        }
        return result
    }

    @Override
    AbstractAttachedDoc createAttachedDoc(Object params) {
        return new RepAttachedDoc(params as Map)
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

    RepAttachedDoc addAttDoc(RepAttachedDoc attDoc) {
        attDoc.repats = this
        Utils.addToCollection('attachedDocs', this, attDoc)
        attDoc
    }

    RepAttachedDoc getAttDoc(Integer rank) {
        attachedDocs?.find { it.rank == rank }
    }

    RepAttachedDoc getAttDoc(String docTypName) {
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

    ClearanceOfDom getClearanceOfDom(Integer rank, String ecReference) {
        clearances?.find {
            it.ecReference == ecReference && it.rank == rank
        }
    }

    ClearanceOfDom addClearanceOfDom(ClearanceOfDom clearanceOfDom) {
        clearanceOfDom.repats = this
        Utils.addToCollection("clearances", this, clearanceOfDom)
        clearanceOfDom
    }

    List<ClearanceOfDom> removeClearanceOfDom(ClearanceOfDom clearanceOfDom) {
        Utils.removeFromCollection(this.clearances, clearanceOfDom)
        clearances
    }

    @Override
    RepatriationLog instanceLog() {
        return new RepatriationLog()
    }

    List<ClearanceOfDom> getAllClearanceOfDom() {
        List<ClearanceOfDom> clearenceOfDomList
        if(!(isBankAgent() || isTrader() || isDeclarant())){
            clearenceOfDomList = clearances
        }else{
            clearenceOfDomList = clearances?.findAll {it.status == true}
        }
        return clearenceOfDomList
    }

}
