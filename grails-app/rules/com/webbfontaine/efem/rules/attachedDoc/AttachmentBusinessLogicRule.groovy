package com.webbfontaine.efem.rules.attachedDoc

import com.webbfontaine.efem.attachedDoc.AbstractAttachedDoc
import com.webbfontaine.efem.attachment.AttachmentService
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.workflow.Operation.CANCEL_APPROVED
import static com.webbfontaine.efem.workflow.Operation.CANCEL_QUERIED
import static com.webbfontaine.efem.workflow.Operation.QUERY_PARTIALLY_APPROVED
import static com.webbfontaine.efem.workflow.Operation.QUERY_REQUESTED
import static com.webbfontaine.efem.workflow.Operation.REJECT_PARTIALLY_APPROVED
import static com.webbfontaine.efem.workflow.Operation.REJECT_REQUESTED

@Slf4j('LOGGER')
class AttachmentBusinessLogicRule implements Rule {
    private static def COMMON_ATTACHMENT_RULES = [
            new AttachedDocRule()
    ]

    @Override
    void apply(RuleContext ruleContext) {

        LOGGER.debug("in apply() of ${AttachmentBusinessLogicRule}")
        def attachmentService = new AttachmentService()
        def attachedDoc = ruleContext.getTargetAs(AbstractAttachedDoc)
        def domainInstance = attachmentService.getDomainInstance(attachedDoc)
        def startTime = System.currentTimeMillis()
        def params = WebRequestUtils.getParams()
        def conversationId = params.conversationId

        LOGGER.debug("id = {}, cid = {} Attachment # {}. Started. ", domainInstance?.id, conversationId, attachedDoc?.rank)
        if (isAttachmentCommonRuleEnable()) {
            RuleUtils.executeSetOfRules(COMMON_ATTACHMENT_RULES, ruleContext)
        }
        LOGGER.debug("id = {}, cid = {} Attachment # {}. AttachmentBusinessLogicRule execution took {}ms", domainInstance?.id, conversationId, attachedDoc?.rank, (System.currentTimeMillis() - startTime))

    }

    private static boolean isAttachmentCommonRuleEnable() {
        return !(WebRequestUtils.params.commitOperation in [QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED, REJECT_REQUESTED, REJECT_PARTIALLY_APPROVED, CANCEL_APPROVED, CANCEL_QUERIED])
    }

}
