<%@ page import="com.webbfontaine.efem.security.Roles; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.AppConfig;com.webbfontaine.efem.constants.ExchangeRequestType;" %>

<g:each in="${BusinessLogicUtils.handleGetExecutionsForUsers(executionList)}" var="execution" status="i">
    <tr class="${execution.state == ExchangeRequestType.EXECUTION_CANCEL_STATE ? 'danger' : ''}">
        <td>
            <g:if test="${BusinessLogicUtils.isTransferOrderExist(execution)}">
                <g:hiddenField name="loadExecutionTransferUrl" value="${createLink(controller: 'transferOrder', action: 'loadExecution', params: [id : exchangeInstance?.id, rank : execution.rank, conversationId:params.conversationId])}"/>
                <a title="View" onclick="loadExecution(${execution.rank}, ${exchangeInstance?.id})" class="execIcon">
                    <bootstrap:icon name="eye-open"></bootstrap:icon>
                </a>
            </g:if>
        </td>
        <td>${i + 1}</td>
        <td>${execution.executionReference}</td>
        <td>${execution?.executionDate?.format(AppConfig.dateFormat())}</td>
        <td>${execution.currencyExCode}</td>
        <td>${execution.amountMentionedExCurrency}</td>
        <td>${execution.amountSettledMentionedCurrency}</td>
        <td>${execution.countryProvenanceDestinationExCode}</td>
        <td>${execution.provenanceDestinationExBank}</td>
        <td>${execution.bankAccountNumberCreditedDebited}</td>
    </tr>
</g:each>