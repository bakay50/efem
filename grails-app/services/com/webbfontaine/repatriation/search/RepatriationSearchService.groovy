package com.webbfontaine.repatriation.search

import com.webbfontaine.efem.command.RepatriationSearchCommand
import static com.webbfontaine.efem.xml.DataBindingHelper.*
import com.webbfontaine.efem.repatriation.Repatriation
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.Criteria
import org.hibernate.transform.AliasToBeanResultTransformer
import org.springframework.beans.factory.annotation.Value
import com.webbfontaine.grails.plugins.search.QueryBuilder
import  com.webbfontaine.efem.SearchUtils;

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

@Slf4j("LOGGER")
class RepatriationSearchService {

    @Value('${com.webbfontaine.efem.exchange.search.max}')
    def searchResultMax
    def searchService
    def repatriationWorkflowService
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
            resultList.each { Repatriation repatriation ->
                repatriationWorkflowService.initOperations(repatriation)
                repatriationWorkflowService.requestCurrencyTransfer(repatriation)
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

    private getSearchResult(QueryBuilder queryBuilder, max, params, RepatriationSearchCommand searchCommand) {
        Criteria criteria = Repatriation.createCriteria()
        def critListParameter = SearchUtils.setCriteriaParameters(params, max)
        def resultList = criteria.list(critListParameter) {
            queryBuilder.initListCriteria(criteria, false)
            SearchUtils.applyRepatriationCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationBankCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationTraderCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationStoredOwnerCriteria(criteria)
            resultTransformer(new AliasToBeanResultTransformer(Repatriation.class))
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
            synchronized (RepatriationSearchService.class) {
                if (allResultFields == null) {
                    allResultFields = RepatriationSearchCommand.resultFields.collect { it.getName() } + REPATRIATION_ADDITIONAL_FIELDS_FOR_PRINT
                }
            }
        }
        return allResultFields
    }

    private getTotalCount(QueryBuilder queryBuilder, RepatriationSearchCommand searchCommand) {
        Criteria criteria = Repatriation.createCriteria()
        criteria.get {
            queryBuilder.initCriteria(criteria)
            SearchUtils.applyRepatriationCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationBankCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationTraderCriteria(criteria, searchCommand)
            SearchUtils.applyRepatriationStoredOwnerCriteria(criteria)
            projections {
                countDistinct("id")
            }
        }
    }


}
