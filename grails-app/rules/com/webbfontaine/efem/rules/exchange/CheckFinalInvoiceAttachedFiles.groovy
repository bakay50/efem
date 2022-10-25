package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j

import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED

@Slf4j("LOGGER")
class CheckFinalInvoiceAttachedFiles implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        checkIfFinalInvoiceFilesAreAttached(exchange)
    }

    def checkIfFinalInvoiceFilesAreAttached(Exchange exchange) {
        def attachedDocs = exchange?.attachedDocs
        if (exchange.isFinalAmount) {
            if ((exchange?.requestType == ExchangeRequestType.EC) && (exchange?.startedOperation == UPDATE_APPROVED)) {
                if (!attachedDocs?.any { it?.docType == ExchangeRequestType.CODE_FACTURE && !it.id }) {
                    exchange.errors.rejectValue('attachedDocs', 'attachment.attDoc.errors.new.file.mandatory', 'Please Attach the final invoice.')
                }
            }
        }
    }
}
