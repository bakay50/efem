package com.webbfontaine.efem.rules

import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.grails.plugins.validation.rules.ref.setter.RefValueSetterRule
import com.webbfontaine.grails.plugins.validation.rules.ref.utils.RefUtils
import groovy.util.logging.Slf4j

import static com.webbfontaine.grails.plugins.validation.rules.ref.utils.RefUtils.*

@Slf4j("LOGGER")
class EfemRefValueSetterRule extends RefValueSetterRule{

    EfemRefValueSetterRule() {
    }

    EfemRefValueSetterRule(Class refDomainClass, String codeField, Boolean setDescriptions = true, Boolean strictDescriptions = false, String nameField = null, String refCodeField = RefUtils.refDomainCodeFieldName, String refNameField = RefUtils.refDomainDescriptionFieldName, boolean cachedQuery = true) {
        this.refDomainClass = refDomainClass
        this.setDescriptions = setDescriptions
        this.strictDescriptions = strictDescriptions
        this.codeField = codeField
        this.nameField = nameField ?: this.codeField.replace(appDomainFieldsCodePostFix, appDomainFieldsDescriptionPostFix)
        this.refCodeField = refCodeField
        this.refNameField = refNameField
        this.useCachedQuery = cachedQuery
    }

    @Override
    void apply(RuleContext ruleContext) {
        def domainInstance = ruleContext.getTarget()
        if (preValidate(domainInstance)) {

            def resultRefObject = checkAndGetRefObject(domainInstance)
            if (!resultRefObject) {
                LOGGER.warn("{} with cod '{}' not found. UseQueryCache = {}", refDomainClass.getSimpleName(), domainInstance[codeField])
                rejectCodeValue(domainInstance, codeField)
                domainInstance[nameField] = ""
                return
            }

            if (setDescriptions) {
                setDescriptionForField(domainInstance, resultRefObject, '')

                if (setTranslatedDescription) {
                    setDescriptionForField(domainInstance, resultRefObject, translatedFieldPostfixAtRimm)
                }
            }
        }
    }

    void rejectCodeValue(def domainInstance, String fieldToReject, String defaultRejectMessageCode = '') {
        if(domainInstance?.hasProperty('rank') && domainInstance?.rank){
            domainInstance.errors.rejectValue(fieldToReject, "${domainInstance?.class?.simpleName?.toLowerCase()}.errors.${fieldToReject}.invalid.with.rank",
                    [domainInstance?.rank,domainInstance[fieldToReject]] as Object[], defaultRejectMessageCode ?: "${domainInstance?.class?.simpleName?.capitalize()} $domainInstance.rank: Invalid $fieldToReject value ${domainInstance.getAt(fieldToReject)}")
        }
        /*else {
            domainInstance.errors.rejectValue(fieldToReject, "${domainInstance?.class?.simpleName?.toLowerCase()}.errors.${fieldToReject}.invalid",
                    [domainInstance[fieldToReject]] as Object[], defaultRejectMessageCode ?: "Invalid $fieldToReject value: ${domainInstance.getAt(fieldToReject)}")
        }*/
    }
}
