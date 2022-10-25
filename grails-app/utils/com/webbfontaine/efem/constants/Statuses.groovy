package com.webbfontaine.efem.constants

public interface Statuses {
    static final String ST_STORED = "Stored"
    static final String ST_REQUESTED = "Requested"
    static final String ST_PARTIALLY_APPROVED = "Partially Approved"
    static final String ST_QUERIED = "Queried"
    static final String ST_REJECTED = "Rejected"
    static final String ST_APPROVED = "Approved"
    static final String ST_CANCELLED = "Cancelled"
    static final String ST_EXPIRED = "Expired"
    static final String ST_USED = "Used"
    static final String ST_PRE_APPROVE = "Pre Approve"
    static final String ST_PRE_EXEUTE = "Pre Execute"
    static final String ST_EXECUTED = "Executed"
    static final String ST_CEDED = "Ceded"

    static final String ST_ASSESSED = "Assessed"
    static final String ST_PAID = "Paid"
    static final String ST_EXITED = "Exited"
    static final String ST_TOTALLYEXITED = "Totally exited"


    static final String ST_DECLARED = "Declared"
    static final String ST_CONFIRMED = "Confirmed"
    static final String ST_PRE_CONFIRMED = "Pre Confirmed"

    static final String ST_VALIDATED = "Validated"

    static final String ST_CLOSED = "Closed"
    static final String ST_TRANSFERRED = "Transferred"

    static final String ST_VALID = "Valid"
    static final String ST_INVALID = "Invalid"
    static final Set EXCHANGE_STATUS = [ST_STORED, ST_REQUESTED, ST_QUERIED, ST_CANCELLED, ST_REJECTED,
                                        ST_PARTIALLY_APPROVED, ST_APPROVED, ST_EXECUTED]
    static final Set REPATRIATION_STATUS = [ST_STORED,ST_DECLARED,ST_CONFIRMED,ST_QUERIED,ST_CANCELLED, ST_CEDED]

    static final Set TRANSFER_STATUS = [ST_STORED,ST_REQUESTED,ST_VALIDATED,ST_QUERIED,ST_CANCELLED]

    static final Set CURRENCYTRANSFER_STATUS = [ST_STORED,ST_TRANSFERRED,ST_CANCELLED]
    static final Set PERMITTED_STATUS_FOR_PRINT_ACTION = [ST_APPROVED, ST_EXECUTED]
    static final Set DENIED_STATUS_FOR_DRAFT_PRINT = [ST_STORED, ST_APPROVED, ST_EXECUTED]
    static final Set CHECKED_STATUS_FOR_AMOUNT_RULE = [ST_REQUESTED, ST_PARTIALLY_APPROVED, ST_APPROVED, ST_EXECUTED]
    static final Set RIMM_STATUS = [ST_VALID, ST_INVALID]
    static final Set CHECKED_STATUS_FOR_FINAL_AMOUNT = [ST_DECLARED,ST_CONFIRMED, ST_CEDED]
}






