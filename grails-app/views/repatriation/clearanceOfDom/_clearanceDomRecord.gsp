<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.grails.plugins.taglibs.FormattingUtils; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.AppConfig;" %>
<g:set var="i" value="${clearanceDom?.rank}"/>
<g:set var="color" value="${clearanceDom?.status == true ? '':'danger'}"/>
<tr class="clearenceRow_${i} cleaRow ${color} ">
    <td>
        <g:hiddenField name="loadExchangeCommitmentUrl" value="${createLink(controller: 'exchange', action: 'loadExchange', params: [requestNo : clearanceDom?.ecReference, conversationId:params.conversationId, requestType : ExchangeRequestType.EC])}"/>
        <g:if test="${clearanceDom?.status && !clearanceDom?.ceded && repatriationInstance.isDocumentEditable() && !BusinessLogicUtils.isOldRepatriationClearance(repatriationInstance.natureOfFund, clearanceDom?.id > 0, repatriationInstance.startedOperation)}">
            <a title="${message(code: 'exec.edit.title', default: 'Edit')}"
                onclick="editClearenceOfDom(${i})" class="editItemIcon">
                <bootstrap:icon name="pencil"></bootstrap:icon>
            </a>
            <a title="${message(code: 'exec.delete.title', default: 'Delete')}"
                onclick="removeClearenceOfDom(${i})" class="deleteItemIcon">
                <bootstrap:icon name="trash"></bootstrap:icon>
            </a>
        </g:if>
        <g:if test="${BusinessLogicUtils.isExchangeExist(ExchangeRequestType.EC, clearanceDom?.ecReference)}">
            <a href="javascript:void(0)" title="${message(code: 'exec.show.title', 'View')}"
               class="js_viewEc" data-reference="${clearanceDom?.ecReference}">
            <bootstrap:icon name="eye-open"/>
            </a>
        </g:if>

    </td>
    <td style="text-align: center;" id="rank_${i}">${i}</td>
    <td id="ecReference_${clearanceDom?.rank}" title="${clearanceDom?.ecReference}">${clearanceDom?.ecReference}</td>
    <g:if test="${clearanceDom?.ecDate}">
        <td id="ecDate_${clearanceDom?.rank}" title="${clearanceDom?.ecDate?.format(AppConfig.dateFormat())}">${clearanceDom?.ecDate?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:else>
    <td id="ecDate_${clearanceDom?.rank}"></td>
    </g:else>
    <td id="domiciliaryBank_${clearanceDom?.rank}" title="${clearanceDom?.domiciliaryBank}">${clearanceDom?.domiciliaryBank}</td>
    <td id="domiciliationNo_${clearanceDom?.rank}" title="${clearanceDom?.domiciliationNo}">${clearanceDom?.domiciliationNo}</td>
    <g:if test="${clearanceDom?.domiciliationDate}">
        <td id="domiciliationDate_${clearanceDom?.rank}" title="${clearanceDom?.domiciliationDate?.format(AppConfig.dateFormat())}">${clearanceDom?.domiciliationDate?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:else>
    <td id="domiciliationDate_${clearanceDom?.rank}"></td>
    </g:else>
    <g:if test="${clearanceDom?.dateOfBoarding}">
        <td id="dateOfBoarding_${clearanceDom?.rank}" title="${clearanceDom?.dateOfBoarding?.format(AppConfig.dateFormat())}">${clearanceDom?.dateOfBoarding?.format(AppConfig.dateFormat())}</td>
    </g:if>
    <g:else>
    <td id="dateOfBoarding_${clearanceDom?.rank}"></td>
    </g:else>
    <g:set var="domAmtInCurr" value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.domAmtInCurr == null || clearanceDom?.domAmtInCurr == "") ? BigDecimal.ZERO : clearanceDom?.domAmtInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="domAmtInCurr_${clearanceDom?.rank}" title="${domAmtInCurr}">${domAmtInCurr}</td>

    <g:set var="invFinalAmtInCurr" value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.invFinalAmtInCurr == null || clearanceDom?.invFinalAmtInCurr == "") ? '' : clearanceDom?.invFinalAmtInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="invFinalAmtInCurr_${clearanceDom?.rank}" title="${invFinalAmtInCurr}">${invFinalAmtInCurr}</td>

    <g:set var="repatriatedAmtInCurr" value="${FormattingUtils.formatDecimalNumber(((clearanceDom?.repatriatedAmtInCurr == null || clearanceDom?.repatriatedAmtInCurr == "") ? BigDecimal.ZERO : clearanceDom?.repatriatedAmtInCurr), grailsApplication.config.numberFormatConfig.decimalNumberFormat, request)}"/>
    <td id="repatriatedAmtInCurr_${clearanceDom?.rank}" title="${repatriatedAmtInCurr}">${repatriatedAmtInCurr}</td>
