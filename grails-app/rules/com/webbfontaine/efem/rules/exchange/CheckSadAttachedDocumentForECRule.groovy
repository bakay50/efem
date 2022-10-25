package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders

import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_001
import static com.webbfontaine.efem.constants.ExchangeRequestType.AREA_003
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED

class CheckSadAttachedDocumentForECRule implements Rule {
    @Override
    void apply(RuleContext ruleContext) {
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        checkRule(exchangeInstance)
    }

    static def checkRule(Exchange exchange) {
        if (exchange.requestType == EC && (exchange.geoArea == AREA_003 || (exchange.geoArea == AREA_001 && exchange.areaPartyCode == AREA_003)) && exchange.startedOperation in [UPDATE_APPROVED, UPDATE_EXECUTED]) {
            def sadAttachedDoc = getSadAttachedDocument(exchange)
            if (sadAttachedDoc) {
                def referenceDoc = sadAttachedDoc.collect { it.toString().trim().toUpperCase().replaceAll("\\s+", " ").replaceAll("\\u00A0", "").replaceAll("\\s+", "") }
                referenceDoc.collect {
                    return parseValue(it)
                }
                referenceDoc = referenceDoc?.unique()
                def registrationNo = exchange?.registrationNumberBank?.toString()?.trim()?.replaceAll("\\s+", " ")?.replaceAll("\\u00A0", "")?.replaceAll("\\s+", "")
                if (registrationNo) {
                    registrationNo = parseValue(registrationNo)
                }
                registrationNo = registrationNo?.toString()?.toUpperCase()
                if (registrationNo && referenceDoc && !referenceDoc.contains(registrationNo?.toString())) {
                    exchange.errors.rejectValue('registrationNumberBank', 'exchange.errors.sadAttachedDocument2052', 'The declaration refers to a domiciliation number other than the one mentioned on the exchange document')
                }
            }else{
                exchange.errors.rejectValue('registrationNumberBank', 'exchange.errors.sadreferencemissing2052', 'Domiciliation reference of SAD details is not mentioned')
            }
        }
    }

    static String parseValue(String item) {
        StringBuffer buf = new StringBuffer()
        char[] charArray = item?.toString()?.toCharArray()
        for (char ouput in charArray) {
            if (ouput && !Character.isSpaceChar(ouput)) {
                buf.append(ouput)
            }
        }
        return buf.toString()
    }

    static def getSadAttachedDocument(exchange) {
        SadService service = Holders.applicationContext.getBean("sadService")
        def sadAttachDoc = service.sadAttachedDocument(exchange)
        return sadAttachDoc
    }
}
