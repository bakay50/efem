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
    <bootstrap:form small="${true}" name="showForm" class="form-horizontal" grid='12(3-3-6)'>
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
        </layout:pageResources>
        <div class="aboveFormLabel">
            <h3>${message(code: 'bank.show.label')}</h3>
        </div>
        <g:render template="/utils/messages"/>
        <bootstrap:div id="exchangeId">
            <g:hiddenField name="id" value="${bankInstance?.id}"/>
            <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
        </bootstrap:div>
        <g:render template="templates/form" model="[bankInstance:bankInstance, referenceService:referenceService]"/>
        <header id="overview" class="jumbotron subhead">
            <bootstrap:linkButton class="btn Close" controller="bank" action="list">
                <g:message code="exec.button.close.label" default="Close"/></bootstrap:linkButton>
        </header>
        <g:render template="templates/sucessdlg" model="[bankInstance:bankInstance,endOperation:endOperation]"/>
        <g:render template="/utils/hiddenFields" model="[bankInstance:bankInstance, referenceService:referenceService]"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render plugin="wf-utils" template="/templates/operationAjaxSuccessTemplate"/>
    </bootstrap:form>
</content>
<g:render template="/utils/successMsgPopUp" />
</body>
