<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; org.springframework.context.i18n.LocaleContextHolder; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.workflow.Operation" %>
<head>
    <meta name="layout" content="page">
    <title></title>
</head>
<body>
<content tag="application">
    <bootstrap:div id="formContents">

        <wf:confirmDialog model="[formName: 'appMainForm']"/>

        <h3 style="font-weight: normal" class="text-uppercase">
            ${message(code: titleCode, default: 'e-Forex')}
        </h3>
        <g:set var="bean" value="${bean ?: bankInstance}"/>
        <bootstrap:form small="${true}" name="appMainForm" class="form-horizontal" action="${bankInstance?.id ? 'update' : 'save'}" grid='12(3-3-6)'>
            <layout:pageResources dependsOn="application">
                <wf:tagLibSetup locale="${request.locale}" validation="all"/>
                <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
                <layout:resource name="${message(code: 'dataTablesResources')}"/>
                <layout:resource name="${message(code: 'i18messages')}"/>
                <layout:resource name="notification"/>
                <layout:resource name="tvf"/>
                <layout:resource name="/resources/editResources"/>
                <layout:resource name="exchange.css"/>
                <layout:resource name="exchange.js"/>
                <layout:resource name="xml.js"/>
                <wf:jsI18nSupport/>
                <asset:i18n name="messages" locale="${LocaleContextHolder.locale}" />
            </layout:pageResources>
            <div class="aboveFormLabel">
                <h3>${!bankInstance?.id ? message(code: 'bank.create.label') : message(code: 'bank.edit.label')}</h3>
            </div>
            <g:render template="/utils/messages" model="[domainInstance:bankInstance]"/>
            <bootstrap:div id="bankId">
                <g:hiddenField name="id" value="${bankInstance?.id}"/>
                <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
            </bootstrap:div>
            <g:render template="templates/form" model="[bankInstance:bankInstance, referenceService:referenceService]"/>
            <g:render template="templates/operations"  model="[bankInstance:bankInstance, referenceService:referenceService]"/>
            <g:render template="/utils/hiddenFields" model="[bankInstance:bankInstance, referenceService:referenceService]"/>
            <bootstrap:submitButton name="submitCurrentOperation" style="display: none"/>
        </bootstrap:form>
        <g:render template="/utils/warningdlg"/>
        <g:render template="/utils/chgLngConfirmDialog"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render plugin="wf-utils" template="/dialogs/confirmDialog"/>
    </bootstrap:div>
</content>
</body>
