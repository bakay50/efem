<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.constants.ExchangeRequestType" %>
<g:set var="exchangeHasStatus" value="${exchangeInstance.status}"/>
<g:set var="declarationNumber" value="${exchangeInstance?.declarationNumber}"/>
<g:set var="displayDeclarationForViews" value="${BusinessLogicUtils.displayDeclaration(exchangeInstance)}"/>
<bootstrap:formSection labelCode="requestType.SAD">
    <bootstrap:genericInput field="clearanceOfficeCode" bean="${exchangeInstance}"
                            labelField="clearanceOfficeName" onfocusout="limitFieldsBehavior(this.id,'#clearanceOfficeName')"
                            labelCode="exchange.clearanceOfficeCode.label"
                            readonly="${exchangeHasStatus || hasExchangeError}"/>

    <bootstrap:formGroup name="sad.requestNo" groupSpan="12" labelSpan="3" labelCode="exchange.declarationNumber.label">
        <bootstrap:formInput span="3">
            <wf:textInputInUppercase name="declarationSerial" value="${exchangeInstance.declarationSerial}"
                          class="short-input" maxLength="1" readonly="${exchangeHasStatus || hasExchangeError}" regexp="${'[a-zA-Z]'}"/>
            <wf:textInput name="declarationNumber" value="${exchangeInstance.declarationNumber}"
                          class="span2" maxLength="50" regexp="${'^[\\d]'}"
                          readonly="${exchangeHasStatus || hasExchangeError}"/>
        </bootstrap:formInput>
    </bootstrap:formGroup>

    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="exchange.declarationDate.label">
        <bootstrap:formInput span="3">
            <wf:genericInput field="declarationDate" bean="${exchangeInstance}" type="date" maxDate="0"
                             regexp="${'^[\\d /]+$'}" onkeydown="return false;" readonly="${exchangeHasStatus || hasExchangeError}"
                             labelCode="exchange.declarationDate.label"/>
        </bootstrap:formInput>

        <bootstrap:formInput span="1">
            <g:if test="${displayDeclarationForViews}">
                <a href="javascript:void(0)" id="showSadBtn" onclick="showSad()"
                   title="${message(code: 'exchange.viewSAD.title.label', default: 'Voir la SAD')}">
                    <bootstrap:icon name="eye-open"/>
                </a>
            </g:if>
        </bootstrap:formInput>
    </bootstrap:formGroup>

    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="">
        <bootstrap:formInput span="1">
            <g:if test="${!exchangeHasStatus}">
                <g:if test="${!declarationNumber}">
                    <a href="javascript:void(0)" onclick="loadEsadConfirmButton()" id="upload_sad"
                       class="btn btn-default">
                        <bootstrap:icon id="loadSadBtn" name="download" class="label-icon uploadIcon"/>
                        <wf:message code="label.loadSad"/>
                    </a>
                </g:if>
                <g:else>
                    <a href="javascript:void(0)" onclick="restartSad()"
                       class="btn btn-default ">
                        <bootstrap:icon id="restartSadBtn" name="repeat" class="label-icon uploadIcon"/>
                        <wf:message code="label.restartSad"/>
                    </a>
                </g:else>
            </g:if>
        </bootstrap:formInput>
    </bootstrap:formGroup>
    <wf:hiddenField name="sadInstanceId" value="${exchangeInstance.sadInstanceId}"/>
</bootstrap:formSection>
<wf:confirmAjaxOperationDialogBox name="loadSad" messageCode="confirmation.message.download.sad"/>