<%@ page import="com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.Config.FieldsConfiguration; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.constants.UtilConstants" %>
<g:set var="exchangeCommitment" value="${exchangeInstance?.requestType == ExchangeRequestType.EC}"/>
<g:set var="isTraderOrDeclarant" value="${BusinessLogicUtils.traderOrDeclarantFieldsProhibited(exchangeInstance)}"/>
<g:set var="currencyPayCodeIsProhibited" value="${BusinessLogicUtils.isEAFieldEditable("currencyPayCode",exchangeInstance)}"/>
<g:set var="isProhibitedFinalInvoice" value="${BusinessLogicUtils.isProhibitedFinalInvoice(exchangeInstance)}"/>
<wf:hiddenField name="currencyXOF" id="currencyXOF" value="${UtilConstants.XOF}"/>
<wf:hiddenField name="maxAmountInXofCurrency" id="maxAmountInXofCurrency" value="${AppConfig.getMaxAmountInXofCurrency()}"/>

<bootstrap:div id="namesAndPartiesContainer">
    <bootstrap:formSection labelCode="exchange.operation.label">
        <bootstrap:genericInput field="operType" bean="${exchangeInstance}"
                                labelCode="exchange.operType.label"
                                labelField="operTypeName" onchange="manageOperationType()"/>
        <bootstrap:genericInput bean="${exchangeInstance}" field="currencyCode" onchange="retrieveCurrencyRate()"
                                labelCode="exchange.${ExchangeRequestType.EA.equals(exchangeInstance?.requestType) ? "currencyCode" : "currencyPayCode"}.label"
                                labelField="currencyName" />
        <bootstrap:genericInput bean="${exchangeInstance}" class="wf-exchange-rate-input amount" field="currencyRate" labelCode="exchange.${ExchangeRequestType.EA.equals(exchangeInstance?.requestType) ? "currencyRate" : "currencyPayRate"}.label" />
        <g:if test="${exchangeInstance.requestType == ExchangeRequestType.EA}">
            <bootstrap:genericInput bean="${exchangeInstance}" field="currencyPayCode" onchange="retrieveCurrencyRate(this.value,'currencyPayRate')"
                                    labelCode="exchange.currencyPayCode.label" labelField="currencyPayName" readonly="${!currencyPayCodeIsProhibited}" />
            <bootstrap:genericInput bean="${exchangeInstance}" class="wf-exchange-rate-input amount"  field="currencyPayRate" labelCode="exchange.currencyPayRate.label"  />
        </g:if>
    </bootstrap:formSection>

    <bootstrap:formSection labelCode="blank.label">
        <bootstrap:formGroup labelCode="${exchangeCommitment ? "exchange.amountNationalCurrencyEC.label" : "exchange.amountNationalCurrency.label"}">
            <bootstrap:formInput span="3">
                <wf:genericInput field="amountMentionedCurrency" bean="${exchangeInstance}" type="monetary" onchange="doCalculateNationalAmount()" class="exchangeMonetary"/>
                <wf:textInput class="short-input" name="labelCurrencyOperation" value="${ExchangeRequestType.EA.equals(exchangeInstance?.requestType) ? exchangeInstance?.currencyPayCode : exchangeInstance?.currencyCode}" readonly="readonly"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="3">
                <wf:genericInput field="amountNationalCurrency" bean="${exchangeInstance}" type="monetary" class="exchangeMonetary" />
                <wf:textInput class="short-input" name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup labelCode="exchange.amountInLetter.label">
            <bootstrap:formInput span="6">
                <bootstrap:label  id="digitoperation">
                    ${exchangeInstance?.amountInLetter ? exchangeInstance?.amountInLetter +  UtilConstants.CURRENCY_LABEL : ''}
                </bootstrap:label>
                <g:hiddenField name="amountInLetter" value="${exchangeInstance?.amountInLetter}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>

    <g:if test="${exchangeInstance?.requestType == ExchangeRequestType.EC}">
        <bootstrap:formSection labelCode="blank.label">
            <bootstrap:formGroup labelCode="exchange.isFinalAmount.label">
                <wf:genericInput type="checkbox" readonly="${isProhibitedFinalInvoice}" bean="${exchangeInstance}" field="isFinalAmount" onchange="isFinalInvoice(this,${exchangeInstance.isFinalAmount})" value="${exchangeInstance.isFinalAmount}"/>
            </bootstrap:formGroup>

            <bootstrap:formGroup groupSpan="12" labelCode="exchange.finalAmountInDevise.label">
                <bootstrap:formInput span="3">
                    <wf:genericInput field="finalAmountInDevise"
                                     bean="${exchangeInstance}"
                                     type="monetary"
                                     id="finalAmountInDevise"
                                     value="${exchangeInstance?.finalAmountInDevise}"
                                     maxlength="26"
                                     regexp="${"[0-9,.\\s]"}"
                                     readonly="readonly"
                                     />
                    <wf:textInput class="short-input" name="labelCurrencyOperation" value="${exchangeInstance?.currencyCode}" readonly="readonly"/>
                </bootstrap:formInput>
                <bootstrap:formInput span="3">
                    <wf:decimalInput id="finalAmount" name="finalAmount"
                                     value="${exchangeInstance?.finalAmount}"
                                     class="span2 validate-field"
                                     readOnly="readOnly"/>
                    <wf:textInput class="short-input" name="xof1" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>
            <bootstrap:formGroup labelCode="exchange.amountInLetter.label">
                <bootstrap:formInput span="6">
                    <bootstrap:label  id="digitoperations">
                        ${exchangeInstance?.amountFinalInLetter ? exchangeInstance?.amountFinalInLetter + UtilConstants.CURRENCY_LABEL : ''}
                    </bootstrap:label>
                    <g:hiddenField name="amountFinalInLetter" value="${exchangeInstance?.amountFinalInLetter}"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>
        </bootstrap:formSection>
    </g:if>

    <bootstrap:formSection labelCode="blank.label">
        <g:if test="${exchangeCommitment}">
            <bootstrap:formGroup labelCode="exchange.goodsValuesInXOF.label">
                <bootstrap:formInput span="3">
                    <wf:genericInput field="goodsValuesInXOF" bean="${exchangeInstance}" type="monetary" class="exchangeMonetary" readonly="${!isTraderOrDeclarant}"/>
                    <wf:textInput class="short-input" name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>

            <fieldset class="fieldset-border">
                <legend class="legend-border">
                    <g:message code="exchange.exFeesPaidByExporter.label" default="Extra Fees Paid by Exporter"/>
                </legend>
                <div class="control-group">
                    <bootstrap:formGroup labelCode="exchange.exFeesPaidByExpInCIinXOF.label">
                        <bootstrap:formInput span="3">
                            <wf:genericInput field="exFeesPaidByExpInCIinXOF" bean="${exchangeInstance}" type="monetary" class="exchangeMonetary" readonly="${!isTraderOrDeclarant}"/>
                            <wf:textInput class="short-input" name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                        </bootstrap:formInput>
                    </bootstrap:formGroup>

                    <bootstrap:formGroup labelCode="exchange.exFeesPaidByExpInAbroadinXOF.label">
                        <bootstrap:formInput span="3">
                            <wf:genericInput field="exFeesPaidByExpInAbroadinXOF" bean="${exchangeInstance}" type="monetary" style="text-align: right" readonly="${!isTraderOrDeclarant}"/>
                            <wf:textInput class="short-input" name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                        </bootstrap:formInput>
                    </bootstrap:formGroup>
                </div>
            </fieldset>
        </g:if>

        <bootstrap:formGroup style="margin-top:40px ;" labelCode="exchange.balanceAs.label">
            <bootstrap:formInput span="3" >
                <wf:genericInput field="balanceAs" type="monetary" bean="${exchangeInstance}" style="text-align: right" value="${exchangeInstance?.balanceAs}" readonly="readonly"/>
                <wf:textInput class="short-input" name="labelBalanceAs" value="${exchangeInstance?.currencyCode}" readonly="readonly"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>

    <bootstrap:formSection labelCode="blank.label">
        <bootstrap:genericInput field="countryProvenanceDestinationCode" bean="${exchangeInstance}"
                                onchange="addRuleForCountryProvenanceDestinationCode(this)"
                                labelCode="${exchangeCommitment ? "exchange.countryProvenanceDestinationCodeEC.label" : "exchange.countryProvenanceDestinationCode.label"}"
                                labelField="countryProvenanceDestinationName"/>
        <bootstrap:genericInput field="provenanceDestinationBank" bean="${exchangeInstance}" maxlength="100"
                                labelCode="${exchangeCommitment ? "exchange.provenanceDestinationBankEC.label" : "exchange.provenanceDestinationBank.label"}"/>
    </bootstrap:formSection>

    <bootstrap:formSection labelCode="blank.label">
        <bootstrap:genericInput field="bankAccountNocreditedDebited" bean="${exchangeInstance}" maxlength="34"
                                labelCode="exchange.bankAccountNocreditedDebited.${exchangeInstance?.requestType}.label"/>
        <bootstrap:genericInput field="exportationTitleNo" bean="${exchangeInstance}"
                                labelCode="exchange.exportationTitleNo.label"/>
        <bootstrap:genericInput field="accountNumberBeneficiary" bean="${exchangeInstance}" maxlength="34"
                                labelCode="exchange.accountNumberBeneficiary.${exchangeInstance?.requestType}.label"/>
    </bootstrap:formSection>
    <g:if test="${exchangeInstance?.status in [Statuses.ST_APPROVED,Statuses.ST_EXECUTED ] && !exchangeCommitment}">
        <g:render template="templates/executionSection" model="[exchangeInstance: exchangeInstance, executionList: exchangeInstance?.executions]"/>
    </g:if>
</bootstrap:div>
