package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.ReferenceUtils
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constant.CurrencyTransferConstants
import com.webbfontaine.efem.currencyTransfer.ClearanceDomiciliation
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.efem.rules.RuleUtils
import com.webbfontaine.efem.rules.currencyTransfer.ClearanceCurrencyCodeRule
import com.webbfontaine.efem.rules.currencyTransfer.EditClearanceDomiciliationRule
import com.webbfontaine.efem.rules.currencyTransfer.RepatriatedAmountToBankRule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.web.databinding.DataBindingUtils
import groovy.util.logging.Slf4j
import org.springframework.context.i18n.LocaleContextHolder

@Slf4j("LOGGER")
class ClearanceDomiciliationService {

    def grailsApplication

    def updateParamsFields(Map params) {
        String local = LocaleContextHolder.getLocale().toString()
        LOGGER.debug("local = {}", local)
        if (local in CurrencyTransferConstants.ENGLISH_FIELDS) {
            return null
        } else {
            CurrencyTransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS.each {
                LOGGER.debug("field = {}, params = {}", it, params."${it}")
                def refValue
                if (params."${it}") {
                    if (it in CurrencyTransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS) {
                        refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${it}", true)
                        LOGGER.debug("field = {}, params = {},refValue = {}", it, params."${it}", refValue?.toBigDecimal())
                        params."${it}" = refValue?.toBigDecimal()
                    } else {
                        refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${it}", false)
                        LOGGER.debug("field = {}, params = {},refValue = {}", it, params."${it}", refValue?.toBigDecimal())
                        params."${it}" = refValue?.toBigDecimal()
                    }
                }
            }
        }
    }

    def editClearanceDom(CurrencyTransfer currencyTransferInstance) {
        LOGGER.debug("in method editClearanceDom() of ${ClearanceDomiciliationService}")
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0
        def ecReference = ReferenceUtils.currentRequestParams().ecReference ?: ''
        ClearanceDomiciliation domiciliation = currencyTransferInstance.getClearanceDomiciliation(paramsRank as Integer, ecReference)
        if (domiciliation) {
            if (domiciliation.hasErrors()) {
                domiciliation?.clearErrors()
            }
            def RULES = [
                    new EditClearanceDomiciliationRule()
            ]
            RuleContext ruleContext = new RuleContext(currencyTransferInstance, currencyTransferInstance.errors)
            RuleUtils.executeSetOfRules(RULES, ruleContext)
            domiciliation
        } else {
            new ClearanceDomiciliation(ReferenceUtils.currentRequestParams())
        }
    }

    ClearanceDomiciliation updateClearanceDom(ClearanceDomiciliation clearanceDomiciliation) {
        LOGGER.debug("updating Clearance  in list with details : {}", ReferenceUtils.currentRequestParams())
        updateParamsFields(ReferenceUtils.currentRequestParams())
        DataBindingUtils.bindObjectToInstance(clearanceDomiciliation, ReferenceUtils.currentRequestParams())
    }

    ClearanceDomiciliation deleteClearanceDom(CurrencyTransfer currencyTransferInstance) {
        LOGGER.debug("in delete method() of ${ClearanceDomiciliationService}")
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0
        String ecReference = ReferenceUtils.currentRequestParams()?.ecReference
        ClearanceDomiciliation domiciliation = currencyTransferInstance?.clearanceDomiciliations?.find {
            it.rank == paramsRank as Integer && it.ecReference == ecReference
        }
        if (domiciliation) {
            currencyTransferInstance.removeFromList(currencyTransferInstance?.clearanceDomiciliations, domiciliation)
            return null
        } else {
            LOGGER.debug("cannot find clearance in Clearance Dom list with rank = {}, reference = {}", paramsRank, ecReference)
            domiciliation = ClearanceDomiciliation.newInstance()
            domiciliation.errors.rejectValue('rank', 'Clerance.errors.notFound', 'Cannot find Clearance Domicialiation from the list')
        }
        domiciliation
    }

    def checkCurrencyCode(ClearanceDomiciliation clearanceDomiciliation) {
        def RULES = [
                new ClearanceCurrencyCodeRule(),
                new RepatriatedAmountToBankRule()
        ]
        RuleContext ruleContext = new RuleContext(clearanceDomiciliation, clearanceDomiciliation.errors)
        RuleUtils.executeSetOfRules(RULES, ruleContext)
    }

}
