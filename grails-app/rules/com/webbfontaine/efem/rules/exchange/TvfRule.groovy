package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.constants.TvfConstants
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.efem.tvf.TvfUtils
import com.webbfontaine.efem.xml.DocXmlBinder
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.constants.TvfConstants.*
import static com.webbfontaine.efem.rules.RuleUtils.*

@Slf4j('log')
class TvfRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        log.debug("in apply() of ${TvfRule.name}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange

        boolean tvfEnabled = Holders.config.rest.tvf.enabled
        boolean isTvfUsable = checkIfTvfIsUsable(exchangeInstance)
        def result = Holders.config.rest?.isWebService ? checkIfTvfExists(exchangeInstance) : [tvfExists: false, tvfXmlData: null]

        if (tvfEnabled && result?.tvfExists && result?.tvfXmlData) {

            exchangeInstance?.tvf = DocXmlBinder.bindXmlToTvf(TvfUtils.parseXml(result.tvfXmlData))

            if (isTvfUsable) {
                setTvfFieldsToExchange(exchangeInstance)
            }
        }

        checkIfTvfFlowIsCompatible(exchangeInstance)
    }

    private boolean checkIfTvfIsUsable(Exchange exchangeInstance) {
        boolean isTvfUsable = true
        Tvf tvf = exchangeInstance?.tvf ?: getExchangeTvfFromTvfService(exchangeInstance)
        String tvfFlow = tvf?.flow
        String tvfTin = IM_FLOW.equalsIgnoreCase(tvfFlow) ? tvf?.impTaxPayerAcc : tvf?.expTaxPayerAcc
        String tvfDeclarantCode = tvf?.decCode

        def checkIfTvfIsAccessible = { def userProp, def code ->
            boolean isTvfInaccessible = false
            if (code != null) {
                if (userProp && !ALL.equalsIgnoreCase(userProp)) {
                    if (!userProp.equalsIgnoreCase(code)) {
                        if (userProp.contains(":")) {
                            List userProperties = userProp.split(':') as List
                            if (!(code in userProperties)) {
                                isTvfInaccessible = true
                            }
                        } else {
                            isTvfInaccessible = true
                        }
                    }
                }
            }

            if (isTvfInaccessible) {
                isTvfUsable = false
                if (!RuleUtils.errorAlreadyExist(exchangeInstance, "exchange.errors.tvf.inaccessibleToUser")) {
                    exchangeInstance.errors.rejectValue("tvfNumber", "exchange.errors.tvf.inaccessibleToUser", "The TVF cannot be used by the connected user.");
                }
            }
            setTvfUsable(exchangeInstance, isTvfUsable)
            isTvfUsable
        }

        if (!checkIfTvfIsAccessible(UserUtils.getUserProperty(UserProperties.TIN), tvfTin)) {
            return isTvfUsable
        }

        if (!checkIfTvfIsAccessible(UserUtils.getUserProperty(UserProperties.DEC), tvfDeclarantCode)) {
            return isTvfUsable
        }

        return isTvfUsable
    }

    private def checkIfTvfExists(Exchange exchangeInstance) {
        boolean tvfExists = true
        String tvfXmlData

        if (exchangeInstance?.tvf == null || exchangeInstance?.tvf?.trNumber != exchangeInstance?.tvfNumber
                || exchangeInstance?.tvf?.trDate?.toString(TVF_DATE_FORMAT) != exchangeInstance?.tvfDate?.toString(TVF_DATE_FORMAT)) {

            String tvfDate = exchangeInstance?.tvfDate?.getDayOfMonth() + "-" + exchangeInstance?.tvfDate?.getMonthOfYear() + "-" + exchangeInstance?.tvfDate?.getYear()
            tvfXmlData = tvfService.getTvfResponse(exchangeInstance, exchangeInstance?.tvfNumber?.toString(), tvfDate);
            def response = TvfUtils.checkTvfResponse(tvfXmlData, exchangeInstance)

            if (!response?.isValidTvf) {
                tvfExists = false
            }
        }
        return [tvfExists: tvfExists, tvfXmlData: tvfXmlData];
    }

    private void setTvfFieldsToExchange(Exchange exchangeInstance) {
        if (exchangeInstance?.tvf) {
            Tvf tvf = exchangeInstance?.tvf

            exchangeInstance.domiciliationBankCode = tvf?.bankCode
            exchangeInstance.domiciliationBankName = tvf?.bankName
            exchangeInstance.exporterCode = tvf?.expTaxPayerAcc
            exchangeInstance.exporterNameAddress = tvf?.expName + '\n' + tvf?.expAddress
            exchangeInstance.importerCode = tvf?.impTaxPayerAcc
            exchangeInstance.importerNameAddress = tvf?.impName
            exchangeInstance.currencyCode = tvf?.invCurCode
            exchangeInstance.currencyPayCode = tvf?.invCurCode
        }
    }

    private def checkIfTvfFlowIsCompatible(Exchange exchangeInstance) {
        Tvf tvf = exchangeInstance?.tvf

        if (TvfConstants.EX_FLOW.equals(tvf?.flow)) {
            exchangeInstance.errors.reject("exchange.loadTvf.flow.invalid.error")
            setTvfUsable(exchangeInstance, false)
        }
    }

    def getExchangeTvfFromTvfService(Exchange exchangeInstance) {
        TvfService service = Holders.applicationContext.getBean("tvfService")
        Exchange exchange = service.loadTvf(exchangeInstance?.tvfNumber?.toString(), FormattingUtils.fromDate(exchangeInstance?.tvfDate))
        exchange?.tvf
    }

    static setTvfUsable(Exchange exchange, boolean isTvfUsable) {
        exchange.isTvfUsable = isTvfUsable
        if (!isTvfUsable) {
            exchange.tvfNumber = null
            exchange.tvfDate = null
        }
    }
}
