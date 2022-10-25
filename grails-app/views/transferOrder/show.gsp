<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType; org.springframework.context.i18n.LocaleContextHolder" %>
<head>
    <meta name="layout" content="page">
    <wf:jsI18nSupport/>
    <title></title>
</head>

<body>
<content tag="application">
    <wf:confirmDialog model="[formName: 'appTrMainForm']"/>

    <h3 style="font-weight: normal" class="text-uppercase">
        ${message(code: titleCode, default: 'e-Forex')}
    </h3>
    <layout:pageResources dependsOn="application">
        <wf:tagLibSetup locale="${request.locale}" validation="true"/>
        <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
        <layout:resource name="${message(code: 'dataTablesResources')}"/>
        <layout:resource name="${message(code: 'i18messages')}"/>
        <layout:resource name="exchange.css"/>
        <layout:resource name="/transfer/transfer.js"/>
        <wf:jsI18nSupport/>
    </layout:pageResources>
    <g:set var="bean" value="${bean ?: transferInstance}"/>
    <div class="aboveFormLabel">
        <h3>
            <g:if test="${transferInstance?.requestNo}">
                ${message(code: 'transferOrder.docShow.label', args: [ transferInstance?.requestNo])}
                <g:render template="templates/labelStatus" model="[transferInstance:transferInstance]"/>
            </g:if>
            <g:else >
                ${message(code: 'transferOrder.docShowStored.label')}
                <g:render template="templates/labelStatus" model="[transferInstance:transferInstance]"/>
            </g:else>
        </h3>
    </div>
    <bootstrap:form small="${true}" name="appTrMainForm" class="form-horizontal" grid='12(3-3-6)'>
        <g:render template="/utils/messages"/>
        <bootstrap:div id="transferId">
            <g:hiddenField name="id" value="${transferInstance?.id}"/>
            <g:hiddenField name="version" value="${transferInstance?.version}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
    %{--Operation buttons--}%
        <bootstrap:div id="overview" class="jumbotron">
            <g:render template="templates/operations"
                      model="[transferInstance: transferInstance, referenceService: referenceService]"/>
        </bootstrap:div>
    %{--Tabs--}%
        <bootstrap:tab name="transferTab" type="flat">
            <bootstrap:tabItem name="header" active="true" labelCode="exchange.header.tab.label">
                <g:render template="tabs/header"
                          model="[transferInstance: transferInstance, referenceService: referenceService]"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [transferInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                    <g:render template="tabs/attachments"
                          model="[transferInstance: transferInstance, referenceService: referenceService, domainType:'transfer']"/>
            </bootstrap:tabItem>
            <bootstrap:tabItem name="queryNotification" active="true" labelCode="exchange.querynotification.tab.label">
                <g:render template="tabs/queryNotification"
                          model="[transferInstance: transferInstance, referenceService: referenceService]"/>
            </bootstrap:tabItem>
        </bootstrap:tab>
        <g:render template="/utils/hiddenFieldsTransfer"
                  model="[transferInstance: transferInstance, referenceService: referenceService]"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
    </bootstrap:form>
</content>
</body>
