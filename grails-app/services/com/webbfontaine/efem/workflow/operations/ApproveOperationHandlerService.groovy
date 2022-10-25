package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.security.Roles
import com.webbfontaine.efem.workflow.BpmService
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.UserUtils.*
import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 10/25/2014
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class ApproveOperationHandlerService extends AfterApproveAndRequestOperationHandlerService {

    def exchangeWorkflowService
    def springSecurityService
    def exchangeService
    def sequenceGenerator

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
    }

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation) {

        def regNumber = domainInstance?.registrationNumberBank
        LocalDate regDate = domainInstance?.registrationDateBank
        def params = GrailsWebRequest.lookup().params
        def commitOperationName
        if (!regNumber && !regDate && isBankAgent()) {
            def year = domainInstance?.requestDate?.getYear()
            def bank = domainInstance?.bankCode
            String key = "${year}/${bank}"
            domainInstance?.registrationNumberSequence = sequenceGenerator.nextRegistrationNumber(key)
            domainInstance?.registrationNumberBank = exchangeService.generateRegistrationNumber(domainInstance)
            domainInstance?.registrationDateBank = domainInstance?.requestDate
        }
        commitOperation = APPROVE_REQUESTED
        commitOperationName = APPROVE_REQUESTED.humanName()

        LOGGER.debug("Start setCommitOperAndTreatmentLvl method  for Exchange ${domainInstance?.id}")
        LOGGER.debug("Exchange request number before setCommitOperAndTreatmentLvl ${domainInstance?.requestNo}")
        if (domainInstance.requestType == ExchangeRequestType.EA) {
            setCommitOperAndTreatmentLvl(domainInstance, commitOperation, commitOperationName, params)
        }
        LOGGER.debug("End setCommitOperAndTreatmentLvl ${domainInstance?.requestNo}")

        setAuthorizationForEA(domainInstance)
        setApprovedBy(domainInstance)
        domainInstance.lastTransactionDate = LocalDateTime.now()
        params.put('commitOperation', params?.commitOperation ?: commitOperation)
        params.put('commitOperationName', params?.commitOperation ?: commitOperationName)
    }

    def setCommitOperAndTreatmentLvl(Exchange domainInstance, Operation commitOperation, def commitOperationName, params) {
        Integer maxApprovalLevel = domainInstance.requestType == ExchangeRequestType.EC ? AppConfig.maxApprovalConfigEC : AppConfig.maxApprovalConfig
        def userConnectedLvl = springSecurityService.principal.userProperties?.LVL

        if (isBankAgent()) {
            domainInstance?.treatmentLevel = 1
            if (domainInstance.basedOn == ExchangeRequestType.BASE_ON_SAD) {
                (commitOperation, commitOperationName) = setCommitOpAndStatusIsBankAgent(domainInstance, commitOperation, commitOperationName, params)
            } else if (domainInstance.basedOn == ExchangeRequestType.BASE_ON_TVF) {
                (commitOperation, commitOperationName) = setCommitOpAndStatusIsBankAgentTvf(domainInstance, commitOperation, commitOperationName)
            }
        } else {
            Integer treatmentLevel = userConnectedLvl?.toInteger() ?: 1
            domainInstance?.treatmentLevel = treatmentLevel + 1
            LOGGER.debug("Checking Maximum Approval Level for Exchange ${domainInstance?.id} with Treatment Level ${domainInstance?.treatmentLevel}")
            LOGGER.debug("Checking maxApprovalLevel Level for Exchange ${maxApprovalLevel}")
            LOGGER.debug("Checking Exchange status for treatment Level ${domainInstance?.status}")

            if (domainInstance?.treatmentLevel > maxApprovalLevel && ST_REQUESTED.equals(domainInstance?.status)) {
                LOGGER.debug("Checking setCommitOperAndTreatmentLvl if status is Requested  ${domainInstance.requestNo}")
                commitOperation = APPROVE_REQUESTED
                commitOperationName = APPROVE_REQUESTED.humanName()

            } else if (domainInstance?.treatmentLevel > maxApprovalLevel && ST_PARTIALLY_APPROVED.equals(domainInstance?.status)) {
               LOGGER.debug("Checking setCommitOperAndTreatmentLvl if status is Partially Approved  ${domainInstance.requestNo}")
                domainInstance?.status = ST_PRE_APPROVE
                commitOperation = APPROVE_PARTIALLY_APPROVED
                commitOperationName = APPROVE_PARTIALLY_APPROVED.humanName()

            } else {
                LOGGER.debug("Checking setCommitOperAndTreatmentLvl else case  ${domainInstance.requestNo}")
                domainInstance.status = ST_PARTIALLY_APPROVED
                commitOperation = PARTIALLY_APPROVED
                commitOperationName = PARTIALLY_APPROVED.humanName()
            }
        }

        params.put('commitOperation', commitOperation)
        params.put('commitOperationName', commitOperationName)
        params.put('treatmentLevel', domainInstance?.treatmentLevel)
    }

    def setAuthorizationForEA(Exchange domainInstance) {
        def commitOp = WebRequestUtils.getParams().commitOperation as Operation
        def isApproved = commitOp in [APPROVE_PARTIALLY_APPROVED, APPROVE_REQUESTED]

        if (isApproved) {
            domainInstance?.authorizationDate = new LocalDate()
            domainInstance?.expirationDate = (new LocalDate())?.plusDays(90)
            domainInstance?.authorizedBy = setAuthorizedBy(domainInstance)
        }
        domainInstance?.year = domainInstance?.registrationDateBank?.getYear()
    }

    def setAuthorizedBy(Exchange exchange) {
        def bank = userPropertyValueAsList(UserProperties.ADB)
        if (isBankAgent()) {
            exchange?.authorizedBy = (bank && bank?.size() == 1) ? bank.first() : null
        } else if (isGovOfficer()) {
            exchange?.authorizedBy = UtilConstants.THE_FINEX
        }
    }

    def setApprovedBy(Exchange exchange) {
        def userRoles = springSecurityService?.principal?.authorities
        String approvedBy
        def isBankAgent = userRoles.any { [Roles.BANK_AGENT.authority].contains(it.authority) }
        def isGovOfficer = userRoles.any { [Roles.GOVT_OFFICER.authority].contains(it.authority) }

        if (isBankAgent) {
            approvedBy = "${isBankAgent ? Roles.BANK_AGENT.authority : ""}"
            exchange.bankApprovalDate = LocalDate.now()
        }
        if (isGovOfficer) {
            if (approvedBy) {
                approvedBy = "${approvedBy};${Roles.GOVT_OFFICER.authority}"
            } else {
                approvedBy = "${Roles.GOVT_OFFICER.authority}"
            }
        }

        exchange.approvedBy = approvedBy
    }

    private static List setCommitOpAndStatusIsBankAgent(Exchange domainInstance, Operation commitOperation, String commitOperationName, def params) {
        if (Boolean.valueOf(params.finexApproval) && ST_REQUESTED.equals(domainInstance.status)) {
            domainInstance.status = ST_PARTIALLY_APPROVED
            commitOperation = PARTIALLY_APPROVED
            commitOperationName = PARTIALLY_APPROVED.humanName()
        } else if (!Boolean.valueOf(params.finexApproval) && ST_REQUESTED.equals(domainInstance.status)) {
            commitOperation = APPROVE_REQUESTED
            commitOperationName = APPROVE_REQUESTED.humanName()
        }
        [commitOperation, commitOperationName]
    }

    private static List setCommitOpAndStatusIsBankAgentTvf(Exchange domainInstance, Operation commitOperation, String commitOperationName) {
        if (domainInstance.amountNationalCurrency > new BigDecimal(AppConfig.maxAmountInXofCurrency) && AppConfig.allowFinexApprovalEA()) {
            domainInstance.status = ST_PARTIALLY_APPROVED
            commitOperation = PARTIALLY_APPROVED
            commitOperationName = PARTIALLY_APPROVED.humanName()
        } else {
            commitOperation = APPROVE_REQUESTED
            commitOperationName = APPROVE_REQUESTED.humanName()

        }
        [commitOperation, commitOperationName]
    }
}
