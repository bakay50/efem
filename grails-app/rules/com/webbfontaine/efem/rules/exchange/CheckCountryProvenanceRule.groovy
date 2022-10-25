package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import  com.webbfontaine.efem.constants.ExchangeRequestType

import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.REQUEST
import static com.webbfontaine.efem.workflow.Operation.REQUEST_QUERIED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_QUERIED

class CheckCountryProvenanceRule implements Rule{
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchange = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchange.requestType == ExchangeRequestType.EC) {
            ArrayList<Operation> listOfOperation = [CREATE, REQUEST, REQUEST_QUERIED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED]
            if (exchange?.startedOperation in  listOfOperation && exchange.countryProvenanceDestinationCode == ExchangeRequestType.CI) {
                exchange.errors.rejectValue('countryProvenanceDestinationCode', 'client.exchange.countryProvenanceDestinationCode.error', 'Country of Provenance cannot be Ivory Cost')
            }
        }
    }
}
