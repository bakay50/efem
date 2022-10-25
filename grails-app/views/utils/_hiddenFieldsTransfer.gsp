<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; org.springframework.web.servlet.support.RequestContextUtils" %>

%{--Hidden Fields--}%
<g:hiddenField name="locale" value="${RequestContextUtils.getLocale(request)}"/>
<g:hiddenField name="instanceId" disabled="" value="${transferInstance?.id}"/>
<wf:hiddenField name="listOfRequiredFieldsOfTransfer" id="listOfRequiredFieldsOfTransfer" value="${com.webbfontaine.efem.BusinessLogicUtils.getAllRequiredFieldOfTransFer(transferInstance?.startedOperation)}"/>
<g:hiddenField name="retrieveCurrencyRateUrl" value="${createLink(controller: 'exchange', action: 'retrieveCurrencyRateUrl', params: [conversationId:params.conversationId])}"/>
<wf:hiddenField name="attachmentAcceptedFormats" value="${grailsApplication.config.attachmentAcceptedFormats.join(',')}"/>

<g:hiddenField name="addClearanceDocUrl" value="${createLink(controller: 'OrderClearanceOfDom', action: 'addClearanceDom', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="editClearanceDocUrl" value="${createLink(controller: 'OrderClearanceOfDom', action: 'editClearanceDom', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="cancelEditClearanceDocUrl" value="${createLink(controller: 'OrderClearanceOfDom', action: 'cancelEditClearanceDom')}"/>
<g:hiddenField name="deleteClearanceDocUrl" value="${createLink(controller: 'OrderClearanceOfDom', action: 'deleteClearanceDom', params: [conversationId:params.conversationId])}"/>

<g:hiddenField name="retrieveEAUrl" value="${createLink(controller: 'transferOrder', action: 'retrieveExchangeByRef')}"/>

<g:hiddenField id="importXmlURL" name="importXmlURL" value="${createLink(controller: 'transferOrder', action: 'importXML')}" />
<g:hiddenField id="exportXmlURL" name="exportXmlURL" value="${createLink(controller: 'transferOrder', action: 'exportXML')}" />
<wf:hiddenField name="retrieveClearanceEaIdUrl" value="${createLink(controller: 'OrderClearanceOfDom', action: 'retrieveExchangeIdFromClearance')}"/>
<wf:hiddenField name="retrieveExchangeUrl" value="${createLink(controller: 'exchange', action: 'show')}"/>

<form id="upload-form" method="post" enctype="multipart/form-data" onSubmit="return uploadXMLFile(this);" hidden="hidden">
    <input id="upload-form-file" name="userFile" style="display: none" size="27" type="file" accept=".xml"/>
</form>
