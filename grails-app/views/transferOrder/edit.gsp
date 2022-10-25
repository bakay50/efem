<%@ page import="com.webbfontaine.efem.workflow.Operation; com.webbfontaine.efem.constants.ExchangeRequestType; org.springframework.context.i18n.LocaleContextHolder" %>
<head>
    <meta name="layout" content="page">
    <wf:jsI18nSupport/>
    <title></title>
</head>

<body>
<content tag="application">
    <wf:confirmDialog model="[formName: 'appTrMainForm']"/>
    <layout:pageResources dependsOn="application">
        <wf:tagLibSetup locale="${request.locale}" validation="all"/>
        <layout:resource name="utils-ci" plugin="wf-utils" dependsOn="application.js"/>
        <layout:resource name="${message(code: 'dataTablesResources')}"/>
        <layout:resource name="${message(code: 'i18messages')}"/>
        <layout:resource name="/resources/editTransferResources"/>
        <layout:resource name="exchange.css"/>
        <layout:resource name="/transfer/transfer.js"/>
        <wf:jsI18nSupport/>
    </layout:pageResources>
    <h3 style="font-weight: normal" class="text-uppercase">
        ${message(code: titleCode, default: 'e-TransFer Order')}
    </h3>

    <div class="aboveFormLabel">
        <h3>
            <g:if test="${transferInstance?.requestNo}">
                ${message(code: 'transferOrder.docEdit.label', args: [ transferInstance?.requestNo])}
                <label class="label editTitle ${ExchangeRequestType?.STATUS_LABELS_TRANSFER_ORDER[transferInstance?.status]}">
                    <g:set var="statut" value="${message(code: 'status.' + transferInstance?.status, default: transferInstance?.status?.toUpperCase())}"/>
                    ${statut?.toString()?.toUpperCase()}
                </label>
            </g:if>
            <g:else >
                <g:if test="${transferInstance?.status}">
                    ${message(code: 'transferOrder.docEditStored.label', args: [ transferInstance?.requestNo])}
                    <label class="label editTitle ${ExchangeRequestType?.STATUS_LABELS_TRANSFER_ORDER[transferInstance?.status]}">
                        <g:set var="statut" value="${message(code: 'status.' + transferInstance?.status, default: transferInstance?.status?.toUpperCase())}"/>
                        ${statut?.toString()?.toUpperCase()}
                    </label>
                </g:if>
                <g:else >
                    ${message(code: ((transferInstance.startedOperation == Operation.CREATE) ? 'transferOrder.docCreate.label' : 'transferOrder.docEditStored.label'))}
                </g:else>
            </g:else>
        </h3>
    </div>
    <g:render template="/utils/rimmBeansTransfer"/>
    <g:set var="bean" value="${bean ?: transferInstance}"/>
    <g:render template="/utils/messages" model="[domainInstance: bean]"/>
    <bootstrap:form small="${true}" name="appTrMainForm" class="form-horizontal"
                    action="${transferInstance?.id ? 'update' : 'save'}" grid='12(3-3-6)'>
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
        <bootstrap:div id="formContent">
            <bootstrap:tab name="transferTab" type="flat">
                <bootstrap:tabItem name="header" active="true" labelCode="exchange.header.tab.label">
                    <g:render template="tabs/header"
                              model="[transferInstance: transferInstance, referenceService: referenceService]"/>
                </bootstrap:tabItem>
                <bootstrap:tabItem name="attachements"  label="${g.message(code: 'default.tab.Attachment', args: [transferInstance?.attachedDocs?.size() ?: '0'], default: 'Attachments')}">
                    <g:render template="tabs/attachments"
                              model="[transferInstance: transferInstance, referenceService: referenceService, domainType: 'transfer']"/>
                </bootstrap:tabItem>
                <g:if test="${transferInstance?.status}">
                    <bootstrap:tabItem name="queryNotification" active="true"
                                       labelCode="exchange.querynotification.tab.label">
                        <g:render template="tabs/queryNotification"
                                  model="[transferInstance: transferInstance, referenceService: referenceService]"/>
                    </bootstrap:tabItem>
                </g:if>
            </bootstrap:tab>
        </bootstrap:div>
        <g:render template="/utils/hiddenFieldsTransfer" model="[transferInstance: transferInstance]"/>
        <bootstrap:submitButton name="submitCurrentOperation" style="display: none"/>
    </bootstrap:form>
    <g:render template="/attachedDoc/attDocUploadFile" model="[domainType: 'transfer']"/>
    <g:render template="/utils/warningdlg"/>
    <g:render template="/utils/chgLngConfirmDialog"/>
    <g:render plugin="wf-utils" template="/dialogs/messageDialog"/>
    <g:render plugin="wf-utils" template="/dialogs/confirmDialog"/>
    <g:render template="/attachedDoc/attDocUploadFile" model="[domainType: 'transfer']"/>
</content>
</body>
