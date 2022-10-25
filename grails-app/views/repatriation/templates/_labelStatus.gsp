<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType" %>
<label class="label editTitle ${com.webbfontaine.efem.constants.ExchangeRequestType?.STATUS_LABELS_REPATRIATION[repatriationInstance?.status]}">
    <g:set var="status" value="${message(code: 'status.' + repatriationInstance?.status, default: repatriationInstance?.status?.toUpperCase())}"/>
    ${status?.toString()?.toUpperCase()}
</label>