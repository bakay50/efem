<%@ page import="com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.BusinessLogicUtils; org.springframework.web.servlet.support.RequestContextUtils" %>

%{--Hidden Fields--}%
<g:hiddenField name="locale" value="${RequestContextUtils.getLocale(request)}"/>

<g:hiddenField name="convertDigitUrl" value="${createLink(controller: 'exchange', action: 'convertDigitToLetter')}"/>
<g:hiddenField name="retrieveCurrencyRateUrl" value="${createLink(controller: 'exchange', action: 'retrieveCurrencyRateUrl', params: [conversationId:params.conversationId])}"/>

<g:hiddenField name="instanceId" disabled="" value="${repatriationInstance?.id}"/>
<g:hiddenField name="j_currencyRate" id="j_currencyRate" value=""/>
<g:hiddenField name="repatriationCurrencyCodeRefUrl" value="${createLink(controller: 'rimm', action: 'getLoadRef')}?refTab=REF_RATE"/>

<g:hiddenField name="addClearanceDocUrl" value="${createLink(controller: 'clearanceOfDom', action: 'addClearanceDom', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="editClearanceDocUrl" value="${createLink(controller: 'clearanceOfDom', action: 'editClearanceDom', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="cancelEditClearanceDocUrl" value="${createLink(controller: 'clearanceOfDom', action: 'cancelEditClearanceDom')}"/>
<g:hiddenField name="deleteClearanceDocUrl" value="${createLink(controller: 'clearanceOfDom', action: 'deleteClearanceDom', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="retrieveEcUrl" value="${createLink(controller: 'repatriation', action: 'retrieveExchangeReference')}"/>
<g:hiddenField name="removeEcUrl" value="${createLink(controller: 'clearanceOfDom', action: 'initializeClearanceData')}"/>
<wf:hiddenField name="verifyURL" value="${createLink(controller: 'repatriation', action: 'verify')}"/>
<wf:hiddenField name="exportXmlUrl" value="${createLink(controller: 'repatriation', action: 'exportXML')}"/>
<wf:hiddenField name="importXmlUrl" value="${createLink(controller: 'repatriation', action: 'importXML')}"/>
<wf:hiddenField name="isBankAgent" value="${UserUtils.isBankAgent()}"/>
<wf:hiddenField name="retrieveClearanceEcIdUrl" value="${createLink(controller: 'clearanceOfDom', action: 'retrieveExchangeIdFromClearance')}"/>
<wf:hiddenField name="retrieveExchangeUrl" value="${createLink(controller: 'exchange', action: 'show')}"/>