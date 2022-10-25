package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.grails.plugins.rimm.cmp.Company
import com.webbfontaine.grails.plugins.rimm.dec.Declarant
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.joda.time.LocalDate

@Slf4j
class ChkNamesAndPartiesRule implements Rule {

    private boolean withError = false

    @Override
    void apply(RuleContext ruleContext) {
        withError = false

        log.debug("in apply of ${ChkNamesAndPartiesRule}.")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange

        String declarant = exchangeInstance.declarantCode
        String importer = exchangeInstance.importerCode
        String basedOn = exchangeInstance.basedOn
        String requestType = exchangeInstance.requestType

        checkDeclarant(exchangeInstance, declarant, basedOn, requestType)

        if (!withError)
            checkCompany(exchangeInstance, importer, basedOn, requestType)
    }

    def checkDeclarant(Exchange exchange, String declarant, String basedOn, String requestType) {
        log.debug("in checkDeclarant of ${ChkNamesAndPartiesRule}.")

        if (isNameNotNullAndBasedOnTvfAndEaRequestType(declarant, basedOn, requestType) && AppConfig.checkDeclarantValidity()) {
            BuildableCriteria criteria = Declarant.createCriteria()
            check(exchange, criteria, declarant, "declarantCode")
        }
    }

    def checkCompany(Exchange exchange, String importer, String basedOn, String requestType)  {
        if (isNameNotNullAndBasedOnTvfAndEaRequestType(importer, basedOn, requestType) && AppConfig.checkCompanyValidity()) {
            BuildableCriteria criteria = Company.createCriteria()
            check(exchange, criteria, importer, "importerCode")
        }
    }

    def isNameNotNullAndBasedOnTvfAndEaRequestType(String code, String basedOn, String requestType) {
        return code && Objects.equals(basedOn, ExchangeRequestType.BASE_ON_TVF) &&
                Objects.equals(requestType, ExchangeRequestType.EA)
    }

    def check(Exchange exchange, def criteria, String code, String field) {
        def result = criteria.list {
            eq("code",code)
            le("dov", LocalDate.now().toDate().clearTime())
            or {
                isNull("eov")
                ge('eov', LocalDate.now().toDate().clearTime())
            }
            order('code', 'asc')
        }
        if (!result) {
            exchange.errors.rejectValue(field, "exchange.errors.declarantSuspended",
                    [] as Object[], "The declarant or importer account has been suspended by the customs services.")

            withError = true
        }
    }
}
