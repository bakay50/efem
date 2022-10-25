<%@ page import="com.webbfontaine.efem.BusinessLogicUtils" %>


<g:set var="docHistory" value="${BusinessLogicUtils.getLicenseLog(exchangeInstance)}"/>

<bootstrap:formSection>
    <bootstrap:label labelCode="support.owner.label"/> ${referenceService.getDocumentOwner(exchangeInstance)}
</bootstrap:formSection>

<bootstrap:formSection>
    <bootstrap:label labelCode="support.status.label"/> <wf:message code="status.${exchangeInstance?.status}"/>
</bootstrap:formSection>

<g:if test="${docHistory}">
    <g:set var="tempDate" value=""/>
    <g:each in="${docHistory}" var="exchangeLog">
        <div class="control-group" >
            <label><strong>${exchangeLog.key}</strong></label>
        </div>
        <g:each in="${exchangeLog.value}" var="logMessage">
            <div class="control-group-sub">
                <div style="color: #777777">
                    [${logMessage?.logTime}] <strong>${logMessage?.userLogin} :</strong>
                    <g:if test="${logMessage?.logMessage}">
                        <strong><g:message code="exchange.log.message.label" default="Message"/>:</strong>
                        <em><span class="span7" style="overflow: hidden; text-overflow: ellipsis; max-width:540px;">${logMessage?.logMessage}</span></em>
                    </g:if>
                    <g:else>
                        <g:message code="exchange.log.committed.label" default="Committed"/>
                        <strong><g:message code="exchange.log.operation.${logMessage?.commitOperation}" default="${logMessage?.commitOperation}"/></strong>
                        <g:message code="exchange.log.operation.label" default="operation"/>
                        <g:message code="exchange.log.newStatusOfDoc.label" default=". New Status of document is "/>
                        <strong><g:message code="exchange.log.status.${logMessage?.newStatus}" default="${logMessage?.newStatus}"/></strong>
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
                         readonly="${BusinessLogicUtils.isCommentDisabled(exchangeInstance)}" style="resize: vertical;"/>
        </bootstrap:formInput>
    </bootstrap:formGroup>
</bootstrap:formSection>