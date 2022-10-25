package com.webbfontaine.efem.rules.currencyTransfer

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.attachedDoc.CurrencyTransferAttachedDoc
import com.webbfontaine.efem.constants.CurrencyTransferRequestType
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.repatriation.constants.NatureOfFund
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.CurrencyTransferRequestType.DOC_SWIFT_MESSAGE

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 17/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class ValidationCurrencyTransferRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {

        def connectedBankCode = userPropertyValueAsList(UserProperties.ADB)
        CurrencyTransfer currencyTransfer = ruleContext.getTargetAs(CurrencyTransfer) as CurrencyTransfer
        checkIfSwiftDocIsAttached(currencyTransfer)
        BigDecimal totalClearanceAmount = 0
        currencyTransfer.clearanceDomiciliations.each() {
            totalClearanceAmount = evalCLearance(it, totalClearanceAmount, currencyTransfer)
        }

        if(totalClearanceAmount == 0 && !isPreFinancingRepatriation(currencyTransfer.repatriationNo)){
            currencyTransfer.errors.rejectValue("amountTransferred", "clerance.errors.totalAmountTransferred.zero","The total amount transferred in Currency must be greater than zero.")
        }
        if((currencyTransfer?.clearanceDomiciliations?.size() == 0 || currencyTransfer?.clearanceDomiciliations == null) && !isPreFinancingRepatriation(currencyTransfer.repatriationNo)){
            currencyTransfer.errors.rejectValue("amountTransferred", "currencyTransfer.errors.clearanceAmount.count","You must specify at least one Exchange Commitment.")
        }
        if(currencyTransfer.amountRepatriated < currencyTransfer.amountTransferred){
            currencyTransfer.errors.rejectValue("amountTransferred", "currencyTransfer.errors.amountRepatriatedInCurr.sum","SUM of Amount Transferred in Currency by Exchange Commitment can not exceed the Amount Transferred.")
        }
        if(currencyTransfer.amountTransferred < totalClearanceAmount){
            currencyTransfer.errors.rejectValue("amountTransferred", "currencyTransfer.errors.amountTransferredInCurr.sum","SUM of Amount Transferred in Currency by Exchange Commitment can not exceed the Amount Transferred.")
        }
        if(currencyTransfer.amountTransferred <= BigDecimal.ZERO){
            currencyTransfer.errors.rejectValue("amountTransferred", "currencyTransfer.amountTransferred.mandatory","The field Amount Transferred must be greater than 0.")
        }
        if(!(currencyTransfer?.bankCode in connectedBankCode)){
            currencyTransfer.errors.rejectValue("bankCode", "currencyTransfer.errors.repatriation.bank", "The Exchange Commitment has not been repatriated to your bank")
        }
        evalCurrentTransferDate(currencyTransfer)
        evalRepatriationDate(currencyTransfer)
    }

    private  evalCurrentTransferDate(CurrencyTransfer currencyTransfer) {
        currencyTransfer?.attachedDocs.each {
            if (it.docType == CurrencyTransferRequestType.DOC_SWIFT_MESSAGE){
                if(currencyTransfer?.currencyTransferDate != it.docDate) {
                    currencyTransfer.errors.rejectValue("currencyTransferDate", "currencyTransfer.errors.repatriation.currencyTransferDate", "The date of transfer and the date of your document \"AVIS DE CESSION OU MESSAGE SWIFT\" are not identical")
                }
            }
        }
    }

    private  evalRepatriationDate(CurrencyTransfer currencyTransfer) {
        currencyTransfer?.attachedDocs.each {
            if (it.docType == CurrencyTransferRequestType.DOC_SWIFT_MESSAGE){
                if(it.docDate < currencyTransfer?.repatriationDate ) {
                    currencyTransfer.errors.rejectValue("repatriationDate", "currencyTransfer.errors.repatriation.repatriationDate", "The date of your \"AVIS DE CESSION OU MESSAGE SWIFT\" document must be no earlier than the date of repatriation.")
                }
            }
        }
    }

    private BigDecimal evalCLearance(ClearanceDomiciliation it, BigDecimal totalClearanceAmount, CurrencyTransfer currencyTransfer) {
        if(it.amountTransferredInCurr <= 0){
            Integer clearanceRank = it.rank
            currencyTransfer.errors.reject("clerance.errors.amountTransferred.zero", [clearanceRank] as Object[],"The Amount transferred in Currency must be greater than zero for Exchange Commitment ${clearanceRank}.")
        }else if (it.ecReference) {
            totalClearanceAmount += it.amountTransferredInCurr
            Exchange exchange = Exchange.findByRequestNo(it.ecReference)
            checkCurrencyCode(exchange, currencyTransfer)
        }
        return totalClearanceAmount
    }

    def checkCurrencyCode(Exchange exchange, CurrencyTransfer currencyTransfer){
        if (exchange && exchange?.currencyCode != currencyTransfer?.currencyCode) {
            currencyTransfer.errors.rejectValue("currencyCode", "currencyTransfer.errors.currency.code.label","The Transfer Currency must be identical to the Exchange Commitment Currency to be cleared")
        }
    }

    boolean isPreFinancingRepatriation(String repatriationNo){
        Repatriation repatriation = Repatriation.findByRequestNo(repatriationNo)
        repatriation?.natureOfFund == NatureOfFund.NOF_PRE
    }

    def checkIfSwiftDocIsAttached(CurrencyTransfer currencyTransfer) {
        List<CurrencyTransferAttachedDoc> attachements = currencyTransfer?.attachedDocs
            if (!attachements.any {it.docType == DOC_SWIFT_MESSAGE}) {
                currencyTransfer.errors.reject( 'currencyTransfer.errors.swiftMessage', 'Provide the Swift Message or Transfer Notice Please.')
            }
    }
}
