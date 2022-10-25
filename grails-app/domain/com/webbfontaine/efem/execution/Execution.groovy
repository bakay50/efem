package com.webbfontaine.efem.execution

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.rules.execution.ExecutionBusinessLogicRule
import com.webbfontaine.grails.plugins.taglibs.ConfigurableFields
import org.joda.time.LocalDate

class Execution implements ConfigurableFields, Serializable{

    Integer rank
    String executionReference
    LocalDate executionDate
    String currencyExCode
    String currencyExName
    BigDecimal currencyExRate
    BigDecimal amountMentionedExCurrency
    BigDecimal amountNationalExCurrency
    BigDecimal amountSettledNationalExCurrency
    BigDecimal amountSettledMentionedCurrency
    String countryProvenanceDestinationExCode
    String countryProvenanceDestinationExName
    String provenanceDestinationExBank
    String bankAccountNumberCreditedDebited
    String amountInExLetter
    String amountInSettledLetter
    String accountExBeneficiary
    String creditCorrespondentAccount
    String creditForeignCfaOrEuro
    String accountOwnerCredited
    String executingBankCode
    String executingBankName
    String executionDomNumber
    LocalDate executionDomDate
    String executionDomBankCode
    String executionDomBankName
    String state

    static belongsTo = [exchange:Exchange]

    static rules = {[ExecutionBusinessLogicRule]}

    static mapping = {
        executionReference maxSize :35
        currencyExCode maxSize :3
        currencyExName maxSize :35
        countryProvenanceDestinationExCode maxSize :2
        countryProvenanceDestinationExName maxSize :35
        provenanceDestinationExBank maxSize :100
        bankAccountNumberCreditedDebited maxSize :34
        accountExBeneficiary maxSize :35
        creditCorrespondentAccount maxSize :15
        creditForeignCfaOrEuro maxSize :35
        accountOwnerCredited maxSize :35
        amountSettledNationalExCurrency column: 'AMT_SET_NAT_EX_CUR'
        amountSettledMentionedCurrency column: 'AMT_SET_MEN_CUR'
        countryProvenanceDestinationExCode column: 'CTY_PROV_DES_EX_COD'
        countryProvenanceDestinationExName column: 'CTY_PROV_DES_EX_NAM'
        provenanceDestinationExBank column: 'DESTINATION_BANK'
        bankAccountNumberCreditedDebited column: 'BNK_ACT_NO_CRED_DEB'
        executingBankCode column: 'EXECUTING_BANK_CODE'
        executingBankName column: 'EXECUTING_BANK_NAME'
        executionDomDate column: 'EXECUTION_DOM_DAT'
        executionDomNumber maxSize: 35, column: 'EXECUTION_DOM_NUMBER'
        executionDomBankCode maxSize: 7, column: 'EXECUTION_DOM_COD'
        executionDomBankName maxSize: 35, column: 'EXECUTION_DOM_NAM'
        state defaultValue: "0", maxSize: 10
    }

    static constraints = {
        executionReference nullable: false, blank: false
        executionDate nullable: false
        currencyExCode nullable: false, blank: false
        amountMentionedExCurrency nullable: false
        provenanceDestinationExBank  nullable: false, blank: false
        bankAccountNumberCreditedDebited nullable: false, blank: false
        amountMentionedExCurrency(nullable: false, min: BigDecimal.ZERO, scale: 3)
        amountNationalExCurrency(nullable: false, min: BigDecimal.ZERO, scale: 3)
        amountSettledMentionedCurrency(nullable: false, min: BigDecimal.ZERO, scale: 3)
        amountSettledNationalExCurrency(nullable: true, min: BigDecimal.ZERO, scale: 3)
        currencyExRate(nullable: true, min: BigDecimal.ZERO, scale: 3)
        countryProvenanceDestinationExCode nullable: true, blank: true
        accountExBeneficiary nullable: true, blank: true
        creditCorrespondentAccount nullable: true, blank: true
        creditForeignCfaOrEuro nullable: true, blank: true
        accountOwnerCredited nullable: true, blank: true

        executingBankCode nullable: true, blank: true
        executingBankName nullable: true, blank: true
        executionDomNumber nullable: true, blank: true
        executionDomDate nullable: true, blank: true
        executionDomBankCode nullable: true, blank: true
        executionDomBankName nullable: true, blank: true
        state nullable: true
    }

    @Override
    boolean isFieldMandatory(String fieldName) {
        return true
    }

    @Override
    boolean isFieldEditable(String fieldName) {
        return true
    }

}
