package com.webbfontaine.efem.constants

import static com.webbfontaine.efem.workflow.Operation.*

final class ExchangeRequestType {
    static String  EA = "EA"
    static String  EC = "EC"
    static String  EA_FROM_TVF = "EA"
    static String  EA_FROM_SAD = "EA"
    static String  EC_FROM_SAD = "EC"
    static String  BASE_ON_TVF="TVF"
    static String  BASE_ON_SAD="SAD"
    static Title_Names =["EA":"exchange.create.ea.label","EC":"exchange.create.ec.label","TVF":"exchange.create.tvf.label","SAD":"exchange.create.sad.label"]
    static Status_Labels =["Requested":"label-warning","Queried": "label-warning","Cancelled":"label-danger","Rejected" :"label-danger","Partially Approved" :"label-success",
                          "Approved" :"label-success","Executed" :"label-success"]
    static String ENGAGEMENT_DE_CHANGE_PROVISOIRE = "EngagementDeChangeProvisoire"
    static String ENGAGEMENT_DE_CHANGE = "EngagementDeChange"
    static String ATTESTATION_EXPORTATION = "AttestationD'Exportation"
    static String AE = "AE"

    static String EXECUTION_INITIAL_STATE = "0"
    static String EXECUTION_CANCEL_STATE = "1"

    static String DEC = "DEC"
    static invoiceCodes = ["0007","0008"]
    static invoiceOnly = ["0007"]
    static operationCodes_EA = ["EA001","EA002","EA003","EA004","EA005","EA006"]
    static operationCodes_EC = ["EC001","EC002","EC003"]
    static String authorizedBy = "LES FINEX"
    static String departmentInCharg = "FINEX"
    static Integer maxLengthAmount = 19
    static BigDecimal MINIMUN_AMOUNT = 500000
    static BigDecimal MAXIMUM_AMOUNT = 10000000
    static String DEVICE_XOF = "XOF"
    static String CODE_FACTURE = "0007"
    static String CODE_PROFORMA = "0008"
    static EXCEPTION_CURRENCY = ["EUR","XOF"]
    static EXCEPTION_COUNTRY = ["BJ","BF","GW","ML","NE","SN","TG"]
    static EXCEPTION_AREA = ["001","002"]
    static AREA_003 = "003"
    static AREA_002 = "002"
    static AREA_001 = "001"
    static CURRENCY_XOF = "XOF"
    static EXCEPTION_XOF_CURRENCY = ["XOF"]
    static CI_CURRENCY = ["CI"]
    static CI = 'CI'
    static Local = 'Local'
    static DEC_OPERATIONS = ["CN"]
    static BANK_OPERATIONS = ["QR","CN","UA","UE"]
    static GOV_OPERATIONS =["QR", "CN"]
    static FR = 'fr'
    static FRENCH_SEPARATOR = 'virgule'
    static ENGLISH_SEPARATOR = 'point'
    static ZERO_VALUE = '00'
    static String UNCLEARED_OPERTYPE = 'EA007'
    static String AVD_EXIST_COD = '900'
    static REGISTRATION_BANK_FIELDS = ['registrationNumberBank', 'registrationDateBank']
    static String EA006_TRANSACTION = 'EA006'
    static String EA005_TRANSACTION = 'EA005'
    static String OPERATION_WITH_EXPORT_TITLE  = "EC002"
    static String CODE_AUTHORIZATION_LETTER_6057 = '6057'
    static String CODE_AUTHORIZATION_LETTER_6059 = '6059'
    static String CODE_OTHERS_6058 = '6058'
    static INCOTERMS_AUTHORIZED = ["DAT","DAP","DDP"]
    static STATUS_LABELS_CURRENCY_TRANSFER = ["Stored" : "label-warning","Cancelled":"label-danger","Transferred" :"label-success"]
    static STATUS_LABELS_TRANSFER_ORDER = ["Stored" : "label-warning", "Requested":"label-warning","Queried": "label-warning","Cancelled":"label-danger","Validated" :"label-success"]
    static STATUS_LABELS_REPATRIATION = ["Stored" : "label-warning", "Declared":"label-warning","Queried": "label-warning","Cancelled":"label-danger","Confirmed" :"label-success","Ceded" :"label-success"]

    static allOpViews = [VIEW_STORED, VIEW_REQUESTED, VIEW_QUERIED, VIEW_APPROVED, VIEW_CANCELLED, VIEW_REJECTED, VIEW_PARTIALLY_APPROVED, VIEW_EXECUTED]
    static declarantOperations = [STORE, REQUEST, UPDATE_STORED, REQUEST_STORED, REQUEST_QUERIED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED, CANCEL_QUERIED]
    static traderOperations = [STORE, REQUEST, UPDATE_STORED, REQUEST_STORED, REQUEST_QUERIED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED, CANCEL_QUERIED, REQUEST_TRANSFER_ORDER]
    static bankAgentOperations = [QUERY_REQUESTED, REJECT_REQUESTED, EXECUTE_APPROVED, CANCEL_APPROVED, DOMICILIATE, APPROVE_REQUESTED, REQUEST_TRANSFER_ORDER]
    static govOfficerOperations = [QUERY_PARTIALLY_APPROVED,APPROVE_PARTIALLY_APPROVED, REJECT_PARTIALLY_APPROVED, CANCEL_APPROVED, PARTIALLY_APPROVED,REJECT_REQUESTED,QUERY_REQUESTED,APPROVE_REQUESTED]

    static String AMOUNT_IN_NATIONAL_CURRENCY  = "amountNationalCurrency"
    static String AMOUNT_IN_MENTIONNED_CURRENCY  = "amountMentionedCurrency"
}


