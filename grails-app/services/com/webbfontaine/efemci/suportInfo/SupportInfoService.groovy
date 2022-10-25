package com.webbfontaine.efemci.suportInfo

import groovy.util.logging.Slf4j

/*
import com.webbfontaine.workflow.transaction.TransactionRecord
import com.webbfontaine.efemci.Exchange
import com.webbfontaine.efemci.SupportInfo
import com.webbfontaine.efemci.repatriation.RepSupportInfo
import com.webbfontaine.efemci.repatriation.Repatriation
import grails.async.DelegateAsync
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import com.webbfontaine.workflow.transaction.TransactionLogService
*/
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Allan Apor Jr.
 * Date: 10/1/14
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class SupportInfoService {
/*
    private static final LOGGER = LoggerFactory.getLogger(SupportInfoService);
    static OTHER_OPERATIONS = [OP_QUERY,OP_REJECT,OA_REJECT_PARTIAL_APPROVED,OA_QUERY_PARTIAL_APPROVED,OA_CANCEL_APPROVED]
    static OTHER_REPART_OPERATIONS = [OP_QUERY,OA_QUERY_DECLARED,OP_CANCEL,OA_CANCEL_CONFIRMED]

    @DelegateAsync TransactionLogService transactionLogService
    def springSecurityService

    void saveDocumentHistoryAsync(domainInstance, String startStatus, String commitOperationName,String message) {
        LOGGER.debug("in saveHistory() of ${SupportInfoService}");
        if(domainInstance instanceof Exchange){
            LOGGER.info("Exchange Instance")
            TransactionRecord transactionRecord
            if (commitOperationName in OTHER_OPERATIONS){
                transactionRecord = new SupportInfo()
                transactionRecord.messageText = message.trim()
                //transactionRecord.messageDate=new LocalDateTime()
            }else{
                transactionRecord = new TransactionRecord()
            }

            transactionRecord.userLogin = springSecurityService?.principal?.username
            transactionRecord.initialStatus = startStatus
            transactionRecord.endStatus = domainInstance.status
            transactionRecord.operation = commitOperationName
            transactionRecord.operationDate = new LocalDateTime()
            transactionRecord.documentId = domainInstance.id
            transactionLogService.logTransactionAsync(transactionRecord)
        }else if(domainInstance instanceof Repatriation){
            LOGGER.info("Repatriation Instance")
            LOGGER.info("commitOperationName : ${commitOperationName}")
            Repatriation domainInstances = (Repatriation)domainInstance
            TransactionRecord transactionRecord
            if (commitOperationName in OTHER_REPART_OPERATIONS){
                transactionRecord = new RepSupportInfo()
                transactionRecord.messageText = message.trim()
            }else{
                transactionRecord = new RepSupportInfo()
                transactionRecord.typeDoc = "repatriation"
            }
            transactionRecord.userLogin = springSecurityService?.principal?.username
            transactionRecord.initialStatus = startStatus
            transactionRecord.endStatus = domainInstances.status
            transactionRecord.operation = commitOperationName
            transactionRecord.operationDate = new LocalDateTime()
            transactionRecord.documentId = domainInstances.id
            transactionLogService.logTransactionAsync(transactionRecord)
        }

    }


    List<TransactionRecord> getDocumentHistories(domainInstance) {
        LOGGER.debug("in getDocumentHistory() of ${SupportInfoService}");

        List<TransactionRecord> transactionRecords
        try {
            def query = TransactionRecord.where {

                documentId == domainInstance?.id
            }
            transactionRecords = query.list(sort:'operationDate', order: 'asc')
            if (transactionRecords) {
                LOGGER.debug("Returning document history with details: {}", transactionRecords)
            }

        } catch (ex) {
            LOGGER.warn("Cannot retrieve document history for document {} with id {}", domainInstance.class.name, domainInstance.id)
            ex.printStackTrace()
        }
        transactionRecords
    }


    /* *********** Just added for support infos simulation..
      That method must be deleted  when support information is implemented for repatriation
     * ********************/
 /*   List<TransactionRecord> createHistories(domainInstance) {
        LOGGER.debug("in createHistories() of ${SupportInfoService}");
        List<TransactionRecord> transactionRecords
        try {
            def query = TransactionRecord.where {

                documentId == domainInstance?.id
            }
            transactionRecords = query.list(sort:'operationDate', order: 'asc')
            if (transactionRecords) {
                LOGGER.debug("Returning document history with details: {}", transactionRecords)
            }

        } catch (ex) {
            LOGGER.warn("Cannot retrieve document history for document {} with id {}", domainInstance.class.name, domainInstance.id)
            ex.printStackTrace()
        }
        transactionRecords
    }
*/
}
