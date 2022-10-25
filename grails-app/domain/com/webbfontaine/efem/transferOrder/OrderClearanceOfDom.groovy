package com.webbfontaine.efem.transferOrder

import com.webbfontaine.efem.rules.transferOrder.checking.OrderClearanceAmountRule
import grails.databinding.BindingFormat
import org.joda.time.LocalDate

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Bakayoko Abdoulaye
 * Date: 14/04/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class OrderClearanceOfDom {
    Integer rank
    String eaReference
    @BindingFormat("dd/mm/yyyy")
    LocalDate authorizationDate
    String registrationNoBank
    @BindingFormat("dd/mm/yyyy")
    LocalDate registrationDateBank
    BigDecimal amountToBeSettledMentionedCurrency
    BigDecimal amountRequestedMentionedCurrency
    BigDecimal amountSettledMentionedCurrency
    String bankCode
    String bankName
    String state
    static belongsTo = [transfer: TransferOrder]
    static rules = { [OrderClearanceAmountRule] }

    static mapping = {
        table 'TRANSFER_ORDER_CLEARANCE'
        registrationDateBank column: 'REG_DATE_BANK'
        registrationNoBank column: 'REG_NO_BANK'
        amountToBeSettledMentionedCurrency column: 'AMOUNT_TOBE_SETTLED'
        amountRequestedMentionedCurrency column: 'AMOUNT_REQUESTED', defaultValue: null
        amountSettledMentionedCurrency column: 'AMOUNT_SETTLED', defaultValue: null
        authorizationDate column: 'AUTH_DATE'
        eaReference maxSize: 20
        state defaultValue: "0"
    }
    static constraints = {
        bankCode(nullable: true)
        bankName(nullable: true)
        state(nullable: true)
        amountSettledMentionedCurrency(nullable: true)
        amountRequestedMentionedCurrency(nullable: true)
    }
}
