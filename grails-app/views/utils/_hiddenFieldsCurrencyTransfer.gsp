<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; org.springframework.web.servlet.support.RequestContextUtils" %>

%{--Hidden Fields--}%
<g:hiddenField name="locale" value="${RequestContextUtils.getLocale(request)}"/>
<g:hiddenField name="instanceId" disabled="" value="${currencyTransferInstance?.id}"/>

<g:hiddenField name="retrieveCurrencyRateUrl" value="${createLink(controller: 'exchange', action: 'retrieveCurrencyRateUrl', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="retrieveEcUrl" value="${createLink(controller: 'currencyTransfer', action: 'retrieveExchangeReference')}"/>

<g:hiddenField name="addClearanceDomDocUrl" value="${createLink(controller: 'clearanceDom', action: 'addClearanceDom', params: [conversationId: params.conversationId])}"/>
<g:hiddenField name="editClearanceDomDocDocUrl" value="${createLink(controller: 'clearanceDom', action: 'editClearanceDom', params: [conversationId: params.conversationId])}"/>
<g:hiddenField name="deleteClearanceDomDocUrl" value="${createLink(controller: 'clearanceDom', action: 'deleteClearanceDom', params: [conversationId: params.conversationId])}"/>
<g:hiddenField name="cancelEditClearanceDomDocUrl" value="${createLink(controller: 'clearanceDom', action: 'cancelEditClearanceDom')}"/>

<g:hiddenField id="importXmlURL" name="importXmlURL" value="${createLink(controller: 'currencyTransfer', action: 'importXML')}" />
<g:hiddenField id="exportXmlURL" name="exportXmlURL" value="${createLink(controller: 'currencyTransfer', action: 'exportXML')}" />
<g:hiddenField name="op" value="${params?.op}"/>

<wf:hiddenField name="retrieveClearanceEcIdUrl" value="${createLink(controller: 'clearanceDom', action: 'retrieveExchangebyReference')}"/>
<wf:hiddenField name="retrieveExchangeUrl" value="${createLink(controller: 'exchange', action: 'show')}"/>
<g:hiddenField name="loadRepatriationUrl" value="${createLink(controller: "currencyTransfer", action: "loadRepatriation", params: [conversationId: params.conversationId])}"/>
<g:hiddenField name="restartRepatriationUrl" value="${createLink(controller: "currencyTransfer", action: "create", params: [conversationId: params.conversationId])}"/>
<form id="upload-form" method="post" enctype="multipart/form-data" onSubmit="return uploadXMLFile(this);" hidden="hidden">
    <input id="upload-form-file" name="userFile" style="display: none" size="27" type="file" accept=".xml"/>
</form>
<g:render template="/utils/currencyTransferNotificationProperties" />