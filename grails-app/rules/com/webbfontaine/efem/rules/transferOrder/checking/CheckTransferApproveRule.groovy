package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext

import static com.webbfontaine.efem.workflow.Operation.*

class CheckTransferApproveRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        if(WebRequestUtils.params.commitOperation in [REQUEST, REQUEST_STORED, VALIDATE, UPDATE_VALIDATED, REQUEST_QUERIED]){
            TransferOrder transferInstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
            checkValideExchange(transferInstance)
        }
    }

    def checkValideExchange(TransferOrder transferInstance) {
        transferInstance.allOrderClearanceOfDoms.each { OrderClearanceOfDom clearanceOfDom->
            Exchange exchangeInstance = Exchange.findByRequestNo(clearanceOfDom.eaReference)
            if (!(exchangeInstance.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED])) {
                transferInstance.errors.reject('transferOrder.approve.exchange', "One or more EA are not in correct Status")
            }
        }
    }
}