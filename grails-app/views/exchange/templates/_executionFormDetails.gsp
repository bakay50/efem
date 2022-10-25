<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils; com.webbfontaine.efem.constants.TypeCreditAccount"%>
<bootstrap:form small="${true}" name="executionForm" id="executionForm" class="form-horizontal" grid='12(3-3-6)'>
    <bootstrap:div class="js_executionFormContainer" id="executionContainer">
        <bootstrap:div id="executionListError"></bootstrap:div>
        <g:hiddenField name="rank" value="${execution.rank}"/>
        <bootstrap:formSection labelCode="exec.execution.label">
            <bootstrap:genericInput field="executionReference" bean="${execution}" labelCode="repatriation.executionRef.label" maxlength="35"/>
            <bootstrap:genericInput field="executionDate" bean="${execution}" labelCode="repatriation.executionDate.label" type="date"/>
        </bootstrap:formSection>
        <bootstrap:formSection>
            <bootstrap:genericInput field="currencyExCode" bean="${execution}" readonly="true" labelField="currencyExName" labelCode="exchange.currencyPayCode.label"/>
            <bootstrap:genericInput field="currencyExRate" bean="${execution}" readonly="true" labelCode="exchange.currencyPayRate.label"/>

            <bootstrap:formGroup labelCode="exec.amountMentionedExCurrency.label">
                <bootstrap:formInput span="3">
                    <wf:genericInput field="amountMentionedExCurrency" bean="${execution}" value="${execution?.amountMentionedExCurrency}" type="monetary" onchange="calculNationalAmountExecution()" style="text-align: right"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="1">
                    <wf:textInput name="labelCurrencyOperation" value="${execution?.currencyExCode}" readonly="readonly"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="3">
                    <wf:genericInput field="amountNationalExCurrency" bean="${execution}" value="${execution?.amountNationalExCurrency}" type="monetary" style="text-align: right" readonly="readonly"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="1">
                    <wf:textInput name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>

            <bootstrap:formGroup labelCode="exchange.amountInLetter.label">
                <bootstrap:formInput span="6">
                    <bootstrap:label  id="digitLetter">
                        ${execution?.amountInExLetter ? execution?.amountInExLetter + ' Francs CFA' : ''}
                    </bootstrap:label>
                    <wf:hiddenField id="amountInExLetter" name="amountInExLetter"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>
        </bootstrap:formSection>

        <bootstrap:formSection>
            <bootstrap:formGroup labelCode="exec.amountSettledNationalExCurrency.label">
                <bootstrap:formInput span="3">
                    <wf:genericInput field="amountSettledMentionedCurrency" bean="${execution}" value="${execution?.amountSettledMentionedCurrency}" type="monetary" onchange="calculateAmountSettledNationalExCurrency()" style="text-align: right"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="1">
                    <wf:textInput name="labelCurrencyOperation" value="${execution?.currencyExCode}" readonly="readonly"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="3">
                    <wf:genericInput field="amountSettledNationalExCurrency" bean="${execution}" value="${execution?.amountSettledNationalExCurrency}" type="monetary" style="text-align: right" readonly="readonly"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="1">
                    <wf:textInput name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                </bootstrap:formInput>
                </bootstrap:formGroup>

                <bootstrap:formGroup labelCode="exchange.amountInLetter.label">
                    <bootstrap:formInput span="6">
                        <bootstrap:label  id="digitLetterSettled">
                            ${execution?.amountInSettledLetter ? execution?.amountInSettledLetter + ' Francs CFA' : ''}
                        </bootstrap:label>
                        <wf:hiddenField id="amountInSettledLetter" name="amountInSettledLetter"/>
                    </bootstrap:formInput>
            </bootstrap:formGroup>
        </bootstrap:formSection>

        <bootstrap:formSection>
            <bootstrap:genericInput field="countryProvenanceDestinationExCode" bean="${execution}" onfocusout="limitFieldsBehavior(this.id,'#countryProvenanceDestinationExName')"
                                    labelField="countryProvenanceDestinationExName"
                                    labelCode="exchange.countryProvenanceDestinationCode.label"/>
            <bootstrap:genericInput field="provenanceDestinationExBank" bean="${execution}" labelCode="exchange.provenanceDestinationBank.label"/>
            <bootstrap:genericInput field="bankAccountNumberCreditedDebited" bean="${execution}" labelCode="exec.bankAccountNumberCreditedDebited.label"/>
            <bootstrap:genericInput field="accountExBeneficiary" bean="${execution}" labelCode="exec.accountExBeneficiary.label"/>
        </bootstrap:formSection>

        <bootstrap:formSection>
            <bootstrap:genericInput field="creditCorrespondentAccount" bean="${execution}" type="select" noSelection="['': '']"
                                    optionKey="key" optionValue="value" from="${referenceService.getTypeCreditAccount()}"
                                    labelCode="exec.creditCorrespondentAccount.label"/>
            <bootstrap:genericInput field="executingBankCode" bean="${execution}" onfocusout="limitFieldsBehavior(this.id,'#executingBankName')"
                                    labelField="executingBankName"
                                    labelCode="exec.executingBankCode.label"/>
            <bootstrap:genericInput field="accountOwnerCredited" bean="${execution}" labelCode="exec.accountOwnerCredited.label"/>
        </bootstrap:formSection>

        <bootstrap:formSection>
            <bootstrap:genericInput field="executionDomNumber" bean="${execution}" maxlength="35"
                                    labelCode="exchange.domiciliationNumber.label"/>
            <bootstrap:genericInput field="executionDomDate" bean="${execution}" type="date"
                                    regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                    labelCode="exchange.domiciliationDate.label"/>
            <bootstrap:genericInput field="executionDomBankCode" bean="${execution}" onfocusout="limitFieldsBehavior(this.id,'#executionDomBankName')"
                                    labelCode="exchange.domiciliationBankCode.label" labelField="executionDomBankName"/>
        </bootstrap:formSection>

        <p style="margin-top: 10px; margin-bottom: 10px;">
        <bootstrap:formSection id="itemButtons">
            <g:if test="${addExecution}">
                <bootstrap:div id="addExecButtons" class="control-group-sub">
                    <bootstrap:div class="controls">
                        <a id="saveAddedExec" href="javascript:void(0)" onclick="saveExecution();" class="btn btn-success btn-small">
                            <g:message code="exec.button.add.label" default="Add"/>
                        </a>
                        <a id="cancelAddedExec" href="javascript:void(0)" onclick="cancelExecution('${params.action}');" class="btn btn-default btn-small">
                            <g:message code="exec.button.cancel.label" default="Cancel"/>
                        </a>
                    </bootstrap:div>
                </bootstrap:div>
            </g:if>
            <g:elseif test="${editExecution}">
                <bootstrap:div id="editExecButtons" class="control-group-sub">
                    <bootstrap:div class="controls">
                        <a id="updateExec" href="javascript:void(0)" onclick="saveExecution();" class="btn btn-warning btn-small">
                            <g:message code="exec.button.edit.label" default="Edit"/>
                        </a>
                        <a id="cancelEditingExec" href="javascript:void(0)" onclick="cancelExecution('${params.action}');"
                           class="btn btn-default btn-small">
                            <g:message code="exec.button.cancel.label" default="Cancel"/>
                        </a>
                    </bootstrap:div>
                </bootstrap:div>
            </g:elseif>
            <g:elseif test="${removeExecution}">
                <bootstrap:div id="editExecButtons" class="control-group-sub">
                    <bootstrap:div class="controls">
                        <a id="deleteExec" href="javascript:void(0)" onclick="deleteExecution();" class="btn btn-danger btn-small">
                            <g:message code="exec.button.delete.label" default="Delete"/>
                        </a>
                        <a id="cancelDeletingExec" href="javascript:void(0)" onclick="cancelExecution('${params.action}');"
                           class="btn btn-default btn-small">
                            <g:message code="exec.button.cancel.label" default="Cancel"/>
                        </a>
                    </bootstrap:div>
                </bootstrap:div>
            </g:elseif>
            <g:else>
                <bootstrap:div id="showExecButtons" class="control-group-sub">
                    <bootstrap:div class="controls">
                        <a id="closeExec" href="javascript:void(0)" onclick="cancelExecution('${params.action}');" class="btn btn-default btn-small">
                            <g:message code="exec.button.close.label" default="Close"/>
                        </a>
                    </bootstrap:div>
                </bootstrap:div>
            </g:else>
        </bootstrap:formSection>
    </bootstrap:div>
    %{--<g:render template="/utils/rimmBeans"/>--}%
</bootstrap:form>