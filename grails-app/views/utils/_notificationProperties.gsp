<%@ page import="com.webbfontaine.efem.UserUtils;" %>

<g:hiddenField name="displayNotification" value="${ UserUtils.isBankAgent()?: UserUtils.isTrader()?: false}"/>
<g:hiddenField name="notificationUrl" value="${createLink(controller: 'exchange', action: 'retrieveNotification')}"/>