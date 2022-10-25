<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.repatriation.constants.NatureOfFund; com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles; com.webbfontaine.efem.constants.RepatriationRequestType;" %>
<g:set var="isSingleTin" value="${UserUtils.checkForSingleProp(UserProperties.TIN)}"/>
<bootstrap:div id="headerContainer">
    <g:if test="${repatriationInstance?.requestNo && repatriationInstance?.requestDate}">
        <bootstrap:formSection>

            <bootstrap:genericInput field="requestNo" bean="${repatriationInstance}"
                                    labelCode="repatriation.requestNo.label"/>
            <bootstrap:genericInput field="requestDate" bean="${repatriationInstance}" type="date"
                                    regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                    labelCode="repatriation.requestDate.label"/>
        </bootstrap:formSection>
    </g:if>
    <bootstrap:formSection>
        <bootstrap:genericInput type="select" field="natureOfFund" bean="${repatriationInstance}"
                                onchange="updateClearancePanel()"
                                labelCode="repatriation.natureOfFund.label"
                                from="${[
                                        [key: NatureOfFund.NOF_REP, value: message(code: "repatriation.select.rep.label", default: 'Repatriation')],
                                        [key: NatureOfFund.NOF_PRE, value: message(code: "repatriation.select.pre.label", default: 'Pre-financing')]
                                ]}"
                                optionKey="key"
                                optionValue="value"
                                omitEmptyOption="true"/>

    </bootstrap:formSection>

    <bootstrap:formSection labelCode="repatriation.company.title.label">
        <bootstrap:genericInput field="code" bean="${repatriationInstance}"
                                labelCode="repatriation.code.label"
                                value="${isSingleTin ? UserUtils.getUserProperty(UserProperties.TIN) : repatriationInstance.code}"/>

        <bootstrap:genericInput field="nameAndAddress" bean="${repatriationInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="repatriation.nameAndAddress.label"/>

    </bootstrap:formSection>
    <bootstrap:formSection labelCode="repatriation.repatriationBank.title.label">

        <bootstrap:genericInput field="repatriationBankCode" bean="${repatriationInstance}"
                                labelCode="repatriation.repatriationBankCode.label"
                                onchange="limitFieldsBehavior(this.id, '#repatriationBankName')"/>

        <bootstrap:genericInput field="repatriationBankName" bean="${repatriationInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="repatriation.repatriationBankName.label"/>

    </bootstrap:formSection>

    <bootstrap:formSection>
        <bootstrap:formGroup name="repatriation.currencyCode" groupSpan="12" labelSpan="3"
                             labelCode="repatriation.currencyCode.label">
            <bootstrap:formInput span="3">
                <wf:textInput
                        class="span2 ${BusinessLogicUtils.isRepatriationFieldMandatory(repatriationInstance, "currencyCode") ? 'mandatory' : ''}"
                        name="currencyCode" id="currencyCode" maxlength="3"
                        onchange="retrieveCurrencyRateRapatriation()"
                        placeholder="SUGGEST(MIN 2)"
                        required="${BusinessLogicUtils.isRepatriationFieldMandatory(repatriationInstance, "currencyCode")}"
                        readonly="${BusinessLogicUtils.isRepatriationFieldReadonly(repatriationInstance, "currencyCode")}"
                        value="${repatriationInstance?.currencyCode}"/>
                <wf:exchangeRateInput name="currencyRate" id="currencyRate" style="margin-left: 5px!important;"
                                      value="${repatriationInstance?.currencyRate == null ? '' : repatriationInstance?.currencyRate}"
                                      readonly="readonly"
                                      class="span2 wfinput"/>
                <label name="currencyName" id="currencyName">${repatriationInstance?.currencyName}</label>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:formGroup name="repatriation.receivedAmount" groupSpan="12" labelSpan="3"
                             labelCode="repatriation.receivedAmount.label">
            <bootstrap:formInput span="3">
                <wf:decimalInput class="span2" name="receivedAmount" maxlength="20" onChange="updateReceivedAmount()"
                                 regexp="${"[0-9,.\\s]"}"
                                 readonly="${BusinessLogicUtils.isReceivedAmountDisable(repatriationInstance)}"
                                 value="${repatriationInstance?.receivedAmount}"/>
                <wf:decimalInput class="span2" name="receivedAmountNat" id="receivedAmountNat" maxlength="20"
                                 readonly="readonly" style="margin-left: 5px!important;"
                                 value="${repatriationInstance?.receivedAmountNat}"/>
                <label id="xof" class="add-on"><g:message code="exchange.xof.label" default="XOF"/></label>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="receptionDate" bean="${repatriationInstance}" type="date" maxDate="0"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                labelCode="repatriation.receptionDate.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:formGroup name="repatriation.countryOfOriginCode" groupSpan="12" labelSpan="3"
                             labelCode="repatriation.countryOfOriginCode.label">
            <bootstrap:formInput span="3">
                <wf:textInput
                        class="span2 ${BusinessLogicUtils.isRepatriationFieldMandatory(repatriationInstance, "countryOfOriginCode") ? 'mandatory' : ''}"
                        name="countryOfOriginCode" id="countryOfOriginCode" maxlength="2"
                        readonly="${BusinessLogicUtils.isRepatriationFieldReadonly(repatriationInstance, "countryOfOriginCode")}"
                        required="${BusinessLogicUtils.isRepatriationFieldMandatory(repatriationInstance, "countryOfOriginCode")}"
                        placeholder="SUGGEST(MIN 2)"
                        value="${repatriationInstance?.countryOfOriginCode}"/>
                <label name="countryOfOriginName"
                       id="countryOfOriginName">${repatriationInstance?.countryOfOriginName}</label>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="bankOfOriginCode" bean="${repatriationInstance}"
                                labelCode="repatriation.bankOfOrigin.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="bankNotificationDate" bean="${repatriationInstance}" type="date"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                labelCode="repatriation.bankNotificationDate.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="executionRef" bean="${repatriationInstance}"
                                labelCode="repatriation.executionRef.label"/>
        <bootstrap:genericInput field="executionDate" bean="${repatriationInstance}" type="date" maxDate="0"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                labelCode="repatriation.executionDate.label"/>
        <bootstrap:formGroup labelCode="repatriation.status.label">
            <bootstrap:formInput>
                <wf:textInput name="repatriationStatus" disabled="true"
                              value="${message(code: "status.${repatriationInstance?.status}")}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <div id="listOfClearanceDomsBody">
        <g:render template="/repatriation/tabs/listOfClearanceDoms"
                  model="[repatriationInstance: repatriationInstance]"/>

    </div>
</bootstrap:div>
