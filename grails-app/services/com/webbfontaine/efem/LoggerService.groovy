package com.webbfontaine.efem

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime

@Slf4j("LOGGER")
@Transactional
class LoggerService {

    def springSecurityService

    def saveDocumentHistory(domainInstance, String initialStatus, commitOperation, user = null) {
        LOGGER.debug("Creating transaction log for  {} ", domainInstance)
        def log = domainInstance?.instanceLog()
        log.operation = commitOperation
        log.initialStatus = initialStatus
        log.endStatus = domainInstance?.status
        log.userLogin = user ?: springSecurityService?.principal?.username
        log.operationDate = new LocalDateTime()
        domainInstance?.addToLogs(log)
    }

    void addMessage(domainInstance, String message) {
        LOGGER.debug("Adding user message for  {} ", domainInstance)
        def log = domainInstance?.instanceLog()
        log.userLogin = springSecurityService?.principal?.username
        log.operationDate = new LocalDateTime()
        log.logMessage = message
        domainInstance?.addToLogs(log)
    }

    def removeLastLog(domainInstance) {
        domainInstance?.logs?.remove(domainInstance?.logs?.last())
    }

}
