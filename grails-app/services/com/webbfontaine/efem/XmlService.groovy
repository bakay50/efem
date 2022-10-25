package com.webbfontaine.efem

import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.attachedDoc.TransferAttachedDoc
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.xml.DocXmlBinder
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import static com.webbfontaine.efem.constants.UtilConstants.EXCHANGE
import static com.webbfontaine.efem.constants.UtilConstants.REPATRIATION
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import static com.webbfontaine.efem.constants.UtilConstants.CURRENCY_TRANSFER_XML_TAG
import static com.webbfontaine.efem.constants.UtilConstants.TRANSFER_ORDER_XML_TAG

@Transactional
@Slf4j("LOGGER")
class XmlService {
    def repatriationService
    def exchangeService
    def springSecurityService
    def referenceService
    def xmlValidationService

    def buildCurrencyTransfer(CurrencyTransfer currencyTransfer, GPathResult xml) {
        LOGGER.debug("begin buildCurrencyTransfer() in ${XmlService}")
        LOGGER.debug("get currency content : {} and xml : {}", currencyTransfer, xml)
        if (xml.name() == CURRENCY_TRANSFER_XML_TAG) {
            currencyTransfer.bankCode = xml.header.bank_code as String
            currencyTransfer.bankName = xml.header.bank_name as String
            currencyTransfer.currencyCode = xml.header.currency_code as String
            currencyTransfer.currencyName = xml.header.currency_name as String
            currencyTransfer.currencyRate = isNotEmpty(xml.header.currency_rate) ? new BigDecimal(xml.header.currency_rate as String) : null
            currencyTransfer.amountTransferred = isNotEmpty(xml.header.amount_transferred) ? new BigDecimal(xml.header.amount_transferred as String) : null
            currencyTransfer.amountTransferredNat = isNotEmpty(xml.header.amount_transferred_nat) ? new BigDecimal(xml.header.amount_transferred_nat as String) : null
            currencyTransfer.currencyTransferDate = ReferenceUtils.gPathToLocalDate(xml.header.currency_transfer_date as String)

            currencyTransfer.clearanceDomiciliations = []
            xml.clearance_domiciliations.domiciliation.collect { domiciliation ->
                ClearanceDomiciliation clearanceDomiciliation = new ClearanceDomiciliation(
                        currencyTransfer: currencyTransfer,
                        ecReference: domiciliation.ec_reference as String,
                        ecDate: ReferenceUtils.gPathToLocalDate(domiciliation.ec_date as String),
                        domiciliationCodeBank: domiciliation.domiciliation_code_bank as String,
                        domiciliationDate: ReferenceUtils.gPathToLocalDate(domiciliation.domiciliation_date as String),
                        domiciliationNo: domiciliation.domiciliation_no as String,
                        domiciliatedAmounttInCurr: isNotEmpty(domiciliation.domiciliated_amountt_in_curr) ? new BigDecimal(domiciliation.domiciliated_amountt_in_curr as String) : null,
                        invoiceFinalAmountInCurr: isNotEmpty(domiciliation.invoice_final_amount_in_curr) ? new BigDecimal(domiciliation.invoice_final_amount_in_curr as String) : null,
                        repatriatedAmountToBank: isNotEmpty(domiciliation.repatriated_amount_to_bank) ? new BigDecimal(domiciliation.repatriated_amount_to_bank as String) : null,
                        amountTransferredInCurr: isNotEmpty(domiciliation.amount_transferred_in_curr) ? new BigDecimal(domiciliation.amount_transferred_in_curr as String) : null,
                )
                currencyTransfer.addClearanceDomiciliation(clearanceDomiciliation)
            }

            currencyTransfer.attachedDocs = []
            xml.attachments.attachment.collect { att ->
                CurrencyTransferAttachedDoc attachedDoc = new CurrencyTransferAttachedDoc(
                        currencyTransfer: currencyTransfer,
                        docRef: att.doc_ref as String,
                        docType: att.doc_type as String,
                        docTypeName: att.doc_type_name as String,
                        docDate: ReferenceUtils.gPathToLocalDate(att.doc_date as String)
                )
                currencyTransfer.addAttDoc(attachedDoc)
            }
        } else {
            currencyTransfer.errors.rejectValue('id', 'importXML.invalid.xml', 'Invalid XML File')
        }
        currencyTransfer
    }

    def buildCurrencyTransferXMLFile(CurrencyTransfer currencyTransfer) {
        LOGGER.debug("Get currency content for xml: {}", currencyTransfer)
        new StreamingMarkupBuilder().bind() {
            CURRENCY_TRANSFER {
                header {
                    bank_code(currencyTransfer.bankCode)
                    bank_name(currencyTransfer.bankName)
                    currency_code(currencyTransfer.currencyCode)
                    currency_name(currencyTransfer.currencyName)
                    currency_rate(currencyTransfer.currencyRate)
                    amount_transferred(currencyTransfer.amountTransferred)
                    amount_transferred_nat(currencyTransfer.amountTransferredNat)
                    currency_transfer_date(TypeCastUtils.formatDate(currencyTransfer.currencyTransferDate))
                }
                clearance_domiciliations {
                    currencyTransfer.clearanceDomiciliations.each { ClearanceDomiciliation clearanceDomiciliation ->
                        domiciliation {
                            ec_reference(clearanceDomiciliation.ecReference)
                            ec_date(TypeCastUtils.formatDate(clearanceDomiciliation.ecDate))
                            domiciliation_code_bank(clearanceDomiciliation.domiciliationCodeBank)
                            domiciliation_no(clearanceDomiciliation.domiciliationNo)
                            domiciliation_date(TypeCastUtils.formatDate(clearanceDomiciliation.domiciliationDate))
                            domiciliated_amountt_in_curr(clearanceDomiciliation.domiciliatedAmounttInCurr)
                            invoice_final_amount_in_curr(clearanceDomiciliation.invoiceFinalAmountInCurr)
                            repatriated_amount_to_bank(clearanceDomiciliation.repatriatedAmountToBank)
                            amount_transferred_in_curr(clearanceDomiciliation.amountTransferredInCurr)
                        }
                    }
                }
                attachments {
                    currencyTransfer.attachedDocs.each { CurrencyTransferAttachedDoc attachedDoc ->
                        attachment {
                            doc_ref(attachedDoc.docRef)
                            doc_type(attachedDoc.docType)
                            doc_type_name(attachedDoc.docTypeName)
                            doc_date(TypeCastUtils.formatDate(attachedDoc.docDate))
                        }
                    }
                }
            }
        }
    }

    def buildTransferOrder(TransferOrder transferOrder, MultipartFile xmlFile) {
        LOGGER.debug("begin buildTransferOrder() in ${XmlService}")
        GPathResult xml = new XmlSlurper().parseText(new String(xmlFile.bytes, StandardCharsets.UTF_8))
        LOGGER.debug("get transfer order content : {} and xml : {}", transferOrder, xml)
        if (xml.name() == TRANSFER_ORDER_XML_TAG) {
            transferOrder.eaReference = xml.header.ea_reference as String
            transferOrder.importerCode = xml.header.importer_code as String
            transferOrder.importerNameAddress = xml.header.importer_name_address as String
            transferOrder.countryBenefBankCode = xml.header.country_benef_bank_code as String
            transferOrder.countryBenefBankName = xml.header.country_benef_bank_name as String
            transferOrder.destinationBank = xml.header.destination_bank as String
            transferOrder.byCreditOfAccntOfCorsp = xml.header.by_credit_account as String
            transferOrder.bankAccntNoCredit = xml.header.bank_account_no_credit as String
            transferOrder.nameOfAccntHoldCredit = xml.header.name_account_hold_credit as String
            transferOrder.bankName = xml.header.bank_name as String
            transferOrder.bankCode = xml.header.bank_code as String
            transferOrder.bankAccntNoDebited = xml.header.bank_account_no_debit as String
            transferOrder.charges = xml.header.charges as String
            transferOrder.currencyPayCode = xml.header.currency_pay_code as String
            transferOrder.currencyPayName = xml.header.currency_pay_name as String
            transferOrder.ratePayment = isNotEmpty(xml.header.rate_payment) ? new BigDecimal(xml.header.rate_payment as String) : null
            if (UserUtils.isBankAgent()) {
                transferOrder.executionRef = xml.header.execution_ref as String
                transferOrder.executionDate = ReferenceUtils.gPathToLocalDate(xml.header.execution_date as String)
            }

            transferOrder.orderClearanceOfDoms = []
            xml.clearance_domiciliations.domiciliation.collect { domiciliation ->
                OrderClearanceOfDom clearance = new OrderClearanceOfDom(
                        transfer: transferOrder,
                        eaReference: domiciliation.ea_reference as String,
                        state: "0",
                        authorizationDate: ReferenceUtils.gPathToLocalDate(domiciliation.authorization_date as String),
                        bankName: domiciliation.bank_name as String,
                        registrationNoBank: domiciliation.registration_no_bank as String,
                        registrationDateBank: ReferenceUtils.gPathToLocalDate(domiciliation.registration_date_bank as String),
                        amountToBeSettledMentionedCurrency: isNotEmpty(domiciliation.amount_to_be_settle_mentioned_currency) ? new BigDecimal(domiciliation.amount_to_be_settle_mentioned_currency as String) : null
                )
                transferOrder.addOrderClearanceOfDoms(clearance)
            }

            transferOrder.attachedDocs = []
            xml.attachments.attachment.collect { att ->
                TransferAttachedDoc attachedDoc = new TransferAttachedDoc(
                        transfert: transferOrder,
                        docRef: att.doc_ref as String,
                        docType: att.doc_type as String,
                        docTypeName: att.doc_type_name as String,
                        docDate: ReferenceUtils.gPathToLocalDate(att.doc_date as String)
                )
                transferOrder.addAttDoc(attachedDoc)
            }

        } else {
            transferOrder.errors.rejectValue('id', 'importXML.invalid.xml', 'Invalid XML File')
        }
        transferOrder
    }

    def buildTransferOrderXMLFile(TransferOrder transferOrder) {
        LOGGER.debug("Get transfer order content for xml: {}", transferOrder)
        new StreamingMarkupBuilder().bind() {
            TRANSFER_ORDER {
                header {
                    ea_reference(transferOrder.eaReference)
                    importer_code(transferOrder.importerCode)
                    importer_name_address(transferOrder.importerNameAddress)
                    country_benef_bank_code(transferOrder.countryBenefBankCode)
                    country_benef_bank_name(transferOrder.countryBenefBankName)
                    destination_bank(transferOrder.destinationBank)
                    by_credit_account(transferOrder.byCreditOfAccntOfCorsp)
                    bank_account_no_credit(transferOrder.bankAccntNoCredit)
                    name_account_hold_credit(transferOrder.nameOfAccntHoldCredit)
                    bank_name(transferOrder.bankName)
                    bank_code(transferOrder.bankCode)
                    bank_account_no_debit(transferOrder.bankAccntNoDebited)
                    charges(transferOrder.charges)
                    currency_pay_code(transferOrder.currencyPayCode)
                    currency_pay_name(transferOrder.currencyPayName)
                    rate_payment(transferOrder.ratePayment)
                    if (UserUtils.isBankAgent()) {
                        execution_ref(transferOrder.executionRef)
                        execution_date(TypeCastUtils.formatDate(transferOrder.executionDate))
                    }
                }

                clearance_domiciliations {
                    transferOrder.orderClearanceOfDoms.each { OrderClearanceOfDom clearance ->
                        domiciliation {
                            ea_reference(clearance.eaReference)
                            authorization_date(TypeCastUtils.formatDate(clearance.authorizationDate))
                            bank_name(clearance.bankName)
                            registration_no_bank(clearance.registrationNoBank)
                            registration_date_bank(TypeCastUtils.formatDate(clearance.registrationDateBank))
                            amount_to_be_settle_mentioned_currency(clearance.amountToBeSettledMentionedCurrency)
                        }
                    }
                }
                attachments {
                    transferOrder.attachedDocs.each { TransferAttachedDoc attachedDoc ->
                        attachment {
                            doc_ref(attachedDoc.docRef)
                            doc_type(attachedDoc.docType)
                            doc_type_name(attachedDoc.docTypeName)
                            doc_date(TypeCastUtils.formatDate(attachedDoc.docDate))
                        }
                    }
                }
            }
        }
    }

    def isNotEmpty(field) {
        field as String != ""
    }

    String objectToXml(domainInstance, domainName) {
        return XmlUtil.serialize(DocXmlBinder.bindDomainToXml(domainInstance, domainName))
    }

    def buildObjectInstance(GPathResult importedXml, String domainName) throws RuntimeException {
        def domainInstance = DocXmlBinder.bindXmlToDomain(importedXml, domainName)
        domainInstance
    }

    def getDomain(String domainName, id, params) {
        def domainInstance
        def idParams = params.id[0]
        if (domainName.toUpperCase()  == EXCHANGE.toUpperCase()) {
            domainInstance = idParams ? exchangeService.loadExchange(Long.parseLong(idParams)) : exchangeService.findFromSessionStore(params.converstionId)
            if(!domainInstance){
                domainInstance = Exchange.get(id)
            }
        }else if (domainName.toUpperCase()  == REPATRIATION.toUpperCase()){
            domainInstance = idParams ? repatriationService.loadRepatriation(Long.parseLong(idParams)) : repatriationService.findFromSessionStore(params.converstionId)
            if(!domainInstance){
                domainInstance = Repatriation.get(id)
            }
        }
        domainInstance
    }


    def getXml(domainInstance, String domainName) {
        objectToXml(domainInstance, domainName.toUpperCase())
    }

    def importFileXml(request, docType) {
        def domainInstance
        if (request.multipartFileUploadException) {
            LOGGER.warn("User's ${springSecurityService?.principal?.username} xml file upload finished with - ${request.multipartFileUploadException}")
            return ""
        }

        if (request instanceof SecurityContextHolderAwareRequestWrapper) {
            return ""
        }
        MultipartFile file = request.getFile('xmlFile')
        if (file == null) {
            def error
            LOGGER.debug("User ${springSecurityService?.principal?.username} tried upload xml document file: Invalid Content in Request")
            error = "default.xml.invalidContent.message"
            return error
        }
        GPathResult importedXml = new XmlSlurper().parseText(TypeCastUtils.toUTF8String(file.bytes))

        def errorMessage = xmlValidationService.validateContent(importedXml, docType)

        if (errorMessage == null) {
            domainInstance = buildObjectInstance(importedXml, docType)
        } else{
            domainInstance.errors.reject('default.xml.invalidContent.message', 'Imported xml file contains invalid node')
        }
        domainInstance
    }

    def exportDomainToXml(domainName, params) {
        def xml
        Integer id = TypeCastUtils.toInteger(params?.id)
        def domainInstance = getDomain(domainName, id, params)

        if (domainInstance) {
            xml = getXml(domainInstance, domainName)
            xml
        }
    }
}
