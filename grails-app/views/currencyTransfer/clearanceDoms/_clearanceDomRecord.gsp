<%@ page import="com.webbfontaine.efem.UserUtils; com.webbfontaine.grails.plugins.taglibs.FormattingUtils; com.webbfontaine.efem.constants.Statuses;com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.AppConfig;" %>
<g:set var="i" value="${clearanceDom?.rank}"/>
<tr class="clearenceRow_${i} cleaRow">
    <td>
        <g:if test="${currencyTransferInstance.isDocumentEditable()}">
            <a title="${message(code: 'exec.edit.title', default: 'Edit')}"
               onclick="editClearenceOfDom(${i})" class="editItemIcon">
                <bootstrap:icon name="pencil"></bootstrap:icon>
            </a>
        </g:if>

        <g:if test="${UserUtils.isBankAgent()}">
            <a href="javascript:void(0)" title="${message(code: 'exec.show.title', 'View')}"
               class="js_viewEc" data-reference="${clearanceDom?.ecReference}">
                <bootstrap:icon name="eye-open"/>
            </a>
        </g:if>

    </td>
    <td class="-align-center" id="rank_${i}">${i}</td>
    <td id="ecReference_${clearanceDom?.rank}" title="${clearanceDom?.ecReference}">${clearanceDom?.ecReference}</td>
    <g:if test="${clearanceDom?.ecDate}">
        <td id="ecDate_${clearanceDom?.rank}"
            title="${clearanceDom?.ecDate?.format(AppConfig.dateFormat())}">${clearanceDom?.ecDate?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:else>
        <td id="ecDate_${clearanceDom?.rank}"></td>
    </g:else>
    <td id="ecExporterName_${clearanceDom?.rank}" title="${clearanceDom?.ecExporterName}">${clearanceDom?.ecExporterName}</td>
    <td id="domiciliationCodeBank_${clearanceDom?.rank}"
        title="${clearanceDom?.domiciliationCodeBank}">${clearanceDom?.domiciliationCodeBank}</td>
    <td id="domiciliationNo_${clearanceDom?.rank}"
        title="${clearanceDom?.domiciliationNo}">${clearanceDom?.domiciliationNo}</td>
    <g:if test="${clearanceDom?.domiciliationDate}">
        <td id="domiciliationDate_${clearanceDom?.rank}"
            title="${clearanceDom?.domiciliationDate?.format(AppConfig.dateFormat())}">${clearanceDom?.domiciliationDate?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:set var="domiciliatedAmounttInCurr"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.domiciliatedAmounttInCurr == null || clearanceDom?.domiciliatedAmounttInCurr == "") ? BigDecimal.ZERO : clearanceDom?.domiciliatedAmounttInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="domiciliatedAmounttInCurr_${clearanceDom?.rank}"
        title="${domiciliatedAmounttInCurr}">${domiciliatedAmounttInCurr}</td>

    <g:set var="invoiceFinalAmountInCurr"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.invoiceFinalAmountInCurr == null || clearanceDom?.invoiceFinalAmountInCurr == "") ? '' : clearanceDom?.invoiceFinalAmountInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="invoiceFinalAmountInCurr_${clearanceDom?.rank}"
        title="${invoiceFinalAmountInCurr}">${invoiceFinalAmountInCurr}</td>
    
    <g:set var="repatriatedAmountToBank"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.repatriatedAmountToBank == null || clearanceDom?.repatriatedAmountToBank == "") ? '' : clearanceDom?.repatriatedAmountToBank), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="repatriatedAmountToBank_${clearanceDom?.rank}"
        title="${repatriatedAmountToBank}">${repatriatedAmountToBank}</td>

    <g:set var="amountTransferredInCurr"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.amountTransferredInCurr == null || clearanceDom?.amountTransferredInCurr == "") ? BigDecimal.ZERO : clearanceDom?.amountTransferredInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="amountTransferredInCurr_${clearanceDom?.rank}"
        title="${amountTransferredInCurr}">${amountTransferredInCurr}</td>
