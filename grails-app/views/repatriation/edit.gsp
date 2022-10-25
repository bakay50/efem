<%@ page import="com.webbfontaine.repatriation.constants.NatureOfFund; com.webbfontaine.efem.workflow.Operation; com.webbfontaine.efem.constants.ExchangeRequestType; org.springframework.context.i18n.LocaleContextHolder" %>
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
        <g:render template="/utils/rimmBeansRep"/>
        <g:set var="bean" value="${bean ?: repatriationInstance}"/>
        <bootstrap:form small="${true}" name="appMainForm" class="form-horizontal" action="${repatriationInstance?.id ? 'update' : 'save'}" grid='12(3-3-6)'>
            <layout:pageResources dependsOn="application">
                <wf:tagLibSetup locale="${request.locale}" validation="all"/>
                <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
                <layout:resource name="${message(code: 'dataTablesResources')}"/>
                <layout:resource name="${message(code: 'i18messages')}"/>
                <layout:resource name="tvf"/>
                <layout:resource name="/resources/editRepResources"/>
                <layout:resource name="exchange.css"/>
                <layout:resource name="repatriation.css"/>
                <layout:resource name="exchange.js"/>
                <layout:resource name="xml.js"/>
                <wf:jsI18nSupport/>
            </layout:pageResources>
            <div class="aboveFormLabel">
                <h3>
                    <g:if test="${repatriationInstance?.requestNo}">
                        ${message(code: (repatriationInstance?.natureOfFund == NatureOfFund.NOF_PRE) ? 'prefinancing.docEdit.label' : 'repatriation.docEdit.label', args: [ repatriationInstance?.requestNo])}
                        <g:render template="templates/labelStatus" model="[repatriationInstance:repatriationInstance]"/>
                    </g:if>
                    <g:else >
                        <g:if test="${repatriationInstance?.status}">
                            ${message(code: 'repatriation.docEditStored.label', args: [ repatriationInstance?.requestNo])}
                            <g:render template="templates/labelStatus" model="[repatriationInstance:repatriationInstance]"/>
                        </g:if>
                        <g:else >
                            ${message(code: ((repatriationInstance.startedOperation == Operation.CREATE) ? 'repatriation.docCreate.label' : 'repatriation.docEditStored.label'))}
                        </g:else>
                    </g:else>
                </h3>
            </div>
            <g:render template="/utils/messages" model="[domainInstance:repatriationInstance]"/>
            <bootstrap:div id="repartriationId">
                <g:hiddenField name="repatStatus" value="${repatriationInstance?.status}"/>
                <g:hiddenField name="id" value="${repatriationInstance?.id}"/>
                <g:hiddenField name="version" value="${repatriationInstance?.version}"/>
                <wf:hiddenField name="conversationId" value="${params?.conversationId}"/>
            </bootstrap:div>
            %{--Operation buttons--}%
            <bootstrap:div id="overview" class="jumbotron">
                <g:render template="templates/operations"  model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
            </bootstrap:div>
            %{--Tabs--}%
            <bootstrap:tab name="repartriationTab" type="flat">
                <bootstrap:tabItem name="header" active="true" labelCode="exchange.header.tab.label">
                    <g:render template="tabs/header" model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
                </bootstrap:tabItem>
                <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [repatriationInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                    <g:render template="tabs/attachments" model="[repatriationInstance:repatriationInstance, referenceService:referenceService, domainType:'repatriation']"/>
                </bootstrap:tabItem>
                <g:if test="${repatriationInstance?.status}">
                    <bootstrap:tabItem name="queryNotification" active="true" labelCode="exchange.querynotification.tab.label">
                        <g:render template="tabs/queryNotification" model="[repatriationInstance:repatriationInstance, referenceService:referenceService]"/>
                    </bootstrap:tabItem>
                </g:if>
            </bootstrap:tab>
            <g:render template="/utils/hiddenFieldsRep" model="[repatriationInstance:repatriationInstance]"/>
            <bootstrap:submitButton name="submitCurrentOperation" style="display: none"/>
        </bootstrap:form>
        <g:render template="/utils/importXmlDialog"
                  model="['controller': 'repatriation', action: 'importXML', domainName : 'Repatriation']"/>
        <g:render template="/attachedDoc/attDocUploadFile" model="[domainType:'repatriation']"/>
        <g:render template="/utils/warningdlg"/>
        <g:render template="/utils/chgLngConfirmDialog"/>
        <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
        <g:render plugin="wf-utils" template="/dialogs/confirmDialog"/>
</bootstrap:div>
    </content>
</body>
