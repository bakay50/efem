<%@ page import="com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.security.Roles" %>
<g:set var="isStatusAvailable" value="${BusinessLogicUtils.checkIfStatusInList(exchangeInstance, [Statuses.ST_APPROVED, Statuses.ST_PARTIALLY_APPROVED, Statuses.ST_EXECUTED])}"/>
<g:set var="isBankCodeEditable" value="${defAdbProperty || !exchangeInstance.isFieldEditable('bankCode')  ? true : false}" />
<g:set var="isAuthAvailable" value="${(isStatusAvailable && (UserUtils.isTrader() || UserUtils.isDeclarant()))}"/>
<g:set var="exchangeHasError" value="${(exchangeInstance?.hasErrors() && exchangeInstance?.isDocumentValid)}"/>
<g:set var="canDisplayDeclarationSection" value="${(BusinessLogicUtils.canDisplayDeclarationSection(exchangeInstance))}"/>
<g:set var="exchangeHasStatus" value="${!(BusinessLogicUtils.isNull(exchangeInstance.status))}"/>
<g:set var="showAuthorizationByAndDate" value="${BusinessLogicUtils.checkIfStatusInList(exchangeInstance, [Statuses.ST_APPROVED, Statuses.ST_EXECUTED])}"/>
<g:set var="disableFieldForBankAndStatus" value="${true}"/>
<bootstrap:div id="headerContainer">
    <wf:hiddenField id="ecStatus" name="ecStatus" value="${exchangeInstance.status}"/>
    <wf:hiddenField id="isBankAgent" name="isBankAgent" value="${UserUtils.isBankAgent()}"/>
    <g:if test="${exchangeInstance?.requestNo && exchangeInstance?.requestDate}">
        <bootstrap:genericInput field="requestNo" bean="${exchangeInstance}"
                                labelCode="exchange.requestNo.label"/>
        <bootstrap:genericInput field="requestDate" bean="${exchangeInstance}" type="date"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                labelCode="exchange.requestDate.label"/>
    </g:if>
    <g:if test="${exchangeInstance?.basedOn == ExchangeRequestType.BASE_ON_TVF }">
        <g:render template="/exchange/templates/requestTypeTvf" model="[exchangeInstance:exchangeInstance, hasExchangeError:exchangeHasError]"/>
    </g:if>
    <g:elseif test="${canDisplayDeclarationSection}">
        <g:render template="/exchange/templates/requestTypeSad" model="[exchangeInstance:exchangeInstance, hasExchangeError:exchangeHasError]"/>
    </g:elseif>
    <bootstrap:formSection labelCode="exchange.transport.title.label">
        <bootstrap:genericInput field="countryOfExportCode" bean="${exchangeInstance}"
                                labelCode="exchange.${ExchangeRequestType.EA.equals(exchangeInstance?.requestType) ? "exportCode" : "ctyDest"}.label"
                                labelField="countryOfExportName"/>

        <g:if test="${exchangeInstance?.requestType != ExchangeRequestType.EA}">
            <bootstrap:genericInput field="dateOfBoarding" bean="${exchangeInstance}" type="date"
                                    regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                    labelCode="exchange.dateOfBoarding.label"/>
        </g:if>
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="exchange.bank.title.label">
        <bootstrap:genericInput field="bankCode" bean="${exchangeInstance}" onfocusout="limitFieldsBehavior(this.id,'#bankName')"
                                readonly="${isBankCodeEditable}"
                                labelCode="exchange.bankCode.label"
                                labelField="bankName"/>
        <g:if test="${isStatusAvailable || UserUtils.isBankAgent()}">
            <bootstrap:genericInput field="registrationNumberBank" bean="${exchangeInstance}" maxlength="35"
                                    labelCode="exchange.registrationNumberBank.label"/>
            <bootstrap:genericInput field="registrationDateBank" bean="${exchangeInstance}" type="date"
                                    regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                    labelCode="exchange.registrationDateBank.label"/>
        </g:if>

        <g:if test="${exchangeInstance?.requestType == ExchangeRequestType.EC}">
            <bootstrap:genericInput field="geoArea" bean="${exchangeInstance}" onchange="disableBankFields()"
                                    labelCode="exchange.geoArea.label"
                                    labelField="geoAreaName"/>
            <bootstrap:genericInput field="countryOfDestinationCode" bean="${exchangeInstance}"
                                    onchange="manageEcCurrencyCodePayment(this)"
                                    labelCode="exchange.countryOfDestinationCode.label"
                                    labelField="countryOfDestinationName"/>
        </g:if>
    </bootstrap:formSection>
    <g:if test="${exchangeInstance?.requestType in [ExchangeRequestType.EA_FROM_TVF, ExchangeRequestType.EA_FROM_SAD] }">
        <bootstrap:formSection labelCode="exchange.domiciliation.title.label">
            <bootstrap:genericInput field="domiciliationNumber" bean="${exchangeInstance}" maxlength="35"
                                    labelCode="exchange.domiciliationNumber.label"
                                    class="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation) ? 'mandatory' : ''}"
                                    required="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation)}" readonly="${disableFieldForBankAndStatus}" />
            <bootstrap:genericInput field="domiciliationDate" bean="${exchangeInstance}" type="date"
                                    regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                    labelCode="exchange.domiciliationDate.label"
                                    class="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation) ? 'mandatory' : ''}"
                                    required="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation)}" readonly="${disableFieldForBankAndStatus}" />
            <bootstrap:genericInput field="domiciliationBankCode" bean="${exchangeInstance}" onfocusout="limitFieldsBehavior(this.id,'#domiciliationBankName')"
                                    labelCode="exchange.domiciliationBankCode.label" labelField="domiciliationBankName"
                                    class="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation) ? 'mandatory' : ''}"
                                    required="${BusinessLogicUtils.requireDomiciliationFields(exchangeInstance.startedOperation)}" readonly="${disableFieldForBankAndStatus}" />
        </bootstrap:formSection>
        <bootstrap:formSection labelCode="blank.label">
            <g:if test="${showAuthorizationByAndDate}">
                <bootstrap:genericInput field="authorizationDate" bean="${exchangeInstance}" type="date"
                                        regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                        labelCode="exchange.authorizationDate.label"/>
                <bootstrap:genericInput field="authorizedBy" bean="${exchangeInstance}"
                                        labelCode="exchange.authorizedBy.label"/>
            </g:if>
            <g:if test="${isAuthAvailable || UserUtils.isBankAgent() || UserUtils.isGovOfficer()}">
                <bootstrap:genericInput field="expirationDate" bean="${exchangeInstance}" type="date"
                                        regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                        labelCode="exchange.expirationDate.label"/>
                <bootstrap:genericInput name="exchangeStatus" readonly="true"
                                        labelCode="exchange.status.label" value="${message(code: "status.${exchangeInstance?.status}")}"/>
            </g:if>

            <sec:ifAnyGranted roles="${Roles.GOVT_OFFICER.authority}">
                <bootstrap:genericInput field="treatmentLevel" bean="${exchangeInstance}"
                                    labelCode="exchange.treatmentLevel.label"/>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles="${Roles.GOVT_OFFICER.authority}, ${Roles.BANK_AGENT.authority}, ${Roles.TRADER.authority}, ${Roles.DECLARANT.authority}">
                <bootstrap:genericInput field="commentHeader" bean="${exchangeInstance}" type="area" maxlength="4000"
                                    inputSpan="6" rows="4"
                                    labelCode="exchange.comments.label"/>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles="${Roles.GOVT_OFFICER.authority}, ${Roles.ADMIN.authority}">
            <bootstrap:genericInput field="evaluationReport" bean="${exchangeInstance}" type="area" maxlength="4000"
                                    inputSpan="6" rows="4"
                                    labelCode="exchange.evaluationReport.label"/>
            </sec:ifAnyGranted>
        </bootstrap:formSection>
    </g:if>
    <g:if test="${exchangeInstance?.requestType == ExchangeRequestType.EC}">
        <sec:ifAnyGranted roles="${Roles.GOVT_OFFICER.authority}, ${Roles.BANK_AGENT.authority}, ${Roles.TRADER.authority}, ${Roles.DECLARANT.authority}">
            <bootstrap:genericInput field="commentHeader" bean="${exchangeInstance}" type="area" maxlength="4000"
                                    inputSpan="6" rows="4"
                                    labelCode="exchange.comments.label"/>
        </sec:ifAnyGranted>

    </g:if>
    <g:if test="${(exchangeInstance?.requestType == ExchangeRequestType.EC) && exchangeHasStatus && (exchangeInstance?.status != Statuses.ST_STORED)}">
        <bootstrap:formSection labelCode="blank.label">
            <bootstrap:genericInput field="departmentInCharge"
                                    bean="${exchangeInstance}"
                                    readonly="true"
                                    labelCode="exchange.departmentInCharge.label"
                                    labelField="${exchangeInstance?.departmentInCharge?.equals(exchangeInstance?.bankCode) ? "bankName" : ""}"/>
        </bootstrap:formSection>
    </g:if>
</bootstrap:div>
