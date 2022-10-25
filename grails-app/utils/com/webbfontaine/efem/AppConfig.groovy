package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.security.josso.Utils
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest

@Slf4j(value = "LOGGER")
public class AppConfig {


    static ConfigObject getEfemConfig() {
        Holders.config.com.webbfontaine.efem
    }

    static ConfigObject getTvfVariance() {
        efemConfig.tvfVariance
    }

    public static List getListCountryOfDestination () {
        return efemConfig.listCountryOfDestination
    }

    public static List getListGeoArea () {
        return efemConfig.listGeoArea
    }

    public static String getGeoArea1 () {
        return efemConfig.geoArea1
    }

    public static String getGeoArea2 () {
        return efemConfig.geoArea2
    }

    public static String getGeoArea3 () {
        return efemConfig.geoArea3
    }

    public static def getDecimalNumberFormat(){
        return Holders.config.numberFormatConfig.decimalNumberFormat
    }

    public static List getRequiredAttachments () {
        return efemConfig.requiredAttachments
    }

    public static List getFactureAttachments () {
        return efemConfig.factureAttachments
    }

    public static Integer getMaxApprovalConfig () {
        return Integer.valueOf(efemConfig.maxApprovalConfig)
    }

    public static Integer getMaxApprovalConfigEC () {
        return Integer.valueOf(efemConfig.maxApprovalConfigEC)
    }

    public static String getMaxAmountInXofCurrency () {
        return efemConfig.maxAmountInXofCurrency
    }

    public static Map getBusinessLogicEaRequiredFields () {
        return efemConfig.exchange.businessLogicRuleValidation.EaRequiredFields
    }

    public static Map getBusinessLogicEcRequiredFields () {
        return efemConfig.exchange.businessLogicRuleValidation.EcRequiredFields
    }

    public static String getSadUrl(){
        return Holders.config.rest.sad.url

    }
    public static String getNotifUrl(){
        return Holders.config.rest.notif.url
    }


    public static String getSadOperationtypeCode() {
        return efemConfig.sad.operationtypeCode
    }



    public static resolveJossoSessionId() {
        return Holders.config.jossoSessionId as String
    }

    public static String getJossoId() {
        Utils.getJossoCookie(RequestContextHolder.currentRequestAttributes().request as HttpServletRequest)?.value ?: resolveJossoSessionId()
    }

    public static List getListIncoterm() {
        return Holders.config.efemAllowedOfficeCode.incoterm
    }

    static String getJossoAdmin(){
        return efemConfig.josso.jossoAdmin
    }

    static boolean resolveEmailEnabled(){
        return Holders.config.grails.mail.enabled
    }

    static String resolveEmailFrom(){
        return Holders.config.grails.mail.from
    }

    static List resolveEmailToAdmin(){
        return Holders.config.grails.mail.to_admins.to
    }

    static Boolean resolveEnabledQueueing(){
        return Holders.config.grails.mail.queueing
    }

    static String resolveEmailToAdminSubject(){
        return Holders.config.grails.mail.to_admins.subject
    }

    static String resolveEmailToAdminBody(){
        return Holders.config.grails.mail.to_admins.body
    }

    static boolean resolveNotificationEnabledForOperation(String operationType) {
        Holders.config.notification.operations."${operationType}".enable
    }

    static def resolveRecipientList(String operation, String endStatus) {
        Holders.config.notification.operations."${operation}".endStatus."${endStatus}"
    }

    static def resolveOrderRecipientList(String operation, String endStatus) {
        Holders.config.orderNotification.operations."${operation}".endStatus."${endStatus}"
    }

    static def resolveOrderNotificationEnabledForOperation(String operationType) {
        Holders.config.orderNotification.operations."${operationType}".enable
    }

    static def resolveRepatriationRecipientList(String operation, String endStatus) {
        Holders.config.repatriationNotification.operations."${operation}".endStatus."${endStatus}"
    }

    static def resolveRepatriationNotificationEnabledForOperation(String operationType) {
        Holders.config.repatriationNotification.operations."${operationType}".enable
    }

    static String resolveGovEmail(Exchange exchange) {
        if (exchange?.requestType == ExchangeRequestType.EA){
            Holders.config.emails.govOfficerEA
        }else{
            Holders.config.emails.govOfficerEC
        }
    }

    static String resolveRetrieveAvdURL(){
        efemConfig.erc.retrieveAvd.url
    }

    static boolean resolveRetrievingAttachmentAvailable(){
        return efemConfig.isRetrievingAttachmentAvailable
    }

    static boolean isRuleEnabled(String ruleName) {
        return efemConfig.enabled.rule."${ruleName}"
    }

    static boolean resolveRetrieveSadHasNoParamsInPayload(){
        return efemConfig.isRetrieveSadHasNoParams
    }

    static String resolevRoleName() {
        return Holders?.config?.com?.webbfontaine?.grails?.plugins?.security?.roleName
    }

    static boolean isRimmEnabled() {
        return efemConfig.enabled.rimm.bankFlag
    }

    static boolean isTransferEnabled() {
        return efemConfig.exchange.createTransfer.enabled
    }

    static boolean displayEAImportExport(){
        return efemConfig.displayEAImportExport
    }

    static boolean displayImportExport(){
        return efemConfig.displayImportExport
    }

    static boolean displayTransferOrderLinkFromExecution(){
        return efemConfig.displayTransferOrderLinkFromExecution
    }

    static boolean handleDisplayFieldsRemainingBalanceTransfer() {
        return efemConfig.displayFieldsRemainingBalanceTransfer
    }

    static boolean checkDeclarantValidity() {
        return efemConfig.checkDeclarantValidity
    }

    static boolean checkCompanyValidity() {
        return efemConfig.checkCompanyValidity
    }

    static def resolveStartRequestDate() {
        Holders?.config?.efemAllowedCheckingCurrencyCode?.startingSearchRequestDate
    }
    
    static BigDecimal getTvfVarianceRate() {
        tvfVariance.rate
    }

    static BigDecimal getTvfVarianceAmount() {
        tvfVariance.amount
    }

    static List getXofOfficeCode() {
        return Holders.config.efemAllowedOfficeCode.code_office
    }

    static String dateFormat() {
        return Holders.config.dateFormat
    }

    static String StampPath() {
        return Holders.config.com.webbfontaine.efem.printea.stamppath
    }

    static String StartStampDate() {
        return Holders.config.com.webbfontaine.efem.printea.startstampdate
    }

    static boolean isCurrencyEnabled() {
        return Holders.config.com.webbfontaine.efem.exchange.createCurrencies.enabled
    }

    static boolean isBankEnabled() {
        return Holders.config.com.webbfontaine.efem.rimm.bank.enabled
    }

    static def getSearchResultConfig(domain){
        return Holders.config.com.webbfontaine.efem.exchange.search."${domain}".defaultColumns
    }

    static boolean exportCurrentMonthOnly () {
        return efemConfig.exportCurrentMonth
    }

    static int maxDaysToExport () {
        return efemConfig.maxDaysToExport
    }

    static boolean allowFinexApprovalEA() {
        return Holders.config.efemciApplicationConfig.exchange.enableFinexApproval
    }

    static boolean getIsBlockUserEnabled(){
        Holders.config.com.webbfontaine.efem.blocked.enabled
    }

    static List getBlockOperationsList() {
        Holders.config.com.webbfontaine.efem.blocked.operations
    }

    static List getBlockNccList() {
        Holders.config.com.webbfontaine.efem.blocked.ncc
    }

    static List getBlockDecCodeList() {
        Holders.config.com.webbfontaine.efem.blocked.decCode
    }

}
