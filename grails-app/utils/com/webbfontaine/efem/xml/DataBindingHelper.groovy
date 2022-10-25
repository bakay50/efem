package com.webbfontaine.efem.xml

import static com.webbfontaine.efem.constants.UtilConstants.EXCHANGE
import static com.webbfontaine.efem.constants.UtilConstants.REPATRIATION

class DataBindingHelper {

    public static final REPATRIATION_ACCEPTABLE_FIELDS_FOR_EXPORT = ['requestNo', 'requestDate', 'natureOfFund', 'code', 'nameAndAddress',
                                                                  'repatriationBankCode', 'repatriationBankName', 'currencyCode', 'currencyRate', 'receivedAmountNat',
                                                                  'currencyName', 'receivedAmount', 'receptionDate', 'countryOfOriginCode', 'countryOfOriginName', 'bankOfOriginCode',
                                                                  'bankOfOriginName', 'bankNotificationDate', 'executionRef',
                                                                  'executionDate', 'currencyTransfertDate', 'amountTransferred', 'amountRemaining','amountTransferredNat',
                                                                  'amountRemainingNat']

    public static final EXCHANGE_ACCEPTABLE_FIELDS_FOR_EXPORT = ['requestType', 'basedOn', 'clearanceOfficeCode', 'declarationSerial', 'declarationNumber', 'clearanceOfficeName',
                                                                     'declarationDate', 'tvfNumber', 'tvfDate', 'dateOfBoarding', 'geoArea', 'geoAreaName', 'countryOfDestinationCode',
                                                                     'countryOfDestinationName', 'countryOfExportCode', 'bankCode', 'bankName', 'domiciliationNumber', 'domiciliationDate', 'domiciliationBankCode',
                                                                     'authorizationDate', 'authorizedBy', 'commentHeader','exporterCode', 'exporterNameAddress',
                                                                     'importerCode', 'importerNameAddress', 'declarantNameAddress', 'declarantCode','beneficiaryName', 'beneficiaryAddress',
                                                                     'nationalityCode', 'resident', 'operType', 'operTypeName', 'currencyCode', 'currencyName','currencyRate',
                                                                     'currencyPayCode', 'currencyPayName', 'currencyPayRate', 'isFinalAmount', 'finalAmountInDevise', 'finalAmount', 'goodsValuesInXOF', 'exFeesPaidByExpInCIinXOF','exFeesPaidByExpInAbroadinXOF',
                                                                     'amountMentionedCurrency', 'amountNationalCurrency', 'balanceAs', 'countryProvenanceDestinationCode',
                                                                     'provenanceDestinationBank','bankAccountNocreditedDebited', 'exportationTitleNo','accountNumberBeneficiary']


    public static final DECIMAL_FIELDS_TO_REFORMAT = []

    public static final ITEM_CLEARANCES_OF_DOM_ACCEPTABLE_FIELDS_FOR_EXPORT = ['rank', 'ecReference', 'ecDate', 'domiciliaryBank', 'domiciliationNo', 'domiciliationDate', 'dateOfBoarding', 'domAmtInCurr',
                                                                      'invFinalAmtInCurr', 'repatriatedAmtInCurr']

    public static final ITEM_LIST_REPATRIATION_ACCEPTABLE_FIELDS_FOR_EXPORT = ['clearances', 'itemClearances', 'attachedDocs', 'attachment']

    public static final ITEM_LIST_EXCHANGE_ACCEPTABLE_FIELDS_FOR_EXPORT = ['attachedDocs', 'attachment']

    public static final ATTACHMENT_ACCEPTABLE_FIELDS_FOR_EXPORT = ['rank','docType','docTypeName','docRef','docDate','attachedFile']

    public static  final REPATRIATION_ACCEPTABLE_FIELDS_FOR_IMPORT = REPATRIATION_ACCEPTABLE_FIELDS_FOR_EXPORT + ITEM_LIST_REPATRIATION_ACCEPTABLE_FIELDS_FOR_EXPORT

    public static  final EXCHANGE_ACCEPTABLE_FIELDS_FOR_IMPORT = EXCHANGE_ACCEPTABLE_FIELDS_FOR_EXPORT + ITEM_LIST_EXCHANGE_ACCEPTABLE_FIELDS_FOR_EXPORT

    public static  final REPATRIATION_ADDITIONAL_FIELDS_FOR_PRINT = ['id', 'receivedAmount', 'receivedAmountNat','repatriationBankName','receptionDate','requestDate']
    public static  final EXCHANGE_ADDITIONAL_FIELDS_FOR_PRINT = ['id', 'declarationSerial', 'declarationDate', 'clearanceOfficeCode', 'geoArea','treatmentLevel', 'approvedBy', 'domiciliationNumber', 'registrationNumberBank' , 'areaPartyCode']

    public static boolean isXmlAcceptableField(String name, String docType) {
        boolean isXmlAcceptableFields
            isXmlAcceptableFields = getXmlAcceptableFields(docType).contains(name)
        return isXmlAcceptableFields
    }

    public static Collection<String> getXmlAcceptableFields(String docType) {
        def acceptField
        if(docType.toUpperCase() == REPATRIATION.toUpperCase()){
            acceptField = REPATRIATION_ACCEPTABLE_FIELDS_FOR_IMPORT
        }else if(docType.toUpperCase() == EXCHANGE.toUpperCase()){
            acceptField = EXCHANGE_ACCEPTABLE_FIELDS_FOR_IMPORT
        }
        acceptField
    }

}
