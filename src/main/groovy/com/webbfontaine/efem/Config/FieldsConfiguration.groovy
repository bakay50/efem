package com.webbfontaine.efem.Config

import static com.webbfontaine.efem.AppConfig.getSearchResultConfig
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.workflow.Operation
import grails.util.Holders
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.Config.Config.*

@Slf4j("LOGGER")
class FieldsConfiguration {

    static getConfig(){
        return Holders.config.com.webbfontaine.efem.exchange.fieldsConfig
    }

    static getDomiciliationFields(){
        return Holders.config.com.webbfontaine.efem.exchange.domiciliationFields
    }

    static getExchangeAuhorizationFieldConfig(){
        return config.exchangeAuhorization as Map
    }

    static getRepatriationAuhorizationFieldConfig(){
        return config.repatriationAuhorization as Map
    }

    static getBankAuhorizationFieldConfig(){
        return config.bankAuhorization as Map
    }

    static getCurrencyTransferAuhorizationFieldConfig(){
        return config.currencyTransferAuhorization as Map
    }

    static getTransferAuhorizationFieldConfig(){
        return config.transferOrderAuhorization as Map
    }

    static getExchangeFieldsConfig(String fieldName, Operation startedOperation){
        ArrayList<String> fieldConfig = getFieldConfig(fieldName)
        if (fieldConfig) {
            fieldConfig.get(indexOfExchangeOperation(startedOperation)) as String
        } else {
            throw new FieldsConfigurationException("no configuration found for field ${fieldName}")
        }
    }

    static getRepatriationFieldsConfig(String fieldName, Operation startedOperation){
        ArrayList<String> fieldConfig = getRepatFieldsConfig(fieldName)
        if (fieldConfig) {
            fieldConfig.get(indexOfRepatriationOperation(startedOperation)) as String
        } else {
            throw new FieldsConfigurationException("no configuration found for field ${fieldName}")
        }
    }

    static getBankFieldsConfig(String fieldName, Operation startedOperation){
        ArrayList<String> fieldConfig = getBankFieldsConfig(fieldName)
        if (fieldConfig) {
            fieldConfig.get(indexOfBankOperation(startedOperation)) as String
        } else {
            throw new FieldsConfigurationException("no configuration found for field ${fieldName}")
        }
    }

    static getTransferFieldsConfig(String fieldName, Operation startedOperation){
        ArrayList<String> fieldConfig = getTransferFieldsConfig(fieldName)
        if (fieldConfig) {
            fieldConfig.get(indexOfTransferOperation(startedOperation)) as String
        } else {
            throw new FieldsConfigurationException("no configuration found for field ${fieldName}")
        }
    }

    static getCurrencyTransferFieldsConfig(String fieldName, Operation startedOperation){
        ArrayList<String> fieldConfig = getCurrencyTransferFieldsConfig(fieldName)
        if (fieldConfig) {
            fieldConfig.get(indexOfCurrencyTransferOperation(startedOperation)) as String
        } else {
            throw new FieldsConfigurationException("no configuration found for field ${fieldName}")
        }
    }


    static getFieldConfig(String fieldName){
        return getExchangeAuhorizationFieldConfig().get(fieldName) as ArrayList<String>
    }

    static getRepatFieldsConfig(String fieldName){
        return getRepatriationAuhorizationFieldConfig().get(fieldName) as ArrayList<String>
    }

    static getBankFieldsConfig(String fieldName){
        return getBankAuhorizationFieldConfig().get(fieldName) as ArrayList<String>
    }

    static getCurrencyTransferFieldsConfig(String fieldName){
        return getCurrencyTransferAuhorizationFieldConfig().get(fieldName) as ArrayList<String>
    }

    static getTransferFieldsConfig(String fieldName){
        return getTransferAuhorizationFieldConfig().get(fieldName) as ArrayList<String>
    }

    static Integer indexOfExchangeOperation(def startedOperation){
        def index
        switch (startedOperation){
            case null:
            case CREATE:
            case REQUEST:
            case STORE:
            case UPDATE_STORED:
            case REQUEST_STORED:
                index = 0
                break

            case REQUEST_QUERIED:
                index = 1
                break

            case QUERY_REQUESTED:
            case QUERY_PARTIALLY_APPROVED:
                index = 2
                break

            case REJECT_REQUESTED:
            case REJECT_PARTIALLY_APPROVED:
                index = 3
                break

            case APPROVE_REQUESTED:
                index = 4
                break

            case APPROVE_PARTIALLY_APPROVED:
                index = 5
                break

            case UPDATE_QUERIED:
                index = 6
                break

            case UPDATE_APPROVED:
                index = 7
                break

            case UPDATE_EXECUTED:
                index = 8
                break

            case CANCEL_QUERIED:
            case CANCEL_APPROVED:
                index = 9
                break

            case DOMICILIATE:
                index = 10
                break

            default:
                index = 9
        }

        return index
    }

    static Integer indexOfRepatriationOperation(def startedOperation){
        def index
        switch (startedOperation){
            case null:
            case CREATE:
            case DECLARE:
            case STORE:
            case UPDATE_STORED:
            case DECLARE_STORED:
                index = 0
                break

            case DECLARE_QUERIED:
                index = 1
                break

            case CONFIRM_DECLARED:
            case CONFIRM:
                index = 4
                break

            case UPDATE_QUERIED:
                index = 6
                break
            case UPDATE_CONFIRMED:
                index = 8
                break

            default:
                index = 9
        }

        return index
    }

    static Integer indexOfCurrencyTransferOperation(def startedOperation){
        def index
        switch (startedOperation){
            case null:
            case STORE:
                index = 0
                break
            case DELETE_STORED:
                index = 1
                break
            case TRANSFER:
                index = 2
                break
            case TRANSFER_STORED:
                index = 3
                break
            case UPDATE_STORED:
                index = 4
                break
            case UPDATE_TRANSFERRED:
                index = 5
                break
            case CANCEL_TRANSFERRED:
                index = 6
                break
            default:
                index = 0
        }

        return index
    }

    static Integer indexOfTransferOperation(def startedOperation){
        def index
        switch (startedOperation){
            case null:
            case CREATE:
            case STORE:
                index = 0
                break
            case DELETE_STORED:
                index = 1
                break
            case REQUEST:
            case REQUEST_QUERIED:
            case REQUEST_STORED:
                index = 2
                break
            case VALIDATE:
                index = 4
                break
            case UPDATE_STORED:
                index = 5
                break
            case UPDATE_QUERIED:
                index = 6
                break
            case UPDATE_VALIDATED:
                index = 7
                break
            case CANCEL_QUERIED:
                index = 8
                break
            default:
                index = 9
        }

        return index
    }

    static Integer indexOfBankOperation(def startedOperation){
        def index
        switch (startedOperation){
            case null:
            case CREATE:
            case REGISTER:
                index = 0
                break
            case UPDATE_VALID:
                index = 1
                break
            case VIEW_VALID:
            case CANCEL_VALID:
                index = 2
                break
            case ACTIVATE:
                index = 3
                break
            default:
                index = -1
        }

        return index
    }

   static boolean isMandatory(String fieldName, Operation startedOperation,String domaintType=UtilConstants.EXCHANGE) {
        if(domaintType.equalsIgnoreCase(UtilConstants.EXCHANGE)){
            MANDATORY.isConform(getExchangeFieldsConfig(fieldName, startedOperation))
        }else if(domaintType.equalsIgnoreCase(UtilConstants.REPATRIATION)){
            MANDATORY.isConform(getRepatriationFieldsConfig(fieldName, startedOperation))
        }else if(domaintType.equalsIgnoreCase(UtilConstants.BANK)){
            MANDATORY.isConform(getBankFieldsConfig(fieldName, startedOperation))
        }else {
            MANDATORY.isConform(getTransferFieldsConfig(fieldName, startedOperation))
        }
    }

    static boolean isProhibited(String fieldName, Operation startedOperation,String domaintType=UtilConstants.EXCHANGE) {
        if(domaintType.equalsIgnoreCase(UtilConstants.EXCHANGE)){
            PROHIBITED.isConform(getExchangeFieldsConfig(fieldName, startedOperation))
        }else if(domaintType.equalsIgnoreCase(UtilConstants.REPATRIATION)){
            PROHIBITED.isConform(getRepatriationFieldsConfig(fieldName, startedOperation))
        }else if(domaintType.equalsIgnoreCase(UtilConstants.BANK)){
            PROHIBITED.isConform(getBankFieldsConfig(fieldName, startedOperation))
        }else{
            PROHIBITED.isConform(getTransferFieldsConfig(fieldName, startedOperation))
        }
    }

    static boolean isCustom(String fieldName, Operation startedOperation,String domaintType=UtilConstants.EXCHANGE) {
        if(domaintType.equalsIgnoreCase(UtilConstants.EXCHANGE)){
            CUSTOMIZED.isConform(getExchangeFieldsConfig(fieldName, startedOperation))
        }else if (domaintType.equalsIgnoreCase(UtilConstants.REPATRIATION)){
            CUSTOMIZED.isConform(getRepatriationFieldsConfig(fieldName, startedOperation))
        }else if (domaintType.equalsIgnoreCase(UtilConstants.BANK)){
            CUSTOMIZED.isConform(getBankFieldsConfig(fieldName, startedOperation))
        }else {
            CUSTOMIZED.isConform(getTransferFieldsConfig(fieldName, startedOperation))
        }
    }

    static boolean isTransferFieldsMandatory(String fieldName, Operation startedOperation){
        MANDATORY.isConform(getTransferFieldsConfig(fieldName, startedOperation))
    }

    static boolean isCurrencyTransferFieldsMandatory(String fieldName, Operation startedOperation){
        MANDATORY.isConform(getCurrencyTransferFieldsConfig(fieldName, startedOperation))
    }

    static boolean isTransferFieldsProhibited(String fieldName, Operation startedOperation) {
        PROHIBITED.isConform(getTransferFieldsConfig(fieldName, startedOperation))
    }

    static boolean isCurrencyTransferFieldsProhibited(String fieldName, Operation startedOperation) {
        PROHIBITED.isConform(getCurrencyTransferFieldsConfig(fieldName, startedOperation))
    }

    static boolean isTransferFieldsCustom(String fieldName, Operation startedOperation) {
        CUSTOMIZED.isConform(getTransferFieldsConfig(fieldName, startedOperation))
    }

    static boolean isCurrencyTransferFieldsCustom(String fieldName, Operation startedOperation) {
        CUSTOMIZED.isConform(getCurrencyTransferFieldsConfig(fieldName, startedOperation))
    }

    static def getTransferFieldsConfigPerOperation(Config configuration, def startedOperation){
        def list = []
        def fields = getTransferAuhorizationFieldConfig()
        def statusIndex = indexOfTransferOperation(startedOperation)
        fields.each{ configs ->
            if(configs.getAt('value')[statusIndex] == configuration.label){
                list.add(configs.getAt('key'))
            }
        }
        return list
    }

    static def getDefaultColumnsForSearchResultByDomain (domain) {
        return domain ? getSearchResultConfig(domain) : getSearchResultConfig(UtilConstants.EXCHANGE)
    }

}
