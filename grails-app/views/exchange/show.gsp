<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.BusinessLogicUtils;" %>
<head>
    <meta name="layout" content="page">
    <title></title>
</head>
<body>
<content tag="application">
    <wf:confirmDialog model="[formName: 'appMainForm']"/>

    <h3 style="font-weight: normal" class="text-uppercase">
        ${message(code: titleCode, default: 'e-Forex')}
    </h3>
    <g:set var="bean" value="${bean ?: exchangeInstance}"/>
    <bootstrap:form small="${true}" name="appMainForm" class="form-horizontal" grid='12(3-3-6)'>
        <layout:pageResources dependsOn="application">
            <wf:tagLibSetup locale="${request.locale}" validation="true"/>
            <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
            <layout:resource name="${message(code: 'dataTablesResources')}"/>
            <layout:resource name="${message(code: 'i18messages')}"/>
            <layout:resource name="notification"/>
            <layout:resource name="tvf"/>
            <layout:resource name="sad"/>
            <layout:resource name="exchangeFormValidation"/>
            <layout:resource name="execution"/>
            <layout:resource name="calculation"/>
            <layout:resource name="exchange.css"/>
            <wf:jsI18nSupport/>
            <layout:resource name="exchange.js"/>
        </layout:pageResources>
        <div class="aboveFormLabel">
            <h3>
                <g:if test="${exchangeInstance?.requestNo}">
                    ${message(code: 'exchange.docShow.label', args: [ message(code: "exchange.${exchangeInstance?.requestType}.title", default: 'EA'),exchangeInstance?.requestNo])}
                    <label class="label editTitle ${ExchangeRequestType?.Status_Labels[exchangeInstance?.status]}">
                        <g:set var="statut" value="${message(code: 'status.' + exchangeInstance?.status, default: exchangeInstance?.status?.toUpperCase())}"/>
                        ${statut?.toString()?.toUpperCase()}
                    </label>
                </g:if>
                <g:else >
                    ${message(code: 'exchange.docShowStored.label', args: [ message(code: "exchange.${exchangeInstance?.requestType}.title", default: 'EA')])}
                </g:else>
            </h3>
        </div>
        <g:render template="/utils/messages"/>
    %{--<g:render template="/utils/setup"/>--}%
        <bootstrap:div id="exchangeId">
            <g:hiddenField name="id" value="${exchangeInstance?.id}"/>
            <g:hiddenField name="version" value="${exchangeInstance?.version}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
    %{--Operation buttons--}%
        <bootstrap:div id="overview" class="jumbotron">
            <g:render template="templates/operations"  model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
        </bootstrap:div>
    %{--Tabs--}%
        <bootstrap:tab name="exchangeTab" type="flat">
            <bootstrap:tabItem name="header" active="true" labelCode="exchange.header.tab.label">
                <g:render template="tabs/header" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="namesAndParties" active="true" labelCode="exchange.nameAndParties.tab.label">
                <g:render template="tabs/namesAndParties" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="operationAndExecution" active="true" labelCode="exchange.operationAndExecution.tab.label">
                <g:render template="tabs/operationAndExecution" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <g:if test="${BusinessLogicUtils.handleDisplayWriteOffTab(exchangeInstance)}">
            <bootstrap:tabItem name="writeOff" active="true" labelCode="exchange.writeOff.tab.label">
                <g:render template="tabs/writeOff" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            </g:if>
            <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [exchangeInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                <g:render template="tabs/attachments" model="[exchangeInstance:exchangeInstance, referenceService:referenceService, domainType:'exchange']"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="queryNotification" active="true" labelCode="exchange.querynotification.tab.label">
                <g:render template="tabs/queryNotification" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
        </bootstrap:tab>
        <g:render template="/utils/successdlg" model="[exchangeInstance:exchangeInstance,endOperation:endOperation]"/>
        <g:render template="/utils/hiddenFields" model="[exchangeInstance:exchangeInstance, referenceService:referenceService]"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render plugin="wf-utils" template="/templates/operationAjaxSuccessTemplate"/>
    </bootstrap:form>
</content>
<g:render template="/utils/successMsgPopUp" />
</body>
