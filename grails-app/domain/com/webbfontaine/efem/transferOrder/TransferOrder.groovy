package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.AttachedDocContainer
import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.LoggerInstance
import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachedDoc.TransferAttachedDoc
import com.webbfontaine.efem.log.TransferLog
import com.webbfontaine.efem.rules.transferOrder.TransferBusinessLogicRule
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import com.webbfontaine.grails.plugins.utils.Utils
import grails.databinding.BindingFormat
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

import static com.webbfontaine.efem.UserUtils.isSuperAdministrator

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferOrder implements ConfigurableFields, Serializable, AttachedDocContainer, LoggerInstance {

    Integer requestNumberSequence
    Integer requestYear
    String requestNo                                             //Request No
    @BindingFormat("dd/mm/yyyy")
    LocalDate requestDate                                        //Request Date
    String eaReference
    String importerCode                                          //Code Importer
    String importerNameAddress                                   //importer Name And Address
    String countryBenefBankCode                                  //countryBeneficiaryBank
    String countryBenefBankName                                  //countryBeneficiaryBank
    String destinationBank                                       //destinationBank
    String byCreditOfAccntOfCorsp                                //By credit of account of correspondent
    String bankAccntNoCredit                                     //Bank Account No credited
    String nameOfAccntHoldCredit                                 //Name of Account Holder Credited
    String bankCode                                              //Bank Code
    String bankName                                              //Bank Name
    String bankAccntNoDebited                                    //Bank Account No debited
    String charges                                               //Charges
    String currencyPayCode                                       //Currency Code Payment Code
    String currencyPayName                                        //Currency Code Payment Name
    BigDecimal ratePayment                                       //Rate Payment
    String executionRef                                          //Execution Reference
    @BindingFormat("dd/mm/yyyy")
    LocalDate executionDate                                      //Execution Date
    BigDecimal transferAmntRequested                             //Transfer Amount requested
    BigDecimal transferNatAmntRequest                             //Transfer Amount requested in Nationnal
    String amntRequestedInLetters                                //Amount in letters
    BigDecimal transferAmntExecuted                             //Transfer Amount executed
    BigDecimal transferNatAmntExecuted                         //Transfer Amount executed in Nationnal
    String amntExecutedInLetters                                 //Amount in letters
    BigDecimal amntSetInMentionedCurr
    LocalDateTime lastTransactionDate
    boolean isDocumentEditable = false
    boolean isAttachmentEditable = false
    boolean isEaOpearationsEditable = false
    String status
    String userOwner
    String userGroup
    String storedOwner
    boolean addAttachedDocs
    boolean editAttachedDocs
    boolean isQueryOrReject
    List<TransferAttachedDoc> attachedDocs
    List<OrderClearanceOfDom> orderClearanceOfDoms
    SortedSet<TransferLog> logs
    Collection<Operation> operations
    Operation startedOperation
    String initialStatus
    BigDecimal sumOfClearanceOfDoms
    static hasMany = [attachedDocs: TransferAttachedDoc, operations: Operation, logs: TransferLog,orderClearanceOfDoms:OrderClearanceOfDom]
    static fetchMode = [attachedDocs: 'join', logs: 'join',orderClearanceOfDoms:'join']

    static transients = ['operations', 'startedOperation', 'initialStatus', 'isDocumentEditable', 'isEaOpearationsEditable',
                         'isDocumentValid', 'addAttachedDocs', 'editAttachedDocs',
                         'isQueryOrReject', 'isAttachmentEditable']

    static rules = { [TransferBusinessLogicRule] }

    static constraints = {
        requestNo(nullable: true)
        requestDate(nullable: true)
        importerCode(nullable: true)
        importerNameAddress(nullable: true)
        ratePayment(nullable: true, scale: 3)
        byCreditOfAccntOfCorsp(nullable: true)
        bankAccntNoCredit(nullable: true)
        nameOfAccntHoldCredit(nullable: true)
        bankAccntNoDebited(nullable: true)
        charges(nullable: true)
        countryBenefBankCode(nullable: true)
        countryBenefBankName(nullable: true)
        destinationBank(nullable: true)
        bankCode(nullable: true)
        bankName(nullable: true)
        currencyPayCode(nullable: true)
        transferAmntRequested(nullable: true, scale: 2)
        transferAmntExecuted(nullable: true, scale: 2)
        transferNatAmntRequest(nullable: true, scale: 2)
        transferNatAmntExecuted(nullable: true, scale: 2)
        amntRequestedInLetters(nullable: true)
        amntExecutedInLetters(nullable: true)
        executionRef(nullable: true)
        executionDate(nullable: true)
        userGroup(nullable: true)
        storedOwner(nullable: true, blank: true)
        requestNumberSequence(nullable: true, min: 1)
        requestYear(nullable: true)
        attachedDocs(nullable: true, blank: true)
        orderClearanceOfDoms(nullable: true, blank: true)
        amntSetInMentionedCurr(nullable: true, scale: 2)
    }

    static mapping = {
        table 'TRANSFER_ORDER'
        requestNumberSequence column: 'REQ_SEQ'
        requestYear column: 'REQ_YER'
        requestNo maxSize: 11, column: 'REQUEST_NO'
        requestDate column: 'REQUEST_DATE'
        importerCode maxSize: 17, column: 'IMP_CODE'
        importerNameAddress sqlType: 'clob', column: 'IMP_NAME_ADDR'
        ratePayment column: 'RATE_PAYMENT'
        countryBenefBankCode maxSize: 2
        countryBenefBankName maxSize: 35
        destinationBank maxSize: 100
        bankCode maxSize: 5
        bankName maxSize: 35
        currencyPayCode maxSize: 3, column: 'CURR_COD_PAY'
        currencyPayName maxSize: 35, column: 'CURR_NAME_PAY'
        transferAmntRequested column: 'TRANSF_AMT_REQSTED'
        transferNatAmntRequest column: 'TRANSF_NAT_AMT_REQSTED'
        transferAmntExecuted column: 'TRANSF_AMT_EXECTED'
        transferNatAmntExecuted column: 'TRANSF_NAT_AMT_EXECTED'
        amntRequestedInLetters column: 'AMT_REQSTED_LETTER'
        amntExecutedInLetters column: 'AMT_EXECTED_LETTER'
        executionRef column: 'EXEC_REF'
        executionDate maxSize: 35,column: 'EXEC_DAT'
        status maxSize: 20, column: 'STATUS'
        userGroup column: 'USER_GROUP'
        userOwner column: 'USER_OWNER'
        storedOwner column: 'STORE_OWNER'
        bankAccntNoDebited maxSize: 34
        bankAccntNoCredit maxSize: 34
        charges maxSize: 65
        amntSetInMentionedCurr column: 'AMT_SET_MENT_CUR'
        attachedDocs cascade: "all-delete-orphan", fetch: 'join'
        orderClearanceOfDoms cascade: "all-delete-orphan", fetch: 'join'
        sumOfClearanceOfDoms column: 'SUM_OF_CLEAREANCE'
        sumOfClearanceOfDoms formula: '((SELECT NVL(SUM(CL.AMOUNT_SETTLED),0) FROM TRANSFER_ORDER_CLEARANCE CL WHERE CL.TRANSFER_ID = ID))'
    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return FieldsConfiguration.isTransferFieldsMandatory(fieldName, this?.startedOperation)
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        def result = false
        if (!FieldsConfiguration.isTransferFieldsProhibited(fieldName, this?.startedOperation)) {
            if(FieldsConfiguration.isTransferFieldsCustom(fieldName, this?.startedOperation)){
                result = BusinessLogicUtils.isFieldEditableTransferOrder(fieldName, this?.startedOperation)
            }else{
                result = true
            }
        }
        return result
    }

    void setIsDocumentEditable(boolean editable) {
        this.isDocumentEditable = editable
    }

    boolean isDocumentEditable() {
        return this.isDocumentEditable
    }

    boolean isEaOpearationsEditable() {
        return this.isEaOpearationsEditable
    }

    void setIsAttachmentEditable(boolean editable) {
        this.isAttachmentEditable = editable
    }

    boolean isAttachmentEditable() {
        return this.isAttachmentEditable
    }

    TransferAttachedDoc addAttDoc(TransferAttachedDoc attDoc) {
        attDoc.transfert = this
        Utils.addToCollection('attachedDocs', this, attDoc)
        attDoc
    }

    TransferAttachedDoc getAttDoc(Integer rank) {
        attachedDocs?.find { it.rank == rank }
    }

    TransferAttachedDoc getAttDoc(String docTypName) {
        attachedDocs?.find {
            it.docTypName == docTypName
        }
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

    OrderClearanceOfDom getOrderClearanceOfDoms(Integer orderNumber) {
        orderClearanceOfDoms?.find { it.rank == orderNumber }
    }

    OrderClearanceOfDom addOrderClearanceOfDoms(OrderClearanceOfDom orderClearanceOfDom) {
        orderClearanceOfDom.transfer = this
        Utils.addToCollection('orderClearanceOfDoms', this, orderClearanceOfDom)
        orderClearanceOfDom
    }


    List<OrderClearanceOfDom> removeOrderClearanceOfDoms(OrderClearanceOfDom orderClearanceOfDom) {
        orderClearanceOfDoms = Utils.removeFromCollection(orderClearanceOfDoms.toList(), orderClearanceOfDom)
        orderClearanceOfDoms
    }

    OrderClearanceOfDom getClearanceOfDom(Integer rank, String eaReference) {
        orderClearanceOfDoms?.find {
            it.eaReference == eaReference && it.rank == rank
        }
    }

    OrderClearanceOfDom getClearanceOfDomByRef(String eaReference) {
        orderClearanceOfDoms?.find {
            it.eaReference == eaReference
        }
    }
    
    @Override
    AbstractAttachedDoc createAttachedDoc(Object params) {
        return new TransferAttachedDoc(params as Map)
    }

    @Override
    TransferLog instanceLog() {
        return new TransferLog()
    }

    List<OrderClearanceOfDom> getAllOrderClearanceOfDoms() {
        def criteriaCheck = {
            it?.state == '0' || !it?.state
        }
        def clearenceOfDomList = orderClearanceOfDoms?.findAll {criteriaCheck}
        if(isSuperAdministrator()){
            clearenceOfDomList = orderClearanceOfDoms
        }
        return clearenceOfDomList
    }

}
