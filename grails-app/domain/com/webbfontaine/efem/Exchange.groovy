package com.webbfontaine.efem
import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.log.ExchangeLog
import com.webbfontaine.efem.rules.exchange.CheckExchangeAuthorizedUserRule
import com.webbfontaine.efem.rules.exchange.ExchangeBusinessLogicRule
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import com.webbfontaine.grails.plugins.utils.Utils
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime


class Exchange implements ConfigurableFields, Serializable, AttachedDocContainer, LoggerInstance {

    Integer requestNumberSequence
    Integer requestYear

    String requestNo
    LocalDate requestDate
    Integer tvfNumber
    LocalDate tvfDate
    String clearanceOfficeCode
    String clearanceOfficeName
    String declarationSerial
    String declarationNumber
    String declarationID
    String importFrom
    LocalDate declarationDate
    String bankCode
    String bankName
    String registrationNumberBank
    LocalDate registrationDateBank
    String countryOfDestinationCode
    String countryOfDestinationName

    String domiciliationNumber
    LocalDate domiciliationDate
    String domiciliationBankCode
    String domiciliationBankName
    LocalDate authorizationDate
    LocalDate expirationDate
    LocalDate bankApprovalDate
    LocalDate dateOfBoarding
    String comments
    String status
    String requestType
    String basedOn
    Integer year
    String exporterCode
    String exporterNameAddress
    String importerCode
    String importerNameAddress
    String declarantCode
    String declarantNameAddress
    String operType
    String operTypeName
    String geoArea
    String geoAreaName
    String currencyCode
    String currencyName
    String currencyPayCode
    String currencyPayName
    BigDecimal currencyRate
    BigDecimal currencyPayRate
    BigDecimal amountMentionedCurrency
    BigDecimal amountNationalCurrency
    BigDecimal balanceAs
    BigDecimal goodsValuesInXOF
    BigDecimal exFeesPaidByExpInCIinXOF
    BigDecimal exFeesPaidByExpInAbroadinXOF

    String countryProvenanceDestinationCode
    String countryProvenanceDestinationName
    String provenanceDestinationBank
    String bankAccountNocreditedDebited
    String exportationTitleNo
    String amountInLetter
    String authorizedBy
    String evaluationReport
    Integer treatmentLevel
    String nationalityCode
    String nationalityName
    String resident
    String accountNumberBeneficiary
    String beneficiaryAddress
    String beneficiaryName
    String beneficiaryNameAddress
    LocalDate approvalDate
    LocalDateTime lastTransactionDate
    String departmentInCharge
    String userOwner
    String userGroup
    String operation

    boolean isFinalAmount = false

    String countryOfExportCode
    String countryOfExportName

    String commentHeader

    BigDecimal remainingBalanceDeclaredAmount
    BigDecimal remainingBalanceDeclaredNatAmount
    BigDecimal remainingBalanceTransferDoneAmount
    BigDecimal remainingBalanceTransferDoneNatAmount

    boolean isDocumentEditable
    boolean isExecutionsEditable = true
    boolean isAttachmentEditable = true
    boolean isSupDeclarationEditable = true
    boolean isSadOwner = true
    boolean isDocumentValid = true
    boolean isDocumentApprovable
    boolean addAttachedDocs
    boolean editAttachedDocs
    boolean isAssignableToFinex
    boolean isSelected
    boolean hasExecutionReference
    boolean isQueryOrReject
    boolean isReleaseOrder = false

    BigDecimal finalAmount
    BigDecimal finalAmountInDevise
    BigDecimal finalCurrencyRate
    String finalCurrencyCode
    String amountFinalInLetter

    String approvedBy
    String requestedBy

    Integer numberOfexecutions
    Integer numberAtt

    List<AttachedDoc> attachedDocs
    List<Execution> executions
    List<SupDeclaration> supDeclarations
    SortedSet<ExchangeLog> logs

    Collection<Operation> operations
    Operation startedOperation
    String initialStatus

    Tvf tvf
    String tvfInstanceId

    String sadInstanceId
    BigDecimal sadInvoiceAmountInForeignCurrency
    String sadStatus
    String sadTypeOfDeclaration
    BigDecimal totalAmountMentionedExCurrency
    BigDecimal totalAmountOfCif
    BigDecimal cifAmtFcx
    BigDecimal convertedCif
    String sadIncoterm

    Integer registrationNumberSequence
    boolean specialImporter = false
    Boolean isTvfUsable = null

    String attachedDocCodeSad
    String attachedDocReferenceSad
    LocalDate dateSad

    BigDecimal totalAmountSettledInCurrency
    BigDecimal totalAmountSettled
    String totalAmountSettledInLetter
    String areaPartyCode
    String areaPartyName
    String countryPartyCode
    String countryPartyName

    static transients = ['operations', 'startedOperation', 'initialStatus', 'isSupDeclarationEditable', 'isSadOwner', 'isDocumentEditable', 'isExecutionsEditable',
                         'isDocumentValid', 'totalAmountMentionedExCurrency', 'isTvfUsable', 'isDocumentApprovable', 'addAttachedDocs', 'editAttachedDocs',
                        'isAssignableToFinex', 'isSelected', 'hasExecutionReference', 'isQueryOrReject', 'isAttachmentEditable', 'totalAmountSettled', 'totalAmountSettledInCurrency', 'totalAmountSettledInLetter', 'cifAmtFcx']
    static hasMany = [attachedDocs: AttachedDoc, executions: Execution, supDeclarations: SupDeclaration, logs: ExchangeLog]
    static fetchMode = [attachedDocs: 'join', logs: 'join', executions: 'join', supDeclarations: 'join']

    static rules = { [ExchangeBusinessLogicRule, CheckExchangeAuthorizedUserRule] }

    static constraints = {

        declarantCode maxSize: 17
        amountMentionedCurrency(nullable: false, scale: 3)
        balanceAs(nullable: false, scale: 3)
        beneficiaryName(nullable: true, blank: true)
        beneficiaryAddress(nullable: true, blank: true)
        currencyRate(nullable: true, scale: 3)
        currencyPayRate(nullable: true, scale: 3)
        currencyPayCode(nullable: true, blank: true)
        currencyPayName(nullable: true, blank: true)
        amountNationalCurrency(nullable: false, scale: 3)
        requestNumberSequence(nullable: true, min: 1)
        registrationNumberSequence(nullable: true, min: 1)
        requestYear(nullable: true)
        goodsValuesInXOF(nullable: true, scale: 3)
        exFeesPaidByExpInCIinXOF(nullable: true, scale: 3)
        exFeesPaidByExpInAbroadinXOF(nullable: true, scale: 3)
        countryOfExportCode(nullable: true, blank: true)
        countryOfExportName(nullable: true, blank: true)
        bankApprovalDate(nullable: true, blank: true)
        dateOfBoarding(nullable: true, blank: true)
        finalAmount(nullable: true, blank: true, scale: 3)
        finalAmountInDevise(nullable: true, blank: true, scale: 3)
        finalCurrencyRate(nullable: true, blank: true, scale: 3)
        finalCurrencyCode(nullable: true, blank: true)
        requestedBy(nullable: true, blank: true)
        attachedDocCodeSad(nullable: true)
        attachedDocReferenceSad(nullable: true)
        dateSad(nullable: true)
    }

    static mapping = {
        requestNumberSequence column: 'REQ_SEQ'
        requestYear column: 'REQ_YER'

        requestNo column: 'REQUEST_NO', index: 'EXCHANGE_REF_NUMBER_IDX', unique: true
        requestDate column: 'REQUEST_DATE'
        dateOfBoarding column: 'BOARD_DATE'

        clearanceOfficeCode maxSize: 5, column: 'CLERANCE_OFFICE_CODE'
        clearanceOfficeName maxSize: 35, column: 'CLERANCE_OFFICE_NAME'
        declarationSerial maxSize: 1

        registrationNumberBank maxSize: 35
        domiciliationNumber maxSize: 35
        domiciliationBankCode maxSize: 7
        domiciliationBankName maxSize: 35
        status maxSize: 20
        requestType maxSize: 2
        basedOn maxSize: 3
        exporterCode maxSize: 17
        importerCode maxSize: 17
        declarantCode maxSize: 17
        operType maxSize: 25
        operTypeName maxSize: 255
        geoArea maxSize: 5
        geoAreaName maxSize: 75
        currencyCode maxSize: 3
        currencyName maxSize: 35
        currencyPayCode maxSize: 3
        currencyPayName maxSize: 35
        nationalityCode column: 'NATIONALITY', maxSize: 30
        nationalityName maxSize: 35
        resident maxSize: 3
        countryProvenanceDestinationCode maxSize: 2, column: 'countProvDestCode'
        countryProvenanceDestinationName maxSize: 35, column: 'countProvDestName'
        countryOfDestinationCode maxSize: 2, column: 'countDestCode'
        countryOfDestinationName maxSize: 35, column: 'countDestName'
        provenanceDestinationBank maxSize: 100
        bankAccountNocreditedDebited maxSize: 34, column: 'bnkAccNumCredDebited'
        accountNumberBeneficiary maxSize: 34, column: 'accountBenef'
        beneficiaryAddress sqlType: 'clob', column: 'BENEF_ADDR'
        beneficiaryName maxSize: 255, column: 'BENEF_NAME'
        declarationID column: 'DECLARATION_ID'
        importFrom column: 'IMPORT_FROM'
        commentHeader sqlType: 'clob', column: 'COMMENT_HEADER'

        goodsValuesInXOF column: 'GOODS_VALUES_IN_XOF'
        exFeesPaidByExpInCIinXOF column: 'EX_FEES_PAID_BY_EXP_CI'
        exFeesPaidByExpInAbroadinXOF column: 'EX_FEES_PAID_BY_EXP_ABROAD'

        exportationTitleNo maxSize: 35
        departmentInCharge maxSize: 5
        comments sqlType: 'clob'

        exporterNameAddress sqlType: 'clob'
        importerNameAddress sqlType: 'clob'
        declarantNameAddress sqlType: 'clob'
        evaluationReport sqlType: 'clob'

        countryOfExportCode column: 'CNTRY_EXP_COD'
        countryOfExportName column: 'CNTRY_EXP_NAM'

        finalAmount column: 'FINAL_AMOUNT'
        finalAmountInDevise column: 'FINAL_AMOUNT_DEVISE'
        finalCurrencyRate column: 'FINAL_CURR_RATE'
        finalCurrencyCode column: 'FINAL_CURR_CODE'

        approvedBy column: 'APPROVED_BY'
        requestedBy column: 'REQUESTED_BY'

        sadInstanceId column: 'SAD_INSTANCE_ID'
        sadInvoiceAmountInForeignCurrency column: 'SAD_INV_AMT_FCX'
        sadStatus column: 'SAD_STATUS'
        sadTypeOfDeclaration column: 'SAD_TYP_DEC'

        totalAmountOfCif column: 'SAD_TTL_AMT_CIF'
        convertedCif column: 'CONVERTED_CIF'
        sadIncoterm column: 'SAD_INCO_TERM'

        attachedDocCodeSad column: 'SAD_ATT_COD'
        attachedDocReferenceSad column: 'SAD_ATT_REF'
        dateSad column: 'SAD_ATT_DAT'
        isFinalAmount column:'IS_FINAL_AMOUNT'
        isReleaseOrder column: 'IS_RELEASE_ORDER'

        registrationNumberSequence column: 'REG_SEQ'

        attachedDocs  cascade: "all-delete-orphan", lazy: false
        executions cascade: "all-delete-orphan", lazy: false
        supDeclarations lazy: false
        logs lazy: false
        tvf lazy: false

        remainingBalanceDeclaredAmount column: 'DEC_RMG_BAL_AMT'
        remainingBalanceDeclaredNatAmount column: 'DEC_RMG_BAL_AMT_NAT'
        remainingBalanceTransferDoneAmount column: 'DEC_RMG_BAL_TRANS_AMT'
        remainingBalanceTransferDoneNatAmount column: 'DEC_RMG_BAL_TRANS_AMT_NAT'
        areaPartyCode column: 'AREA_CODE', maxSize: 5
        areaPartyName column: 'AREA_NAME', maxSize: 75
        countryPartyCode column: 'COUNTRY_CODE', maxSize: 5
        countryPartyName column: 'COUNTRY_NAME', maxSize: 75
        specialImporter column: 'SPECIAL_IMPORTER'

    }

    boolean isAttachmentEditable() {
        return this.isAttachmentEditable
    }

    AttachedDoc getAttDoc(Integer rank) {
        attachedDocs?.find { it.rank == rank }
    }

    AttachedDoc addAttDoc(AttachedDoc attDoc) {
        attDoc.exchange = this
        Utils.addToCollection('attachedDocs', this, attDoc)
        attDoc
    }

    List removeFromList(List list, domain) {
        Utils.removeFromCollection(list, domain)
    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return FieldsConfiguration.isMandatory(fieldName, this?.startedOperation)
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        def result = false
        if (!FieldsConfiguration.isProhibited(fieldName, this?.startedOperation)) {
            if (FieldsConfiguration.isCustom(fieldName, this?.startedOperation)) {
                result = BusinessLogicUtils.isFieldEditable(fieldName, this)
            } else {
                result = true
                if (ExchangeRequestType.REGISTRATION_BANK_FIELDS.contains(fieldName)) {
                    result = BusinessLogicUtils.isFieldEditable(fieldName, this)
                }
            }
        }
        return result
    }

    @Override
    AbstractAttachedDoc createAttachedDoc(Object params) {
        return new AttachedDoc(params as Map)
    }

    void setIsExecutionEditable(boolean editable) {
        this.isExecutionsEditable = editable
    }

    boolean isExecutionEditable() {
        return this.isExecutionsEditable
    }

    Execution getExecution(Integer executionNumber) {
        executions?.find { it.rank == executionNumber }
    }

    Execution addExecution(Execution execution) {
        execution.exchange = this
        Utils.addToCollection('executions', this, execution)
        execution
    }

    List<Execution> removeExecution(Execution execution) {
        executions = Utils.removeFromCollection(executions.toList(), execution)
        numberOfexecutions = executions?.size()
        executions
    }

    void setIsSupDeclarationEditable(boolean editable) {
        this.isSupDeclarationEditable = editable
    }

    boolean isSupDeclarationEditable() {
        return this.isSupDeclarationEditable
    }

    void defineSpecialImporter(String importerCode){
        this.specialImporter = importerCode.equalsIgnoreCase(UtilConstants.SPECIAL_IMPORTER_CODE)
    }

    SupDeclaration getSupDeclaration(Integer supDeclarationNumber) {
        supDeclarations?.find { it.rank == supDeclarationNumber }
    }

    SupDeclaration addSupDeclaration(SupDeclaration supDeclaration) {
        supDeclaration.exchange = this
        Utils.addToCollection('supDeclarations', this, supDeclaration)
        supDeclaration
    }

    List<SupDeclaration> removeExecution(SupDeclaration supDeclaration) {
        supDeclarations = Utils.removeFromCollection(supDeclarations.toList(), supDeclaration)
        supDeclarations
    }

    @Override
    ExchangeLog instanceLog() {
        return new ExchangeLog()
    }

}
