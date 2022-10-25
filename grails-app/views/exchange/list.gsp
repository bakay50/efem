<%@ page import="com.webbfontaine.efem.constants.UtilConstants; org.springframework.web.servlet.support.RequestContextUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="page">
    <g:set var="entityName" value="${message(code: 'exchange.label', default: 'exchange')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<wf:ajaxProgress/>
<content tag="services">
    <g:render template="/services"/>
</content>
<content tag="application">
    <layout:pageResources dependsOn="application">
        <wf:tagLibSetup locale="${request.locale}"/>
        <layout:resource name="searchUtils" plugin="wf-search" dependsOn="application.js"/>
        <layout:resource name="notification"/>
        <layout:resource name="autoCompleteManager"/>
        <layout:resource name="/resources/searchResources"/>
        <layout:resource name="${message(code: 'i18messages')}"/>
        <layout:resource name="search-core" type="css"/>
    </layout:pageResources>
    <layout:resource name="sorttable"/>
    <h3 id="title"><g:message code="exchange.search.title" default="SEARCH EA / EC"/></h3>
    <g:render template="/utils/messages"/>
    <wf:searchForm name="searchForm" action="search" method="get" model="[searchCommand: searchCommand]" template="search/searchCriteria"/>
    <bootstrap:div id="searchResults">
        <g:render template="/utils/search/searchResults"
                  model="[searchCommand : searchCommand,
                          domain        : UtilConstants.EXCHANGE,
                          max           : max,
                          resultList    : resultList,
                          totalCount    : totalCount]"/>
        %{--<g:if test="${resultList}">
            <g:render template="/export/exportList" model="[searchCommand: searchCommand]"/>
        </g:if>--}%
    </bootstrap:div>
    <g:if test="${resultList?.size() > 0}">
        <g:render template="/exchange/search/exportList" model="[searchCommand: searchCommand]" />
    </g:if>
    <g:render template="/utils/searchRefBeans"/>
    <g:render template="/utils/notificationProperties"/>
</content>

</body>
</html>
