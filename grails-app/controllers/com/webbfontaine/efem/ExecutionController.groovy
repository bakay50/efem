package com.webbfontaine.efem

import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.workflow.Operations
import grails.converters.JSON
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletResponse

@Slf4j("LOGGER")
class ExecutionController {

    def exchangeService
    def executionService
    def referenceService
    def docVerificationService
    def paginationService
    def convertDigitsToLetterService
    def transferOrderService

    def addExecution() {
        LOGGER.debug("in addExecution() of ${ExecutionController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)

        if (exchangeInstance) {
            Execution execution = new Execution()
            executionService.initAddExecutions(exchangeInstance, execution)
            exchangeInstance.addExecution(execution)
            def model = [
                    template: g.render(template: "/exchange/templates/executionFormDetails",
                            model: [execution: execution, exchangeInstance: exchangeInstance, addExecution: true, referenceService: referenceService])
            ]
            render model as JSON
        } else {
            badRequest()
        }
    }

    def saveExecution() {
        LOGGER.debug("in saveExecution() of ${ExecutionController}")
        Execution execution = new Execution(executionService.findExecution(params).properties)
        Execution currentExecution = execution.exchange.getExecution(Integer.parseInt(params.rank))
        bindData(execution, params)
        def isValid = docVerificationService.deepVerify(execution)
        def response
        if (isValid) {
            currentExecution.properties = execution.properties
            Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
            exchangeInstance.numberOfexecutions = exchangeInstance?.executions?.size() ?: 0
            params.itemNumber = params.rank
            exchangeService.computationOfBalanceAs(exchangeInstance)
            def numberOfExecution = exchangeInstance?.executions?.size() ?: 0
            def executionsRangeToRender = paginationService.getSubListRangeAfterItemUpdate(numberOfExecution, params)
            def executionList = exchangeInstance.executions.subList(executionsRangeToRender.from, executionsRangeToRender.to)
            response = [
                    numberOfExecutions: exchangeInstance.numberOfexecutions,
                    updatedBalance    : exchangeInstance?.balanceAs,
                    template          : g.render(template: "/exchange/templates/executionSection", model: [exchangeInstance: exchangeInstance, executionList: executionList])
            ]
        } else {
            response = [
                    error       : 'error',
                    responseData: g.render(template: "/utils/executionErrorMessages", model: [executionInstance: execution])
            ]
        }

        render response as JSON
    }

    def editExecution() {
        LOGGER.debug("in editExecution() of ${ExecutionController}");
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            Execution execution = exchangeInstance.getExecution(params.rank as Integer)
            exchangeService.computationOfBalanceAs(exchangeInstance)
            if (execution) {
                def response = [updatedBalance: exchangeInstance?.balanceAs,
                                template      : g.render(template: "/exchange/templates/executionFormDetails",
                                        model: [exchangeInstance: exchangeInstance, execution: execution, editExecution: true, referenceService: referenceService])]
                render response as JSON
            } else {
                executionNotFound()
            }
        } else {
            badRequest()
        }
    }

    def removeExecution() {
        LOGGER.debug("in removeExecution() of ${ExecutionController}");
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            Execution execution = exchangeInstance.getExecution(params.rank as Integer)
            exchangeService.computationOfBalanceAs(exchangeInstance)
            if (execution) {
                def response = [updatedBalance: exchangeInstance?.balanceAs,
                                template      : g.render(template: "/exchange/templates/executionFormDetails", model: [exchangeInstance: exchangeInstance, execution: execution, referenceService: referenceService, removeExecution: true])]
                render response as JSON
            } else {
                executionNotFound()
            }
        } else {
            badRequest()
        }
    }

    def deleteExecution() {
        LOGGER.debug("in deleteExecution() of ${ExecutionController}");
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            if (executionService.deleteExecution(exchangeInstance, params)) {
                params.itemNumber = params.rank
                exchangeService.computationOfBalanceAs(exchangeInstance)
                def numberOfExecution = exchangeInstance?.executions?.size() ?: 0
                def executionsRangeToRender = paginationService.getSubListRangeAfterItemUpdate(numberOfExecution, params)
                def executionList = exchangeInstance.executions.subList(executionsRangeToRender.from, executionsRangeToRender.to)
                def response = [
                        numberOfExecution: exchangeInstance?.numberOfexecutions,
                        updatedBalance   : exchangeInstance?.balanceAs,
                        template         : g.render(template: "/exchange/templates/executionSection", model: [exchangeInstance: exchangeInstance, executionList: executionList])
                ]
                render response as JSON
            } else {
                executionNotFound()
            }
        } else {
            badRequest()
        }
    }

    def cancelExecution() {
        LOGGER.debug("in cancelExecution() of ${ExecutionController}");
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {

            Execution execution = exchangeInstance.getExecution(params.rank as Integer)
            if (params.previousOperation == Operations.OP_ADD_EXECUTION) {
                exchangeInstance.removeExecution(execution)
            }
            params.itemNumber = params.rank
            exchangeService.computationOfBalanceAs(exchangeInstance)
            def numberOfExecution = exchangeInstance?.executions?.size() ?: 0
            def executionsRangeToRender = paginationService.getSubListRangeAfterItemUpdate(numberOfExecution, params)
            def executionList = exchangeInstance.executions.subList(executionsRangeToRender.from, executionsRangeToRender.to)
            def responseData = [updatedBalance: exchangeInstance?.balanceAs, template: g.render(template: "/exchange/templates/executionSection",
                    model: [exchangeInstance: exchangeInstance, referenceService: referenceService, executionList: executionList, params: params])]
            render responseData as JSON
        } else {
            badRequest()
        }
    }

    def convertDigitToLettre() {
        def amountInBigDecimal = executionService.formatDecimalNumber(params)
        def result = convertDigitsToLetterService.convertToLetter(amountInBigDecimal, params.locale)
        def result_final = [convert_result: result]
        render result_final as JSON
    }

    private def executionNotFound() {
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        exchangeInstance.clearErrors()
        exchangeInstance.errors.rejectValue('executions', 'exec.errors.notFound', [params.rank] as Object[], "Requested execution with number ${params.rank} not found")
        def responseErrorData = [
                error       : 'error',
                responseData: g.render(template: "/utils/executionErrorMessages", model: [executionInstance: exchangeInstance])
        ]
        LOGGER.warn("Requested Execution with number ${params.rank} not found")
        render responseErrorData as JSON
    }

    private def badRequest() {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        render(contentType: 'text/json') {
            [message: message(code: "default.bad.request.message", "Requested action outdated. Please reopen the document")]
        }
    }
}
