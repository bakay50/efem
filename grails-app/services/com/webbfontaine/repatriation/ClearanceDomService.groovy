package com.webbfontaine.repatriation

import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import grails.transaction.Transactional
import grails.web.databinding.DataBindingUtils
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import java.text.DecimalFormat
import java.text.ParsePosition

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class ClearanceDomService {

    private static final LOGGER = LoggerFactory.getLogger(ClearanceDomService);

    def grailsApplication
    def repatriationService

    ClearanceOfDom editClearanceDoms(Repatriation repatriationInstance) {
        LOGGER.debug("in editClearanceDoms() of ${ClearanceDomService}");
        String local = LocaleContextHolder.getLocale().toString()
        def paramsRank = currentRequestParams().rank ?: 0
        def ecReference = currentRequestParams().ecReference ?: ''
        def repatriatedAmtInCurr = currentRequestParams().repatriatedAmtInCurr ?: ''
        BigDecimal originalRepatriatedAmtInCurr = BigDecimal.ZERO
        if(repatriationInstance?.id > 0)
            originalRepatriatedAmtInCurr = repatriationService.loadRepatriatedAmountFromClearance(repatriationInstance.id,paramsRank as Integer, ecReference)
        ClearanceOfDom clearanceOfDom = repatriationInstance.getClearanceOfDom(paramsRank as Integer, ecReference)
        LOGGER.debug("in editClearanceDoms() ecReference : ${ecReference}")
        LOGGER.debug("in editClearanceDoms() in same session ${repatriationInstance?.clearances?.findAll { it?.ecReference?.equalsIgnoreCase(clearanceOfDom?.ecReference) && !it?.id }}")
        if (clearanceOfDom) {
            if (clearanceOfDom.hasErrors()) {
                clearanceOfDom?.clearErrors()
            }
            if (repatriatedAmtInCurr in RepatriationConstants.ZERO || !repatriatedAmtInCurr) {
                clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.zero', 'The Repatriated Amount in Currency must be greater than zero.')
            } else if ((ecReference && ecReference != clearanceOfDom?.ecReference) && repatriationInstance?.clearances?.findAll {
                it?.ecReference?.equalsIgnoreCase(clearanceOfDom?.ecReference) && !it?.id
            }?.size() == 1) {
                clearanceOfDom.errors.rejectValue('ecReference', 'clerance.errors.ecReference.checking', 'You are not allowed to add the same EC reference multiple times in a current session.')
            } else {
                // check if the reference is already exist in same session
                BigDecimal balance = repatriationService.checkBalanceOfExchange(ecReference)
                LOGGER.debug("in editClearanceDoms() of balance :  ${balance}")
                LOGGER.debug("in editClearanceDoms() of repatriatedAmtInCurr  :  ${currentRequestParams().repatriatedAmtInCurr}")
                updateParamsFieldsClearance(currentRequestParams())
                def result
                def allPrevAmount = repatriationInstance?.clearances.findAll {
                    it?.ecReference == clearanceOfDom?.ecReference && !it?.id && it.status == true
                }?.repatriatedAmtInCurr?.sum()
                LOGGER.debug("in editClearanceDoms() of allPrevAmount  :  ${allPrevAmount}")
                def total_sum_rep = allPrevAmount == null ? BigDecimal.ZERO : allPrevAmount
                LOGGER.debug("in editClearanceDoms() of previous total_sum_rep in BigDecimal  :  ${total_sum_rep}")
                def total
                if (local in RepatriationConstants.ENGLISH_FIELDS) {
                    def decimal_format = grailsApplication.config.numberFormatConfig.decimalNumberFormat
                    DecimalFormat decimalFormat = new DecimalFormat(decimal_format);
                    DecimalFormat df = (DecimalFormat) decimalFormat.getInstance(Locale.ENGLISH)
                    df.setParseBigDecimal(true);
                    def newRepatriatedAmtInCurrs = (BigDecimal) df.parse(currentRequestParams()?.repatriatedAmtInCurr, new ParsePosition(0))
                    LOGGER.debug("in editClearanceDoms() of newRepatriatedAmtInCurrs in BigDecimal  :  ${newRepatriatedAmtInCurrs}")
                    if (repatriationInstance?.clearances?.findAll{it.status == true}?.size() > 1) {
                        total = (total_sum_rep - clearanceOfDom?.repatriatedAmtInCurr) + newRepatriatedAmtInCurrs - originalRepatriatedAmtInCurr
                    } else {
                        total = newRepatriatedAmtInCurrs - originalRepatriatedAmtInCurr
                    }
                    LOGGER.debug("in editClearanceDoms() of total in BigDecimal  :  ${total}")
                    result = total?.compareTo(balance)
                } else {
                    if (repatriationInstance?.clearances?.findAll{it.status == true}?.size() > 1) {
                        total = (total_sum_rep - clearanceOfDom?.repatriatedAmtInCurr) + currentRequestParams()?.repatriatedAmtInCurr - originalRepatriatedAmtInCurr
                    } else {
                        total = currentRequestParams()?.repatriatedAmtInCurr - originalRepatriatedAmtInCurr
                    }
                    LOGGER.debug("in editClearanceDoms() of total in BigDecimal  :  ${total}")
                    result = total?.compareTo(balance)
                }
                LOGGER.debug("in editClearanceDoms() of result :  ${result}")
                if (result == 1) {
                    if (repatriationInstance?.clearances?.findAll{it.status == true}?.size() > 1) {
                        clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.sum', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Domiciliated Amount in Currency')
                    } else {
                        clearanceOfDom.errors.rejectValue('repatriatedAmtInCurr', 'clerance.errors.repatriatedAmtInCurr.Great', 'The sum of the Repatriated Amount in Currency for this Exchange Commitment EC Reference must not exceed the Final Invoice Amount in Currency')
                    }

                } else {
                    LOGGER.debug('updating Clearance  in list with details : {}', currentRequestParams())
                    updateParamsFieldsClearance(currentRequestParams())
                    DataBindingUtils.bindObjectToInstance(clearanceOfDom, currentRequestParams())
                }
            }
            clearanceOfDom
        } else {
            new ClearanceOfDom(currentRequestParams())
        }
    }

    ClearanceOfDom deleteClearanceDoms(Repatriation repatriationInstance) {
        LOGGER.debug("in deleteClearanceDoms() of ${ClearanceDomService}");
        def paramsRank = currentRequestParams().rank ?: 0
        String ecReference = currentRequestParams().ecReference
        ClearanceOfDom clearanceOfDom = repatriationInstance?.clearances.find {
            it.rank == paramsRank as Integer && it.ecReference.equals(ecReference)
        }
        if (clearanceOfDom) {
            repatriationInstance.ecRefToBeDeleted.add(clearanceOfDom.ecReference)
            clearanceOfDom.status = false
        } else {
            LOGGER.warn('cannot find clearance in Clearance Dom list with rank = {}, reference = {}', paramsRank, ecReference)
            clearanceOfDom = ClearanceOfDom.newInstance()
            clearanceOfDom.errors.rejectValue('rank', 'Clerance.errors.notFound', 'Cannot find Clearance Domicialiation from the list')
        }
        clearanceOfDom
    }

    def updateParamsFieldsClearance(Map params) {
        String local = LocaleContextHolder.getLocale().toString()
        LOGGER.debug("local = {}", local)
        if (local in RepatriationConstants.ENGLISH_FIELDS) {
            return
        } else {
            RepatriationConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS.each {
                LOGGER.debug("field = {}, params = {}", it, params."${it}")
                def refValue
                if (params."${it}") {
                    if (it in RepatriationConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS) {
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

    private static Map currentRequestParams() {
        GrailsWebRequest.lookup().params
    }

    def updateFinalAmount(String ecReference, BigDecimal finalAmount){
        ClearanceOfDom.findAllByEcReference(ecReference).each { ClearanceOfDom clearance ->
            clearance.invFinalAmtInCurr = finalAmount
            clearance.save(flush: true, deepValidate: false, validate: false)
        }
    }

}
