<%@ page import="com.webbfontaine.efem.constants.UtilConstants; com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles;" %>
<bootstrap:div id="headerContainer">
    <bootstrap:formSection>
        <bootstrap:genericInput id="requestNo" name="requestNo" field="requestNo" bean="${currencyTransferInstance}"  labelCode="currencyTransfer.requestNo.label"  />
        <bootstrap:genericInput bean="${currencyTransferInstance}" field="requestDate" id="requestDate" name="requestDate" labelCode="currencyTransfer.requestDate.label" value="${currencyTransferInstance?.requestDate?.format("dd/MM/yyyy")}"  />
    </bootstrap:formSection>
    <g:render template="templates/loadRepatriation" model="[currencyTransferInstance : currencyTransferInstance]" />
    <bootstrap:formSection labelCode="currencyTransfer.bank.section.label">
        <bootstrap:genericInput bean="${currencyTransferInstance}" field="bankCode" id="bankCode" name="bankCode" labelCode="currencyTransfer.bankCode.label" />
        <bootstrap:genericInput bean="${currencyTransferInstance}" field="bankName" id="bankName" name="bankName" labelCode="currencyTransfer.bankName.label"  type="area" rows="3" inputSpan="4" maxlength="4000"/>
    </bootstrap:formSection>
    <bootstrap:formSection>
    <bootstrap:formGroup  groupSpan="12" labelSpan="3" labelCode="currencyTransfer.CurrencyCode.label">
        <bootstrap:formInput span="3">
            <wf:genericInput bean="${currencyTransferInstance}" field="currencyCode" id="currencyCode" name="currencyCode" onchange="retrieveCurrencyTransferRate()" readonly="${!currencyTransferInstance?.isFieldEditable('currencyCode')}" value="${currencyTransferInstance?.currencyCode}"/>
            <wf:exchangeRateInput class="span2 wfinput ml-5-important" name="currencyRate" id="currencyRate" value="${currencyTransferInstance?.currencyRate == null ? '': currencyTransferInstance?.currencyRate}" readonly="readonly" />
            <label name="currencyName" id="currencyName">${currencyTransferInstance?.currencyName == null ? UtilConstants.EURO : currencyTransferInstance?.currencyName}</label>
        </bootstrap:formInput>
    </bootstrap:formGroup>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="currencyTransfer.amountRepatriated.label">
            <bootstrap:formInput span="3">
                <wf:genericInput bean="${currencyTransferInstance}" class="exchangeMonetary" field="amountRepatriated" name="amountRepatriated" id="amountRepatriated" value="${currencyTransferInstance?.amountRepatriated}" type="monetary" readonly="readonly" />
                <wf:genericInput bean="${currencyTransferInstance}" class="exchangeMonetary ml-5-important" field="amountRepatriatedNat" name="amountRepatriatedNat" id="amountRepatriatedNat" maxlength="20" value="${currencyTransferInstance?.amountRepatriatedNat}" type="monetary"  readonly="readonly" />
                <label id="xof" class="add-on"><g:message code="exchange.xof.label" default="XOF"/></label>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup groupSpan="12" labelSpan="3" labelCode="currencyTransfer.amountTransferred.label">
            <bootstrap:formInput span="3">
                <g:hiddenField name="isPrefinancingWithoutEC" value="${currencyTransferInstance?.isPrefinancingWithoutEC}"/>
                <wf:genericInput bean="${currencyTransferInstance}" class="exchangeMonetary" field="amountTransferred" name="amountTransferred" id="amountTransferred" value="${currencyTransferInstance?.amountTransferred}" type="monetary"  onblur="calculateAmountTransferredNat();calculateAmountTransferredRate()" readonly="${!(BusinessLogicUtils.isPrefinancingWithoutClearance(currencyTransferInstance?.startedOperation,currencyTransferInstance?.isPrefinancingWithoutEC))}" />
                <wf:genericInput bean="${currencyTransferInstance}" class="exchangeMonetary ml-5-important" field="amountTransferredNat" name="amountTransferredNat" id="amountTransferredNat" maxlength="20" value="${currencyTransferInstance?.amountTransferredNat}" type="monetary"  readonly="readonly" />
                <label id="xof" class="add-on"><g:message code="exchange.xof.label" default="XOF"/></label>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup  groupSpan="12" labelSpan="3" labelCode="currencyTransfer.transferRate.label">
            <bootstrap:formInput>
                <g:hiddenField name="decimalNumberFormat" value="${grailsApplication.config.numberFormatConfig.decimalNumberFormat}"/>
                <wf:genericInput bean="${currencyTransferInstance}" field="transferRate" id="transferRate" name="transferRate" type="monetary" />
                <label id="transfer_rate" class="add-on"><g:message code="currencyTransfer.transferRate.suffix.label" default="%"/></label>
            </bootstrap:formInput>
        </bootstrap:formGroup>

    </bootstrap:formSection>
    <bootstrap:formSection labelCode="">
        <bootstrap:genericInput type="date" bean="${currencyTransferInstance}" field="currencyTransferDate" id="currencyTransferDate" name="currencyTransferDate" labelCode="currencyTransfer.transferDate.label" />
        <bootstrap:formGroup labelCode="currencyTransfer.status.label">
            <bootstrap:formInput>
                <wf:textInput name="transferStatus" disabled="true" value="${message(code: "status.${currencyTransferInstance?.status}")}" />
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <div id="listOfClearanceDomsBody">
        <g:render template="tabs/listOfClearenceDoms" model="[currencyTransferInstance:currencyTransferInstance]"/>
    </div>
</bootstrap:div>
