package com.webbfontaine.transferOrder

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constant.TransferConstants
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.gorm.transactions.Transactional
import org.springframework.context.i18n.LocaleContextHolder

@Transactional
class TransferOrderValidationService {
    def transferOrderService
    def docVerificationService
    def messageSource

    def validateClearanceOfDom(clearanceOfDom) {
        docVerificationService.deepVerify(clearanceOfDom) && clearanceOfDom.validate()
    }

    def doAllCheckingOnDoc(TransferOrder transferOrder, params) {
        def resp
        if (isRefUnique(transferOrder, params?.eaReference)) {
            def exchange = transferOrderService.retrieveExchangeData(params.eaReference)
            if (exchange) {
                if (!isBankCodeValid(params?.bankCode, exchange)) {
                    Map args = [clearanceBankCode : params?.bankCode, eaBankCode : exchange.bankCode]
                    resp = setMessageError("bankCode", args)
                } else if (!isImporterCodeValid(params?.importerCode, exchange)) {
                    resp = setMessageError("importerCode")
                } else if (!isStatusValid(exchange)) {
                    resp = setMessageError("status")
                } else if (!isCurrencyCodeValid(params?.currencyCode, exchange)) {
                    resp = setMessageError("currencyCode")
                } else {
                    resp = [error: false, errorMessage: null, content: exchange]
                }

            } else {
                resp = setMessageError("notfound")
            }
        } else {
            resp = setMessageError("eaReference")
        }
        resp
    }

    def doAmountCheckingOnDoc(OrderClearanceOfDom clearance) {
        def error = null
        if (checkAmountRequestedMentionedCurrency(clearance)) {
            error = setMessageError("amountRequestedMentionedCurrency")
        }else if(checkamountSettledMentionedCurrency(clearance)){
            error = setMessageError("amountSettledMentionedCurrency")
        }
        error
    }

    def checkAmountRequestedMentionedCurrency(OrderClearanceOfDom clearance) {
        (clearance?.amountRequestedMentionedCurrency <= 0) ? true : false
    }

    def checkamountSettledMentionedCurrency(OrderClearanceOfDom clearance) {
        (clearance?.amountSettledMentionedCurrency <= 0 && UserUtils.isBankAgent())
    }

    def isRefUnique(TransferOrder transferOrder, String eaReference) {
        (transferOrder?.getClearanceOfDomByRef(eaReference)) ? false : true
    }

    def isImporterCodeValid(String importerCode, Exchange exchange) {
        (exchange?.importerCode?.equalsIgnoreCase(importerCode)) ? true : false
    }

    def isStatusValid(Exchange exchange) {
        (exchange?.status in TransferConstants.TRANSFER_STATUS) ? true : false
    }

    def isCurrencyCodeValid(String currencyCode, Exchange exchange) {
        (exchange?.currencyCode?.equalsIgnoreCase(currencyCode)) ? true : false
    }

    def isBankCodeValid(String bankCode, Exchange exchange) {
        (exchange?.bankCode?.equalsIgnoreCase(bankCode)) ? true : false
    }

    def setMessageError(String field, Map args = null) {
        Locale locale = LocaleContextHolder.getLocale()
        if (field.equalsIgnoreCase("notfound")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.notfound.error", null, locale), content: null]
        } else if (field.equalsIgnoreCase("bankCode")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.bankCode.equal.error", [args.clearanceBankCode, args.eaBankCode] as Object[], locale), content: null]
        } else if (field.equalsIgnoreCase("importerCode")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.importerCode.equal.error", null, locale), content: null]
        } else if (field.equalsIgnoreCase("currencyCode")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.currenryCode.equal.error", null, locale), content: null]
        } else if (field.equalsIgnoreCase("status")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.status.equal.error", null, locale), content: null]
        } else if (field.equalsIgnoreCase("eaReference")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.eaReference.unique.error", null, locale), content: null]
        } else if (field.equalsIgnoreCase("amountRequestedMentionedCurrency")) {
            return [error: true, errorMessage: messageSource.getMessage("transferOrder.amountRequestedMentionedCurrency.unique.error", null, locale), content: null]
        }else if (field.equalsIgnoreCase("amountSettledMentionedCurrency")) {
            return [error: true, errorMessage: messageSource.getMessage("execution.errors.amountSettledMentionedCurrency", null, locale), content: null]
        }
    }
}
