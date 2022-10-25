package com.webbfontaine.efem.rules.attachedDoc

import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachment.AttachmentService
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class AttachedDocRule implements Rule {
    private static def propertyMap = ["docType": " Doc Type Code", "docTypeName": " Doc Type Name"]
    private static def requiredFields = ["docType"]

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${AttachedDocRule}")
        def attachmentService = new AttachmentService()
        def attachedDoc = ruleContext.getTargetAs(AbstractAttachedDoc) as AbstractAttachedDoc
        def domainInstance = attachmentService.getDomainInstance(attachedDoc)
        checkForMandatoryFields(attachedDoc)

        if (domainInstance) {
            checkAttachedCodeExists(attachedDoc, domainInstance)
        }
    }

    private void checkAttachedCodeExists(def attachedDoc, def domainInstance) {
        if (attachedDoc.docType) {
            BeanDataLoadService beanDataLoadService = AppContextUtils.getBean(BeanDataLoadService)
            Map criteria = [code: attachedDoc.docType]
            def htDate = domainInstance?.requestDate?.toDate()?.clearTime() ?: new Date().clearTime()
            def beanData = beanDataLoadService.loadFullBeanData("HT_ATT", criteria, "get", null, htDate)
            if (!beanData) {
                def attachedDocRank = attachedDoc?.rank ?: ""
                attachedDoc.errors.rejectValue('docType', "attachedDoc.errors.docType.invalid", [attachedDocRank] as Object[], "Attached Document Code ${attachedDocRank}: Invalid value")
            }
        }
    }

    private void checkForMandatoryFields(def attachedDocInstance) {
        requiredFields.each { rf ->
            def fieldValue = attachedDocInstance?."$rf"
            if (!fieldValue) {
                def displayName = propertyMap.get(rf)
                def error = "attachedDoc.errors." + "$rf" + ".required"
                def errorMsg = "$displayName" + " is mandatory."
                def rank = attachedDocInstance?.rank ?: ""
                attachedDocInstance.errors.rejectValue("$rf", error, [rank] as Object[], errorMsg);
            }
        }
    }
}
