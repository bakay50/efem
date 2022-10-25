package com.webbfontaine.rimm.bank

import com.webbfontaine.efem.SearchUtils
import com.webbfontaine.efem.command.BankSearchCommand
import com.webbfontaine.efem.rimm.Bank
import com.webbfontaine.grails.plugins.search.QueryBuilder
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.Criteria
import org.springframework.beans.factory.annotation.Value

@Slf4j("LOGGER")
@Transactional
class BankSearchService {

    @Value('${com.webbfontaine.efem.exchange.search.max}')
    def searchResultMax
    def searchService
    def bankWorkflowService

    def getSearchResults(BankSearchCommand searchCommand, params = null) {
        QueryBuilder queryBuilder = new QueryBuilder(searchCommand)
        def max = getMax(params)
        def totalCount
        def resultList

        try {
            totalCount = getTotalCount(queryBuilder) ?: 0
            resultList = totalCount ? getSearchResult(queryBuilder, max, params) : Collections.emptyList()

        } catch (IllegalArgumentException e) {
            LOGGER.error("Error encountered during Search", e)
            totalCount = 0
            resultList = Collections.emptyList()
        }

        def searchResultMessage = searchService.getSearchResultMessage(resultList?.size(), totalCount)

        if (totalCount) {
            resultList.each { Bank bank ->
                bankWorkflowService.initOperations(bank)
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

    private getSearchResult(QueryBuilder queryBuilder, max, params) {
        Criteria criteria = Bank.createCriteria()
        def critListParameter = SearchUtils.setCriteriaParameters(params, max)
        def resultList = criteria.list(critListParameter) {
            queryBuilder.initListCriteria(criteria, false)
        }
        return resultList
    }

    private getTotalCount(QueryBuilder queryBuilder) {
        Criteria criteria = Bank.createCriteria()
        criteria.get {
            queryBuilder.initCriteria(criteria)
            projections { count() }
        }
    }

    def getMax(params) {
        def max = null
        if (params.containsKey('max')) {
            if (params.max != null) {
                max = params.max as Long
            }
        } else {
            max = Long.parseLong(searchResultMax)
        }
        return max
    }
}
