package com.webbfontaine.efem

import com.webbfontaine.efem.Config.FieldsConfiguration
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.execution.Execution
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.efem.workflow.ExchangeOperationHandlerUtils
import com.webbfontaine.efem.workflow.Operation
import com.webbfontaine.repatriation.RepatriationService
import com.webbfontaine.repatriation.constants.NatureOfFund
import com.webbfontaine.transferOrder.TransferOrderService
import com.webbfontaine.wfutils.AppContextUtils
import grails.util.Holders
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.validation.FieldError
import org.springframework.web.servlet.support.RequestContextUtils
import javax.servlet.http.HttpServletRequest
import static com.webbfontaine.efem.Config.Config.MANDATORY
import static com.webbfontaine.efem.constant.TransferConstants.CLEARANCE_DOMICILIATION_FIELDS
import static com.webbfontaine.efem.constants.Statuses.*
import static com.webbfontaine.efem.workflow.Operation.*
import static com.webbfontaine.efem.workflow.Operations.*
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_TVF
import static com.webbfontaine.efem.constants.ExchangeRequestType.MAXIMUM_AMOUNT
import static com.webbfontaine.efem.constants.ExchangeRequestType.REGISTRATION_BANK_FIELDS
import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_SAD
import static com.webbfontaine.efem.constants.ExchangeRequestType.EXECUTION_CANCEL_STATE


class BusinessLogicUtils {

    static Locale getLocale() {
        HttpServletRequest servletRequest = GrailsWebRequest.lookup().getRequest()
        Locale locale = RequestContextUtils.getLocale(servletRequest)
        locale
    }

    static def getLicenseLog(domain) {
        BusinessLogicService service = AppContextUtils.getBean(BusinessLogicService) as BusinessLogicService
        return service.getHistory(domain)
    }

    static def checkStartedOperationInListOfOperationsByRoles(def startedOperation, def listOfOperation) {
        return startedOperation in listOfOperation
    }


    static boolean isNull(def value) {
        null == value
    }

    static boolean canDisplayDeclarationSection(Exchange exchange) {
        if ((exchange.requestType == EA) || (exchange.requestType == EC && exchange.status in [ST_QUERIED, ST_PARTIALLY_APPROVED, ST_APPROVED, ST_EXECUTED, ST_CANCELLED])) {
            return true
        }
        return false
    }

    static boolean isTvfUsable(Exchange exchange) {
        return exchange.isTvfUsable != null && exchange.isTvfUsable
    }

    static boolean isFieldEditable(String field, Exchange exchange) {
        Boolean result
        if (checkIfEA(exchange)) {
            result = isEAFieldEditable(field, exchange)
        } else {
            result = isECFieldEditable(field, exchange)
        }
        return result
    }

    static boolean isFieldEditableTransferOrder(String field, def op) {
        Boolean result
        switch (field) {
            case ["executionRef", "executionDate"]:
                result = op in [CREATE, STORE, REQUEST, REQUEST_QUERIED, REQUEST_STORED] && UserUtils.isBankAgent()
                break
            case CLEARANCE_DOMICILIATION_FIELDS:
                result = enableTransferDomiciliationFields(op)
                break
            default:
                result = true
                break
        }
        result
    }

    static boolean isECFieldEditable(String field, Exchange exchange) {
        def result
        def op = ExchangeOperationHandlerUtils.getOp() as Operation
        switch (field) {
            case ["declarantCode"]:
                result = false
                break
            case ["comments"]:
                result = op.humanName() in [OP_QUERY, OP_REJECT]
                break
            case ["countryOfExportCode"]:
                result = false
                break
            case ["importerNameAddress"]:
                result = exchange.status in [null, ST_REQUESTED, ST_QUERIED]
                break
            case ["exporterNameAddress"]:
                result = !(exchange.status in [null, ST_REQUESTED, ST_QUERIED])
                break
            case FieldsConfiguration.getDomiciliationFields():
                result = enableDomiciliationFields(op)
                break
            case REGISTRATION_BANK_FIELDS:
                result = enableRegistrationBankFields(op)
                break
            default:
                result = true
                break
        }
        return result
    }


    static boolean isEAFieldEditable(String field, Exchange exchange) {
        def result
        def op = ExchangeOperationHandlerUtils.getOp() as Operation
        switch (field) {
            case ["exporterNameAddress", "declarantNameAddress", "currencyCode"]:
                result = !(exchange.status in [null, ST_STORED, ST_QUERIED])
                break
            case ["declarantCode"]:
                result = false
                break
            case ["currencyPayCode"]:
                result = enableCurrencyPayCodeByOperation(exchange, op)
                break
            case ["countryOfExportCode"]:
                result = !(op in [UPDATE_QUERIED, REQUEST_QUERIED, APPROVE_REQUESTED, APPROVE_PARTIALLY_APPROVED]) ?: false
                break
            case ["exporterCode"]:
                result = !(exchange.status in [null, ST_STORED, ST_QUERIED]) || (exchange.status in [null, ST_STORED, ST_QUERIED] && exchange.specialImporter)
                break
            case FieldsConfiguration.getDomiciliationFields():
                result = enableDomiciliationFields(op)
                break
            default:
                result = true
                break
        }
        return result
    }

    static boolean isFieldEditableRepatriation(String field, def op) {
        Boolean result
        switch (field) {
            case ["executionRef", "executionDate"]:
                result = op in [CREATE, STORE, DECLARE, CONFIRM, CONFIRM_DECLARED, CONFIRM_STORED] && UserUtils.isBankAgent()
                break
            default:
                result = true
                break
        }
        result


    }

    static void setAttachmentAccess(domain) {
        def commitOp = ExchangeOperationHandlerUtils.getOp() as Operation
        switch (commitOp) {
            case [CREATE, UPDATE_STORED, REQUEST_STORED, REQUEST_QUERIED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED, TRANSFER_STORED, UPDATE_VALIDATED, CANCEL_VALIDATED, DECLARE_STORED, DECLARE_QUERIED]:
                domain.isAttachmentEditable = true
                break
            default:
                domain.isAttachmentEditable = false
                break
        }
    }

    static def setTransferIsEaOperationsEditable(TransferOrder transfer) {
        def commitOp = GrailsWebRequest.lookup().params.op
        if (commitOp in [REQUEST.name(), REQUEST_STORED.name(), REQUEST_QUERIED.name(), UPDATE_STORED.name(), UPDATE_QUERIED.name(), VALIDATE.name()]) {
            transfer.isEaOpearationsEditable = true
        }
    }

    static def setRepatriationOperationsIsDocumentEditable(Repatriation repatriation) {
        def operation = GrailsWebRequest?.lookup()?.params?.op as Operation
        if (operation in [CONFIRM_DECLARED, DECLARE_QUERIED, UPDATE_QUERIED, UPDATE_STORED, DECLARE_STORED]) {
            repatriation.isDocumentEditable = true
        }
    }


    static def checkIfEC(Exchange exchangeInstance) {
        return exchangeInstance.requestType == EC
    }

    static def checkIfEA(Exchange exchangeInstance) {
        return exchangeInstance.requestType == EA
    }

    static boolean checkIfEAAndDeclarantCodeMandatory(Exchange exchange) {
        return exchange.requestType == EA && exchange.status != ST_QUERIED
    }

    static def hasAccessToSupDeclaration(Exchange exchangeInstance) {
        return (checkIfEA(exchangeInstance) && BASE_ON_TVF.equals(exchangeInstance.basedOn))
    }

    static enableCurrencyPayCodeByOperation(exchange, op) {
        return exchange?.requestType == EA && exchange?.clearanceOfficeCode in Holders.config.efemAllowedOfficeCode.code_office && op in [null, REQUEST, REQUEST_STORED, REQUEST_QUERIED, UPDATE_QUERIED]
    }

    static enableDomiciliationFields(def op) {
        def result
        if (UserUtils.isBankAgent() && op in [Operation.DOMICILIATE, Operation.APPROVE_REQUESTED, Operation.UPDATE_APPROVED, Operation.UPDATE_EXECUTED]) {
            result = true
        } else {
            result = false
        }
        return result
    }

    static enableRegistrationBankFields(def op) {
        def result
        result = !(UserUtils.isGovOfficer() && op in [Operation.DOMICILIATE, Operation.APPROVE_REQUESTED, Operation.APPROVE_PARTIALLY_APPROVED])
        result
    }

    static requireDomiciliationFields(Operation op) {
        return UserUtils.isBankAgent() && op in [Operation.UPDATE_APPROVED, Operation.UPDATE_EXECUTED]
    }

    static String concatenateAddresses(String... address) {
        StringBuilder str = new StringBuilder()
        address?.each {
            if (it) {
                str.append(it.trim()).append(" ")
            }
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1)
        }
        str.toString()
    }

    public static String getUserProp(property) {
        def userProp = UserUtils.getUserProperty(property)
        def prop = userProp && !(UserProperties.ALL.equalsIgnoreCase(userProp)) ? userProp : null
        return prop
    }

    static boolean isLoadedDocumentValid(Exchange exchange) {
        boolean isWebService = Holders.config.rest?.isWebService
        if (exchange.requestType == EC) {
            return true
        } else {
            boolean isValid = exchange?.sadInstanceId || exchange?.tvfInstanceId || !isWebService
            return isValid
        }
    }

    static boolean checkIfStatusInList(Exchange exchange, List<String> statuses) {
        exchange?.status in statuses
    }

    static boolean isCommentDisabled(Exchange exchange) {
        !(exchange?.startedOperation in [QUERY_REQUESTED, QUERY_PARTIALLY_APPROVED, REJECT_REQUESTED, REJECT_PARTIALLY_APPROVED, APPROVE_REQUESTED, PARTIALLY_APPROVED, CANCEL_QUERIED, CANCEL_APPROVED])
    }

    static boolean isCommentDisabled(Repatriation repatriation) {
        !(repatriation?.startedOperation in [QUERY_DECLARED, CONFIRM_DECLARED, CANCEL_QUERIED, CANCEL_CONFIRMED])
    }

    static boolean isCommentTransferDisabled(TransferOrder transfer) {
        !(transfer?.startedOperation in [QUERY_REQUESTED, CANCEL_QUERIED, CANCEL_VALIDATED])
    }

    static boolean isUpdateOper(Exchange exchange) {
        !(exchange?.startedOperation in [UPDATE_STORED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED])
    }

    static boolean isCommentDisabled(CurrencyTransfer currencyTransfer) {
        !(currencyTransfer?.startedOperation in [CANCEL_TRANSFERRED])
    }

    public static String getRequestType(Exchange exchange) {
        return exchange?.requestType
    }

    static def isDomiciliationRequired(def amountVal) {
        return amountVal >= MAXIMUM_AMOUNT
    }

    static boolean isVerifyAvailable(domain) {
        if (domain.status in [null, ST_STORED, ST_REQUESTED]) {
            return true
        } else {
            return domain.status in [ST_APPROVED] && domain.requestType == EC
        }
    }

    static Long getOriginalVersion(id) {
        return (Long) Exchange.createCriteria().list {
            eq("id", id)
            projections {
                property("version")
            }
        }[0]
    }

    static Long getRepOriginalVersion(id) {
        return (Long) Repatriation.createCriteria().list {
            eq("id", id)
            projections {
                property("version")
            }
        }[0]
    }

    static Long getTransferOriginalVersion(id) {
        return (Long) TransferOrder.createCriteria().list {
            eq("id", id)
            projections {
                property("version")
            }
        }[0]
    }

    static Long getCurrencyTransferOriginalVersion(id) {
        return (Long) CurrencyTransfer.createCriteria().list {
            eq("id", id)
            projections {
                property("version")
            }
        }[0]
    }

    static def getAllRequiredFieldOfTransFer(def startedOperation) {
        def MANDATORY_FIELDS = [:]
        def fields
        Operation.values().each {
            MANDATORY_FIELDS.put(it, FieldsConfiguration.getTransferFieldsConfigPerOperation(MANDATORY, it))
        }
        fields = MANDATORY_FIELDS.get(startedOperation)
        return fields
    }

    static def enableTransferDomiciliationFields(def op) {
        def result
        if (UserUtils.isBankAgent() && op.name() in [VALIDATE.name(), UPDATE_STORED.name(), CREATE.name(), REQUEST_STORED.name(), REQUEST_QUERIED.name()]) {
            result = true
        } else if (UserUtils.isTrader() && op.name() in [VALIDATE.name()]) {
            result = true
        } else {
            result = false
        }
        result
    }

    static boolean isPrintExchange(Exchange exchange) {
        Boolean result = false
        if (exchange?.requestType == EA) {
            if (exchange?.status in [ST_REQUESTED, ST_APPROVED, ST_EXECUTED]) {
                result = true
            }
        } else if (exchange?.requestType == EC) {
            if (exchange?.status in [ST_APPROVED, ST_EXECUTED]) {
                result = true
            }
        }
        result
    }

    static boolean traderOrDeclarantFieldsProhibited(Exchange exchange) {
        Boolean result = false
        if (exchange?.requestType == EC && (UserUtils.isTrader() || UserUtils.isDeclarant())) {
            if (exchange?.startedOperation in [CREATE, REQUEST_QUERIED, UPDATE_QUERIED, UPDATE_APPROVED, UPDATE_EXECUTED]) {
                result = true
            }
            return result
        }
    }

    static boolean canEnableDomiciliationDetails(exchangeInstance) {
        def result = false
        if (exchangeInstance.requestType == EA) {
            if ((UserUtils.isBankAgent() && exchangeInstance?.status == Statuses.ST_REQUESTED)) {
                result = true
            } else if (UserUtils.isTrader() || UserUtils.isDeclarant()) {
                result = false
            }
        }
        result
    }

    static def handleNameAndPartiesErrors(Exchange exchange, exchangeField, transactionField, field, errorCode, defautMessage) {
        if (exchangeField && !exchangeField?.equals(transactionField)) {
            exchange.errors.rejectValue(field, errorCode, defautMessage)
        }
    }

    static boolean canDisplayExchangeImportExportXml(Exchange exchangeInstance) {
        Boolean displayEAImportExport = AppConfig.displayEAImportExport()
        def result = true
        if (exchangeInstance?.requestType == EA) {
            if (!displayEAImportExport) {
                result = false
            }
        }
        return result
    }

    static boolean isProhibitedFinalInvoice(Exchange exchangeInstance) {
        boolean result = true
        int repatCount = ClearanceOfDom.findAllByEcReference(exchangeInstance.requestNo).count { it.repats.status in Statuses.CHECKED_STATUS_FOR_FINAL_AMOUNT  && it.status}
        if (!exchangeInstance.isFinalAmount && repatCount == 0 && isFinalInvoiceOperation(exchangeInstance) && UserUtils.isTrader()) {
            result = false
        } else if (exchangeInstance.isFinalAmount && repatCount == 0 && isFinalInvoiceOperation(exchangeInstance) && exchangeInstance.hasErrors() && UserUtils.isTrader()) {
            result = false
        }
        return result
    }

    static boolean handleDisplayWriteOffTab(Exchange exchange) {
        return exchange.basedOn == BASE_ON_TVF && exchange.requestType == EA && exchange.status in [ST_APPROVED, ST_EXECUTED]
    }

    static boolean handleOpenViewEC(String ecReference, String status) {
        boolean viewEC = false
        if (status in [ST_DECLARED, ST_CONFIRMED]) {
            if (UserUtils.isBankAgent()) {
                def userProps = UserUtils.getUserProperty(UserProperties.ADB)
                def allowedValues = userProps.split(UtilConstants.SEPARATOR)
                def bankCode = Exchange.findByRequestNo(ecReference)?.bankCode
                viewEC = bankCode in allowedValues
            } else if (UserUtils.isTrader()) {
                viewEC = true
            }
        }
        return viewEC
    }

    static boolean isFinalInvoiceOperation(Exchange exchange) {
        return exchange.startedOperation in [UPDATE_APPROVED]
    }

    static def setBaseOnForEcDocument(Exchange exchange) {
        if (exchange.requestType == EC) {
            exchange.basedOn = BASE_ON_SAD
        }
    }

    static def handleGetExecutionsForUsers(List<Execution> executions) {
        if (!UserUtils.isSuperAdministrator()) {
            executions?.removeAll { element -> element.state == EXECUTION_CANCEL_STATE }
        }
        return executions
    }

    static def isRepatriationExist(requestNo, requestDate) {
        RepatriationService service = AppContextUtils.getBean(RepatriationService)
        service.findRepatriationByRequestNoAndDate(requestNo, requestDate)
    }

    static boolean isTransferOrderExist(Execution execution) {
        TransferOrderService service = AppContextUtils.getBean(TransferOrderService)
        TransferOrder transferInstance = service.foundTransferorderByCriteria(execution?.executionDate?.getYear(), execution?.executingBankCode, execution?.executionReference)
        transferInstance?.id > 0
    }

    static def capitalizeResult(String value) {
        value ? value.substring(0, 1).toUpperCase() + value.substring(1) : ""
    }

    static boolean isReceivedAmountDisable(Repatriation repatriation) {
        repatriation.startedOperation in [CANCEL_CONFIRMED, CONFIRM_DECLARED, QUERY_DECLARED, UPDATE_CONFIRMED, DELETE_STORED, UPDATE_CLEARANCE]
    }

    static def isExchangeExist(requestType, requestNo) {
        ExchangeService service = AppContextUtils.getBean(ExchangeService)
        service.findExchangeByRequestTypeAndRequestNo(requestType, requestNo)
    }

    static boolean canDisplayStamp(Exchange exchange) {
        exchange?.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED] && exchange?.authorizedBy?.equalsIgnoreCase(UtilConstants.THE_FINEX)
    }

    static boolean displayDeclaration(Exchange exchange) {
        return exchange.clearanceOfficeCode && exchange.declarationSerial && exchange.declarationDate && exchange.declarationNumber && exchange.sadInstanceId
    }

    static boolean displayFieldNameAndAddress(Exchange exchange) {
        displayFieldNameAndAddressForEC(exchange) || disabledFieldNameAndAddressForEC(exchange) || exchange?.requestType == EA
    }

    static boolean displayFieldNameAndAddressForEC(Exchange exchange) {
        return exchange?.requestType == EC && (exchange?.startedOperation == APPROVE_REQUESTED || exchange.startedOperation == UPDATE_APPROVED || exchange.startedOperation == UPDATE_EXECUTED) && exchange.clearanceOfficeCode && exchange.declarationSerial && exchange.declarationNumber && exchange.declarationDate
    }

    static disabledFieldNameAndAddressForEC(Exchange exchange) {
        return (UserUtils.isBankAgent() || UserUtils.isGovOfficer()) && exchange.requestType == EC
    }

    static boolean handleRemoveOperationForBankOfficer(Exchange exchange) {
        boolean isEc = exchange.requestType == EC
        boolean result = (exchange.areaPartyCode != ExchangeRequestType.AREA_003 && exchange?.geoArea != ExchangeRequestType.AREA_002) || exchange.geoArea == ExchangeRequestType.AREA_002
        return isEc && UserUtils.isBankAgent() && result
    }

    static boolean handleRemoveOperationForGovOfficer(Exchange exchange) {
        boolean isEc = exchange.requestType == EC
        boolean result = (exchange?.geoArea == ExchangeRequestType.AREA_001 || exchange.geoArea == ExchangeRequestType.AREA_003) && exchange.areaPartyCode == ExchangeRequestType.AREA_003
        return isEc && UserUtils.isGovOfficer() && result
    }

    static boolean isRepatriationFieldReadonly(Repatriation repatriation, String field) {
        return !repatriation.isFieldEditable(field)
    }

    static boolean isRepatriationFieldMandatory(Repatriation repatriation, String field) {
        return repatriation.isFieldEditable(field) && repatriation.isFieldMandatory(field)
    }

    static FieldError checkExchangeFieldsErrors(Exchange exchangeInstance) {
        exchangeInstance.errors.fieldErrors.find { it.field in ["sadTypeOfDeclaration", "declarationNumber", "exporterCode", "registrationNumberBank"] }

    }

    static boolean isOldRepatriationClearance(String natureOfFund, boolean isOldClearance, Operation startedOperation) {
        return natureOfFund == NatureOfFund.NOF_REP && isOldClearance && startedOperation == UPDATE_CONFIRMED
    }

    static boolean isPrefinancingWithoutClearance(Operation startedOperation, boolean isPrefinancingWithoutEC) {
        return isPrefinancingWithoutEC && startedOperation in [UPDATE_STORED, TRANSFER_STORED, CREATE]
    }

}