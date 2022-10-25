package com.webbfontaine.efem.attachment

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.attachedDoc.AttachedFile
import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.attachedDoc.RepAttachedDoc
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.rules.attachedDoc.AttachedDocRule
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBindingUtils
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import static com.webbfontaine.efem.constants.DocumentTypes.*

@Transactional
@Slf4j('LOGGER')
class AttachmentService {

    def grailsApplication
    def exchangeService
    def repatriationService
    def transferOrderService
    def currencyTransferService

    def addAttDoc(domain, attachedDocInstance) {
        LOGGER.debug("in addAttDoc() of ${AttachmentService}");
        grailsApplication.mainContext.getBean(AttachedDocRule)?.apply(new RuleContext(attachedDocInstance, attachedDocInstance.errors, null))
        boolean hasErrors = attachedDocInstance.hasErrors()
        if (!hasErrors) {
            domain.addAttDoc(attachedDocInstance)
        }
        attachedDocInstance
    }

    def editAttDoc(domain) {
        LOGGER.debug("in editAttDoc() of ${AttachmentService}");
        def paramsRank = currentRequestParams().rank ?: 0
        String docTypeName = currentRequestParams().docTypeName ?: ''
        def attDoc = domain.createAttachedDoc(currentRequestParams())
        grailsApplication.mainContext.getBean(AttachedDocRule)?.apply(new RuleContext(attDoc, attDoc.errors, null))
        if (!attDoc.hasErrors()) {
            if (isDuplicateRecord(domain.attachedDocs, paramsRank as Integer, 'docTypeName', docTypeName)) {
                LOGGER.warn('attachedDoc with docTypeName {} already exist in the list', docTypeName)
                attDoc.errors.rejectValue('docType', 'attachedDoc.errors.duplicate', 'AttDoc already exist in the list')
            } else {
                attDoc = domain.getAttDoc(paramsRank as Integer)
                if (attDoc) {
                    LOGGER.debug('updating attachedDoc in list with details : {}', currentRequestParams())
                    DataBindingUtils.bindObjectToInstance(attDoc, currentRequestParams())
                } else {
                    LOGGER.warn('cannot find attachedDoc in attachedDoc list with rank = {} docTypeName = {}', paramsRank, docTypeName)
                    attDoc.errors.rejectValue('rank', 'attachedDoc.errors.notFound', 'Cannot find Attached Document from the list')
                }
            }
        }

        attDoc
    }

    def deleteAttDoc(domain, def rank) {
        LOGGER.debug("in deleteAttDoc() of ${AttachmentService}");
        def attDoc = domain.attachedDocs.find {
            it.rank == rank as Integer
        }
        if (attDoc) {
            domain.removeFromList(domain.attachedDocs, attDoc)
            return
        } else {
            LOGGER.warn('cannot find attachedDoc in attachedDoc list with rank = {}, docTypeName = {}', rank)
            attDoc = domain.createAttachedDoc(null)
            attDoc.errors.rejectValue('rank', 'attachedDoc.errors.notFound', 'Cannot find Attached Document from the list')
        }
        attDoc
    }

    private static boolean isDuplicateRecord(subDocuments, int subDocumentRank, String keyField, def keyValue) {
        LOGGER.debug("Checking duplicate records");
        boolean duplicate = false
        for (obj in subDocuments) {
            if (obj.rank != subDocumentRank) {
                if (obj."$keyField".equals(keyValue)) {
                    duplicate = true
                    break
                }
            }
        }
        duplicate
    }

    private static Map currentRequestParams() {
        GrailsWebRequest.lookup().params
    }

    def getDomainInstance(attDoc) {
        if (attDoc instanceof RepAttachedDoc) {
            return attDoc.repats
        } else if (attDoc instanceof AttachedDoc){
            return attDoc.exchange
        } else if (attDoc instanceof CurrencyTransferAttachedDoc) {
            return  attDoc.currencyTransfer
        } else {
            return attDoc.transfert
        }
    }

    def getAttachedFileInstance(domain,attachedDoc,params){
        attachedDoc = domain.createAttachedDoc(params)
        attachedDoc.attachedFile = new AttachedFile(upl_fil: session.uploadedFile)
        return attachedDoc
    }

    def getDomainFromSession(params){
        def domain
        if (params?.domainType?.equals(EXCHANGE.getLabel())) {
            domain = exchangeService.findFromSessionStore(params?.conversationId)
        } else if (params?.domainType?.equals(REPATRIATION.getLabel())) {
            domain = repatriationService.findFromSessionStore(params?.conversationId)
        } else if (params?.domainType?.equals(CURRENCY_TRANSFER.getLabel())) {
            domain = currencyTransferService.findFromSessionStore(params?.conversationId)
        } else {
            domain = transferOrderService.findFromSessionStore(params?.conversationId)
        }
        return domain
    }

    def getNewInstance(domainInstance){
        def domain
        if (domainInstance instanceof Exchange) {
            domain = Exchange.newInstance()
        } else if(domainInstance instanceof Repatriation){
            domain = Repatriation.newInstance()
        } else if (domainInstance instanceof CurrencyTransfer) {
            domain = CurrencyTransfer.newInstance()
        } else {
            domain = TransferOrder.newInstance()
        }
        domain
    }

    static HttpSession getSession() {
            return RequestContextHolder.currentRequestAttributes().getSession()
    }

    String getDomainTypeName(Object domain) {
        def domainType
        if (domain instanceof Exchange) {
            domainType = EXCHANGE.getLabel()
        } else if (domain instanceof Repatriation) {
            domainType = REPATRIATION.getLabel()
        } else if (domain instanceof CurrencyTransfer) {
            domainType = CURRENCY_TRANSFER.getLabel()
        } else {
            domainType = TRANSFER.getLabel()
        }
        domainType
    }
}
