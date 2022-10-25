package com.webbfontaine.transferOrder

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constant.TransferConstants
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import com.webbfontaine.efem.workflow.operations.OperationHandlerService
import com.webbfontaine.grails.plugins.conversation.store.session.SessionStore
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.context.i18n.LocaleContextHolder

import java.text.DecimalFormat
import com.webbfontaine.efem.UserUtils

import static com.webbfontaine.efem.constants.ExchangeRequestType.EXECUTION_CANCEL_STATE
import static com.webbfontaine.efem.workflow.Operation.CANCEL_VALIDATED

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
@Transactional
class TransferOrderService {
    SessionStore sessionStoreService
    def transferOrderWorkflowService
    def transferBusinessLogicService
    def transferOrderValidationService
    def convertDigitsToLetterService
    def loggerService

    def addToSessionStore(TransferOrder transfer, conversationId = null) {
        if (conversationId) {
            sessionStoreService.put(conversationId, transfer)
        } else {
            sessionStoreService.put(transfer)
        }
    }

    def findFromSessionStore(def conversationId) {
        sessionStoreService.get(conversationId) as TransferOrder
    }

    def checkingCurrencyOfExchangeData(eaReference) {
        return Exchange.findByRequestNoAndRequestTypeAndCurrencyCodeNotInList(eaReference, TransferConstants.REQUEST_TYPE_EA, TransferConstants.ALLOWED_CURRENCYCODE)
    }

    def checkAndSetBankCode(clearanceOfDom, eaReference) {
        if (!clearanceOfDom.bankCode && eaReference) {
            def bankCode = checkingCurrencyOfExchangeData(eaReference)?.bankCode
            LOGGER.debug("in addClearanceDom() banCode  retrieve :  ${bankCode}")
            if (bankCode) {
                clearanceOfDom.bankCode = bankCode
            }
        }
    }

    def doAddClearanceChecking(transferInstance, OrderClearanceOfDom clearanceOfDom) {
        transferOrderValidationService.validateClearanceOfDom(clearanceOfDom)
        clearanceOfDom.errors.hasErrors()?:transferInstance?.addOrderClearanceOfDoms(clearanceOfDom)
    }

    def doPersist(TransferOrder transferOrder, Operation commitOperation) {

        OperationHandlerService opHandler = transferBusinessLogicService.resolveOperation(commitOperation)
        TransferOrder.withTransaction { transactionStatus ->
            transferOrder = opHandler.execute(transferOrder, transactionStatus, commitOperation)
        }
        transferOrder
    }

    def generateRequestNumber(TransferOrder domainInstance) {
        String nbr = new DecimalFormat("000000").format(domainInstance?.requestNumberSequence)
        return "${domainInstance?.requestDate.getYear()}${nbr}"
    }

    def checkCommitOperation(params) {
        def commitOperation = transferOrderWorkflowService.getCommitOperation(params)
        if (commitOperation) {
            params.commitOperation = Operation.valueOf(commitOperation.id)
            params.commitOperationName = transferOrderWorkflowService.getOperationName(params?.commitOperation)
        }

        if (transferOrderWorkflowService.validationNotRequired(params?.commitOperation)) {
            params.validationNotRequired = true
        } else {
            params.validationRequired = true
        }
    }


    def loadTransfer(id) {
        TransferOrder.findById(id, [fetch: [attachedDocs: 'join', logs: 'join']])
    }

    def retrieveExchangeData(eaReference) {
        return Exchange.findByRequestNoAndRequestType(eaReference?.trim(), TransferConstants.REQUEST_TYPE_EA)
    }

    def checkExecutionRef(TransferOrder transfer) {
        Integer year = transfer?.executionDate?.getYear()
        return TransferOrder.findByExecutionRefAndRequestYearAndBankCode(transfer?.executionRef?.trim(), year, transfer?.bankCode)
    }

    def updateEA(TransferOrder transferOrder) {
        if (transferOrder.orderClearanceOfDoms) {
            transferOrder.orderClearanceOfDoms.each {
                Exchange exchange = Exchange.findByRequestNo(it?.eaReference)
                if (exchange) {
                    Execution execution = new Execution()
                    execution = initAddExecutions(transferOrder, execution, it)
                    saveExecution(exchange, execution)
                    updateExchangeBalanceAndStatus(exchange, execution)
                }
            }
        }
    }

    def initAddExecutions(transferOrder,execution,it){
        execution.rank= it?.rank
        execution.executionReference =  transferOrder.executionRef?: null
        execution.executionDate =  transferOrder.executionDate?: null
        execution.provenanceDestinationExBank = transferOrder.destinationBank?: null
        execution.currencyExCode =  transferOrder.currencyPayCode?: null
        execution.bankAccountNumberCreditedDebited = transferOrder.bankAccntNoCredit?: null
        execution?.amountMentionedExCurrency = it?.amountRequestedMentionedCurrency
        execution.amountNationalExCurrency = execution?.amountMentionedExCurrency
        execution.amountSettledMentionedCurrency = it?.amountSettledMentionedCurrency
        execution.countryProvenanceDestinationExCode = transferOrder.countryBenefBankCode?: null
        execution.countryProvenanceDestinationExName = transferOrder.countryBenefBankName?: null
        execution.creditCorrespondentAccount = transferOrder.byCreditOfAccntOfCorsp?: null
        execution.accountExBeneficiary = transferOrder.nameOfAccntHoldCredit?: null
        execution.accountOwnerCredited = transferOrder.bankAccntNoDebited?: null
        execution.executingBankCode = transferOrder.bankCode?: null
        execution.executingBankName = transferOrder.bankName?: null
        execution
    }

    def updateAmountExchange(TransferOrder transfer){
        LOGGER.info("In updateAmountExchange() of ${TransferOrderService}")
        def orderClearanceDelete = transfer?.orderClearanceOfDoms?.findAll {it?.state == '1'}
        LOGGER.info("orderClearanceDelete : ${orderClearanceDelete}")
        orderClearanceDelete.collectEntries { OrderClearanceOfDom orderCl ->
            [(orderCl.eaReference) : orderClearanceDelete.findAll {it.eaReference == orderCl.eaReference}?.amountSettledMentionedCurrency?.grep { it != null }?.sum() as BigDecimal]
        }.each { key, val ->
              LOGGER.info("Reference: $key = Amount : $val")
              Exchange exchangeInstance = retrieveExchangeData(key as String)
              LOGGER.info("Exchange id : $exchangeInstance.id")
              if(exchangeInstance?.id){
                  BigDecimal newBalance = computingNewBalance(exchangeInstance.balanceAs, val as BigDecimal)
                  updateExchange(exchangeInstance,newBalance)
                  exchangeInstance.merge(flush: true, validate: false)
              }else{
                  LOGGER.info("Exchange not found")
              }
        }
    }

    def checkStatusAndExecutionNumberOfExchange(Exchange exchangeInstance){
        return exchangeInstance?.status == Statuses.ST_EXECUTED && exchangeInstance?.executions?.size() == 1
    }

    def computingNewBalance(BigDecimal oldBalance , BigDecimal totalCurrencyAmount){
        return oldBalance + totalCurrencyAmount
    }

    def updateExchange(Exchange exchangeInstance,BigDecimal newBalance){
        if(checkStatusAndExecutionNumberOfExchange(exchangeInstance)){
            exchangeInstance.setBalanceAs(newBalance)
            exchangeInstance.setStatus(Statuses.ST_APPROVED)
        }else{
            exchangeInstance.setBalanceAs(newBalance)
        }
        exchangeInstance
    }

    def saveExecution(Exchange exchange, Execution execution) {
        execution.exchange = exchange
        exchange.addToExecutions(execution)
        execution.save(flush: true, deepValidate: false, validate: false)
    }

    def updateExchangeBalanceAndStatus(Exchange exchange, Execution execution) {
        def balanceAs = exchange?.balanceAs ?: BigDecimal.ZERO
        exchange.balanceAs = balanceAs - execution?.amountSettledMentionedCurrency
        if (exchange?.status in [Statuses.ST_APPROVED]) {
            exchange.status = Statuses.ST_EXECUTED
        }
        exchange.save(flush: true, deepValidate: false, validate: false)
    }

    def setTransferAmountRequested(TransferOrder transferOrder, params) {
        String locale = LocaleContextHolder.getLocale().toString()
        List<OrderClearanceOfDom> domList = transferOrder?.orderClearanceOfDoms?.findAll()
        BigDecimal rate = new BigDecimal(params?.ratePayment as String)
        BigDecimal allTransferAmountRequest = domList?.sum { it.amountRequestedMentionedCurrency ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        BigDecimal totalSumAmountRequest = allTransferAmountRequest ?: BigDecimal.ZERO as BigDecimal
        transferOrder.transferAmntRequested = totalSumAmountRequest
        transferOrder.transferNatAmntRequest = totalSumAmountRequest * rate
        transferOrder.amntRequestedInLetters = convertDigitsToLetterService.convertToLetter(transferOrder.transferNatAmntRequest, locale)
    }

    def setTransferAmountExecuted(TransferOrder transferOrder, params) {
        if (UserUtils.isBankAgent()) {
            String locale = LocaleContextHolder.getLocale().toString()
            List<OrderClearanceOfDom> domList  = transferOrder?.orderClearanceOfDoms?.findAll()
            BigDecimal rate = new BigDecimal(params.ratePayment as String)
            BigDecimal allTransferAmountExecuted = domList?.sum { it.amountSettledMentionedCurrency ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
            BigDecimal totalSumAmountExecuted = allTransferAmountExecuted ?: BigDecimal.ZERO as BigDecimal
            transferOrder.transferAmntExecuted = totalSumAmountExecuted
            transferOrder.transferNatAmntExecuted = totalSumAmountExecuted * rate
            transferOrder.amntExecutedInLetters = convertDigitsToLetterService.convertToLetter(transferOrder.transferNatAmntExecuted, locale)
        }
    }

    def setTransferOrderFromEADocument(params) {
        TransferOrder transferOrder = new TransferOrder()
        Exchange  exchange = Exchange.findById(params.id)
        if (exchange) {
            transferOrder.eaReference = exchange.requestNo
            transferOrder.importerCode = exchange.importerCode
            transferOrder.importerNameAddress = exchange.importerNameAddress
            transferOrder.countryBenefBankCode = exchange.countryProvenanceDestinationCode
            transferOrder.countryBenefBankName = exchange.countryProvenanceDestinationName
            transferOrder.destinationBank = exchange.provenanceDestinationBank
            transferOrder.bankAccntNoCredit = exchange.bankAccountNocreditedDebited
            transferOrder.bankAccntNoDebited = exchange.accountNumberBeneficiary
            transferOrder.currencyPayCode = exchange.currencyPayCode
            transferOrder.currencyPayName = exchange.currencyPayName
            transferOrder.ratePayment = exchange.currencyPayRate
            transferOrder.bankCode = exchange.bankCode
            transferOrder.bankName = exchange.bankName
            OrderClearanceOfDom orderClearanceOfDom = new OrderClearanceOfDom()
            orderClearanceOfDom.state = "0"
            orderClearanceOfDom.eaReference = exchange.requestNo
            orderClearanceOfDom.authorizationDate = exchange.authorizationDate
            orderClearanceOfDom.bankName = exchange.bankName
            orderClearanceOfDom.registrationNoBank = exchange.registrationNumberBank
            orderClearanceOfDom.registrationDateBank = exchange.registrationDateBank
            orderClearanceOfDom.amountToBeSettledMentionedCurrency = exchange.balanceAs
            orderClearanceOfDom.amountRequestedMentionedCurrency = BigDecimal.ZERO
            orderClearanceOfDom.amountSettledMentionedCurrency = BigDecimal.ZERO
            transferOrder.addOrderClearanceOfDoms(orderClearanceOfDom)
        }
        transferOrder
    }

    def foundTransferorderByCriteria(currentYear, currentBankCode, currentExecutionReference){
        return TransferOrder.findByRequestYearAndBankCodeAndExecutionRef(currentYear, currentBankCode, currentExecutionReference)
    }

    boolean isTransferEnabled() {
        return AppConfig.isTransferEnabled()
    }

    def logConcernedEA(TransferOrder transfer){
        transfer.orderClearanceOfDoms.findAll {
            Exchange exchangeAuthorization = Exchange.findByRequestNo(it.eaReference)
            loggerService.saveDocumentHistory(exchangeAuthorization, transfer.status, Operations.OP_UPDATE)
        }
    }

    def handleSetCancelOrderClearanceOfDom(TransferOrder transferOrder) {
        List<OrderClearanceOfDom> domList = transferOrder.orderClearanceOfDoms
        if (domList) {
            domList.each { OrderClearanceOfDom clearance ->
                Exchange exchange = Exchange.findByRequestNo(clearance.eaReference)
                if (exchange) {
                    BigDecimal amount = exchange.balanceAs?: BigDecimal.ZERO
                    BigDecimal balance = amount + clearance.amountSettledMentionedCurrency
                    exchange.balanceAs = balance
                    Execution execution = exchange?.executions?.find { it.executionReference == transferOrder.executionRef }
                    execution?.state = EXECUTION_CANCEL_STATE
                    execution?.save(flush: true, deepValidate: false, validate: false)
                    exchange.save(flush: true, deepValidate: false, validate: false)
                }
            }
        }
    }

    def handleChangeEAStatusWhenAllExecutionsCancel(TransferOrder transferOrder) {
        List<OrderClearanceOfDom> domList = transferOrder?.orderClearanceOfDoms
        domList.each { OrderClearanceOfDom clearance ->
            Exchange exchange = Exchange.findByRequestNo(clearance.eaReference)
            boolean result = exchange?.executions?.every { it.state == EXECUTION_CANCEL_STATE }
            if (result) {
                exchange.status = Statuses.ST_APPROVED
                exchange.save(flush: true, deepValidate: false, validate: false)
            }
            loggerService.saveDocumentHistory(exchange, Statuses.ST_EXECUTED, CANCEL_VALIDATED)
        }
    }

}
