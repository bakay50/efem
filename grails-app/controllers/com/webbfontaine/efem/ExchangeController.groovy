package com.webbfontaine.efem


import com.webbfontaine.efem.command.ExchangeSearchCommand
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.efem.workflow.Operations
import com.webbfontaine.sw.rimm.RimmSadDetails
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import java.util.concurrent.locks.Lock

import static com.webbfontaine.efem.UserUtils.getUserProperty
import static com.webbfontaine.efem.constants.UserProperties.BNK
import static com.webbfontaine.efem.constants.UserProperties.ADB
import static com.webbfontaine.efem.constants.UserProperties.TIN
import static com.webbfontaine.efem.workflow.Operation.DELETE_STORED
import org.apache.http.HttpStatus

@Slf4j("LOGGER")
class ExchangeController {

    def exchangeService
    def referenceService
    def businessLogicService
    def docVerificationService
    def exchangeSearchService
    def tvfService
    def convertDigitsToLetterService
    def sadService
    def xmlService
    def notificationService
    def rimmLoadSadTvfService
    def synchronizationService

    def index() {
        redirect(action: 'list')
    }

    def list(ExchangeSearchCommand searchCommand) {
        LOGGER.debug("in list() of ${ExchangeController}")

        [searchCommand: searchCommand, referenceService: referenceService]
    }

    def search(ExchangeSearchCommand searchCommand) {
        Map searchResultModel = exchangeSearchService.getSearchResults(searchCommand, params)

        searchResultModel.referenceService = referenceService
        searchResultModel.linkParams = searchCommand.searchParams

        flash.searchResultMessage = searchResultModel.searchResultMessage

        if (request.isXhr()) {
            render(template: '/utils/search/searchResults', model: searchResultModel)
        } else {
            render(view: 'list', model: searchResultModel)
        }
    }

    def create() {
        LOGGER.debug("in create() of ${ExchangeController}")

        Exchange exchangeInstance = new Exchange()

        if (chainModel?.exchangeInstance) {
            exchangeInstance = chainModel?.exchangeInstance
        } else {
            params.conversationId = exchangeService.addToSessionStore(exchangeInstance)
        }

        businessLogicService.initDocumentForCreate(exchangeInstance, params)
        params.isDocumentEditable = true

        render(view: 'edit', model: [hasDocErrors: exchangeInstance.hasErrors(), exchangeInstance: exchangeInstance, referenceService: referenceService])
    }

    @Transactional
    def save() {
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params?.conversationId)
        if (exchangeInstance) {
            Lock lock = synchronizationService.lock(Exchange.class, exchangeInstance.id)
            lock.lock()
            try {
                bindData(exchangeInstance, params)
                Operation commitOperation = params.commitOperation as Operation
                exchangeService.getConvertedCIF(exchangeInstance)
                docVerificationService.removeAllErrors(exchangeInstance)
                if (docVerificationService.deepVerify(exchangeInstance) && BusinessLogicUtils.isLoadedDocumentValid(exchangeInstance)) {
                    Exchange result = exchangeService.doPersist(exchangeInstance, commitOperation)
                    flash.exhangeInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    if (params?.commitOperationName == Operations.OP_STORE) {
                        flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation.humanName()}")])
                    } else {
                        flash.successOperation = true
                        flash.endOperation = params?.commitOperationName
                    }
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = exchangeInstance?.id
                    chain(action: 'create', params: setParams(), model: [exchangeInstance: exchangeInstance])
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during exchange Save >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, exchangeInstance: exchangeInstance, referenceService: referenceService]
            } finally {
                lock.unlock()
            }

        } else {
            notFound()
        }
    }

    def show() {
        Exchange exchangeInstance
        params.isDocumentEditable = false

        if (flash.exhangeInstanceToShow) {
            exchangeInstance = flash.exhangeInstanceToShow
            exchangeInstance?.operations?.clear()
        } else {
            exchangeInstance = Exchange.read(params.id)
        }

        if (exchangeInstance) {
            exchangeInstance.isDocumentEditable = false
            params.conversationId = exchangeService.addToSessionStore(exchangeInstance)
            businessLogicService.initDocumentForView(exchangeInstance)
            [exchangeInstance: exchangeInstance, referenceService: referenceService]
        } else {
            notFound()
        }
    }

    def edit() {
        Exchange exchangeInstance = params.id ? exchangeService.loadExchange(Long.parseLong(params.id)) : exchangeService.findFromSessionStore(params.converstionId)
        params.isDocumentEditable = true

        if (exchangeInstance) {
            businessLogicService.initDocumentForEdit(exchangeInstance, params)
            params.conversationId = exchangeService.addToSessionStore(exchangeInstance)
            if (exchangeInstance.startedOperation == DELETE_STORED) {
                params.isDocumentEditable = false
            }
            exchangeService.setInstanceRegistrationDefaultDate(exchangeInstance)
            render view: 'edit', model: [exchangeInstance: exchangeInstance, referenceService: referenceService]

        } else {
            notFound()
        }
    }

    def update() {

        LOGGER.debug("in update() of ${ExchangeController}")
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)

        if (exchangeInstance) {
            Lock lock = synchronizationService.lock(Exchange.class, exchangeInstance.id)
            lock.lock()
            try {
                Operation commitOperation = params?.commitOperation
                params.op = params.startedOperation
                bindData(exchangeInstance, params)

                if (isValidInstance(exchangeInstance)) {
                    Exchange result = exchangeService.doPersist(exchangeInstance, commitOperation)
                    flash.exhangeInstanceToShow = result
                    flash.commitOperation = params.commitOperation
                    if (params?.commitOperationName == Operations.OI_UPDATE_STORED) {
                        flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${commitOperation.humanName()}")])
                    } else {
                        flash.successOperation = true
                        flash.endOperation = params?.commitOperationName
                    }
                    redirect(action: 'show', id: result.id)
                } else {
                    params.id = exchangeInstance?.id
                    params.op = exchangeInstance?.startedOperation
                    respond exchangeInstance.errors, view: 'edit', model: [hasDocErrors: true, exchangeInstance: exchangeInstance, referenceService: referenceService]
                    return
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.error("ERROR - Exception during exchange Update >>> ", ex)
                flash.errorMessage = wf.message(code: 'default.operation.error')
                render view: 'edit', model: [hasDocErrors: true, exchangeInstance: exchangeInstance, referenceService: referenceService]

            } finally {
                lock.unlock()
            }
        } else {
            notFound()
            return
        }
    }

    def delete() {
        LOGGER.debug("in delete() of ${ExchangeController}");

        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)
        if (exchangeInstance) {
            exchangeInstance.delete(flush: true)
            flash.commitOperation = params.commitOperation
            flash.message = message(code: 'default.operation.done.message', args: [message(code: "exchange.operation.${Operations.OP_DELETE}")])
        } else {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exchange.EA.title', default: 'EA'), exchangeInstance?.id])
        }
        redirect(action: 'list')
    }

    def verify() {
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)
        def instanceModel
        flash.message = null
        params.commitOperation = Operation.VERIFY
        params.isDocumentEditable = true
        bindData(exchangeInstance, params)
        exchangeService.getConvertedCIF(exchangeInstance)
        params.op = exchangeInstance?.startedOperation
        if (docVerificationService.deepVerify(exchangeInstance)) {
            instanceModel = [hasDocErrors: false, exchangeInstance: exchangeInstance, referenceService: referenceService]
            flash.message = message(code: "verification.done.message", default: "Verify operation is done")
        } else {
            instanceModel = [hasDocErrors: true, exchangeInstance: exchangeInstance, referenceService: referenceService]
        }

        render view: 'edit', model: instanceModel
    }

    def loadTvf() {
        LOGGER.debug("in loadTvf() of ${ExchangeController}, params: ${params}")

        if (params.tvfNumber && params.tvfDate) {
            Exchange exchangeInstance = tvfService.loadTvf(params.tvfNumber, params.tvfDate)

            def hasErrors = exchangeInstance?.hasErrors()
            businessLogicService.initDocumentForCreate(exchangeInstance, params)
            exchangeService.addToSessionStore(exchangeInstance)

            if (!hasErrors) {
                flash.message = exchangeInstance.tvf ? message(code: "loadTvf.successful", default: "TVF data has been imported.") : null
            }

            chain(action: 'create', params: setParams(), model: [exchangeInstance: exchangeInstance])
        }
    }

    def loadSad() {
        LOGGER.debug("in loadSad() of ${ExchangeController}, params: ${params}")

        Exchange exchangeInstance = sadService.retrieveExchangeFromSad(params)
        businessLogicService.initDocumentForCreate(exchangeInstance, params)
        params.conversationId = exchangeService.addToSessionStore(exchangeInstance)

        flash.message = exchangeInstance.sadInstanceId ? message(code: "loadSad.successful", default: "SAD data has been imported.") : null
        chain(action: 'create', params: setParams(), model: [exchangeInstance: exchangeInstance])
    }

    private boolean isValidInstance(Exchange exchangeInstance) {
        docVerificationService.removeAllErrors(exchangeInstance)

        return docVerificationService.deepVerify(exchangeInstance) && exchangeInstance.validate()
    }

    def convertDigitToLetter() {
        def decimal_format = Holders.config.numberFormatConfig.decimalNumberFormat
        LOGGER.debug("Decimal Format = {} ", decimal_format)
        def result = convertDigitsToLetterService.retrieveAndConvertNumberToLetter(params.amount, params.locale, decimal_format)
        LOGGER.debug("result convertor in Letter = {} ", result)
        def result_final = [
                convert_result: result
        ]
        render result_final as JSON
    }

    def convertGeoAreaName() {
        def GeoArea = exchangeService.findGeoAreaByCode(params?.geoArea)
        def result_final = [
                convert_result: GeoArea
        ]
        render result_final as JSON
    }

    def retrieveCurrencyRateUrl() {
        LOGGER.debug("Retrieving Currency Rate for {} ", params.currencyCode)
        def currencyRate = exchangeService.getCurrencyRate(params.currencyCode)
        def response = [rateValue: currencyRate]

        render response as JSON
    }

    def checkMaxAmountInXofCurrency() {
        Exchange exchangeInstance = exchangeService.findFromSessionStore(params.conversationId)
        BigDecimal sumOfAmountNationalCurrency = exchangeService.getSumOfAmountNationalCurrency(exchangeInstance)
        def totalAmountNationalCurrency = (exchangeInstance?.amountNationalCurrency ?: BigDecimal.ZERO) + (sumOfAmountNationalCurrency ?: BigDecimal.ZERO)
        String maxAmountInXofCurrency = AppConfig.maxAmountInXofCurrency
        BigDecimal invAmtInCurXof = new BigDecimal(maxAmountInXofCurrency)
        def response = [isExceed: totalAmountNationalCurrency > invAmtInCurXof]

        render response as JSON
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'exchange.label', default: 'e-Forex'), params.id])
        redirect action: "index", method: "GET"
    }

    private setParams() {
        [requestType        : params?.requestType,
         basedOn            : params?.basedOn,
         clearanceOfficeCode: params?.clearanceOfficeCode,
         clearanceOfficeName: params?.clearanceOfficeName,
         declarationSerial  : params?.declarationSerial,
         declarationNumber  : params?.declarationNumber,
         declarationDate    : params?.declarationDate,
         currencyRate       : params?.currencyRate,
         currencyPayRate    : params?.currencyPayRate,
         conversationId     : params?.conversationId]
    }

    def importXML() {
        params.isDocumentEditable = true
        LOGGER.debug("Trying to import Exchange xml file.")
        try {
            def domainInstance
            def declarationDate
            String docType = params?.domainName
            domainInstance = xmlService.importFileXml(request, docType)
            Exchange exchangeInstance = new Exchange()
            if (domainInstance instanceof Exchange) {
                if (domainInstance?.requestType == params?.requestType) {
                    if (domainInstance?.requestType == ExchangeRequestType.EA && domainInstance?.basedOn == params?.basedOn) {
                        if (domainInstance?.basedOn == ExchangeRequestType.BASE_ON_SAD) {
                            params.clearanceOfficeCode = domainInstance?.clearanceOfficeCode
                            params.clearanceOfficeName = domainInstance?.clearanceOfficeName
                            params.declarationSerial = domainInstance?.declarationSerial
                            params.declarationNumber = domainInstance?.declarationNumber
                            declarationDate = domainInstance?.declarationDate
                        }
                        exchangeInstance = domainInstance
                    } else if (domainInstance?.requestType == ExchangeRequestType.EC) {
                        exchangeInstance = domainInstance
                    }
                }
            }
            params.conversationId = exchangeService.addToSessionStore(exchangeInstance)
            businessLogicService.initDocumentForCreate(exchangeInstance, params)
            exchangeInstance?.declarationDate = exchangeInstance?.basedOn.equals(ExchangeRequestType.BASE_ON_SAD) ? declarationDate : null
            render(view: 'edit', model: [hasDocErrors: exchangeInstance.hasErrors(), exchangeInstance: exchangeInstance, referenceService: referenceService])
        } catch (IllegalArgumentException ex) {
            flash.errorMessage = wf.message(code: 'default.operation.error')
            LOGGER.warn("WARNING - Exception during import Exchange : Invalid data in xml content >> ", ex)
        }
    }

    def exportXML() {
        String domainName = params?.domainName
        if (domainName.toUpperCase() == UtilConstants.EXCHANGE.toUpperCase()) {
            def xml = xmlService.exportDomainToXml(domainName, params)
            def fileName = domainName.toUpperCase()
            response.contentType = 'application/xml'
            response.setHeader 'Content-disposition', "attachment; filename=\"${fileName}-${params?.id}.xml\""
            response.outputStream << xml
            response.outputStream.flush()
        }
    }

    def retrieveNotification() {
        def result
        if (UserUtils.isBankAgent()) {
            def bankCode = getUserProperty(BNK) ?: getUserProperty(ADB)
            result = retrieveNotificationCount(bankCode, ADB)
        } else if (UserUtils.isTrader()) {
            def userProp = getUserProperty(TIN)
            result = retrieveNotificationCount(userProp, TIN)
        }
        render result as JSON
    }

    protected  retrieveNotificationCount(userProp,userType) {
        def allowedValues = userProp.split(UtilConstants.SEPARATOR)
        def numberNotification = notificationService.handleQueryNotification(userType, allowedValues)
        def result = [numberNotif: numberNotification]
        result
    }

    def loadExchange() {
        LOGGER.debug("in loadExchange() of ${ExchangeController}");
        Exchange exchangeInstance = exchangeService.findExchangeByRequestTypeAndRequestNo(params?.requestType, params?.requestNo)
        exchangeInstance?.isDocumentEditable = false
        params.conversationId = exchangeService.addToSessionStore(exchangeInstance)
        businessLogicService.initDocumentForView(exchangeInstance)
        render(view: 'show', model: [exchangeInstance: exchangeInstance, referenceService: referenceService])
    }

    def loadDeclaration() {
        LOGGER.debug("in loadDeclaration() of ${ExchangeController}, params: ${params}")
        RimmSadDetails sadDetails = rimmLoadSadTvfService.retrieveOnesad(params?.clearanceOfficeCode, LocalDate.parse(params?.declarationDate as String, DateTimeFormat.forPattern("dd/MM/yyyy")), params?.declarationSerial, params?.declarationNumber)
        LOGGER.debug("in loadDeclaration() of ${ExchangeController}, sadDetails: ${sadDetails}")
        def result = [:]
        result.statusCode = sadDetails == null ? HttpStatus.SC_NOT_FOUND : HttpStatus.SC_OK
        result.data = sadDetails.id
        LOGGER.debug("in loadDeclaration() of ${ExchangeController}, result: ${result}")
        render result as JSON
    }
}
