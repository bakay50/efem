<%@ page import="com.webbfontaine.efem.BusinessLogicUtils" %>
<g:set var="docHistory" value="${BusinessLogicUtils.getLicenseLog(currencyTransferInstance)}"/>

<bootstrap:formSection>
    <bootstrap:label labelCode="support.owner.label"/> ${referenceService?.getDocumentOwner(currencyTransferInstance)}
</bootstrap:formSection>

<bootstrap:formSection>
    <bootstrap:label labelCode="support.status.label"/> <wf:message code="status.${currencyTransferInstance?.status}"/>
</bootstrap:formSection>
<g:if test="${docHistory}">
    <g:set var="tempDate" value=""/>
    <g:each in="${docHistory}" var="currencyTransferLog">
        <div class="control-group" >
            <label><strong>${currencyTransferLog.key}</strong></label>
        </div>
        <g:each in="${currencyTransferLog.value}" var="logMessage">
            <div class="control-group-sub">
                <div class="blockMessageStyle">
                    [${logMessage?.logTime}] <strong>${logMessage?.userLogin} :</strong>
                    <g:if test="${logMessage?.logMessage}">
                        <strong> <g:message code="currencyTransfer.log.message.label" default="Message"/>:</strong>
                        <em><span class="span7 logMessageStyle">${logMessage?.logMessage}</span></em>
                    </g:if>
                    <g:else>
                        <g:message code="currencyTransfer.log.committed.label" default="Committed"/>
                        <strong><g:message code="currencyTransfer.log.operation.${logMessage?.commitOperation}" default="${logMessage?.commitOperation}"/></strong>
                        <g:message code="currencyTransfer.log.operation.label" default="operation"/>
                        <g:message code="currencyTransfer.log.newStatusOfDoc.label" default=". New Status of document is "/>
                        <strong><g:message code="currencyTransfer.log.status.${logMessage?.newStatus}" default="${logMessage?.newStatus}"/></strong>
                    </g:else>
                </div>
            </div>
        </g:each>
    </g:each>
</g:if>

<bootstrap:formSection>
    <bootstrap:formGroup labelCode="support.query_message.label">
        <bootstrap:formInput span="6">
            <wf:textArea value="${params.comments}" name="comments" cols="50" rows="4" class="span6" maxlength="1024"
                         readonly="${BusinessLogicUtils.isCommentDisabled(currencyTransferInstance)}" style="resize: vertical;"/>
        </bootstrap:formInput>
    </bootstrap:formGroup>
</bootstrap:formSection>
