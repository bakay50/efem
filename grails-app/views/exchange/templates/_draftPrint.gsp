<%@ page import="com.webbfontaine.efem.sad.SadService; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.BusinessLogicUtils" %>

<g:if test="${BusinessLogicUtils.isPrintExchange(exchangeInstance)}">
    <bootstrap:linkButton class="btn btn-default" controller="print" action="${printAction}" icon="glyphicon glyphicon-print"
                          params="[id:exchangeInstance?.id, conversationId: params.conversationId, exportFormat: 'pdf', extension: 'pdf', typePdf : 'draft', printType : exchangeInstance?.requestType]"
                          id="printExchange_${exchangeInstance?.id}">
        <g:message code="operations.Print${exchangeInstance?.requestType}.label" default="Print PDF"/>
    </bootstrap:linkButton>
</g:if>

<g:if test="${SadService.checkPrintAE(exchangeInstance)}">
        <bootstrap:linkButton controller="print" action="printEc" icon="glyphicon glyphicon-print"
                              params="[id:exchangeInstance?.id, conversationId: params.conversationId, exportFormat: 'pdf', extension: 'pdf', typePdf : 'draft', printType : 'AE']"
                              id="printExchange_${exchangeInstance?.id}">
            <g:message code="operations.PrintEC_AE.label" default="Print EA"/>
        </bootstrap:linkButton>

</g:if>