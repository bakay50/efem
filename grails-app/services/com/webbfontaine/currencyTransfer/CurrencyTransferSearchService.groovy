package com.webbfontaine.currencyTransfer

import com.webbfontaine.efem.SearchUtils
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer
import com.webbfontaine.grails.plugins.search.QueryBuilder
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.Criteria
import org.hibernate.transform.AliasToBeanResultTransformer
import org.springframework.beans.factory.annotation.Value
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady DIARRA
 * Date: 10/08/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class CurrencyTransferSearchService {
    @Value('${com.webbfontaine.efem.exchange.search.max}')
    def searchResultMax
    def searchService
    def currencyTransferWorkflowService
    def springSecurityService
    private volatile static Collection<String> allResultFields;


    def getSearchResults(searchCommand, params = null) {
        QueryBuilder queryBuilder = new QueryBuilder(searchCommand)
        def max = null
        if (params.containsKey('max')) {
            if (params.max != null) {
                max = params.max as Long
            }
        } else {
            max = Long.parseLong(searchResultMax)
        }

        def totalCount
        def resultList

        try {
            totalCount = getTotalCount(queryBuilder, searchCommand) ?: 0
            resultList = totalCount ? getSearchResult(queryBuilder, max, params, searchCommand) : Collections.emptyList()
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error encountered during Search", e)
            totalCount = 0
            resultList = Collections.emptyList()
        }

        totalCount = SearchUtils.setSearchLimimt(totalCount)
        def searchResultMessage = searchService.getSearchResultMessage(resultList?.size(), totalCount)
        if (totalCount) {
            resultList.each { CurrencyTransfer currencyTransfer ->
                currencyTransferWorkflowService.initOperations(currencyTransfer)
            }
        }
        return [actionsTemplatePlugin: 'wf-workflow',
                actionsTemplate      : 'searchResultActions',
                searchCommand        : searchCommand,
                max                  : max,
                resultList           : resultList,
                totalCount           : totalCount,
                searchResultMessage  : searchResultMessage
        ]
    }

    private getSearchResult(QueryBuilder queryBuilder, max, params, searchCommand) {
        Criteria criteria = CurrencyTransfer.createCriteria()
        def critListParameter = SearchUtils.setCriteriaParameters(params, max)
        def resultList = criteria.list(critListParameter) {
            queryBuilder.initListCriteria(criteria, false)
            applyCurrencyTransferCriteria(criteria, searchCommand)
            applyRoleSpecificRestrictions(criteria)
            resultTransformer(new AliasToBeanResultTransformer(CurrencyTransfer.class))
            projections {
                distinct ( "id" )
                resultFields.each {
                    property(it, it)
                }
            }
        }
        return resultList
    }

    private static Collection<String> getResultFields() {
        if (!allResultFields) {
            synchronized (CurrencyTransferSearchService.class) {
                if (allResultFields == null) {
                    allResultFields = CurrencyTransferSearchCommand.resultFields.collect { it.getName() } + ['id']
                }
            }
        }
        return allResultFields
    }
    private getTotalCount(QueryBuilder queryBuilder, CurrencyTransferSearchCommand searchCommand) {
        Criteria criteria = CurrencyTransfer.createCriteria()

        return criteria.get {
            queryBuilder.initCountCriteria(criteria)
            applyCurrencyTransferCriteria(criteria, searchCommand)
            applyRoleSpecificRestrictions(criteria)
            projections { count() }
        }
    }

    def applyRoleSpecificRestrictions(Criteria criteria) {
        if (UserUtils.isGovSupervisor()) {
            LOGGER.debug("APPLYING CRITERIA FOR STORED")
            criteria.and {
                ne("status", Statuses.ST_STORED)
            }
        }
    }

    def applyCurrencyTransferCriteria(Criteria criteria, CurrencyTransferSearchCommand searchCommand) {
        LOGGER.info("applyCurrencyTransferCriteria Methode")
        def isNull = {
            return it == null && !"".equals(it)
        }

        if(UserUtils.isBankAgent()){
            def connectedBank = userPropertyValueAsList(UserProperties.ADB)
            if(isNull(searchCommand.bankCode)){
                UserUtils.searchForUndefinedCode(criteria, connectedBank, 'bankCode')
            }else{
                UserUtils.searchForDefinedCode(searchCommand.bankCode, connectedBank, criteria, 'bankCode')
            }
        }else {
            searchCommand?.bankCode ? criteria.eq('bankCode', searchCommand.bankCode) : ''
        }

        criteria.and {
            if (!isNull(searchCommand.ecReference) || !isNull(searchCommand.ecDate) || !isNull(searchCommand.ecDateTo)) {
                clearanceDomiciliations {
                    criteria.or {
                        if (!isNull(searchCommand?.ecReference)) {
                            criteria.eq('ecReference', searchCommand?.ecReference)
                        }
                        if (!isNull(searchCommand?.ecDate) || !isNull(searchCommand?.ecDateTo)) {
                            if ("equals".equals(searchCommand.op_ecDate)) {
                                criteria.eq('ecDate', searchCommand?.ecDate)
                            } else if ("less than".equals(searchCommand.op_ecDate.trim())) {
                                criteria.lt('ecDate', searchCommand?.ecDate)
                            } else if ("greater than".equals(searchCommand.op_ecDate)) {
                                criteria.gt('ecDate', searchCommand?.ecDate)
                            } else if ("between".equals(searchCommand.op_ecDate)) {
                                criteria.between('ecDate', searchCommand?.ecDate, searchCommand?.ecDateTo)
                            }
                        }
                    }
                }

            }
        }
    }
}
