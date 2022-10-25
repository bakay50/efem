<%@ page import="grails.util.Holders; com.webbfontaine.efem.TypeCastUtils" %>
<bootstrap:div id="headerContainer">
    <bootstrap:div class="well">
        <layout:resource name="messages.js"/>
        <bootstrap:formSection>
            <bootstrap:formGroup groupSpan="8" labelSpan="3" default="Code" labelCode="bank.code.label">
                <bootstrap:formInput span="5">
                    <wf:genericInput   onPaste="return false;" field="code" id="bankCode" name="code" bean="${bankInstance}" maxlength="35" value="${bankInstance?.code}" />
                </bootstrap:formInput>
            </bootstrap:formGroup>

            <bootstrap:formGroup groupSpan="8" labelSpan="3" labelCode="bank.dateOfValidity.label">
                <bootstrap:formInput span="5">
                    <wf:genericInput field="dateOfValidity" bean="${bankInstance}" type="dateTime" dateformat="${Holders.config.jodatime.format.org.joda.time.LocalDateTime}"
                                            regexp="${'^[\\d /]+$'}" onkeydown="return false;" />
                </bootstrap:formInput>
            </bootstrap:formGroup>
            <bootstrap:formGroup groupSpan="8" labelSpan="3" default="emailEA" labelCode="bank.emailEA.label">
                <bootstrap:formInput span="5">
                    <wf:genericInput field="emailEA" id="emailEA" name="emailEA" bean="${bankInstance}" maxlength="128" value="${bankInstance?.emailEA}" />
                </bootstrap:formInput>
            </bootstrap:formGroup>
            <bootstrap:formGroup groupSpan="8" labelSpan="3" default="Code" labelCode="bank.emailEC.label">
                <bootstrap:formInput span="5">
                    <wf:genericInput field="emailEC" id="emailEC" name="emailEC" bean="${bankInstance}" maxlength="128" value="${bankInstance?.emailEC}" />
                </bootstrap:formInput>
            </bootstrap:formGroup>
            <g:if test="${bankInstance?.status}">
                <bootstrap:formGroup groupSpan="8" labelSpan="3" name="bank.status.label" default="Status">
                    <bootstrap:formInput span="5">
                        <wf:genericInput field="status" id="status"
                                         name="status" bean="${bankInstance}"
                                         value="${message(code: "status.${bankInstance?.status}", default: "${bankInstance?.status}")}"/>
                    </bootstrap:formInput>
                </bootstrap:formGroup>
            </g:if>
        </bootstrap:formSection>
    </bootstrap:div>
    </bootstrap:div>