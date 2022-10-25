<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType; org.springframework.context.i18n.LocaleContextHolder; com.webbfontaine.repatriation.constants.NatureOfFund" %>
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
    <g:set var="bean" value="${bean ?: repatriationInstance}"/>
    <bootstrap:form small="${true}" name="showForm" class="form-horizontal" grid='12(3-3-6)'>
        <layout:pageResources dependsOn="application">
            <wf:tagLibSetup locale="${request.locale}" validation="true"/>
            <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
            <layout:resource name="${message(code: 'dataTablesResources')}"/>
            <layout:resource name="${message(code: 'i18messages')}"/>
            <layout:resource name="tvf"/>
            <layout:resource name="sad"/>
            <layout:resource name="exchangeFormValidation"/>
            <layout:resource name="execution"/>
            <layout:resource name="repatriation.css"/>
            <layout:resource name="exchange.css"/>
            <layout:resource name="clearanceDomManager"/>
            <wf:jsI18nSupport/>
        </layout:pageResources>
        <div class="aboveFormLabel">
            <h3>
                <g:if test="${repatriationInstance?.requestNo}">
                    ${message(code: (repatriationInstance?.natureOfFund == NatureOfFund.NOF_PRE) ? 'prefinancing.docShow.label' : 'repatriation.docShow.label', args: [ repatriationInstance?.requestNo])}
                    <g:render template="templates/labelStatus" model="[repatriationInstance:repatriationInstance]"/>
                </g:if>
                <g:else >
                    ${message(code: 'repatriation.docShowStored.label')}
                    <g:render template="templates/labelStatus" model="[repatriationInstance:repatriationInstance]"/>
                </g:else>
            </h3>
        </div>
        <g:render template="/utils/messages"/>
    %{--<g:render template="/utils/setup"/>--}%
        <bootstrap:div id="exchangeId">
            <g:hiddenField name="id" value="${repatriationInstance?.id}"/>
            <g:hiddenField name="version" value="${repatriationInstance?.version}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
    %{--Operation buttons--}%
        <bootstrap:div id="overview" class="jumbotron">
            <g:render template="templates/operations"  model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
        </bootstrap:div>
    %{--Tabs--}%
        <bootstrap:tab name="exchangeTab" type="flat">
            <bootstrap:tabItem name="header" active="true" labelCode="exchange.header.tab.label">
                <g:render template="tabs/header" model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [repatriationInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                    <g:render template="tabs/attachments" model="[repatriationInstance:repatriationInstance, referenceService:referenceService, domainType:'repatriation']"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="queryNotification" active="true" labelCode="exchange.querynotification.tab.label">
                <g:render template="tabs/queryNotification" model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
            </bootstrap:tabItem>
        </bootstrap:tab>
        <g:render template="/utils/successRepDlg" model="[repatriationInstance:repatriationInstance,endOperation:endOperation]"/>
        <g:render template="/utils/hiddenFieldsRep" model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render plugin="wf-utils" template="/templates/operationAjaxSuccessTemplate"/>
    </bootstrap:form>
</content>
<g:render template="/utils/successMsgPopUp" />
</body>
