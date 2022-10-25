package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.tvf.TvfService
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j

@Slf4j
class CheckNamesAndPartiesFromTVFRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        log.debug("in apply of ${CheckNamesAndPartiesFromTVFRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        def tvfData = retrieveExchangeDocument(exchangeInstance)
        handleRule(exchangeInstance, tvfData)
    }

    static def handleRule(Exchange exchange, Exchange tvfData) {
        if (tvfData?.hasErrors()) {
            exchange.errors.rejectValue('tvfNumber', tvfData?.errors?.allErrors?.first()?.code, tvfData?.errors?.allErrors?.first()?.defaultMessage)
        } else {
            BusinessLogicUtils.handleNameAndPartiesErrors(exchange, exchange?.importerCode, tvfData?.tvf?.impTaxPayerAcc, "importerCode", "exchange.errors.tvf.importerCode.error", "Importer non consistent with that of the TVF")
            BusinessLogicUtils.handleNameAndPartiesErrors(exchange, exchange?.exporterCode, tvfData?.tvf?.expTaxPayerAcc, "exporterCode", "exchange.errors.tvf.exporterCode.error", "Exporter non consistent with that of the TVF")
        }
    }

    static def retrieveExchangeDocument(Exchange exchange) {
        TvfService service = AppContextUtils.getBean(TvfService)
        def exchangeDocument = service.loadExchangeFromTvfByView(exchange?.tvfNumber?.toString(), FormattingUtils.fromDate(exchange?.tvfDate))
        exchangeDocument
    }
}
