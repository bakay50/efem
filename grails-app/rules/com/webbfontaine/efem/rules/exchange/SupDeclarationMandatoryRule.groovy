package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import org.springframework.context.i18n.LocaleContextHolder

class SupDeclarationMandatoryRule implements Rule {

    static final List<String> SUP_DEC_MANDATORY = ["clearanceOfficeCode", "declarationSerial", "declarationNumber", "declarationDate"]

    @Override
    void apply(RuleContext ruleContext) {
        SupDeclaration supDeclaration = ruleContext.getTargetAs(SupDeclaration)
        checkMandatoryFields(supDeclaration)

    }

    void checkMandatoryFields(SupDeclaration supDec){
        def fieldsNeeded = []
        SUP_DEC_MANDATORY.each {
            if(!supDec."${it}"){
                fieldsNeeded.add(getSupDecFieldLabel(it))
            }
        }

        if(fieldsNeeded){
            def fields = fieldsNeeded.toString()
            supDec.errors.rejectValue("clearanceOfficeCode", "exchange.list.fields.mandatory", [fields] as Object[], "The fields are mandatory")
        }
    }

    def getSupDecFieldLabel(def field) {
        def messageSource = Holders.applicationContext.messageSource
        messageSource.getMessage("dec.${field}.label".toString(), null, LocaleContextHolder.getLocale())
    }

}
