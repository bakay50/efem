package com.webbfontaine.efem

import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.constants.NatureOfFund
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.hibernate.FetchMode

@Transactional
@Slf4j("LOGGER")
class ToastNotificationService {

    def buildQueryNotification() {
        if (UserUtils.isBankAgent()) {
            def bankCode = UserUtils.getUserProperty(UserProperties.BNK) ?: UserUtils.getUserProperty(UserProperties.ADB)
            LOGGER.info("get connected bank agent code : {}", bankCode)
            String[] allowedValues = bankCode.tokenize(UtilConstants.SEPARATOR)
            LOGGER.info("get allowedValues : {}", allowedValues)
            List<Repatriation> results = Repatriation.withCriteria {
                eq('natureOfFund', NatureOfFund.NOF_PRE)
                fetchMode "clearances", FetchMode.SELECT
                if (allowedValues.contains(UserProperties.ALL)) {
                    isNotNull('repatriationBankCode')
                } else {
                    inList('repatriationBankCode', allowedValues)
                }
                clearances {
                    eq('status', true)
                }
            } as List<Repatriation>

            ArrayList repatriationRequestNumber  = []
            results.each {
                repatriationRequestNumber << it.requestNo
            }
            List<CurrencyTransfer> currencyTransfers = CurrencyTransfer.withCriteria {
                fetchMode "clearanceDomiciliations", FetchMode.SELECT
                eq('status', Statuses.ST_TRANSFERRED)
                inList('repatriationNo', repatriationRequestNumber)
                if (allowedValues.contains(UserProperties.ALL)) {
                    isNotNull('bankCode')
                } else {
                    inList('bankCode', allowedValues)
                }
            } as List<CurrencyTransfer>
            LOGGER.info("get pre-financing file related to currency transfer count : {}", currencyTransfers.size())
            return currencyTransfers.size()
        }

    }
}
