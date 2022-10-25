package com.webbfontaine.efem

import com.webbfontaine.efem.command.ExchangeSearchCommand
import com.webbfontaine.efem.command.RepatriationSearchCommand
import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.efem.constant.RepatriationConstants
import com.webbfontaine.efem.constants.DepartmentInCharge
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import grails.util.Holders
import org.grails.datastore.mapping.query.api.Criteria

import static com.webbfontaine.efem.UserUtils.isBankAgent
import static com.webbfontaine.efem.UserUtils.isDeclarant
import static com.webbfontaine.efem.UserUtils.isTrader
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.Balance.EQUAL_TO_ZERO
import static com.webbfontaine.efem.constants.Balance.GREATER_THAN_ZERO
import static com.webbfontaine.efem.constants.Balance.LESS_THAN_ZERO

class SearchUtils {

    static applyExchangeDeclarantCriteria(Criteria criteria, ExchangeSearchCommand searchCommand) {
        if (UserUtils.isDeclarant()) {
            def connectedDeclarant = userPropertyValueAsList(UserProperties.DEC)
            if (searchCommand?.declarantCode) {
                UserUtils.searchForDefinedCode(searchCommand.declarantCode, connectedDeclarant, criteria, 'declarantCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedDeclarant, 'declarantCode')
            }
        } else {
            searchCommand?.declarantCode ? criteria.eq('declarantCode', searchCommand.declarantCode) : ''
        }
    }

    static applyExchangeTraderCriteria(Criteria criteria, ExchangeSearchCommand searchCommand) {
        if (UserUtils.isTrader()) {
            def connectedTrader = userPropertyValueAsList(UserProperties.TIN)
            if (searchCommand?.importerCode) {
                UserUtils.searchForDefinedCode(searchCommand.importerCode, connectedTrader, criteria, 'importerCode', 'exporterCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedTrader, 'importerCode', 'exporterCode')
            }
        } else {
            if (searchCommand?.importerCode) {
                criteria.or {
                    criteria.eq('importerCode', searchCommand.importerCode)
                    criteria.eq('exporterCode', searchCommand.importerCode)
                }
            }
        }
    }

    static applyExchangeBankCriteria(Criteria criteria, ExchangeSearchCommand searchCommand) {
        if (UserUtils.isBankAgent()) {
            def connectedBank = userPropertyValueAsList(UserProperties.ADB)
            if (searchCommand?.bankCode) {
                UserUtils.searchForDefinedCode(searchCommand.bankCode, connectedBank, criteria, 'bankCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedBank, 'bankCode')
            }
        } else {
            searchCommand?.bankCode ? criteria.eq('bankCode', searchCommand.bankCode) : ''
            searchCommand?.domiciliationBankCode ? criteria.eq('domiciliationBankCode', searchCommand.domiciliationBankCode) : ''
        }
    }

    static applyExchangeExecutionCriteria(Criteria criteria, searchCommand) {

        criteria.and {
            if (searchCommand?.executingBankCode) {
                executions {
                    eq('executingBankCode', searchCommand.executingBankCode)
                }
            }
        }
        criteria.and {
            if (searchCommand.balanceAs) {
                if (EQUAL_TO_ZERO.equals(searchCommand.balanceAs)) {
                    criteria.eq('balanceAs', BigDecimal.ZERO)
                } else if (LESS_THAN_ZERO.equals(searchCommand.balanceAs)) {
                    criteria.lt('balanceAs', BigDecimal.ZERO)
                } else if (GREATER_THAN_ZERO.equals(searchCommand.balanceAs)) {
                    criteria.gt('balanceAs', BigDecimal.ZERO)
                }
            }
        }
    }

    static applyCriteriaForDepartmentInCharge(Criteria criteria, mustInitAfterSearch){
        if (mustInitAfterSearch) {
        criteria.ne('departmentInCharge', DepartmentInCharge.FINEX)
        }
    }

    static applyRepatriationBankCriteria(Criteria criteria, RepatriationSearchCommand searchCommand) {
        if (UserUtils.isBankAgent()) {
            def connectedBank = userPropertyValueAsList(UserProperties.ADB)
            if (searchCommand?.repatriationBankCode) {
                UserUtils.searchForDefinedCode(searchCommand.repatriationBankCode, connectedBank, criteria, 'repatriationBankCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedBank, 'repatriationBankCode')
            }
        } else {
            searchCommand?.repatriationBankCode ? criteria.eq('repatriationBankCode', searchCommand.repatriationBankCode) : ''
        }
    }

    static applyRepatriationTraderCriteria(Criteria criteria, RepatriationSearchCommand searchCommand) {
        if (UserUtils.isTrader()) {
            def connectedTrader = userPropertyValueAsList(UserProperties.TIN)
            if (searchCommand?.code) {
                UserUtils.searchForDefinedCode(searchCommand.code, connectedTrader, criteria, 'code')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedTrader, 'code')
            }
        } else {
            searchCommand?.code ? criteria.eq('code', searchCommand.code) : ''
        }
    }

    static applyRepatriationCriteria(Criteria criteria, RepatriationSearchCommand searchCommand) {

        if (searchCommand?.ecReference) {
            criteria.and {
                clearances {
                    criteria.and {
                        criteria.eq('ecReference', searchCommand?.ecReference)
                        if(isBankAgent() || isTrader() || isDeclarant()){
                            criteria.eq('status', true)
                        }
                    }
                }
            }
        }
    }

    static applyTransferRoleSpecificRestrictions(Criteria criteria) {
        if (UserUtils.isGovSupervisor()) {
            criteria.and {
                ne("status", Statuses.ST_STORED)
            }
        }
    }

    static applyTransferBankCriteria(Criteria criteria, TransferOrderSearchCommand searchCommand) {
        if (UserUtils.isBankAgent()) {
            def connectedBank = userPropertyValueAsList(UserProperties.ADB)
            if (searchCommand?.bankCode) {
                UserUtils.searchForDefinedCode(searchCommand.bankCode, connectedBank, criteria, 'bankCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedBank, 'bankCode')
            }
        } else {
            searchCommand?.bankCode ? criteria.eq('bankCode', searchCommand.bankCode) : ''
        }
    }

    static applyTransferTraderCriteria(Criteria criteria, TransferOrderSearchCommand searchCommand) {
        if (UserUtils.isTrader()) {
            def connectedTrader = userPropertyValueAsList(UserProperties.TIN)
            if (searchCommand?.importerCode) {
                UserUtils.searchForDefinedCode(searchCommand.importerCode, connectedTrader, criteria, 'importerCode')
            } else {
                UserUtils.searchForUndefinedCode(criteria, connectedTrader, 'importerCode')
            }
        } else {
            searchCommand?.importerCode ? criteria.eq('importerCode', searchCommand.importerCode) : ''
        }
    }

    static applyTransferOrderCriteria(Criteria criteria, TransferOrderSearchCommand searchCommand) {
        if (searchCommand?.eaReference) {
            criteria.and {
                orderClearanceOfDoms {
                    criteria.or {
                        criteria.eq('eaReference', searchCommand?.eaReference)
                    }
                }
            }
        }
    }

    static applyExchangeAttachmentCriteria(Criteria criteria, ExchangeSearchCommand searchCommand) {
        if (searchCommand?.docRef) {
            def isNull = {
                return it == null && !"".equals(it)
            }
            criteria.and{
                if(!isNull(searchCommand?.docRef)){
                    attachedDocs{
                        eq('docRef', searchCommand?.docRef)
                    }
                }
            }
        }
    }

    static setCriteriaParameters(params, max){
        def critListParameter = [order: params?.order?params?.order:"desc", offset: params?.offset, sort: params?.sort?params?.sort:"id", max:max?max:10]
        critListParameter
    }
    
    static applyRepatriationStoredOwnerCriteria(Criteria criteria) {
        criteria.or {
            if (UserUtils.isBankAgent()) {
                and {
                    eq('status', Statuses.ST_STORED)
                    eq('storedOwner', RepatriationConstants.BANK)
                }
                or {
                    ne('status', Statuses.ST_STORED)
                }
            } else if (UserUtils.isTrader()) {
                and {
                    eq('status', Statuses.ST_STORED)
                    eq('storedOwner', RepatriationConstants.TRADER)
                }
                or {
                    ne('status', Statuses.ST_STORED)
                }
            }
        }
    }

    static setSearchLimimt(totalCount){
        def searchLimit = Holders.config.com.webbfontaine.efem.exchange.search.maxLimit
        return totalCount > searchLimit ? searchLimit : totalCount
     }

    static def modifySearchCommand(ExchangeSearchCommand searchCommand, mustInitAfterSearch) {
        if (searchCommand?.departmentInCharge && searchCommand?.departmentInCharge.equalsIgnoreCase(DepartmentInCharge.BANK)) {
            searchCommand?.departmentInCharge = null
            mustInitAfterSearch = true
        }
        [searchCommand, mustInitAfterSearch]
    }

    static ExchangeSearchCommand setDefaultBankDepartmentInCharge(searchCommand, mustInitAfterSearch){
        if (mustInitAfterSearch) {
            searchCommand?.departmentInCharge = DepartmentInCharge.BANK
        }
        searchCommand
    }

    static def applyGovAndBankRestrictionsForStored(Criteria criteria) {
        if (UserUtils.isGovOfficer() || UserUtils.isGovSupervisor() || UserUtils.isBankAgent()) {
            criteria.and {
                ne("status", Statuses.ST_STORED)
            }
        }
    }

    static def applyTransferOrderStoredRestrictionForBankAndTrader(Criteria criteria) {
        if (UserUtils.isTrader() || UserUtils.isBankAgent()) {
            criteria.or {
              ne("status", Statuses.ST_STORED)
              eq("userOwner", UserUtils.getCurrentUsername())
            }
        }
    }
}
