package com.webbfontaine.efem.rules.transferOrder.checking

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.transferOrder.TransferOrderService
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate

import static com.webbfontaine.efem.constants.Statuses.ST_CANCELLED
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.STORE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_STORED
import static com.webbfontaine.efem.workflow.Operation.VALIDATE

@Slf4j("LOGGER")
class CheckExecutionReferenceRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        TransferOrder transferOrderinstance = ruleContext.getTargetAs(TransferOrder) as TransferOrder
        checkExecutionReference(transferOrderinstance)
    }

    static def checkExecutionReference(TransferOrder transferOrder) {
        TransferOrderService service = Holders.applicationContext.getBean("transferOrderService")
        if (UserUtils.isBankAgent() && handleOperation(transferOrder.startedOperation)) {
            int currentYear = new LocalDate().year
            String currentBankCode = transferOrder.bankCode
            String currentExecutionReference = transferOrder.executionRef
            TransferOrder transferFoundByCriteria = service.foundTransferorderByCriteria(currentYear, currentBankCode, currentExecutionReference)
            LOGGER.debug("In apply rule ${CheckExecutionReferenceRule} Current year : {}, bankCode : {}, ExecutionRef : {} and Transfer Order founds data : {} ", currentYear, currentBankCode, currentExecutionReference, transferFoundByCriteria)

            if (transferFoundByCriteria && transferFoundByCriteria?.id != transferOrder?.id && !(transferFoundByCriteria?.status in [ST_CANCELLED]) && transferFoundByCriteria?.executionRef == transferOrder.executionRef) {
                transferOrder.errors.rejectValue('executionRef', 'transferOrder.executionRef.unique.error', 'This execution reference has already been used this year in another transfer order document.')
            }
        }
    }

    static def handleOperation(Operation startedOperation) {
        !(WebRequestUtils.params.commitOperation in [STORE, UPDATE_STORED]) && startedOperation in [CREATE, VALIDATE]
    }
}
