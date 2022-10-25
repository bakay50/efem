package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.layout.MessageTagLib
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.util.GrailsNameUtils
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ApplicationContextEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.ContextStartedEvent
import static com.webbfontaine.efem.constants.Balance.*
import static com.webbfontaine.efem.constants.DepartmentInCharge.BANK
import static com.webbfontaine.efem.constants.DepartmentInCharge.FINEX
import static com.webbfontaine.efem.constants.Statuses.EXCHANGE_STATUS
import static com.webbfontaine.efem.constants.Statuses.REPATRIATION_STATUS
import static com.webbfontaine.efem.constants.Statuses.TRANSFER_STATUS
import static com.webbfontaine.efem.constants.TypeCreditAccount.*
import static com.webbfontaine.efem.constants.Statuses.CURRENCYTRANSFER_STATUS
import static com.webbfontaine.efem.constants.Statuses.RIMM_STATUS

class ReferenceService implements ApplicationListener<ApplicationContextEvent> {

    public static final String[] STRING_OPERATORS = ['starts with', 'ends with', 'contains', 'equals']
    public static final String[] RANGE_OPERATORS = ['equals', 'less than', 'greater than','between']
    public static final String[] RESIDENT_SELECTION = ['yes', 'no']
    public static final String[] APPROVAL_LEVEL_SELECTION = ['1', '2']
    public static final String[] CREDIT_ACCOUNT_SELECTION = ['FRANCE', 'LOCAL','AUTRES']
    public static final String[] CHARGES_SELECTION = ['All charges to be paid by the ordering party','Only charges on-site to be paid by the ordering party','All charges to be paid by the beneficiary']
    private def wf_message

    @Override
    void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent || event instanceof ContextStartedEvent) {
            wf_message = event.getApplicationContext().getBean(MessageTagLib.class)
        }
    }

    def getOperators(operators) {
        operators?.collect {
            def i18n = GrailsNameUtils.getPropertyNameForLowerCaseHyphenSeparatedName(it.replaceAll(" ", "-"))
            [key: it, value: wf_message.message(code: "search.${i18n}.operator", default: it)]
        }
    }

    def getExchangeRequestType(){
        [ExchangeRequestType.EA, ExchangeRequestType.EC].collect {[key: it, value: wf_message.message(code: "exchangeRequest.${it}", default: it)]}
    }

    def getExchangeStatus(){
        EXCHANGE_STATUS.collect {
            if(it instanceof String){
                [key: it, value: wf_message.message(prefix: 'status', code: it)]
            }
        }
    }

    def getRepatriationStatus(){
        REPATRIATION_STATUS.collect {
            if(it instanceof String){
                [key: it, value: wf_message.message(prefix: 'status', code: it)]
            }
        }
    }

    def getTransferOrderStatus(){
        TRANSFER_STATUS.collect {
            if(it instanceof String){
                [key: it, value: wf_message.message(prefix: 'status', code: it)]
            }
        }
    }

    def getBaseOn(){
        [ExchangeRequestType.BASE_ON_TVF, ExchangeRequestType.BASE_ON_SAD].collect {[key: it, value: wf_message.message(code: "requestType.${it}", default: it)]}
    }

    def getNatureOfFund(){
        [NatureOfFund.NOF_REP, NatureOfFund.NOF_PRE].collect {[key: it, value: wf_message.message(code: "natureOfFund.${it}", default: it)]}
    }

    def getBalance(){
        [EQUAL_TO_ZERO, GREATER_THAN_ZERO, LESS_THAN_ZERO].collect {[key: it, value: wf_message.message(code: "balance.${it}", default: it)]}
    }

    def getDepartmentInCharge(){
        [FINEX, BANK].collect {[key: it, value: wf_message.message(code: "departmentInCharge.${it}", default: it)]}
    }


    def getDocumentOwner(doc) {
        return doc?.userOwner
    }

    def getResidentSelection() {
        RESIDENT_SELECTION.collect {
            [key: it, value: wf_message.message(prefix: 'exchange.select', code: it)]
        }
    }

    def getByCreditOfAccountSelection() {
        CREDIT_ACCOUNT_SELECTION.collect {[key: it, value: wf_message.message(code: "transfer.select.${it}", default: it)]}
    }

    def getListOfCharges(){
        CHARGES_SELECTION.collect {[key: it, value: wf_message.message(code: "transfer.charges.select.${it}", default: it)]}
    }

    def getTreatmentLevel(){
        APPROVAL_LEVEL_SELECTION.collect {[key: it, value: it]}
    }

    def getTypeCreditAccount(){
        [CREDIT_FRANCE, CREDIT_LOCAL, CREDIT_FOREIGN].collect {
            [key: it, value: wf_message.message(prefix: 'credit', code: it)]
        }
    }
	
	def getCurrencyTransferStatus(){
        CURRENCYTRANSFER_STATUS.collect {
            if(it instanceof String){
                [key: it, value: wf_message.message(prefix: 'status', code: it)]
            }
        }
    }

    def getReferenceStatus(){
        RIMM_STATUS.collect {
            if(it instanceof String){
                [key: it, value: wf_message.message(prefix: 'status', code: it)]
            }
        }
    }



}
