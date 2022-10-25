package com.webbfontaine.efem

import com.webbfontaine.efem.xml.DataBindingHelper
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import static com.webbfontaine.efem.constants.UtilConstants.EXCLUDES_BANK_CODE
import static com.webbfontaine.efem.xml.DocXmlBinder.getXmlString

@Slf4j("LOGGER")
class XmlValidationService {

    def validateContent(GPathResult xml, String docType) {
        def error = "default.xml.invalidContent.message"
        if (xml.size() == 0) {
            return error
        }
        xml.children().find { element ->
            if (!DataBindingHelper.isXmlAcceptableField(element.name(), docType)) {
                LOGGER.debug("Imported xml file contains invalid node! " + element.name())
                return error
            }
        }
        return null
    }

    Exchange checkXmlToImport(Exchange exchange, GPathResult importXml) {
        if (getXmlString(exchange, importXml, "domiciliationBankCode") in EXCLUDES_BANK_CODE || getXmlString(exchange, importXml, "bankCode") in EXCLUDES_BANK_CODE) {
            exchange.errors.reject('importXML.invalid.bank', 'Invalid Bank code')
        }
    }

}
