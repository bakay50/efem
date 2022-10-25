package com.webbfontaine.efem

import static com.webbfontaine.efem.constant.MailConstants.*
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.notif.ParameterCommand
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.joda.time.LocalDate
import org.springframework.scheduling.annotation.Async
import org.joda.time.format.DateTimeFormat

import static com.webbfontaine.efem.workflow.Operations.*
import static com.webbfontaine.efem.constants.UtilConstants.FORMAT_DATE

/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/10/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
@Transactional
class NotificationService implements GrailsConfigurationAware {

    private static String EMAIL_FROM
    private static String NOTIF_URL
    private static boolean EMAIL_ENABLED
    private static String START_REQUESTDATE_SEARCH
    def mailService
    def groovyPageRenderer
    def mailRecipientService
    CallApiNotifService callApiNotifService

    @Override
    void setConfiguration(Config config) {
        EMAIL_ENABLED = AppConfig.resolveEmailEnabled()
        EMAIL_FROM = AppConfig.resolveEmailFrom()
        NOTIF_URL = AppConfig.getNotifUrl()
        START_REQUESTDATE_SEARCH = AppConfig.resolveStartRequestDate() ?: "01/01/2019"

    }

    def notify(def domainInstance, String operationType) {
        switch (operationType) {
            case EX_REQUEST:
                notifyAboutRequestOperation(domainInstance)
                break
            case EX_APPROVE:
                notifyAboutApproveOperation(domainInstance)
                break
            case EX_UPDATE:
                notifyAboutUpdateOperation(domainInstance)
                break
            case EX_QUERY:
                notifyAboutQueryOperation(domainInstance)
                break
            case EX_CANCEL:
                notifyAboutCancelOperation(domainInstance)
                break
            case EX_REJECT:
                notifyAboutRejectOperation(domainInstance)
                break
            case TR_REQUEST:
                notifyAboutOrderRequestOperation(domainInstance)
                break
            case TR_VALIDATE:
                notifyAboutOrderValidateOperation(domainInstance)
                break
            case TR_QUERY:
                notifyAboutOrderQueryOperation(domainInstance)
                break
            case TR_CANCEL:
                notifyAboutOrderCancelOperation(domainInstance)
                break
            case TR_UPDATE:
                notifyAboutOrderUpdateOperation(domainInstance)
                break
            case REP_DECLARE:
                notifyAboutRepatriationDeclareOperation(domainInstance)
                break
            case REP_CONFIRM:
                notifyAboutRepatriationConfirmOperation(domainInstance)
                break
            case REP_QUERY:
                notifyAboutRepatriationQueryOperation(domainInstance)
                break
            case REP_CANCEL:
                notifyAboutRepatriationCancelOperation(domainInstance)
                break
            case REP_UPDATE:
                notifyAboutRepatriationUpdateOperation(domainInstance)
                break
            default:
                break
        }
    }


    void notifyAboutRequestOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_REQUEST)) return;
        def resolvedRecipients = mailRecipientService.resolveRequestOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutApproveOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_APPROVE)) return;
        def resolvedRecipients = mailRecipientService.resolveApproveOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutUpdateOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_UPDATE)) return;
        def resolvedRecipients = mailRecipientService.resolveUpdateOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutQueryOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_QUERY)) return;
        def resolvedRecipients = mailRecipientService.resolveQueryOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutCancelOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_CANCEL)) return;
        def resolvedRecipients = mailRecipientService.resolveCancelOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutRejectOperation(Exchange exchange) {
        if (!EMAIL_ENABLED || !AppConfig.resolveNotificationEnabledForOperation(OP_REJECT)) return;
        def resolvedRecipients = mailRecipientService.resolveRejectOperationRecipients(exchange)
        resolvedRecipients.each {
            ParameterCommand parameterCommand = it as ParameterCommand
            callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
        }
    }

    void notifyAboutOrderRequestOperation(TransferOrder transferOrder) {
        if (EMAIL_ENABLED && AppConfig.resolveOrderNotificationEnabledForOperation(OP_REQUEST)) {
            def resolvedRecipients = mailRecipientService.resolveOrderRequestedOperationRecipients(transferOrder)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutOrderValidateOperation(TransferOrder transferOrder) {
        if (EMAIL_ENABLED && AppConfig.resolveOrderNotificationEnabledForOperation(OP_VALIDATE)) {
            def resolvedRecipients = mailRecipientService.resolveOrderValidatedOperationRecipients(transferOrder)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutOrderQueryOperation(TransferOrder transferOrder) {
        if (EMAIL_ENABLED && AppConfig.resolveOrderNotificationEnabledForOperation(OP_QUERY)) {
            def resolvedRecipients = mailRecipientService.resolveOrderQueriedOperationRecipients(transferOrder)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutOrderCancelOperation(TransferOrder transferOrder, String iniatlStatus) {
        if (EMAIL_ENABLED && AppConfig.resolveOrderNotificationEnabledForOperation(OP_CANCEL)) {
            def resolvedRecipients = mailRecipientService.resolveOrderCancelledOperationRecipients(transferOrder, iniatlStatus)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutOrderUpdateOperation(TransferOrder transferOrder) {
        if (EMAIL_ENABLED && AppConfig.resolveOrderNotificationEnabledForOperation(OP_UPDATE)) {
            def resolvedRecipients = mailRecipientService.resolveOrderUpdatOperationRecipients(transferOrder)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutRepatriationDeclareOperation(Repatriation repatriation) {
        if (EMAIL_ENABLED && AppConfig.resolveRepatriationNotificationEnabledForOperation(OP_DECLARE)) {
            def resolvedRecipients = mailRecipientService.resolveRepatriationDeclareOperationRecipients(repatriation)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutRepatriationConfirmOperation(Repatriation repatriation) {
        if (EMAIL_ENABLED && AppConfig.resolveRepatriationNotificationEnabledForOperation(OP_CONFIRM)) {
            def resolvedRecipients = mailRecipientService.resolveRepatriationConfirmOperationRecipients(repatriation)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutRepatriationQueryOperation(Repatriation repatriation) {
        if (EMAIL_ENABLED && AppConfig.resolveRepatriationNotificationEnabledForOperation(OP_QUERY)) {
            def resolvedRecipients = mailRecipientService.resolveRepatriationQueryOperationRecipients(repatriation)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutRepatriationCancelOperation(Repatriation repatriation) {
        if (EMAIL_ENABLED && AppConfig.resolveRepatriationNotificationEnabledForOperation(OP_CANCEL)) {
            def resolvedRecipients = mailRecipientService.resolveRepatriationCancelOperationRecipients(repatriation)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    void notifyAboutRepatriationUpdateOperation(Repatriation repatriation) {
        if (EMAIL_ENABLED && AppConfig.resolveRepatriationNotificationEnabledForOperation(OP_UPDATE)) {
            def resolvedRecipients = mailRecipientService.resolveRepatriationUpdateOperationRecipients(repatriation)
            resolvedRecipients.each {
                ParameterCommand parameterCommand = it as ParameterCommand
                callApiNotifService.sendnotif(parameterCommand, NOTIF_URL)
            }
        }
    }

    @Async
    void sendEmail(def parameters) {
        try {
            def emailMessageBody = "efem.mail.${parameters.mailType}.body"
            mailService.sendMail {
                to parameters.recipientEmail
                from EMAIL_FROM
                subject message("efem.mail.${parameters.mailType}.subject", parameters.subjectArgs)
                html groovyPageRenderer.render(view: "/notification/_mailNotification",
                        model: [messageBody: emailMessageBody, messageArgs: parameters.bodyArgs])
            }
            LOGGER.debug("Message has been sent to ${parameters.recipientEmail} as ${parameters.mailType} type")
        } catch (e) {
            LOGGER.error("", e)
        }
    }

    private static String message(String messageCode, Object args) {
        def messageSource = Holders.applicationContext.messageSource
        messageSource.getMessage(messageCode, args as Object[], '', Locale.FRENCH)
    }

    def handleNotificateCriteria(BuildableCriteria notificationCriteria, String fieldName, userProp) {
        notificationCriteria.count() {
            notificationCriteria.eq('requestType', ExchangeRequestType.EC)
            notificationCriteria.ge('requestDate', LocalDate.parse(START_REQUESTDATE_SEARCH, DateTimeFormat.forPattern(FORMAT_DATE)))
            notificationCriteria.or {
                notificationCriteria.and {
                    notificationCriteria.eq('status', Statuses.ST_APPROVED)
                    notificationCriteria.isNotNull('declarationSerial')
                    notificationCriteria.isNotNull('declarationNumber')
                    notificationCriteria.isNotNull('declarationDate')
                    notificationCriteria.isNotNull('dateOfBoarding')
                }
                notificationCriteria.and {
                    notificationCriteria.eq('status', Statuses.ST_EXECUTED)
                    notificationCriteria.isNotNull('balanceAs')
                }
            }
            if (userProp.contains(UserProperties.ALL)) {
                notificationCriteria.isNotNull(fieldName)
            } else {
                notificationCriteria.in(fieldName, userProp)
            }
        }
    }

    def handleQueryNotification(userType, userProps) {
        def notifications = 0
        BuildableCriteria exchangeNotificationCriteria = Exchange.createCriteria()
        if (userType == UserProperties.ADB) {
            notifications = handleNotificateCriteria(exchangeNotificationCriteria, 'bankCode', userProps)
        } else if (userType == UserProperties.TIN) {
            notifications = handleNotificateCriteria(exchangeNotificationCriteria, 'exporterCode', userProps)
        }
        notifications
    }
}
