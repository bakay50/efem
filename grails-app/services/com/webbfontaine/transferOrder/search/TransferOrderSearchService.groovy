package com.webbfontaine.transferOrder.search

import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.efem.SearchUtils
import com.webbfontaine.efem.transferOrder.TransferOrder
import com.webbfontaine.grails.plugins.search.QueryBuilder
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.Criteria
import org.hibernate.transform.AliasToBeanResultTransformer
import org.springframework.beans.factory.annotation.Value

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class TransferOrderSearchService {
    @Value('${com.webbfontaine.efem.exchange.search.max}')
    def searchResultMax
    def searchService
    def transferOrderWorkflowService
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
            resultList.each { TransferOrder transfer ->
                transferOrderWorkflowService.initOperations(transfer)
                transferOrderWorkflowService.removeUpdateOperationForGovOfficer(transfer)
            }
        }
        return [
                actionsTemplate    : 'search/searchResultActions',
                searchCommand      : searchCommand,
                max                : max,
                resultList         : resultList,
                totalCount         : totalCount,
                searchResultMessage: searchResultMessage
        ]
    }

    private getSearchResult(QueryBuilder queryBuilder, max, params, TransferOrderSearchCommand searchCommand) {
        Criteria criteria = TransferOrder.createCriteria()
        def critListParameter = SearchUtils.setCriteriaParameters(params, max)
        def resultList = criteria.list(critListParameter) {
            queryBuilder.initListCriteria(criteria, false)
            SearchUtils.applyTransferRoleSpecificRestrictions(criteria)
            SearchUtils.applyTransferTraderCriteria(criteria, searchCommand)
            SearchUtils.applyTransferBankCriteria(criteria, searchCommand)
            SearchUtils.applyTransferOrderCriteria(criteria, searchCommand)
            SearchUtils.applyTransferOrderStoredRestrictionForBankAndTrader(criteria)
            resultTransformer(new AliasToBeanResultTransformer(TransferOrder.class))
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
            synchronized (TransferOrderSearchService.class) {
                if (allResultFields == null) {
                    allResultFields = TransferOrderSearchCommand.resultFields.collect { it.getName() } + ['id']
                }
            }
        }
        return allResultFields
    }

    private getTotalCount(QueryBuilder queryBuilder, TransferOrderSearchCommand searchCommand) {
        Criteria criteria = TransferOrder.createCriteria()
        criteria.get {
            queryBuilder.initCriteria(criteria)
            SearchUtils.applyTransferRoleSpecificRestrictions(criteria)
            SearchUtils.applyTransferTraderCriteria(criteria, searchCommand)
            SearchUtils.applyTransferBankCriteria(criteria, searchCommand)
            SearchUtils.applyTransferOrderCriteria(criteria, searchCommand)
            SearchUtils.applyTransferOrderStoredRestrictionForBankAndTrader(criteria)
            projections { count() }
        }
    }


}
