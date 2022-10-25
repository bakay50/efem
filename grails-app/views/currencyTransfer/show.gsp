<%@ page import="org.springframework.context.i18n.LocaleContextHolder;org.springframework.web.servlet.support.RequestContextUtils" %>
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

    <g:set var="bean" value="${bean ?: currencyTransferInstance}"/>
    <bootstrap:form small="${true}" name="appMainForm" class="form-horizontal" grid='12(3-3-6)'>
        <layout:pageResources dependsOn="application">
            <wf:tagLibSetup locale="${request.locale}" validation="all"/>
            <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
            <layout:resource name="/currencyTransfer/currencyNotification" />
            <layout:resource name="${message(code: 'dataTablesResources')}"/>
            <layout:resource name="${message(code: 'i18messages')}"/>
            <layout:resource name="exchange.css"/>
            <layout:resource name="query-notification.css"/>
            <layout:resource name="exchange.js"/>
            <layout:resource name="currencyTransfer/clearanceDomManage.js"/>
            <wf:jsI18nSupport/>
        </layout:pageResources>
        <g:render template="/utils/messages"/>
        <bootstrap:div id="currencyTransferId">
            <g:hiddenField name="locale" value="${RequestContextUtils.getLocale(request)}"/>
            <g:hiddenField name="id" value="${currencyTransferInstance?.id}"/>
            <g:hiddenField name="version" value="${currencyTransferInstance?.version}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
    %{--Operation buttons--}%
        <div class="aboveFormLabel">
            <h3>
                <g:if test="${currencyTransferInstance?.requestNo}">
                    ${message(code: 'currencyTransfer.docShow.label', args: [ currencyTransferInstance?.requestNo])}
                    <g:render template="templates/labelStatus" model="[currencyTransferInstance:currencyTransferInstance]"/>
                </g:if>
                <g:else >
                    ${message(code: 'currencyTransfer.docShowStored.label')}
                    <g:render template="templates/labelStatus" model="[currencyTransferInstance:currencyTransferInstance]"/>
                </g:else>
            </h3>
        </div>
        <bootstrap:div id="overview" class="jumbotron">
            <g:render template="templates/operations"
                      model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
        </bootstrap:div>

        <bootstrap:tab name="currency TransferTab" type="flat">
            <bootstrap:tabItem name="header" active="true" labelCode="currencyTransfer.header.label">
                <g:render template="tabs/header" model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [currencyTransferInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">

                <g:render template="tabs/attachment" model="[currencyTransferInstance:currencyTransferInstance, referenceService:referenceService, domainType:'currencyTransfer']"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="queryNotification" active="true"
                               labelCode="currencyTransfer.querynotification.tab.label">
                <g:render template="tabs/queryNotification" model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
        </bootstrap:tab>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render template="/utils/hiddenFieldsCurrencyTransfer" model="[currencyTransferInstance: currencyTransferInstance, referenceService:referenceService]"/>
    </bootstrap:form>
</content>
</body>
