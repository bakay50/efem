<%@ page import="com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.ExchangeRequestType" %>

<g:set var="hasAccess" value="${UserUtils.roleHasAccess()}"/>
<g:set var="enableEAFromTVF" value="${grailsApplication.config.com.webbfontaine.efem.exchange.createFromTVF.enabled}"/>
<g:set var="enableEAFromSAD" value="${grailsApplication.config.com.webbfontaine.efem.exchange.createFromSAD.enabled}"/>
<g:set var="enableEC" value="${grailsApplication.config.com.webbfontaine.efem.exchange.createEC.enabled}"/>
<g:set var="enableRepat" value="${grailsApplication.config.com.webbfontaine.efem.exchange.createRepat.enabled}"/>
<g:set var="enableTransfer" value="${grailsApplication.config.com.webbfontaine.efem.exchange.createTransfer.enabled}"/>
<g:set var="enableCurrencies"
       value="${grailsApplication.config.com.webbfontaine.efem.exchange.createCurrencies.enabled}"/>

<g:if test="${hasAccess}">
    <li>
        <g:if test="${UserUtils.isTrader() || UserUtils.isDeclarant()}">
            <g:if test="${enableEAFromTVF}">
                <g:link controller="exchange" action="create"
                        params="[requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType.BASE_ON_TVF]">
                    <bootstrap:icon name='pencil'/> <g:message code="create.ea.label"
                                                               args="[message(code: 'requestType.TVF', default: 'TVF')]"/>
                </g:link>
            </g:if>
            <g:if test="${enableEAFromSAD}">
                <g:link controller="exchange" action="create"
                        params="[requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType.BASE_ON_SAD]">
                    <bootstrap:icon name='pencil'/> <g:message code="create.ea.label"
                                                               args="[message(code: 'requestType.SAD', default: 'SAD')]"/>
                </g:link>
            </g:if>
            <g:if test="${enableEC}">
                <g:link controller="exchange" action="create" params="[requestType: ExchangeRequestType.EC]">
                    <bootstrap:icon name='pencil'/> <g:message code="create.ec.label"/>
                </g:link>
            </g:if>

        </g:if>
        <g:if test="${UserUtils.isTrader() || UserUtils.isBankAgent()}">
            <g:if test="${enableTransfer}">
                <g:link controller="transferOrder" action="create">
                    <bootstrap:icon name='pencil'/> <g:message code="create.transferOrder.label"/>
                </g:link>
            </g:if>
            <g:if test="${enableRepat}">
                <g:link controller="repatriation" action="create">
                    <bootstrap:icon name='pencil'/> <g:message code="create.repatriation.label"/>
                </g:link>
            </g:if>
        </g:if>
        <g:link controller="exchange" action="list">
            <bootstrap:icon name="search"/> <g:message code="exchange.search.title" default="Search EA / EC"/>
        </g:link>
        <g:if test="${enableTransfer && !UserUtils.isDeclarant()}">
            <g:link controller="transferOrder" action="list">
                <bootstrap:icon name="search"/> <g:message code="transferOrder.search.title"
                                                           default="Transfers Search"/>
            </g:link>
        </g:if>
        <g:if test="${!UserUtils.isDeclarant()}">
            <g:link controller="repatriation" action="list">
                <bootstrap:icon name="search"/> <g:message code="repatriation.search.title"
                                                           default="Search Repatriation"/>
            </g:link>
        </g:if>
        <g:if test="${enableCurrencies}">
            <g:if test="${UserUtils.isBankAgent()}">
                <g:link controller="currencyTransfer" action="create">
                    <bootstrap:icon name='pencil'/> <g:message code="currencyTransfer.create.menu.label"/>
                </g:link>
            </g:if>
            <g:if test="${UserUtils.isAdministrator() || UserUtils.isBankAgent() || UserUtils.isGovSupervisor()}">
                <g:link controller="currencyTransfer" action="list">
                    <bootstrap:icon name="search"/> <g:message code="currencyTransfer.search.label"/>
                </g:link>
            </g:if>
        </g:if>
        <g:if test="${AppConfig.isBankEnabled()}">
            <g:if test="${UserUtils.isSuperAdministrator()}">
                <g:link controller="bank" action="create">
                    <bootstrap:icon name='pencil'/> <g:message code="bank.create.label"/>
                </g:link>
                <g:link controller="bank" action="list">
                    <bootstrap:icon name="search"/> <g:message code="bank.search.label"/>
                </g:link>
            </g:if>
        </g:if>
    </li>
</g:if>