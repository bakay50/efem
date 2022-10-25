package com.webbfontaine.efem.rules.exchange.declaration

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.ExchangeService
import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import static com.webbfontaine.efem.UserUtils.getUserProperty
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.TvfConstants.IM_FLOW
import static com.webbfontaine.efem.constants.UserProperties.*
import static com.webbfontaine.efem.constants.UtilConstants.SUPDECLARATION

@Slf4j("LOGGER")
class CheckSadDeclarationRule implements Rule {

    @Override
    void apply(RuleContext ruleContext) {
        LOGGER.debug("apply() of ${SupDeclarationBusinessLogicRule}")
        def params = WebRequestUtils.getParams()
        if (params?.controller == SUPDECLARATION) {
            SupDeclaration supDeclaration = ruleContext.getTargetAs(SupDeclaration)
            Exchange exchange = supDeclaration.exchange
            def conversationId = params.conversationId
            def sad = handleGetSad()
            checkDeclarationSadExistRule(ruleContext, sad)
            checkDeclarationSadConsigneeCodeRule(ruleContext, sad)
            checkDeclarationSadCurrencyCodeRule(ruleContext, sad)
            checkDuplicateDeclarationSessionRule(ruleContext, supDeclaration)
            checkDeclarationSadTypeRule(ruleContext, sad)
            checkDeclarationSadStatusRule(ruleContext, sad)
            checkDeclarationRemainingBalanceRule(ruleContext, sad)
            LOGGER.debug("id = {}, cid = {} declaration # {}. Started. ", exchange?.id, conversationId, supDeclaration?.rank)
        }
    }

    def checkDeclarationSadExistRule(ruleContext, def sad) {
        if (sad?.statusCode != HttpStatus.OK.value()) {
            ruleContext.reject('exchange.errors.existe.declarationNumber')
        }
    }

    def checkDeclarationSadConsigneeCodeRule(ruleContext, def sad) {
        def tin = userPropertyValueAsList(TIN) ?: getUserProperty(TIN)
        if (!isSadOwner(sad, tin)) {
            ruleContext.reject('load.sad.ownership.error')
        }
    }

    def checkDeclarationSadCurrencyCodeRule(ruleContext, sad) {
        def exchange = handleGetExchange()
        if (sad?.data?.invoiceCurrencyCode != exchange?.currencyCode && sad?.statusCode == HttpStatus.OK.value()) {
            ruleContext.reject('dec.sad.currency.code.error')
        }
    }

    def checkDuplicateDeclarationSessionRule(ruleContext, SupDeclaration supDeclaration) {
        def exchangeInstance = handleGetExchange()
        def declarationList = exchangeInstance?.supDeclarations?.any {
            it.clearanceOfficeCode == supDeclaration.clearanceOfficeCode &&
                    it.rank != supDeclaration.rank && it.declarationDate == supDeclaration.declarationDate
            it.declarationNumber == supDeclaration.declarationNumber && it.declarationSerial == supDeclaration.declarationSerial
        }
        if (declarationList) {
            ruleContext.reject('dec.duplicate.error')
        }
    }

    def checkDeclarationSadTypeRule(ruleContext, sad) {
        if (sad?.data?.typeOfDeclaration != IM_FLOW && sad?.statusCode == HttpStatus.OK.value()) {
            ruleContext.reject('load.sad.declaration.error')
        }
    }

    def checkDeclarationSadStatusRule(ruleContext, sad) {
        if (!isSuccessStatus(sad)) {
            ruleContext.reject('load.sad.status.error')
        }
    }

    def checkDeclarationRemainingBalanceRule(ruleContext, sad) {
        def exchangeFromSad = getExchangeFromSad(sad)
        if (exchangeFromSad?.requestNo) {
            List<SupDeclaration> declarationList = SupDeclaration.findAllByClearanceOfficeCodeAndDeclarationSerialAndDeclarationNumberAndDeclarationDate(exchangeFromSad.clearanceOfficeCode, exchangeFromSad.declarationSerial, exchangeFromSad.declarationNumber as Integer, exchangeFromSad.declarationDate)
            BigDecimal alldeclarationAmountWriteOff = declarationList*.declarationAmountWriteOff?.sum() as BigDecimal ?: BigDecimal.ZERO
            BigDecimal amountOfCif = exchangeFromSad?.totalAmountOfCif / exchangeFromSad?.currencyRate
            BigDecimal amount = Math.round(amountOfCif)
            BigDecimal result = amount - alldeclarationAmountWriteOff
            if (result < 0) {
                ruleContext.reject('dec.remainingBalance.error')
            }
        }
    }

    static def getExchangeFromSad(sad) {
        SadService service = AppContextUtils.getBean(SadService)
        def params = WebRequestUtils.params
        return service.getExchangeFromSad(sad, params?.requestType)
    }

    static boolean isSuccessStatus(props) {
        props?.data?.status in [Statuses.ST_EXITED, Statuses.ST_TOTALLYEXITED, Statuses.ST_PAID] && props?.statusCode == HttpStatus.OK.value()
    }

    static def handleGetExchange() {
        def params = WebRequestUtils.getParams()
        ExchangeService service = AppContextUtils.getBean(ExchangeService)
        return service.findFromSessionStore(params?.conversationId)
    }

    boolean isSadOwner(props, userProps) {
        (props?.data?.consigneeCode in userProps || userProps == ALL) && props?.statusCode == HttpStatus.OK.value()
    }

    static def handleGetSad() {
        SadService service = AppContextUtils.getBean(SadService)
        def params = WebRequestUtils.params
        return service.retrieveSad(params)
    }
}
