<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType" %>
<label class="label editTitle ${com.webbfontaine.efem.constants.ExchangeRequestType?.STATUS_LABELS_TRANSFER_ORDER[transferInstance?.status]}">
    <g:set var="status" value="${message(code: 'status.' + transferInstance?.status, default: transferInstance?.status?.toUpperCase())}"/>
    ${status?.toString()?.toUpperCase()}
</label>