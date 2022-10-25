package com.webbfontaine.efemci

import grails.gorm.transactions.Transactional

/*
import com.webbfontaine.efemci.repatriation.RepAttachedDoc
import com.webbfontaine.efemci.repatriation.Repatriation
import com.webbfontaine.sw.rimm.RimmAtd
import org.codehaus.groovy.grails.web.binding.DataBindingUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.slf4j.LoggerFactory
*/
@Transactional
class ExcSubDocumentService {
/*
    private static final LOGGER = LoggerFactory.getLogger(ExcSubDocumentService);
    def rimmValueCheckService
    def rimmService
    
    def editAttDoc(domainInstance) {
        LOGGER.debug("in editAttDoc() of ${ExcSubDocumentService}");
        def docTypeNames = ''
        def paramsRank = currentRequestParams().rank ?: 0

        String docTypeName = currentRequestParams().docTypeName ?: ''
        String docCode = currentRequestParams().docCode ?: ''
        if(docCode){
            docTypeNames = rimmService.findRefIdName(RimmAtd, 'atd_cod', 'atd_dsc', docCode)
        }
        if(docTypeNames){
            currentRequestParams().docTypeName = docTypeNames
        }
        def attDoc
        if(domainInstance instanceof Exchange){
            attDoc = new AttachedDoc(currentRequestParams())
        }else if(domainInstance instanceof Repatriation){
            attDoc = new RepAttachedDoc(currentRequestParams())
        }

        if(rimmValueCheckService.checkOrRetriveInstance(RimmAtd,'atd_cod',docCode,null,"Boolean") == false){
            LOGGER.warn("attachedDoc type found in domainInstance with docTypeName not exist {}", docCode)
            attDoc.docTypeName = null
            attDoc.errors.rejectValue('docTypeName', 'attachedDoc.errors.invalidcode', 'Invalid Code attachments type')
        }else {
            attDoc = domainInstance.getAttDoc(paramsRank as Integer)
            if (!attDoc) {
                LOGGER.warn('cannot find attachedDoc in attachedDoc list with rank = {} docTypeName = {}', paramsRank, docTypeName)
                attDoc.errors.rejectValue('rank', 'attachedDoc.errors.notFound', 'Cannot find attachedDoc from the list')
            } else {
                LOGGER.debug('updating attachedDoc in list with details : {}', currentRequestParams())
                DataBindingUtils.bindObjectToInstance(attDoc, currentRequestParams())
            }
        }
        attDoc
    }

    def deleteAttDoc(domainInstance) {
        LOGGER.debug("in deleteAttDoc() of ${ExcSubDocumentService}");

        def paramsRank = currentRequestParams().rank ?: 0
        String paramsAttdocTypeName = currentRequestParams().docTypeName
        def attDoc

        if(domainInstance instanceof Exchange){
             attDoc = domainInstance.attachedDocs.find{it.rank == paramsRank as Integer}
        }else if(domainInstance instanceof Repatriation){
             attDoc = domainInstance.repAttachedDocs.find{it.rank == paramsRank as Integer}
        }

        if (attDoc) {
            if(domainInstance instanceof Exchange){
                domainInstance.removeFromList(domainInstance.attachedDocs, attDoc)
            }else if(domainInstance instanceof Repatriation){
                domainInstance.removeFromList(domainInstance?.repAttachedDocs, attDoc)
            }
            return null
        } else {
            LOGGER.warn('cannot find attachedDoc in attachedDoc list with rank = {}, docTypeName = {}', paramsRank, paramsAttdocTypeName)
            if(domainInstance instanceof Exchange){
                attDoc = AttachedDoc.newInstance()

            }else if(domainInstance instanceof Repatriation){
                attDoc = RepAttachedDoc.newInstance()
            }
            attDoc.errors.rejectValue('rank', 'attachedDoc.errors.notFound', 'Cannot find attachedDoc from the list')
        }
        attDoc
    }

    private static boolean isDuplicateRecord(List subDocuments, int subDocumentRank, String keyField, def keyValue) {
        LOGGER.debug("in isDuplicateRecord() of ${ExcSubDocumentService}");
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
    */
}
