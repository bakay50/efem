package com.webbfontaine.efem.rimm.bank

import com.webbfontaine.efem.command.BankSearchCommand
import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.joda.time.format.DateTimeFormat


@Slf4j("LOGGER")
class BankController {

    def bankBusinessLogicService
    def bankService
    def docVerificationService
    def bankSearchService
    def referenceService

    def index() {
        redirect(action: 'list')
    }

    def list(BankSearchCommand searchCommand) {
        LOGGER.debug("in list() of ${BankController}")

        [searchCommand: searchCommand, referenceService: referenceService]
    }

    def edit() {
        Bank bankInstance = params.id ? bankService.loadBank(Long.parseLong(params.id)) : bankService.findFromSessionStore(params.converstionId)
        params.isDocumentEditable = true
        if (bankInstance) {
            bankInstance?.isDocumentEditable = true
            bankBusinessLogicService.initDocumentForEdit(bankInstance, params)
            params.conversationId = bankService.addToSessionStore(bankInstance)
            render view: 'edit', model: [bankInstance: bankInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    def search(BankSearchCommand searchCommand) {
        Map searchResultModel = bankSearchService.getSearchResults(searchCommand, params)
        searchResultModel.referenceService = referenceService
        searchResultModel.linkParams = searchCommand.searchParams
        flash.searchResultMessage = searchResultModel.searchResultMessage
        if (request.isXhr()) {
            render(plugin: 'wf-search', template: '/searchResult', model: searchResultModel)
        } else {
            render(view: 'list', model: searchResultModel)
        }
    }

    def notFound() {
        flash.errorMessage = message(code: 'default.not.found.message', args: ["Document", params.id])
        redirect(action: "edit")
    }

    def create() {
        LOGGER.debug("in create() of ${BankController}")
        Bank bankInstance = new Bank()
        if (chainModel?.bankInstance) {
            bankInstance = chainModel?.bankInstance
        } else {
            params.conversationId = bankService.addToSessionStore(bankInstance)
        }
        bankBusinessLogicService.initDocumentForCreate(bankInstance)
        bankInstance.isDocumentEditable = true
        render(view: 'edit', model: [hasDocErrors: bankInstance.hasErrors(), bankInstance: bankInstance, referenceService: referenceService])
    }

    def show() {
        Bank bankInstance
        params.isDocumentEditable = false

        if (flash.bankInstance) {
            bankInstance = flash.bankInstanceToShow
            bankInstance?.operations?.clear()
        } else {
            bankInstance = Bank.read(params.id)
        }
        if (bankInstance) {
            bankInstance.isDocumentEditable = false
            params.conversationId = bankService.addToSessionStore(bankInstance)
            bankBusinessLogicService.initDocumentForView(bankInstance)
            [bankInstance: bankInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    @Transactional
    def save() {
        Bank bankInstance = bankService.findFromSessionStore(params?.conversationId)
        if (bankInstance) {
            try {
                bindDataToDomain(bankInstance, params)
                Operation commitOperation = params.commitOperation as Operation
                if (isValidInstance(bankInstance)) {
                    Bank result = bankService.doPersist(bankInstance, commitOperation)
                    flash.bankInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    flash.successOperation = true
                    flash.endOperation = params?.commitOperationName
                    redirect(action: 'list')
                } else {
                    params.id = bankInstance?.id
                    chain(action: 'create', model: [bankInstance: bankInstance])
                }
            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during bank Save >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, bankInstance: bankInstance, referenceService: referenceService]
            }
        } else {
            notFound()
        }
    }

    def update() {
        LOGGER.debug("in update() of ${BankController}")
        Bank bankInstance = bankService.findFromSessionStore(params.conversationId)
        if (bankInstance) {
            try {
                Operation commitOperation = params?.commitOperation
                params.op = params.startedOperation
                bindDataToDomain(bankInstance, params)
                if (isValidInstance(bankInstance)) {
                    Bank result = bankService.doPersist(bankInstance, commitOperation)
                    flash.bankInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    if (params?.commitOperationName == Operations.OI_UPDATE_STORED) {
                        flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation.humanName()}")])
                    } else {
                        flash.successOperation = true
                        flash.endOperation = params?.commitOperationName
                    }
                    redirect(action: 'list')
                } else {
                    params.id = bankInstance?.id
                    params.op = bankInstance?.startedOperation
                    respond bankInstance.errors, view: 'edit', model: [hasDocErrors: true, bankInstance: bankInstance, referenceService: referenceService]
                    return
                }
            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during exchange Update >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, exchangeInstance: bankInstance, referenceService: referenceService]
            }
        } else {
            notFound()
            return
        }
    }

    def cancel() {
        params?.commitOperation = Operation.CANCEL_VALID
        update()
    }

    static def bindDataToDomain(bankInstance, params) {
        bankInstance?.dateOfValidity = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseLocalDateTime(params?.dateOfValidity)
        bankInstance?.code = params?.code
        bankInstance?.emailEA = params?.emailEA
        bankInstance?.emailEC = params?.emailEC
        bankInstance
    }

    private boolean isValidInstance(Bank bankInstance) {
        docVerificationService.removeAllErrors(bankInstance)
        return docVerificationService.deepVerify(bankInstance)
    }
}