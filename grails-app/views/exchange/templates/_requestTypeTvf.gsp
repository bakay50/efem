<%@ page import="com.webbfontaine.efem.BusinessLogicUtils" %>
<g:set var="tvfInstanceId" value="${exchangeInstance?.tvfInstanceId}"/>
<g:set var="exchangeHasTvf" value="${!(BusinessLogicUtils.isNull(exchangeInstance?.tvfNumber))}"/>
<g:set var="exchangeHasStatus" value="${!(BusinessLogicUtils.isNull(exchangeInstance?.status))}"/>
<g:set var="isTvfUsable" value="${BusinessLogicUtils.isTvfUsable(exchangeInstance)}"/>
<g:set var="tvfFieldsDisabled" value="${exchangeHasTvf || tvfInstanceId || hasExchangeError && isTvfUsable}"/>
<g:set var="tvfIconEnabled" value="${!hasExchangeError && (exchangeHasStatus || isTvfUsable)}"/>
<bootstrap:formSection labelCode="requestType.TVF">
    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="exchange.tvfNumber.label">
        <bootstrap:formInput span="3">
            <wf:genericInput field="tvfNumber" bean="${exchangeInstance}" onchange="configureShowTvfBtn()"
                             labelCode="exchange.tvfNumber.label" regexp="${'[0-9]'}"
                             readonly="${tvfFieldsDisabled}"/>
        </bootstrap:formInput>
        <g:hiddenField name="tvfNoFromExchange" value="${exchangeInstance?.tvfNumber}"/>
        <g:hiddenField name="tvfDateFromExchange" value="${exchangeInstance?.tvfDate}"/>
        <g:hiddenField name="tvfInstanceId" value="${tvfInstanceId}"/>
        <bootstrap:formInput span="1">
            <g:if test="${exchangeInstance?.id != null && tvfIconEnabled}">
                <a href="javascript:void(0)" id="showTvfBtn"
                   title="${message(code: 'exchange.viewTVF.title.label', default: 'Voir le DVT')}">
                    <bootstrap:icon name="eye-open"/>
                </a>
            </g:if>
        </bootstrap:formInput>
    </bootstrap:formGroup>
    <bootstrap:genericInput field="tvfDate" bean="${exchangeInstance}" type="date" maxDate="0"
                            regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                            labelCode="exchange.tvfDate.label" readonly="${tvfFieldsDisabled}"/>

    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="">
        <bootstrap:formInput span="1">
            <g:if test="${!exchangeHasStatus}">
                <g:if test="${!exchangeHasTvf}">
                    <a href="javascript:void(0)" onclick="loadTvfConfirmButton()"
                       class="btn btn-default upload_sad ">
                        <bootstrap:icon id="loadTvfBtn" name="download" class="label-icon uploadIcon"/>
                        <wf:message code="label.loadTvf"/>
                    </a>
                </g:if>
                <g:else>
                    <a href="javascript:void(0)" onclick="restartTvf()"
                       class="btn btn-default upload_sad ">
                        <bootstrap:icon id="restartTvfBtn" name="repeat" class="label-icon uploadIcon"/>
                        <wf:message code="label.restartTvf"/>
                    </a>
                </g:else>

            </g:if>
        </bootstrap:formInput>
    </bootstrap:formGroup>

</bootstrap:formSection>
<wf:confirmAjaxOperationDialogBox name="loadTvf" messageCode="confirmation.message.download.tvf"/>