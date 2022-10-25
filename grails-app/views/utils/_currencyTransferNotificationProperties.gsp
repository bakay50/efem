<%@ page import="com.webbfontaine.efem.UserUtils" %>
<g:hiddenField name="displayNotification" value="${ UserUtils.isBankAgent()?: false}"/>
<g:hiddenField name="currencyNotificationUrl" value="${createLink(controller: 'notification', action: 'index')}"/>