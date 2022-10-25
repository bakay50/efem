<%@ page import="org.springframework.context.i18n.LocaleContextHolder; com.webbfontaine.efem.constants.ExchangeRequestType" %>
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

    <g:render template="/utils/rimmBeansCurrencyTransfer"/>
    <g:set var="bean" value="${bean ?: currencyTransferInstance}"/>
    <bootstrap:form small="${true}" name="appMainForm" class="form-horizontal"
                    action="${currencyTransferInstance?.id ? 'update' : 'save'}" grid='12(3-3-6)'>
        <layout:pageResources dependsOn="application">
            <wf:tagLibSetup locale="${request.locale}" validation="all"/>
            <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
            <layout:resource name="/currencyTransfer/currencyNotification" />
            <layout:resource name="${message(code: 'dataTablesResources')}"/>
            <layout:resource name="${message(code: 'i18messages')}"/>
            <layout:resource name="tvf"/>
            <layout:resource name="/resources/editCurrResource"/>
            <layout:resource name="exchange.css"/>
            <layout:resource name="currency.css"/>
            <layout:resource name="query-notification.css"/>
            <layout:resource name="exchange.js"/>
            <layout:resource name="/currencyTransfer/currencyTransfer.js"/>
            <wf:jsI18nSupport/>
        </layout:pageResources>
        <g:render template="/utils/messages" model="[domainInstance:currencyTransferInstance]"/>
        <bootstrap:div id="currencyTransferId">
            <g:hiddenField name="loadedRepatriation" value="${params?.id}"/>
            <g:hiddenField name="id" value="${currencyTransferInstance?.id}"/>
            <g:hiddenField name="version" value="${currencyTransferInstance?.version}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
        <div class="aboveFormLabel">
            <h3>
                ${message(code: "currencyTransfer."+currencyTransferInstance?.startedOperation?.toString()?.toLowerCase()+".label", args: [ currencyTransferInstance?.requestNo])}
                <label class="label editTitle ${ExchangeRequestType?.STATUS_LABELS_CURRENCY_TRANSFER[currencyTransferInstance?.status]}">
                    <g:set var="statut" value="${message(code: 'status.' + currencyTransferInstance?.status, default: currencyTransferInstance?.status?.toUpperCase())}"/>
                    ${statut?.toString()?.toUpperCase()}
                </label>
            </h3>
        </div>
    %{--Operation buttons--}%
        <bootstrap:div id="overview" class="jumbotron">
            <g:render template="templates/operations"
                      model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
        </bootstrap:div>

        <bootstrap:tab name="currencyTransferTab" type="flat">
            <bootstrap:tabItem name="header" active="true" labelCode="currencyTransfer.header.label">
                <g:render template="tabs/header" model="[currencyTransferInstance:currencyTransferInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [currencyTransferInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                <g:render template="tabs/attachment" model="[currencyTransferInstance:currencyTransferInstance, referenceService:referenceService, domainType:'currencyTransfer']"/>
            </bootstrap:tabItem>
            <g:if test="${currencyTransferInstance?.status}">
                <bootstrap:tabItem name="queryNotification" active="true"
                                   labelCode="currencyTransfer.querynotification.tab.label">
                    <g:render template="tabs/queryNotification" model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
                </bootstrap:tabItem>
            </g:if>
        </bootstrap:tab>
        <g:render template="/utils/hiddenFieldsCurrencyTransfer" model="[currencyTransferInstance:currencyTransferInstance]"/>
        <bootstrap:submitButton name="submitCurrentOperation" style="display: none"/>
    </bootstrap:form>
    <g:render template="/attachedDoc/attDocUploadFile" model="[domainType:'currencyTransfer']"/>
    <g:render template="/utils/warningdlg"/>
    <g:render template="/utils/warningMsgDlg"/>
    <g:render template="/utils/chgLngConfirmDialog"/>
    <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
    <g:render plugin="wf-utils" template="/dialogs/confirmDialog"/>
</content>
</body>
