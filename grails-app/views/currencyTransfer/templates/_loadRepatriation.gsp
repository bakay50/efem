<%@ page import="com.webbfontaine.efem.BusinessLogicUtils" %>
<bootstrap:formSection labelCode="repatriation.select.rep.label">
    <g:set var="repatriation" value="${BusinessLogicUtils.isRepatriationExist(currencyTransferInstance?.repatriationNo, currencyTransferInstance?.repatriationDate)}"/>
    <g:hiddenField name="idRepatriation" value="${repatriation?.id}"/>
    <g:hiddenField name="showRepatriationUrl" value="${createLink(controller: 'repatriation', action: 'show', params: [id : repatriation?.id])}"/>
    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="currencyTransfer.repatriationNo.label">
        <bootstrap:formInput span="3">
            <wf:textInput name="repatriationNo" value="${currencyTransferInstance?.repatriationNo}" class="span2 wfsearchinput"/>
        </bootstrap:formInput>
    </bootstrap:formGroup>

    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="currencyTransfer.repatriationDate.label">
        <bootstrap:formInput span="3">
            <wf:genericInput field="repatriationDate" bean="${currencyTransferInstance}" type="date"
                             regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                             labelCode="currencyTransfer.repatriationDate.label"/>
        </bootstrap:formInput>
        <g:if test="${repatriation}" >
            <bootstrap:formInput span="1">
                <a title="${message(code: 'currencyTransfer.view.repatriation', default: 'View Repatriation')}"
                   onclick="showRepatiation(event)" class="view-repatriation">
                    <bootstrap:icon name="eye-open"></bootstrap:icon>
                </a>
            </bootstrap:formInput>
        </g:if>
    </bootstrap:formGroup>
    <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="">
        <g:if test="${!currencyTransferInstance?.status}">
            <g:if test="${!repatriation}" >
                <bootstrap:formInput span="3">
                    <a href="javascript:void(0)" onclick="loadRepatriation()"
                       class="btn btn-default">
                        <bootstrap:icon id="loadRepatriationBtn" name="download" class="label-icon uploadIcon"/>
                        <wf:message code="currencyTransfer.loadRepatriation.label"/>
                    </a>
                </bootstrap:formInput>
            </g:if>
            <g:else>
                <bootstrap:formInput span="3">
                    <a href="javascript:void(0)" onclick="restartRepatriation()"
                       class="btn btn-default">
                        <bootstrap:icon id="restartRepatriationBtn" name="repeat" class="label-icon"/>
                        <wf:message code="currencyTransfer.restart.repatriation"/>
                    </a>
                </bootstrap:formInput>
            </g:else>
        </g:if>
    </bootstrap:formGroup>

</bootstrap:formSection>
<wf:confirmAjaxOperationDialogBox name="loadRepatriation" messageCode="confirmation.message.download.repatriation"/>