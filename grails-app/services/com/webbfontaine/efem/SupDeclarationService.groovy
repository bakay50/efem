package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import grails.web.databinding.DataBindingUtils
import org.slf4j.LoggerFactory


class SupDeclarationService {
    def exchangeService

    private static final LOGGER = LoggerFactory.getLogger(SupDeclarationService)

    def editSupdeclaration(Exchange exchange) {
        LOGGER.debug("in editClearanceDoms() of ${SupDeclarationService}")
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0

        SupDeclaration supDeclaration = exchange.getSupDeclaration(paramsRank as Integer)
        if (supDeclaration) {
            if (supDeclaration.hasErrors()) {
                supDeclaration?.clearErrors()
            }
            supDeclaration
        } else {
            new SupDeclaration(ReferenceUtils.currentRequestParams())
        }
    }

    SupDeclaration updateSupDeclaration(SupDeclaration supDeclaration) {
        LOGGER.debug("updating Supdeclaration  in list with details : {}", ReferenceUtils.currentRequestParams())
        DataBindingUtils.bindObjectToInstance(supDeclaration, ReferenceUtils.currentRequestParams()) as SupDeclaration
    }

    SupDeclaration deleteSupDeclaration(Exchange exchange) {
        LOGGER.debug("in deleteSupDeclaration() of ${SupDeclarationService}")
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0
        SupDeclaration supDeclaration = exchange.getSupDeclaration(paramsRank as Integer)
        if (supDeclaration) {
            exchange.removeFromList(exchange.supDeclarations, supDeclaration)
            return null
        } else {
            LOGGER.debug("cannot find Item in SupDeclaration list with rank = {}", paramsRank)
            supDeclaration = SupDeclaration.newInstance()
            supDeclaration.errors.rejectValue('rank', '', 'Cannot find SupDeclaration from the list')
        }
        supDeclaration
    }

    def setRemainingBalanceEADeclaredAmount(Exchange exchange, Exchange exchangeFromSad) {
        List<SupDeclaration> declarationList = exchange?.supDeclarations?.findAll()
        BigDecimal rate = exchangeFromSad.currencyRate ?: 1
        BigDecimal alldeclarationAmountWriteOff = declarationList?.sum { it.declarationAmountWriteOff ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        BigDecimal amountCalculate = alldeclarationAmountWriteOff ? exchange.amountMentionedCurrency - alldeclarationAmountWriteOff : exchange.amountMentionedCurrency - BigDecimal.ZERO
        BigDecimal totaldeclarationAmountWriteOff = amountCalculate
        exchange.remainingBalanceDeclaredAmount = totaldeclarationAmountWriteOff
        exchange.remainingBalanceDeclaredNatAmount = totaldeclarationAmountWriteOff * rate
    }

    def setRemainingBalanceTransferDoneAmount(Exchange exchange) {
        List<OrderClearanceOfDom> domList = OrderClearanceOfDom.findAllByEaReference(exchange.requestNo)
        List<SupDeclaration> declarationList = exchange.supDeclarations.findAll()
        BigDecimal allAmountSettledMentionedCurrency = domList?.sum { it.amountSettledMentionedCurrency ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        BigDecimal allDeclarationAmountWriteOff = declarationList?.sum { it.declarationAmountWriteOff ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        BigDecimal result = allAmountSettledMentionedCurrency - allDeclarationAmountWriteOff
        BigDecimal rate = exchange.currencyRate ?: 1
        exchange.remainingBalanceTransferDoneAmount = result
        exchange.remainingBalanceTransferDoneNatAmount = result * rate
    }

    def setDeclarationCifAmount(Exchange exchange, SupDeclaration supDeclaration) {
        supDeclaration.declarationCifAmount = exchange.cifAmtFcx ?: BigDecimal.ZERO
    }

    def setDeclarationRemainingBalance(Exchange exchange, SupDeclaration supDeclaration) {
        List<SupDeclaration> declarationList = SupDeclaration.findAllByClearanceOfficeCodeAndDeclarationSerialAndDeclarationNumberAndDeclarationDate(exchange.clearanceOfficeCode, exchange.declarationSerial, exchange.declarationNumber as Integer, exchange.declarationDate)
        BigDecimal amountOfCif = exchange.cifAmtFcx?: BigDecimal.ZERO
        BigDecimal allDeclarationAmountWriteOff = declarationList?.sum { it.declarationAmountWriteOff ?: BigDecimal.ZERO } as BigDecimal ?: BigDecimal.ZERO
        BigDecimal sumOfamountInMentionnedCurrency = exchangeService.doSumOfExchangeAmount(exchange, Statuses.CHECKED_STATUS_FOR_AMOUNT_RULE, true, ExchangeRequestType.AMOUNT_IN_MENTIONNED_CURRENCY, true, true)
        BigDecimal result = amountOfCif - (allDeclarationAmountWriteOff + sumOfamountInMentionnedCurrency)
        supDeclaration.declarationRemainingBalance = result
    }
}
