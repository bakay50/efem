package com.webbfontaine.efem

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper

@Slf4j('LOGGER')
class AttachedDocController {
    def attachmentService
    def attachmentFileUploadBase
    def springSecurityService

    def uploadAttDoc() {
        if (request?.multipartFileUploadException) {
            LOGGER.warn("User's ${springSecurityService?.principal?.username} file upload finished with - ${request.multipartFileUploadException}")
            render getResponseErrorData(message(code: 'attachedDoc.error.message', default: 'File exceeds the allowable size (2 MB).')) as JSON
            return
        }
        if (request instanceof SecurityContextHolderAwareRequestWrapper) {
            render getResponseErrorData(message(code: 'invalid.request', default: 'Invalid Request')) as JSON
            return
        }
        byte[] attDocBytes
        attDocBytes = attachmentFileUploadBase.getFile(request, 'attDoc')
        if (attDocBytes == null) {
            LOGGER.debug("User ${springSecurityService?.principal?.username} tried upload attached document file: Invalid Content in Request")
            render getResponseErrorData(message(code: 'invalid.request', default: 'Invalid Request')) as JSON
        }
        if (attachmentFileUploadBase.getValidationResult().hasErrors()) {
            render getResponseErrorData(attachmentFileUploadBase.getValidationResult().getErrors()[0].message) as JSON
        }
        session.uploadedFile = attDocBytes
        LOGGER.debug("User ${springSecurityService?.principal?.username} uploaded attached file, size = ${(attDocBytes?.size() ? attDocBytes?.size() / 1024 : 0).intValue()} kB.")
        render "OK"
    }

    def getResponseErrorData(String message) {
        def responseErrorData = [
                error       : 'error',
                responseData: wf.alert([class: "alert-danger errorContainer"], message)
        ]
        responseErrorData
    }

    def getDomainResponse(domain) {
        def response
        String domainType = attachmentService.getDomainTypeName(domain)
        response = [attachmentData: g.render(template: '/attachedDoc/attachedDocList', model: [domainInstance: domain, attDocList: domain.attachedDocs,domainType:domainType])]
        return response
    }

    def addAttDoc() {
        LOGGER.debug("in addAttDoc() of ${AttachedDocController}");
        def domain = attachmentService.getDomainFromSession(params)
        if (domain) {
            def attachedDoc
            attachedDoc = attachmentService.getAttachedFileInstance(domain, attachedDoc, params)
            attachmentService.addAttDoc(domain, attachedDoc)
            session.uploadedFile = null
            attachedDoc.isUpdated = true
            if (attachedDoc.hasErrors()) {
                def responseErrorData = [
                        error       : 'error',
                        responseData: g.render(template: '/attachedDoc/attachedDocError', model: [attDoc: attachedDoc])
                ]
                render responseErrorData as JSON
            } else {
                LOGGER.debug("sending response data with new attachedDoc {}", attachedDoc)
                def response = getDomainResponse(domain)
                render response as JSON
            }
        } else {
            domainInstanceNotFound(domain)
        }
    }

    def updateAttDoc() {
        LOGGER.debug("in updateAttDoc() of ${AttachedDocController}");
        def domain = attachmentService.getDomainFromSession(params)
        def attachedDoc
        def attDoc
        def attachedFile
        if (domain) {
            attachedDoc = attachmentService.editAttDoc(domain)
            attDoc = domain.getAttDoc(attachedDoc?.rank)
            attachedFile = attDoc?.attachedFile

            if (session.uploadedFile) {
                attachedFile.upl_fil = session.uploadedFile
                attachedDoc.attachedFile = attachedFile
                session.uploadedFile = null
            }
            attachedDoc.attachedFile = attachedFile
            attachedDoc.isUpdated = true

            if (attachedDoc.hasErrors()) {

                LOGGER.warn('errors found during modification of attachedDoc with docType = {} docTypeName = {} docRef = {} docDate = {}', params.docType, params.docTypeName, params.docRef, params.docDate)
                def responseErrorData = [
                        error       : 'error',
                        responseData: g.render(template: '/attachedDoc/attachedDocError', model: [attDoc: attachedDoc])
                ]
                render responseErrorData as JSON
            } else {
                LOGGER.debug("sending response data with new attachedDoc {}", attachedDoc)
                def domainType = attachmentService.getDomainTypeName(domain)
                render(template: '/attachedDoc/attachedDocList', model: [domainInstance: domain, attDocList: domain.attachedDocs, domainType: domainType])

            }
        } else {
            domainInstanceNotFound(domain)
        }
    }

    def cancelEditAttDoc() {
        LOGGER.debug("in cancelEditAttDoc() of ${AttachedDocController}");
        def domain = attachmentService.getDomainFromSession(params)
        if (domain) {
            LOGGER.debug("sending response data with attachedDoc {}", domain.attachedDocs)
            render(template: '/attachedDoc/attachedDocList', model: [domainInstance: domain, attDocList: domain.attachedDocs])
        } else {
            domainInstanceNotFound(domain)
        }
    }

    def deleteAttDoc() {
        LOGGER.debug("in deleteAttDoc() of ${AttachedDocController}");
        def domain = attachmentService.getDomainFromSession(params)
        if (domain) {
            def attachedDoc = attachmentService.deleteAttDoc(domain, params?.rank)
            if (attachedDoc) {
                LOGGER.warn('errors found during modification of attachedDoc with docType = {} docTypeName = {} docRef = {} docDate = {}', params.docType, params.docTypeName, params.docRef, params.docDate)
                def responseErrorData = [
                        error       : 'error',
                        responseData: g.render(template: '/attachedDoc/attachedDocError', model: [attDoc: attachedDoc])
                ]
                render responseErrorData as JSON
            } else {
                LOGGER.debug("sending response data with new attachedDoc {}", attachedDoc)
                render(template: '/attachedDoc/attachedDocList', model: [domainInstance: domain, attDocList: domain.attachedDocs])
            }
        } else {
            domainInstanceNotFound(domain)
        }
    }

    def downloadAttDoc() {
        LOGGER.debug("in downloadAttDoc() of ${AttachedDocController}");
        Integer crtRank = params.id.toInteger()
        def domain = attachmentService.getDomainFromSession(params)
        def currentDoc = null
        def fileName
        for (doc in domain?.attachedDocs) {
            if (doc?.rank == crtRank) {
                currentDoc = doc
                fileName = doc?.docTypeName?.replaceAll("\"", " ") + "." + doc?.fileExtension
                break
            }
        }
        def attachedDoc = (domain?.status ? domain?.getAttDoc(crtRank) : currentDoc)
        if (attachedDoc) {
            response.setContentType("APPLICATION/OCTET-STREAM")
            response.setHeader("Content-disposition", "attachedDoc; filename=\"${fileName}\"")
            response.contentType = currentDoc?.fileExtension
            response.outputStream << attachedDoc?.attachedFile?.upl_fil
        }
    }

    private def domainInstanceNotFound(domainInstance) {
        LOGGER.error("cannot find conversational instance of exchange")
        def domain = attachmentService.getNewInstance(domainInstance)
        domain.setIsDocumentEditable(false)
        domain.errors.rejectValue('id', 'exch.errors.conversationExpired', 'Your session to the document has expired. Please retrieve the document and try again.')
        def responseErrorData = [error: 'error', responseData: g.render(template: '/attachedDoc/attachedDocError', model: [attDoc: domain])]
        render responseErrorData as JSON
    }

}
