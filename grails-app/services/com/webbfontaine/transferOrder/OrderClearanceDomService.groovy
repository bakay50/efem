package com.webbfontaine.transferOrder

import com.webbfontaine.efem.ReferenceUtils
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constant.TransferConstants
import com.webbfontaine.efem.transferOrder.OrderClearanceOfDom
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.transaction.Transactional
import grails.web.databinding.DataBindingUtils
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import static com.webbfontaine.efem.UserUtils.isSuperAdministrator
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Transactional
class OrderClearanceDomService {
    private static final LOGGER = LoggerFactory.getLogger(OrderClearanceDomService);

    def grailsApplication

    OrderClearanceOfDom editClearanceDoms(TransferOrder transferInstance) {
        LOGGER.debug("in editClearanceDoms() of ${OrderClearanceDomService}");
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0
        def eaReference = ReferenceUtils.currentRequestParams().eaReference ?: ''
        OrderClearanceOfDom orderClearanceOfDom = transferInstance.getClearanceOfDom(paramsRank as Integer, eaReference)
        if (orderClearanceOfDom) {
            if (orderClearanceOfDom.hasErrors()) {
                orderClearanceOfDom?.clearErrors()
            }

            DataBindingUtils.bindObjectToInstance(orderClearanceOfDom, ReferenceUtils.currentRequestParams())
            orderClearanceOfDom
        } else {
            new OrderClearanceOfDom(ReferenceUtils.currentRequestParams())
        }
    }

    def deleteClearanceDomsBySuperAdmin(TransferOrder transferInstance, OrderClearanceOfDom clearanceOfDom) {
        LOGGER.debug("in deleteClearanceDomsBySuperAdmin() of ${OrderClearanceDomService}");
        def clearanceOfDoms = transferInstance?.orderClearanceOfDoms
        if (clearanceOfDom) {
            clearanceOfDom.setState("1")
            clearanceOfDoms = deleteListeByState(transferInstance,"orderClearanceOfDoms",clearanceOfDom,clearanceOfDom.rank)
        }
        return clearanceOfDoms
    }

    OrderClearanceOfDom deleteClearanceDoms(TransferOrder transferInstance) {
        LOGGER.debug("in deleteClearanceDoms() of ${OrderClearanceDomService}");
        def paramsRank = ReferenceUtils.currentRequestParams().rank ?: 0
        String eaReference = ReferenceUtils.currentRequestParams().eaReference
        OrderClearanceOfDom clearanceOfDom = transferInstance?.orderClearanceOfDoms?.find {
            it.rank == paramsRank as Integer && it?.eaReference?.equals(eaReference) && it.state == '0'
        }
        if (clearanceOfDom) {
            if(isSuperAdministrator()){
                deleteClearanceDomsBySuperAdmin(transferInstance,clearanceOfDom)
            }else{
                transferInstance.removeFromList(transferInstance?.orderClearanceOfDoms, clearanceOfDom)
            }
            return null
        } else {
            LOGGER.warn('cannot find clearance in Clearance Dom list with rank = {}, reference = {}', paramsRank, eaReference)
            clearanceOfDom = OrderClearanceOfDom.newInstance()
            clearanceOfDom.errors.rejectValue('rank', 'Clerance.errors.notFound', 'Cannot find Clearance Domicialiation from the list')
        }
        clearanceOfDom
    }

    def updateParamsFieldsClearance(Map params) {
        String local = LocaleContextHolder.getLocale().toString()
        LOGGER.debug("local = {}", local)
        if (local in TransferConstants.ENGLISH_FIELDS) {
            return
        } else {
            TransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS.each {
                doParamsFieldsUpdate(params, it)
            }
        }

    }

    def doParamsFieldsUpdate(Map params, item) {
        LOGGER.debug("field = {}, params = {}", item, params."${item}")
        def refValue
        if (params."${item}") {
            if (item in TransferConstants.EXCEPTION_CLEARANCE_FIELD_PARAMS) {
                refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${item}", true)
            } else {
                refValue = TypeCastUtils.parseCurrencyApplyPattern(params."${item}", false)
            }
            LOGGER.debug("field = {}, params = {},refValue = {}", item, params."${item}", refValue?.toBigDecimal())
            params."${item}" = refValue?.toBigDecimal()
        }
    }

    def deleteListeByState(targetDomain,listPropertyName,instanceToSearch,instanceRank){
        def criteriaByState = {
            it?.state != '1'
        }
        List listOfProp = targetDomain?."$listPropertyName"
        def results = listOfProp.findAll {criteriaByState}.collect {
            if (it?.rank == instanceRank) {
                instanceToSearch
            } else {
                it
            }
        }
        for (def b in results) {
            if (b?.rank > instanceToSearch?.rank) {
                b.rank--
            }
        }
        return results
    }

}
