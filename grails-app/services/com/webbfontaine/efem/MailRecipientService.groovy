package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.security.Roles
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.rimm.bnk.Bank
import com.webbfontaine.grails.plugins.rimm.cmp.Company
import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import groovy.util.logging.Slf4j
import com.webbfontaine.efem.constants.EmailConstants
import static com.webbfontaine.efem.UserUtils.isBankAgent
import static com.webbfontaine.efem.UserUtils.isTrader
import static com.webbfontaine.efem.UserUtils.isDeclarant
import static com.webbfontaine.efem.UserUtils.isSuperAdministrator
import static com.webbfontaine.efem.UserUtils.isGovOfficer
import static com.webbfontaine.efem.constants.Statuses.ST_QUERIED
import static com.webbfontaine.efem.constants.Statuses.ST_APPROVED
import static com.webbfontaine.efem.constants.Statuses.ST_CONFIRMED
import static com.webbfontaine.efem.constants.Statuses.ST_VALIDATED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED
import static com.webbfontaine.efem.workflow.Operation.UPDATE_EXECUTED
import static com.webbfontaine.efem.workflow.Operations.OP_CANCEL
import static com.webbfontaine.efem.workflow.Operations.OP_QUERY
import static com.webbfontaine.efem.workflow.Operations.OP_REQUEST
import static com.webbfontaine.efem.workflow.Operations.OP_REJECT
import static com.webbfontaine.efem.workflow.Operations.OP_UPDATE
import static com.webbfontaine.efem.workflow.Operations.OP_APPROVE
import static com.webbfontaine.efem.workflow.Operations.OP_VALIDATE
import static com.webbfontaine.efem.workflow.Operations.OP_DECLARE
import static com.webbfontaine.efem.workflow.Operations.OP_CONFIRM



/**
 * Copyright 2019 Webb Fontaine
 * Developer: John Paul Abiog
 * Date: 10/10/19
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class MailRecipientService {

    static transactional = false
    def tvfService

    def resolveRequestOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_REQUEST, exchange?.status?.toUpperCase())
        def resolvedRecipientList = []
        def cmpName = getCompanyByCode(exchange?.importerCode)?.description
        recipientList.each {
            def resolvedRecipient = [:]

            if(it.mailType) {
                def bankEmail = getBankByCode(exchange?.bankCode)?.email
                if (exchange.requestType == ExchangeRequestType.EC) {
                    cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }
                def declarantCode = exchange?.declarantCode
                def declarantName = getDeclarantByCode(declarantCode)?.description
                def bankName = getBankByCode(exchange?.bankCode)?.description

                switch (it.mailType) {
                    case EmailConstants.REQUESTED_MAIL_TO_BANK :
                        def isValid = checkIfMailToBankRequestIsValid(exchange)
                        if(isValid && exchange?.bankCode) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REQUESTED_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, cmpName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}", it.mailType, bankEmail, cmpName)
                            resolvedRecipientList.add(resolvedRecipient)
                        }

                        break
                    case EmailConstants.REQUESTED_MAIL_TO_APPLICANT :
                        def applicantEmail = getApplicantEmail(exchange)
                        if (exchange.requestType == ExchangeRequestType.EC){
                            String isValid = exchange?.exporterCode
                            if(isValid) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REQUESTED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, declarant name: {}, ref: {}", it.mailType, applicantEmail, declarantName, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        else if (exchange.requestType == ExchangeRequestType.EA){
                            String isValid = exchange?.importerCode
                            if(isValid) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.importerCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REQUESTED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, declarant name: {}, ref: {}", it.mailType, applicantEmail, declarantName, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break

                    case EmailConstants.REQUESTED_MAIL_TO_DOMICIALIATION_BANK :
                        def bankDomiciliationEmail = getBankByCode(exchange?.domiciliationBankCode)?.email
                        def domiciliationNumber = exchange?.domiciliationNumber
                        Boolean isValid = exchange?.domiciliationBankCode && domiciliationNumber && (exchange?.domiciliationBankCode != exchange?.bankCode )
                        if(isValid) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.domiciliationBankCode,
                                                     mailType : EmailConstants.REQUESTED_MAIL_TO_DOMICIALIATION_BANK, recipientEmail: bankDomiciliationEmail,
                                                     subjectArgs : [domiciliationNumber],
                                                     bodyArgs : [domiciliationNumber, bankName, cmpName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}", it.mailType, bankDomiciliationEmail, cmpName)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.REQUESTED_MAIL_TO_GOV :
                        def govEmail = AppConfig.resolveGovEmail(exchange)
                        def isValid = (exchange?.requestType == ExchangeRequestType.EC) && (exchange?.departmentInCharge == ExchangeRequestType.departmentInCharg) && govEmail
                        if(isValid){
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REQUESTED_MAIL_TO_GOV, recipientEmail: govEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType,cmpName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}", it.mailType, govEmail, cmpName)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRejectOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_REJECT, exchange?.status?.toUpperCase())
        def resolvedRecipientList = []

        recipientList.each {
            def resolvedRecipient = [:]
            def cmpName = getCompanyByCode(exchange?.importerCode)?.description
            if(it.mailType) {
                def decMail = getDeclarantByCode(exchange?.declarantCode)?.email
                if (exchange.requestType == ExchangeRequestType.EC) {
                     cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }

                switch (it.mailType) {
                    case EmailConstants.REJECTED_MAIL_TO_APPLICANT :
                        def applicantEmail = getApplicantEmail(exchange)
                        if (exchange.requestType == ExchangeRequestType.EC){
                            if(exchange?.exporterCode) {
                                def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REJECTED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, cmpName)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        if (exchange.requestType == ExchangeRequestType.EA){
                            if(exchange?.importerCode) {
                                def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.importerCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.REJECTED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, cmpName)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break

                    case EmailConstants.REJECTED_MAIL_TO_DECLARANT :
                        if(exchange?.declarantCode) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.REJECTED_MAIL_TO_DECLARANT, recipientEmail: decMail,
                                                 subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                 bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType,cmpName, exchange?.bankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}", it.mailType, decMail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }
            }
        }

        return resolvedRecipientList
    }

    def resolveQueryOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_QUERY, exchange?.status?.toUpperCase())
        def resolvedRecipientList = []

        recipientList.each {
            def resolvedRecipient = [:]
            def cmpName = getCompanyByCode(exchange?.importerCode)?.description
            if(it.mailType) {
                def declarantCode = exchange?.declarantCode
                def decMail = getDeclarantByCode(exchange?.declarantCode)?.email
                if (exchange.requestType == ExchangeRequestType.EC) {
                     cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }

                switch (it.mailType) {
                    case EmailConstants.QUERIED_MAIL_TO_APPLICANT :
                        def applicantEmail = getApplicantEmail(exchange)
                        def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                        if (exchange.requestType == ExchangeRequestType.EC){
                            if(exchange?.exporterCode) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.QUERIED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, exchange?.requestType, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        else if (exchange.requestType == ExchangeRequestType.EA){
                            if(exchange?.importerCode) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.importerCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.QUERIED_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, exchange?.requestType, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break

                    case EmailConstants.QUERIED_MAIL_TO_DECLARANT :
                        def isValid = declarantCode
                        if(isValid) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.QUERIED_MAIL_TO_DECLARANT, recipientEmail: decMail,
                                                 subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                 bodyArgs : [exchange?.requestNo, exchange?.bankName, exchange?.requestType, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, decMail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveCancelOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_CANCEL, exchange?.status?.toUpperCase())
        def initialStatus =  WebRequestUtils.getParams().getProperty('initialStatus')
        def resolvedRecipientList = []

        LOGGER.debug("Found recipient list size ${recipientList?.size}, value : ${recipientList}")
        recipientList.each {
            def resolvedRecipient = [:]
            def cmpName = getCompanyByCode(exchange?.importerCode)?.description
            if(it.mailType) {
                def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                if (exchange.requestType == ExchangeRequestType.EC) {
                     cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }
                switch (it.mailType) {
                    case EmailConstants.CANCELLED_MAIL_TO_APPLICANT :
                        def isValid = checkIfMailToApplicantCancelIsValid(exchange, initialStatus)
                        if(isValid){
                            def fourthParam
                            def mailType
                            (mailType,fourthParam) = getMailTypeCancelApplicant(exchange, initialStatus)
                            def applicantEmail = getApplicantEmail(exchange)
                            if(initialStatus in [ST_QUERIED, ST_APPROVED]){
                                if((exchange?.declarantCode || exchange?.bankCode) && (exchange?.requestType == ExchangeRequestType.EC)) {
                                    resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                         mailType : mailType, recipientEmail: applicantEmail,initialStatus: initialStatus,
                                                         subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                         bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, fourthParam, declarantName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                    resolvedRecipientList.add(resolvedRecipient)
                                }
                                if((exchange?.declarantCode || exchange?.bankCode) && (exchange?.requestType != ExchangeRequestType.EC)) {
                                    resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.importerCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                         mailType : mailType, recipientEmail: applicantEmail,initialStatus: initialStatus,
                                                         subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                         bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, fourthParam, declarantName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                    resolvedRecipientList.add(resolvedRecipient)
                                }
                            }
                        }
                        break
                    case EmailConstants.CANCELLED_MAIL_TO_GOV :
                        def govEmail = AppConfig.resolveGovEmail(exchange)
                        def isValid = (exchange?.requestType == ExchangeRequestType.EC) && (exchange?.departmentInCharge != ExchangeRequestType.departmentInCharg) && govEmail && (initialStatus == ST_APPROVED)
                            if(isValid) {
                                    resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                         mailType : EmailConstants.CANCELLED_MAIL_TO_GOV, recipientEmail: govEmail,initialStatus: initialStatus,
                                                         subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                         bodyArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.bankName, cmpName,declarantName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, ref: {}", it.mailType, govEmail, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        break
                    case EmailConstants.CANCELLED_MAIL_TO_BANK :
                        def bankEmail = getBankByCode(exchange?.bankCode)?.email
                        def isValid = (exchange?.requestType == ExchangeRequestType.EC) && (exchange?.departmentInCharge == ExchangeRequestType.departmentInCharg) && initialStatus == ST_APPROVED && exchange?.bankCode
                        if(isValid){
                                    resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                         mailType : EmailConstants.CANCELLED_MAIL_TO_BANK, recipientEmail: bankEmail,initialStatus: initialStatus,
                                                         subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                         bodyArgs : [exchange?.requestType, exchange?.requestNo,FormattingUtils.fromDate(exchange?.requestDate), cmpName, declarantName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, bankEmail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                }

            }
        }

        return resolvedRecipientList
    }

    def resolveApproveOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_APPROVE, exchange?.status?.toUpperCase())
        def resolvedRecipientList = []

        LOGGER.debug("Found recipient list size ${recipientList?.size}, value : ${recipientList}")
        recipientList.each {
            def resolvedRecipient = [:]

            if(it.mailType) {
                def bankName = getBankByCode(exchange?.bankCode)?.description
                def applicantEmail = getApplicantEmail(exchange)
                def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                def cmpName = getCompanyByCode(exchange?.importerCode)?.description
                if (exchange.requestType == ExchangeRequestType.EC) {
                     cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }
                def decMail = getDeclarantByCode(exchange?.declarantCode)?.email

                switch (it.mailType) {
                    case EmailConstants.APPROVED_GOV_MAIL_TO_APPLICANT :
                        if (exchange.requestType == ExchangeRequestType.EC){
                            if(exchange?.exporterCode) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.APPROVED_GOV_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, cmpName, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        else if (exchange.requestType == ExchangeRequestType.EA){
                            if(exchange?.importerCode) {
                                resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.importerCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                     mailType : EmailConstants.APPROVED_GOV_MAIL_TO_APPLICANT, recipientEmail: applicantEmail,
                                                     subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                     bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, cmpName, exchange?.bankName, declarantName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, applicantEmail, exchange?.requestNo)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break

                    case EmailConstants.APPROVED_GOV_MAIL_TO_DECLARNT :
                        def isValid = exchange?.declarantCode
                        if(isValid) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.APPROVED_GOV_MAIL_TO_DECLARNT, recipientEmail: decMail,
                                                 subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                 bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, cmpName, exchange?.bankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, decMail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                    case EmailConstants.PARTIALLY_APPROVED_BANK_MAIL_TO_GOV :
                        def govEmail = AppConfig.resolveGovEmail(exchange)
                        def isValid = (exchange?.requestType == ExchangeRequestType.EA) && govEmail
                        if(isValid) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.PARTIALLY_APPROVED_BANK_MAIL_TO_GOV, recipientEmail: govEmail,
                                                 subjectArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate)],
                                                 bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), exchange?.requestType, cmpName, exchange?.bankName, declarantName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, govEmail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.APPROVED_BANK_MAIL_TO_GOV :
                        def govEmail = AppConfig.resolveGovEmail(exchange)
                        def isValid = checkIfMailToGovApproveIsValid(exchange)
                        if(isValid && govEmail) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.APPROVED_BANK_MAIL_TO_GOV, recipientEmail: govEmail,
                                                 subjectArgs : [exchange?.requestType, bankName],
                                                 bodyArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), bankName, cmpName, declarantName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, govEmail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.APPROVED_GOV_MAIL_TO_BANK :
                        def bankEmail = getBankByCode(exchange?.bankCode)?.email
                        def isValid = exchange?.requestType == ExchangeRequestType.EC && (exchange?.departmentInCharge == ExchangeRequestType.departmentInCharg) && exchange?.bankCode
                        if(isValid) {
                            resolvedRecipient = [numerodemande:exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                 mailType : EmailConstants.APPROVED_GOV_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                 subjectArgs : [exchange?.requestType],
                                                 bodyArgs : [exchange?.requestType, exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), cmpName, declarantName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, bankEmail, exchange?.requestNo)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }


            }
        }

        return resolvedRecipientList
    }

    def resolveUpdateOperationRecipients(Exchange exchange) {
        def recipientList = AppConfig.resolveRecipientList(OP_UPDATE, exchange?.status?.toUpperCase())
        def startedOperation = WebRequestUtils.getParams().getProperty('operationsStarted')
        def resolvedRecipientList = []
        recipientList.each {
            def resolvedRecipient = [:]

            if(it.mailType) {
                def declarantName = getDeclarantByCode(exchange?.declarantCode)?.description
                def cmpName = getCompanyByCode(exchange?.importerCode)?.description
                if (exchange.requestType == ExchangeRequestType.EC) {
                     cmpName = getCompanyByCode(exchange?.exporterCode)?.description
                }

                switch (it.mailType) {
                    case EmailConstants.UPDATE_APPROVED_OR_UPDATE_EXECUTED_MAIL_TO_BANK :
                        def approveBankMail = getBankByCode(exchange?.bankCode)?.email
                        if(exchange?.requestType == ExchangeRequestType.EC){
                            if(checkIfAttachedFinalInvoice(exchange) && checkGeoArea(exchange) && (isTrader() || isDeclarant()) && startedOperation in [UPDATE_EXECUTED, UPDATE_APPROVED]){
                                if(exchange?.bankCode){
                                    resolvedRecipient = [numerodemande: exchange?.requestNo,traderCode:exchange?.exporterCode,declarantCode:exchange?.declarantCode,bankCode:exchange?.bankCode,
                                                         mailType : EmailConstants.UPDATE_APPROVED_OR_UPDATE_EXECUTED_MAIL_TO_BANK, recipientEmail: approveBankMail,
                                                         subjectArgs : [exchange?.requestNo],
                                                         bodyArgs : [exchange?.requestNo, FormattingUtils.fromDate(exchange?.requestDate), cmpName, declarantName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient email: {}, ref: {}", it.mailType, approveBankMail, exchange?.requestNo)
                                    resolvedRecipientList.add(resolvedRecipient)
                                }
                            }
                        }
                        break
                }
            }
        }

        return resolvedRecipientList
    }

    private String getApplicantEmail(Exchange exchange){
        def resolvedEmail
        if(exchange?.basedOn == ExchangeRequestType.BASE_ON_TVF) {
            def exchangeInstance = tvfService.loadTvf(exchange?.tvfNumber?.toString(), FormattingUtils.fromDate(exchange?.tvfDate))
            resolvedEmail = exchangeInstance?.tvf?.impEmail
        } else {
            def importer = getCompanyByCode(exchange?.importerCode)
            resolvedEmail = importer?.email
        }

        resolvedEmail
    }

    private Declarant getDeclarantByCode(String decCode){
        return Declarant.withNewSession {
            Declarant.findByCode(decCode)
        }
    }


    private String getMailType(String operation){
        def mailType

        if(isGovOfficer() && operation == OP_QUERY) {
            mailType = EmailConstants.QUERIED_GOV_MAIL_TO_APPLICANT
        } else if (isBankAgent() && operation == OP_QUERY) {
            mailType = EmailConstants.QUERIED_BANK_MAIL_TO_APPLICANT
        } else if (isBankAgent() && operation == OP_CANCEL) {
            mailType = EmailConstants.CANCELLED_BANK_MAIL_TO_APPLICANT
        } else if (isDeclarant() && operation == OP_CANCEL) {
            mailType = EmailConstants.CANCELLED_MAIL_TO_APPLICANT
        }
        mailType
    }

    private List getMailTypeCancelApplicant(Exchange exchange, String initialStatus){
        def mailType
        def fourthParam
        if(initialStatus == ST_APPROVED) {
            mailType = EmailConstants.CANCELLED_APPROVED_MAIL_TO_APPLICANT
            fourthParam = getCompanyByCode(exchange?.importerCode)?.description
        } else if(initialStatus == ST_QUERIED){
            mailType = EmailConstants.CANCELLED_QUERY_MAIL_TO_APPLICANT
            fourthParam = getBankByCode(exchange?.bankCode)?.description
        }
        [mailType, fourthParam]
    }

    private String getMailType(String basedOn, String requestedBy){
        def mailType

        if(basedOn == ExchangeRequestType.BASE_ON_TVF && requestedBy == Roles.DECLARANT.authority) {
            mailType = EmailConstants.UPDATE_TVF_MAIL_TO_DECLARANT
        } else if(basedOn == ExchangeRequestType.BASE_ON_SAD && requestedBy == Roles.DECLARANT.authority) {
            mailType = EmailConstants.UPDATE_SAD_MAIL_TO_DECLARANT
        }
        mailType
    }

    private Company getCompanyByCode(String cmpCode) {
        return Company.withNewSession {
            Company.findByCode(cmpCode)
        }
    }

    private Bank getBankByCode(String bankCode) {
        return Bank.withNewSession {
            Bank.findByCode(bankCode)
        }
    }
    private Bank getBankByDescription(String Description) {
        return Bank.withNewSession {
            Bank.findByDescription(Description)
        }
    }

    def resolveOrderRequestedOperationRecipients(TransferOrder transferOrder) {
        def recipientList = AppConfig.resolveOrderRecipientList(OP_REQUEST, transferOrder?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.REQUESTED_ORDER_MAIL_TO_BANK :
                        def bank = getBankByCode(transferOrder.bankCode)
                        def bankName = transferOrder.bankName == null ? bank?.description : transferOrder.bankName
                        def bankEmail = bank?.email
                        def cmpName = getCompanyByCode(transferOrder.importerCode)?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.bankCode && isTrader()) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.REQUESTED_ORDER_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, bankEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                    case EmailConstants.REQ_VALIDATED_ORDER_MAIL_TO_IMPORTER :
                        def bankName = transferOrder.bankName == null ? getBankByCode(transferOrder.bankCode)?.description : transferOrder.bankName
                        def trader = getCompanyByCode(transferOrder.importerCode)
                        def traderEmail = trader?.email
                        def cmpName = trader?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.importerCode && isBankAgent()) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.REQ_VALIDATED_ORDER_MAIL_TO_IMPORTER, recipientEmail: traderEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, transferOrder.executionRef,transferOrder.requestNo,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, traderEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }
            }
        }
        return resolvedRecipientList
    }

    def resolveOrderValidatedOperationRecipients(TransferOrder transferOrder) {
        def recipientList = AppConfig.resolveOrderRecipientList(OP_VALIDATE, transferOrder?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.VALIDATED_ORDER_MAIL_TO_IMPORTER :
                        def bankName = transferOrder.bankName == null ? getBankByCode(transferOrder.bankCode)?.description : transferOrder.bankName
                        def trader = getCompanyByCode(transferOrder.importerCode)
                        def traderEmail = trader?.email
                        def cmpName = trader?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.importerCode && isBankAgent()) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.VALIDATED_ORDER_MAIL_TO_IMPORTER, recipientEmail:traderEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, transferOrder.executionRef,transferOrder.requestNo,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, traderEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveOrderQueriedOperationRecipients(TransferOrder transferOrder) {
        def recipientList = AppConfig.resolveOrderRecipientList(OP_QUERY, transferOrder?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.QUERIED_ORDER_MAIL_TO_IMPORTER :
                        def bankName = transferOrder.bankName == null ? getBankByCode(transferOrder.bankCode)?.description : transferOrder.bankName
                        def trader = getCompanyByCode(transferOrder.importerCode)
                        def traderEmail = trader?.email
                        def cmpName = trader?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.importerCode) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.QUERIED_ORDER_MAIL_TO_IMPORTER, recipientEmail: traderEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa,cmpName,bankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, traderEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }


    def resolveOrderCancelledOperationRecipients(TransferOrder transferOrder,String initialStatus) {
        def recipientList = AppConfig.resolveOrderRecipientList(OP_CANCEL, transferOrder?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.CANCELLED_ORDER_MAIL_TO_BANK :
                        def bank = getBankByCode(transferOrder.bankCode)
                        def bankName = transferOrder.bankName == null ? bank?.description : transferOrder.bankName
                        def bankEmail = bank?.email
                        def cmpName = getCompanyByCode(transferOrder.importerCode)?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(isTrader() && transferOrder?.bankCode && initialStatus == ST_QUERIED) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.CANCELLED_ORDER_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, transferOrder.executionRef,transferOrder.requestNo,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, bankEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                    case EmailConstants.CANCELLED_ORDER_MAIL_TO_IMPORTER :
                        def allEmail = []
                        def allTraderCode = []
                        def bank = getBankByCode(transferOrder.bankCode)
                        def bankName = transferOrder.bankName == null ? bank?.description : transferOrder.bankName
                        def bankEmail = bank?.email
                        if(bankEmail){
                            allEmail.add(bankEmail)
                        }
                        def trader = getCompanyByCode(transferOrder.importerCode)
                        def traderEmail = trader?.email
                        if(traderEmail){
                            allEmail.add(traderEmail)
                        }
                        if(transferOrder?.importerCode){
                            allTraderCode.add(transferOrder?.importerCode)
                        }

                        def cmpName = trader?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        def listeDeleteEa = checkListeOfEa(transferOrder,'1')
                        if(isSuperAdministrator() && initialStatus == ST_VALIDATED && allEmail.size() > 0) {
                            resolvedRecipient = [numerodemande:transferOrder?.requestNo,traderCodeArray:allTraderCode?.toArray(),bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.CANCELLED_ORDER_MAIL_TO_IMPORTER, recipientEmailArray: allEmail?.toArray(),
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, transferOrder.executionRef,transferOrder.requestNo,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {} , EA list : {} ,EA liste Deleted : {} ", it.mailType, allEmail.join(","), cmpName, listeOfEa,listeDeleteEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveOrderUpdatOperationRecipients(TransferOrder transferOrder) {
        def recipientList = AppConfig.resolveOrderRecipientList(OP_UPDATE, transferOrder?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.UPDATE_ORDER_VALIDATED_MAIL_TO_BANK :
                        def bank = getBankByCode(transferOrder.bankCode)
                        def bankName = transferOrder.bankName == null ? bank?.description : transferOrder.bankName
                        def bankEmail = bank?.email
                        def cmpName = getCompanyByCode(transferOrder.importerCode)?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.bankCode && isTrader()) {
                            resolvedRecipient = [numerodemande: transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.UPDATE_ORDER_VALIDATED_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, transferOrder.executionRef,transferOrder.requestNo,bankName, cmpName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, bankEmail, cmpName)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                    case EmailConstants.UPDATE_ORDER_VALIDATED_MAIL_TO_IMPORTER :
                        def bankName = getBankByCode(transferOrder?.bankCode)?.description
                        def trader = getCompanyByCode(transferOrder.importerCode)
                        def traderEmail = trader?.email
                        def cmpName = trader?.description
                        def listeOfEa = checkListeOfEa(transferOrder,'0')
                        if(transferOrder?.importerCode && isBankAgent()) {
                            resolvedRecipient = [numerodemande: transferOrder?.requestNo,traderCode:transferOrder?.importerCode,bankCode:transferOrder?.bankCode,
                                                 mailType : EmailConstants.UPDATE_ORDER_VALIDATED_MAIL_TO_IMPORTER, recipientEmail: traderEmail,
                                                 subjectArgs : [transferOrder.requestNo],
                                                 bodyArgs : [transferOrder.requestNo, FormattingUtils.fromDate(transferOrder.requestDate),listeOfEa, cmpName, bankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient email: {}, applicant name: {}, EA list : {} ", it.mailType, traderEmail, cmpName, listeOfEa)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRepatriationDeclareOperationRecipients(Repatriation repatriation) {
        def recipientList = AppConfig.resolveRepatriationRecipientList(OP_DECLARE, repatriation?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            def bankRepatriation = getBankByCode(repatriation?.repatriationBankCode)
            def bankNameRepatriation = repatriation.repatriationBankName == null ? bankRepatriation?.description : repatriation.repatriationBankName
            def result = it.mailType
            if(result) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.DECLARE_REPATRIATION_MAIL_TO_BANK :
                        def exporter = getCompanyByCode(repatriation?.code)?.description
                        def bankEmail = bankRepatriation?.email
                        if(repatriation?.repatriationBankCode) {
                            resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                 mailType : EmailConstants.DECLARE_REPATRIATION_MAIL_TO_BANK, recipientEmail: bankEmail,
                                                 subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                 bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate),exporter]]
                            LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_EXPORTER :
                        def exporter = getCompanyByCode(repatriation.code)
                        def exporterEmail = exporter?.email
                        if(repatriation?.code) {
                            resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                 mailType : EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_EXPORTER, recipientEmail: exporterEmail,
                                                 subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                 bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate),bankNameRepatriation]]
                            LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, exporterEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_DOMICILIATION_BANK :
                        def listeEC = checkListeOfEc(repatriation)
                        listeEC?.each {
                            def bankDomiciliation = getBankByDescription(it.domiciliaryBank)
                            if(bankDomiciliation != bankRepatriation){
                                def bankDomiciliationEmail = bankDomiciliation?.email
                                if(bankDomiciliationEmail || bankDomiciliation?.code) {
                                    resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:bankDomiciliation?.code,
                                                         mailType : EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_DOMICILIATION_BANK, recipientEmail: bankDomiciliationEmail,
                                                         subjectArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate)],
                                                         bodyArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate),repatriation.requestDate, repatriation.repatriationBankName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankDomiciliationEmail)
                                    resolvedRecipientList.add(resolvedRecipient)
                                }
                            }
                        }

                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRepatriationConfirmOperationRecipients(Repatriation repatriation) {
        def recipientList = AppConfig.resolveRepatriationRecipientList(OP_CONFIRM, repatriation?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            def result = it.mailType
            if(result) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_EXPORTER :
                        def exporter = getCompanyByCode(repatriation.code)
                        def exporterEmail = exporter?.email
                        if(repatriation?.code) {
                            resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                 mailType : EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_EXPORTER, recipientEmail: exporterEmail,
                                                 subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                 bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate),repatriation.repatriationBankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, exporterEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break

                    case EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_DOMICILIATION_BANK :
                        def listeEC = checkListeOfEc(repatriation)
                        listeEC?.each {
                            def bankDomiciliation = getBankByDescription(it.domiciliaryBank)
                            def bankDomiciliationEmail = bankDomiciliation?.email
                            if(bankDomiciliationEmail || bankDomiciliation?.code) {
                                resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:bankDomiciliation?.code,
                                                     mailType : EmailConstants.CONFIRM_REPATRIATION_MAIL_TO_DOMICILIATION_BANK, recipientEmail: bankDomiciliationEmail,
                                                     subjectArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate)],
                                                     bodyArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate),repatriation.requestDate.toString(), repatriation.repatriationBankName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankDomiciliationEmail)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRepatriationQueryOperationRecipients(Repatriation repatriation) {
        def recipientList = AppConfig.resolveRepatriationRecipientList(OP_QUERY, repatriation?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.QUERY_REPATRIATION_MAIL_TO_EXPORTER :
                        def exporter = getCompanyByCode(repatriation.code)
                        def exporterEmail = exporter?.email
                        if(repatriation?.code) {
                            resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                 mailType : EmailConstants.QUERY_REPATRIATION_MAIL_TO_EXPORTER, recipientEmail: exporterEmail,
                                                 subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                 bodyArgs : [repatriation.requestNo, repatriation.repatriationBankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", it.mailType, exporterEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRepatriationCancelOperationRecipients(Repatriation repatriation) {
        def recipientList = AppConfig.resolveRepatriationRecipientList(OP_CANCEL, repatriation?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            if(it.mailType) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.CANCEL_REPATRIATION_MAIL_TO_EXPORTER :
                        def exporter = getCompanyByCode(repatriation.code)
                        def exporterEmail = exporter?.email
                        def initialStatus =  WebRequestUtils.getParams().getProperty('initialStatus')
                        if(initialStatus == ST_CONFIRMED){
                            if(repatriation?.code) {
                                resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                     mailType : EmailConstants.CANCEL_REPATRIATION_MAIL_TO_EXPORTER, recipientEmail: exporterEmail,
                                                     subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                     bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate), repatriation.repatriationBankName]]
                                LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", it.mailType, exporterEmail)
                                resolvedRecipientList.add(resolvedRecipient)
                            }
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }

    def resolveRepatriationUpdateOperationRecipients(Repatriation repatriation) {
        def recipientList = AppConfig.resolveRepatriationRecipientList(OP_UPDATE, repatriation?.status?.toUpperCase())
        def resolvedRecipientList = []
        recipientList.each {
            def result = it.mailType
            if(result) {
                def resolvedRecipient = [:]
                switch (it.mailType) {
                    case EmailConstants.UPDATE_REPATRIATION_MAIL_TO_EXPORTER :
                        def exporter = getCompanyByCode(repatriation.code)
                        def exporterEmail = exporter?.email
                        def isValid = isBankAgent() && repatriation?.code
                        if(isValid){
                            resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:repatriation?.repatriationBankCode,
                                                 mailType : EmailConstants.UPDATE_REPATRIATION_MAIL_TO_EXPORTER, recipientEmail: exporterEmail,
                                                 subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                 bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate), repatriation.repatriationBankName]]
                            LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", it.mailType, exporterEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.UPDATE_REPATRIATION_MAIL_TO_DOMICILIATION_BANK :
                        def Company = getCompanyByCode(repatriation.code)
                        def companyName = Company.description
                        if(isTrader()){
                            def listeEC = checkListeOfEc(repatriation)
                            listeEC?.each {
                                def bankDomiciliation = getBankByDescription(it.domiciliaryBank)
                                def bankDomiciliationEmail = bankDomiciliation?.email
                                if(bankDomiciliationEmail || bankDomiciliation?.code) {
                                    resolvedRecipient = [numerodemande: repatriation?.requestNo,traderCode:repatriation?.code,bankCode:bankDomiciliation?.code,
                                                         mailType : EmailConstants.UPDATE_REPATRIATION_MAIL_TO_DOMICILIATION_BANK, recipientEmail: bankDomiciliationEmail,
                                                         subjectArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate)],
                                                         bodyArgs : [it.ecReference, FormattingUtils.fromDate(it.ecDate),repatriation.requestNo, companyName,repatriation.repatriationBankName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankDomiciliationEmail)
                                    resolvedRecipientList.add(resolvedRecipient)
                                }
                            }
                        }
                        break
                    case EmailConstants.UPDATE_REPATRIATION_MAIL_TO_BANK :
                        def Company = getCompanyByCode(repatriation.code)
                        def companyName = Company.description
                        def bankRepatriationEmail = getBankByCode(repatriation.repatriationBankCode)?.email
                        def bankRepatriationName = getBankByCode(repatriation.repatriationBankCode)?.description
                        def isValid = isTrader() && repatriation.repatriationBankCode
                        if(isValid){
                                    resolvedRecipient = [mailType : EmailConstants.UPDATE_REPATRIATION_MAIL_TO_BANK, recipientEmail: bankRepatriationEmail,bankCode: repatriation.repatriationBankCode,
                                                         subjectArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate)],
                                                         bodyArgs : [repatriation.requestNo, FormattingUtils.fromDate(repatriation.requestDate),repatriation.requestNo, companyName, bankRepatriationName]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankRepatriationEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                    case EmailConstants.UPDATE_CLEARANCE_REPATRIATION_MAIL_TO_BANK :
                        def bankRepatriationEmail = getBankByCode(repatriation.repatriationBankCode)?.email
                        if(bankRepatriationEmail){
                        CurrencyTransfer currencyTransfer = CurrencyTransfer.findByRepatriationNoAndRepatriationDate(repatriation.requestNo, repatriation.requestDate)
                                    resolvedRecipient = [mailType : EmailConstants.UPDATE_CLEARANCE_REPATRIATION_MAIL_TO_BANK, recipientEmail: bankRepatriationEmail,bankCode: repatriation.repatriationBankCode,
                                                         subjectArgs : [currencyTransfer.requestNo, FormattingUtils.fromDate(currencyTransfer.requestDate)],
                                                         bodyArgs : [currencyTransfer.requestNo, FormattingUtils.fromDate(currencyTransfer.requestDate)]]
                                    LOGGER.debug("Sending email with MailType: {}, recipient Email {} ", result, bankRepatriationEmail)
                            resolvedRecipientList.add(resolvedRecipient)
                        }
                        break
                }

            }
        }

        return resolvedRecipientList
    }


    def checkListeOfEa(TransferOrder transferOrder,String state){
        def listeOfEa = transferOrder.orderClearanceOfDoms.findAll {it?.state == state}*.eaReference
        return listeOfEa?.size() == 1 ? listeOfEa?.get(0) : listeOfEa?.join(",")
    }

    def checkListeOfEc(Repatriation repatriation){
        def listeOfEc = repatriation.clearances.findAll{it}
        return listeOfEc
    }

    def checkIfAttachedFinalInvoice(Exchange exchange){
        def result = false
        if(exchange?.isFinalAmount){
            def requiredFacture = AppConfig.requiredAttachments
            def listeOfAttachedDocs = (exchange?.attachedDocs?.docType).intersect(requiredFacture)
            if(listeOfAttachedDocs.size() > 0){
                result = true
            }
        }
        LOGGER.debug("Sending email : Check if is final facture and facture 0007 attached return : {}", result)
        result
    }

    def checkGeoArea(Exchange exchange){
        def result = false
        if(exchange?.geoArea in [ExchangeRequestType.AREA_001, ExchangeRequestType.AREA_002, ExchangeRequestType.AREA_003]){
            result = true
        }
        LOGGER.debug("Sending email : Check if is GeoArea is ok return : {}", result)
        result
    }

    def checkIfMailToGovApproveIsValid(Exchange exchange){
        def result = false
        if(exchange?.requestType == ExchangeRequestType.EA && exchange?.authorizedBy != ExchangeRequestType.authorizedBy){
            result = true
        }else if(exchange?.requestType == ExchangeRequestType.EC && exchange?.departmentInCharge != ExchangeRequestType.departmentInCharg){
            result = true
        }
        result
    }

    def checkIfMailToBankRequestIsValid(Exchange exchange){
        def result = false
        if(exchange?.requestType == ExchangeRequestType.EA){
            result = true
        }else if(exchange?.requestType == ExchangeRequestType.EC && exchange?.departmentInCharge != ExchangeRequestType.departmentInCharg){
            result = true
        }
        result
    }
    def checkIfMailToApplicantCancelIsValid(Exchange exchange, String initialStatus){
        def result = false
        if(exchange?.requestType == ExchangeRequestType.EA && initialStatus == ST_QUERIED){
            result = true
        }else if(exchange?.requestType == ExchangeRequestType.EC && initialStatus in [ST_QUERIED, ST_APPROVED] ){
            result = true
        }
        result
    }
}
