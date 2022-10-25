package com.webbfontaine.efem.search

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.SearchUtils
import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.command.ExchangeSearchCommand
import static com.webbfontaine.efem.xml.DataBindingHelper.EXCHANGE_ADDITIONAL_FIELDS_FOR_PRINT
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.Criteria
import org.hibernate.transform.AliasToBeanResultTransformer
import org.springframework.beans.factory.annotation.Value
import com.webbfontaine.grails.plugins.search.QueryBuilder

@Slf4j("LOGGER")
class ExchangeSearchService {

    @Value('${com.webbfontaine.efem.exchange.search.max}')
    def searchResultMax

    def searchService
    def exchangeWorkflowService
    def springSecurityService
    private volatile static Collection<String> allResultFields;

    def getSearchResults(searchCommand, params = null) {
        boolean mustInitAfterSearch = false
        (searchCommand, mustInitAfterSearch) = SearchUtils.modifySearchCommand(searchCommand, mustInitAfterSearch)
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
            totalCount = getTotalCount(queryBuilder, searchCommand, mustInitAfterSearch) ?: 0
            resultList = totalCount ? getSearchResult(queryBuilder, max, params, searchCommand, mustInitAfterSearch) : Collections.emptyList()

        } catch (IllegalArgumentException e) {
            LOGGER.error("Error encountered during Search", e)
            totalCount = 0
            resultList = Collections.emptyList()

        }

       totalCount = SearchUtils.setSearchLimimt(totalCount)
        def searchResultMessage = searchService.getSearchResultMessage(resultList?.size(), totalCount)

        if (totalCount) {
            resultList.each { Exchange exchange ->
                exchangeWorkflowService.initOperations(exchange)
                exchangeWorkflowService.removeOperationByArea(exchange)
                exchangeWorkflowService.isDomiciliateWhenEc(exchange)
                exchangeWorkflowService.applyTreatmentLevel(exchange)
                exchangeWorkflowService.removeGovOfficerOperationsForEA(exchange)
                exchangeWorkflowService.removeOperationsForGovOfficerByArea(exchange)
                exchangeWorkflowService.requestTransferOrderForEA(exchange)
                exchangeWorkflowService.removeCancelOperation(exchange)
            }
        }
        searchCommand = SearchUtils.setDefaultBankDepartmentInCharge(searchCommand, mustInitAfterSearch)

        return [
                actionsTemplate    : 'search/searchResultActions',
                searchCommand      : searchCommand,
                max                : max,
                resultList         : resultList,
                totalCount         : totalCount,
                searchResultMessage: searchResultMessage
        ]
    }

    private getSearchResult(QueryBuilder queryBuilder, max, params, ExchangeSearchCommand searchCommand, mustInitAfterSearch) {
        Criteria criteria = Exchange.createCriteria()
        if (UserUtils.isGovOfficer()) {
            params?.sort = params?.sort ? params?.sort : "bankApprovalDate"
            params?.order = params?.order ? params?.order : "asc"
        }
        def critListParameter = SearchUtils.setCriteriaParameters(params, max)
        def resultList = criteria.list(critListParameter) {
            queryBuilder.initListCriteria(criteria, false)
            SearchUtils.applyExchangeExecutionCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeDeclarantCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeTraderCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeBankCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeAttachmentCriteria(criteria, searchCommand)
            SearchUtils.applyCriteriaForDepartmentInCharge(criteria, mustInitAfterSearch)
            SearchUtils.applyGovAndBankRestrictionsForStored(criteria)
            resultTransformer(new AliasToBeanResultTransformer(Exchange.class))
            projections {
                searchCommand?.docRef?"":distinct ( "id" )
                resultFields.each {
                    property(it, it)
                }
            }
        }
        return resultList
    }

    private getTotalCount(QueryBuilder queryBuilder, ExchangeSearchCommand searchCommand, mustInitAfterSearch) {
        Criteria criteria = Exchange.createCriteria()
        criteria.get {
            queryBuilder.initCriteria(criteria)
            SearchUtils.applyExchangeExecutionCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeDeclarantCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeTraderCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeBankCriteria(criteria, searchCommand)
            SearchUtils.applyExchangeAttachmentCriteria(criteria, searchCommand)
            SearchUtils.applyCriteriaForDepartmentInCharge(criteria, mustInitAfterSearch)
            SearchUtils.applyGovAndBankRestrictionsForStored(criteria)
            projections { count() }
        }
    }

    private static Collection<String> getResultFields() {
        if (!allResultFields) {
            synchronized (ExchangeSearchService.class) {
                if (allResultFields == null) {
                    allResultFields = ExchangeSearchCommand.resultFields.collect { it.getName() } + EXCHANGE_ADDITIONAL_FIELDS_FOR_PRINT
                }
            }
        }
        return allResultFields
    }


}
