package com.webbfontaine.efem.constants
import static com.webbfontaine.efem.workflow.Operation.*

class RepatriationRequestType {
    static String  EA = "EA"
    static String  EC = "EC"
    static String  EA_FROM_TVF = "EA"
    static String  EA_FROM_SAD = "EA"
    static String  EC_FROM_SAD = "EC"
    static String  BASE_ON_TVF="TVF"
    static String  BASE_ON_SAD="SAD"
    static Title_Names =["EA":"exchange.create.ea.label","EC":"exchange.create.ec.label","TVF":"exchange.create.tvf.label","SAD":"exchange.create.sad.label"]
    static Status_Labels =["Requested":"label-warning","Queried": "label-warning","Cancelled":"label-important","Rejected" :"label-important","Partially Approved" :"label-success",
                           "Approved" :"label-success","Executed" :"label-success"]

    static String DEC = "DEC"
    static invoiceCodes = ["0007","0008"]
    static invoiceOnly = ["0007"]
    static operationCodes_EA = ["EA001","EA002","EA003","EA004"]
    static operationCodes_EC = ["EC001","EC002","EC003"]
    static String authorizedBy = "LES FINEX"
    static String departementInCharg = "FINEX"
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

    static allOpViews = [VIEW_STORED, VIEW_CONFIRMED,VIEW_DECLARED, VIEW_QUERIED, VIEW_CANCELLED, VIEW_CEDED]

    static traderOperations = [STORE,UPDATE_STORED,DECLARE_STORED, DECLARE,DECLARE_QUERIED,UPDATE_QUERIED,UPDATE_CONFIRMED, CANCEL_QUERIED, UPDATE_CLEARANCE]

    static bankAgentOperations =[STORE,UPDATE_STORED, DECLARE_STORED,DECLARE,QUERY_DECLARED,CONFIRM,CONFIRM_DECLARED,CANCEL_CONFIRMED,UPDATE_CONFIRMED, CONFIRM_STORED, REQUEST_CURRENCY_TRANSFER, UPDATE_CLEARANCE]

    static govOfficerOperations = [QUERY_DECLARED,CANCEL_QUERIED]

}


