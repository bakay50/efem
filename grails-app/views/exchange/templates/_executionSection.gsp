<%@ page import="com.webbfontaine.efem.constants.UtilConstants; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles" %>

<bootstrap:div id="executionListContainer">
    <bootstrap:formSection labelCode="exec.execution.label">
        <bootstrap:div id="executionListError"></bootstrap:div>

        <bootstrap:formGroup labelCode="exchange.totalAmountSettled.label" groupSpan="12" labelSpan="3" >
            <bootstrap:formInput span="3">
                <wf:genericInput field="totalAmountSettledInCurrency"  value="${exchangeInstance?.totalAmountSettledInCurrency}"
                                 type="monetary" readonly="readonly"/>
                <wf:textInput class="short-input" name="executionCurrencyCode" value="${exchangeInstance?.currencyCode}" readonly="readonly"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="3">
                <wf:genericInput field="totalAmountSettled"  value="${exchangeInstance?.totalAmountSettled}" type="monetary"
                                 readonly="readonly"/>
                <wf:textInput class="short-input" name="xof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup labelCode="exchange.totalAmountSettledInLetter.label" groupSpan="12" labelSpan="3" >
            <bootstrap:formInput span="7">
                <bootstrap:label span="">${exchangeInstance?.totalAmountSettledInLetter ? BusinessLogicUtils.capitalizeResult(exchangeInstance?.totalAmountSettledInLetter) + ' ' +  UtilConstants.CURRENCY_LABEL : ''}</bootstrap:label>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <strong>
            <bootstrap:label labelCode="exec.execdetail.label" default="Detail Execution" style="margin: 5px 0 5px 10px"/>
        </strong>
        <table class="table table-bordered table-striped tableNoWrap disable-container" id="itemsTable">
            <thead>
            <tr>
                <th class="actions-th">
                </th>
                <th class="span1 listRank"><g:message code="exec.rank.label" default="Rank"/></th>
                <th><g:message code="exec.executionReference.label" default="Execution Reference"/></th>
                <th><g:message code="exec.executionDate.label" default="Execution Date"/></th>
                <th><g:message code="exec.currencyCode.label" default="Currency Code"/></th>
                <th><g:message code="exec.amountNationalExCurrency.label" default="Amount of Transfer in National Currency"/></th>
                <th><g:message code="exec.amountSettledNationalExCurrency.label" default="Amount Settled in mentioned Currency"/></th>
                <th><g:message code="exec.countryBeneficiaryBank.label" default="Country beneficiary bank"/></th>
                <th><g:message code="exec.provenanceDestinationBank.label" default="Destination Bank"/></th>
                <th><g:message code="exec.bankAccountNumberCreditedDebited.label" default="Bank Acount No credited"/></th>
            </tr>
            </thead>
            <tbody id="executionListTableBody">
                <g:render template="/exchange/templates/executionList" model="[exchangeInstance: exchangeInstance, executionList : executionList]"/>
            </tbody>
        </table>
    </bootstrap:formSection>
</bootstrap:div>
