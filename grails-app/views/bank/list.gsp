<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
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
        <layout:resource name="exchangeFormValidation"/>
        <layout:resource name="/resources/searchResources"/>
        <layout:resource name="${message(code: 'i18messages')}"/>
        <layout:resource name="search-core" type="css"/>
    </layout:pageResources>
    <h3 id="title"><g:message code="bank.search.label" default="SEARCH BANK"/></h3>
    <g:render template="/utils/messages"/>
   <wf:searchForm name="searchForm" action="search" method="get" model="[searchCommand: searchCommand]" template="search/searchCriteria"/>
    <bootstrap:div id="searchResults">
        <g:render plugin="wf-search" template="/searchResult"
                  model="[searchCommand : searchCommand,
                          max           : max,
                          resultList    : resultList,
                          totalCount    : totalCount]"/>
    </bootstrap:div>
    <g:render template="/utils/searchRefBeans"/>
    <g:render template="/utils/notificationProperties"/>

    <g:render template="templates/sucessdlg" model="[endOperation:endOperation]"/>
    <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
    <g:render plugin="wf-utils" template="/templates/operationAjaxSuccessTemplate"/>
</content>
<g:render template="/utils/successMsgPopUp" />

</body>
</html>
