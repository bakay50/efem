package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest

import static com.webbfontaine.efem.workflow.Operation.*

@Slf4j("LOGGER")
class MandatoryFieldRule implements Rule{

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${MandatoryFieldRule}");
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkForMandatoryFields(exchangeInstance)
    }

    private void checkForMandatoryFields(Exchange exchangeInstance){

        Operation startedOp = exchangeInstance?.startedOperation
        def requiredFields = BusinessLogicUtils.checkIfEA(exchangeInstance) ? AppConfig.businessLogicEaRequiredFields :  AppConfig.businessLogicEcRequiredFields
        List forMandatoryComment = [QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED, REJECT_REQUESTED, REJECT_PARTIALLY_APPROVED, CANCEL_APPROVED, CANCEL_QUERIED]

        requiredFields.each { rf ->
            def fieldValue = exchangeInstance?."$rf.key"
            def isEditable = FieldsConfiguration.isMandatory("$rf.key", startedOp)
            if(!fieldValue && isEditable){
                mandatoryError(exchangeInstance, rf)
            }
        }

        if(startedOp in forMandatoryComment){
            def supInfoMessage = GrailsWebRequest.lookup().params.comments
            if(isNullOrEmpty(supInfoMessage) && isMandatoryCommentEnable()){
                exchangeInstance.errors.rejectValue("comments", "exchange.comment.required", "Comment is mandatory");
            }
        }
    }

    static mandatoryError(Exchange exchangeInstance, def field){
        def error = "exchange."+"$field.key"+".required"
        def errorMsg = "$field.value"+" is mandatory."
        if (checkIfECAndGeoAreaEqualsOneOrTwo(exchangeInstance)) {
            return exchangeInstance
        }
        exchangeInstance.errors.rejectValue("$field.key", error, errorMsg);
    }

    def isNullOrEmpty(def value){
        return value == null || value == ""
    }

    static boolean checkIfECAndGeoAreaEqualsOneOrTwo(Exchange exchange) {
        return  exchange.requestType == ExchangeRequestType.EC_FROM_SAD && exchange.geoArea == ExchangeRequestType.AREA_002
    }

    static boolean isMandatoryCommentEnable() {
        return UserUtils.isBankAgent() || UserUtils.isGovOfficer()
    }
}
