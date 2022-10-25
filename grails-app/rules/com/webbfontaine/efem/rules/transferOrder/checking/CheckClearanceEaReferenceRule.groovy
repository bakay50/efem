package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.REQUEST_QUERIED
import static com.webbfontaine.efem.workflow.Operation.REQUEST_STORED
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_QUERIED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.VALIDATE

@Slf4j('LOGGER')
class CheckClearanceEaReferenceRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("in apply() of ${CheckClearanceEaReferenceRule}")
        TransferOrder transferOrder = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        if (transferOrder?.orderClearanceOfDoms.size() > 1 && isGoodOperation()) {
            for (clearance in transferOrder?.orderClearanceOfDoms) {
                if (EaReferenceChecking(transferOrder, clearance)) {
                    transferOrder.errors.reject('transfer.errors.eaReference.unique',[clearance?.eaReference] as Object[], "${clearance?.eaReference} : You are not allowed to add the same AC reference multiple times in a current session.")
                    break
                }
            }
        }
    }

    static def EaReferenceChecking(TransferOrder transferOrder, OrderClearanceOfDom clearance) {
        return transferOrder?.orderClearanceOfDoms?.any {it.eaReference == clearance.eaReference && it.rank != clearance.rank && it.state == "0"}.booleanValue()
    }

    static boolean isGoodOperation(){
        return WebRequestUtils.params.commitOperation in [REQUEST, REQUEST_STORED, VALIDATE, UPDATE_VALIDATED, REQUEST_QUERIED, STORE, UPDATE_STORED, UPDATE_QUERIED]
    }

}
