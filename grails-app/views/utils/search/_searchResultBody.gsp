<%@ page import="static com.webbfontaine.grails.plugins.search.utils.SearchPluginConfig.*; com.webbfontaine.grails.plugins.taglibs.FormattingUtils; org.springframework.context.i18n.LocaleContextHolder; com.webbfontaine.grails.plugins.search.SearchUtils; com.webbfontaine.grails.plugins.search.annotations.ResultField; java.lang.reflect.Field; java.lang.annotation.Annotation; grails.util.Holders; java.text.DecimalFormat;" %>

<g:set var="results" value="${searchCommand.results}"/>
<g:set var="resultFields" value="${searchCommand.resultFields}"/>
<g:set var="resultFieldStyles" value="${SearchUtils.getFieldStyles(searchCommand)}"/>
<g:set var="resultFieldValueStyles" value="${SearchUtils.getFieldValueStyles(searchCommand)}"/>
<g:if test="${flash.searchResultMessage}">
    <bootstrap:alert dismiss="true" role="alert" type="info">
        ${flash.searchResultMessage}
    </bootstrap:alert>
</g:if>
<bootstrap:div class='column-selector btn-group pull-right'>
    <a aria-expanded="false" data-toggle='dropdown'>
        <bootstrap:icon name="cog" id="cog"/>
    </a>
    <ul class='dropdown-menu' role='menu' id="columnsToChoose">
        <bootstrap:div id="search_exchange_column_selector">
            <g:render template="/utils/search/columnSelector" model="[domain: domain]"/>
        </bootstrap:div>
    </ul>
</bootstrap:div>
<g:if test="${totalCount > max}">
    <wf:remotePaginate id="${id}" linkAttrs="${paginationLinkAttrs}" action="${action ?: 'search'}" total="${totalCount}"
                       update="${divResults ?: 'searchResults'}" max="${max}"
                       params="${searchCommand.getSearchParams(params)}"
                       onComplete="\$('#searchResults').trigger('remotePaginateComplete');" onSuccess="initSearchFunctions();makeTableSortable()"/>
</g:if>
<table class="table table-condensed table-bordered table-striped table-hover table-top searchRes sortable" id="searchRes"
    ${tableJsClass != null ? "data-class=$tableJsClass" : 'data-class=_searchResult'} >
    <thead>
    <tr>
        <g:if test="${!noActions}">
            <th class="actions-th"></th>
        </g:if>
        <g:render template="/utils/search/tableHeader"/>
    </tr>

    </thead>
    <tbody id="searchResBody">
    <g:each in="${resultList}" status="i" var="instance">
        <g:render template="/resultRow"
                  model="[
                          noActions            : noActions, modal: modal,
                          actionsTemplatePlugin: actionsTemplatePlugin,
                          actionsTemplate      : actionsTemplate,
                          instance             : instance,
                          resultCustomClass    : resultCustomClasses?.get(i),
                          resultFields         : resultFields,
                          results              : results, i: i,
                          resultFieldStyles    : resultFieldStyles,
                          resultFieldValueStyles : resultFieldValueStyles
                  ]"/>
    </g:each>
    </tbody>
</table>
<g:if test="${isExportableControllerExist(controllerName) && resultList}">
    <export:formats formats="${getExportableFormats(controllerName)}" controller="${controllerName}" action="${getExportableAction(controllerName)}" params="${params}"/>
</g:if>
