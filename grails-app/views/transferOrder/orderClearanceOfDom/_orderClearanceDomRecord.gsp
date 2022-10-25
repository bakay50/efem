<%@ page import="com.webbfontaine.grails.plugins.taglibs.FormattingUtils;com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.constants.Statuses;com.webbfontaine.efem.AppConfig;" %>
<g:set var="i" value="${clearanceDom?.rank}"/>
<g:set var="decimalNumberFormat" value="${grailsApplication.config.numberFormatConfig.decimalNumberFormat}"/>

<tr class="clearenceRow_${i} cleaRow">
    <td>
        <a href="javascript:void(0)" title="${message(code: 'exec.show.title', 'View')}"
           class="js_viewEa" data-reference="${clearanceDom?.eaReference}">
            <bootstrap:icon name="eye-open"/>
        </a>
        <g:if test="${transferInstance.isEaOpearationsEditable()}">
            <a href="javascript:void(0)" title="${message(code: 'exec.edit.title', default: 'Edit')}"
               onclick="editOrderClearenceOfDom(${i})" class="editItemIcon">
                <bootstrap:icon name="pencil"/>
            </a>
            <a href="javascript:void(0)" title="${message(code: 'exec.delete.title', default: 'Delete')}"
               onclick="removeOrderClearenceOfDom(${i})" class="deleteItemIcon">
                <bootstrap:icon name="trash"/>
            </a>
        </g:if>

    </td>
    <g:set var="displayColor"
           value="background-color:${clearanceDom?.state == '1' ? '#FE8484;color:black' : 'none'}"></g:set>
    <td style="text-align: center; ${displayColor}"
        id="rank_${i}">${i}</td>
    <td style="${displayColor}" id="eaReference_${clearanceDom?.state == '0' ? clearanceDom?.rank : ''}"
        title="${clearanceDom?.eaReference}">${clearanceDom?.eaReference}</td>
    <td style="${displayColor}" id="authorizationDate_${clearanceDom?.rank}"
        title="${clearanceDom?.authorizationDate?.format(AppConfig.dateFormat())}">${clearanceDom?.authorizationDate?.format(AppConfig.dateFormat())}</td>
    <td style="${displayColor}" id="bankName_${clearanceDom?.rank}"
        title="${clearanceDom?.bankName}">${clearanceDom?.bankName}</td>
    <td style="${displayColor}" id="registrationNoBank_${clearanceDom?.rank}"
        title="${clearanceDom?.registrationNoBank}">${clearanceDom?.registrationNoBank}</td>

    <g:if test="${clearanceDom?.registrationDateBank}">
        <td style="${displayColor}" id="registrationDateBank_${clearanceDom?.rank}"
            title="${clearanceDom?.registrationDateBank?.format(AppConfig.dateFormat())}">${clearanceDom?.registrationDateBank?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:else>
        <td style="${displayColor}" id="registrationDateBank_${clearanceDom?.rank}"></td>
    </g:else>
    <g:set var="amountToBeSettledMentionedCurrency"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.amountToBeSettledMentionedCurrency == null || clearanceDom?.amountToBeSettledMentionedCurrency == "") ? BigDecimal.ZERO : clearanceDom?.amountToBeSettledMentionedCurrency), decimalNumberFormat, request)}"/>
    <td style="${displayColor}" id="amountToBeSettledMentionedCurrency_${clearanceDom?.rank}"
        title="${amountToBeSettledMentionedCurrency}">${amountToBeSettledMentionedCurrency}</td>
    <g:set var="amountRequestedMentionedCurrency"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.amountRequestedMentionedCurrency == null || clearanceDom?.amountRequestedMentionedCurrency == "") ? BigDecimal.ZERO : clearanceDom?.amountRequestedMentionedCurrency), decimalNumberFormat, request)}"/>
    <td style="${displayColor}" id="amountRequestedMentionedCurrency_${clearanceDom?.rank}"
        title="${amountRequestedMentionedCurrency}">${amountRequestedMentionedCurrency}</td>
    <g:set var="amountSettledMentionedCurrency"
           value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.amountSettledMentionedCurrency == null || clearanceDom?.amountSettledMentionedCurrency == "") ? BigDecimal.ZERO : clearanceDom?.amountSettledMentionedCurrency), decimalNumberFormat, request)}"/>
    <td style="${displayColor}" id="amountSettledMentionedCurrency_${clearanceDom?.rank}"
        title="${amountSettledMentionedCurrency}">${amountSettledMentionedCurrency}</td>
