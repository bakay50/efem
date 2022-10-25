package com.webbfontaine.efem

import grails.async.Promises
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager


class MessagingNotifService {

    private static Logger LOGGER = LoggerFactory.getLogger(MessagingNotifService.class)

    void sendAfterCommit(domainInstance, String operationType) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        void afterCommit() {
                            Promises.task {
                                try {
                                    sendNotifEmail(domainInstance, operationType)
                                } catch (e) {
                                    LOGGER.error("", e)
                                }
                            }
                        }
                    }
            )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void sendNotifEmail(domainInstance, String operationType) {
        Holders.applicationContext.getBean("notificationService").notify(domainInstance, operationType)
    }

}
