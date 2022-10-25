package com.webbfontaine.efem.rules.repatriation.clearanceOfDom

import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.RepatriationService
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class CheckAmountReceivedRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        ClearanceOfDom clearanceOfDom = ruleContext.getTargetAs(ClearanceOfDom) as ClearanceOfDom
        LOGGER.debug("in apply() of ${CheckAmountReceivedRule} on ClearanceOfDom id ${clearanceOfDom.id}")
        checkAmountReceived(clearanceOfDom)
    }

    def checkAmountReceived(clearanceOfDom) {
        def params = WebRequestUtils.getParams()
        Repatriation repatriationInstance = getRepatriation(params?.conversationId)
        List<ClearanceOfDom> domList = repatriationInstance?.clearances?.findAll{it.status == true}
        BigDecimal allRepatriationAmountPrevious = domList*.repatriatedAmtInCurr?.sum() as BigDecimal ?: BigDecimal.ZERO
        BigDecimal repatriatedAmtInCurr = clearanceOfDom?.repatriatedAmtInCurr as BigDecimal ?: BigDecimal.ZERO
        BigDecimal amountReceived = params?.amountReceived as BigDecimal ?: BigDecimal.ZERO
        def allRepatriationAmount = allRepatriationAmountPrevious + repatriatedAmtInCurr
        if (allRepatriationAmount > amountReceived && amountReceived) {
            clearanceOfDom.errors.rejectValue('rank', 'clearance.errors.repatriatedAmtInCurr.sum', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Domiciliated Amount in Currency')
        }
    }

    Repatriation getRepatriation(conversationId) {
        RepatriationService service = Holders.applicationContext.getBean("repatriationService")
        return service.findFromSessionStore(conversationId)
    }
}
