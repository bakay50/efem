<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType" %>
<label class="label editTitle ${com.webbfontaine.efem.constants.ExchangeRequestType?.STATUS_LABELS_CURRENCY_TRANSFER[currencyTransferInstance?.status]}">
    <g:set var="status" value="${message(code: 'status.' + currencyTransferInstance?.status, default: currencyTransferInstance?.status?.toUpperCase())}"/>
    ${status?.toString()?.toUpperCase()}
</label>