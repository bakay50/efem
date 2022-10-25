<%@ page import="com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.constants.UtilConstants;com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.constants.ExchangeRequestType;" %>
<g:set var="exchangeCommitment" value="${exchangeInstance?.requestType == ExchangeRequestType.EC}"/>
<g:set var="isSpecialImporter" value="${exchangeInstance?.requestType == ExchangeRequestType.EA && exchangeInstance.status in [null, Statuses.ST_STORED, Statuses.ST_QUERIED] && exchangeInstance.specialImporter}"/>
<g:set var="isImporterEditable" value="${defTinProperty || !exchangeInstance.isFieldEditable('importerCode') ? true : false}" />
<g:set var="isExporterTrader" value="${(!isSpecialImporter || exchangeCommitment) || !exchangeInstance.isFieldEditable('exporterCode') ? true : false}" />
<g:set var="isExporterDeclarant" value="${(!isSpecialImporter && !exchangeCommitment) || !exchangeInstance.isFieldEditable('exporterCode') ? true : false}" />
<g:set var="isExporterEditable" value="${UserUtils.isTrader() ? isExporterTrader : (UserUtils.isDeclarant() ? isExporterDeclarant : true)}" />
<g:set var="isDeclarantCodeEditable" value="${defDecProperty || !exchangeInstance.isFieldEditable('declarantCode')  ? true : false}" />
<bootstrap:div id="namesAndPartiesContainer">
    <bootstrap:formSection labelCode="exchange.exporterDetails.label">
        <bootstrap:genericInput field="exporterCode" bean="${exchangeInstance}"
                                readonly="${isExporterEditable}"
                                value="${exchangeCommitment ? defTinProperty : exchangeInstance.exporterCode}"
                                labelCode="exchange.exporterCode.label"/>
        <bootstrap:genericInput field="exporterNameAddress" bean="${exchangeInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="exchange.exporterNameAddress.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="exchange.importerDetails.label">
        <bootstrap:genericInput field="importerCode" bean="${exchangeInstance}"
                                readonly="${isImporterEditable}"
                                value="${exchangeCommitment ? exchangeInstance.importerCode : defTinProperty}"
                                labelCode="exchange.importerCode.label"/>
        <bootstrap:genericInput field="importerNameAddress" bean="${exchangeInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                readonly="${BusinessLogicUtils.displayFieldNameAndAddress(exchangeInstance)}"
                                labelCode="exchange.importerNameAddressEx.label"/>
        <g:if test="${exchangeInstance?.requestType == ExchangeRequestType.EC}">
            <bootstrap:genericInput field="areaPartyCode" labelField="areaPartyName" bean="${exchangeInstance}"
                                    maxlength="5" labelCode="exchange.areaPartyCode.label"/>
            <bootstrap:genericInput field="countryPartyCode" labelField="countryPartyName" bean="${exchangeInstance}"
                                    maxlength="5" onchange="handleSetEcCurrencyCodePaymentOnCountrySelected(this)"
                                    labelCode="exchange.countryPartyCode.label"/>
        </g:if>

        <g:if test="${exchangeInstance?.requestType in [ExchangeRequestType.EA_FROM_TVF, ExchangeRequestType.EA_FROM_SAD]}">
            <bootstrap:genericInput field="nationalityCode" bean="${exchangeInstance}"
                                    labelCode="exchange.nationality.label"
                                    onfocusout="limitFieldsBehavior(this.id,'#nationalityName')"
                                    labelField="nationalityName"/>
            <bootstrap:genericInput field="resident" bean="${exchangeInstance}" type="select" noSelection="['': '']"
                                    optionKey="key" optionValue="value"
                                    from="${referenceService.getResidentSelection()}"
                                    value="${exchangeInstance?.resident ?: UtilConstants.YES}"
                                    omitEmptyOption="true"
                                    labelCode="exchange.resident.label"/>
        </g:if>
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="exchange.declarantDetails.label">
        <bootstrap:genericInput field="declarantCode" bean="${exchangeInstance}"
                                labelCode="exchange.declarantCode.label"
                                readonly="${isDeclarantCodeEditable}"
                                onfocusout="limitFieldsBehavior(this.id,'#declarantNameAddress')"
                                value="${defDecProperty ?: exchangeInstance.declarantCode}"
                                class="${BusinessLogicUtils.checkIfEAAndDeclarantCodeMandatory(exchangeInstance) ? 'mandatory' : ""}"
                                required="${BusinessLogicUtils.checkIfEAAndDeclarantCodeMandatory(exchangeInstance)}"/>
        <bootstrap:genericInput field="declarantNameAddress" bean="${exchangeInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="exchange.declarantNameAddressEx.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="exchange.beneficiaryTitle.label">
        <bootstrap:genericInput field="beneficiaryName" bean="${exchangeInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="exchange.beneficiaryName.label"/>
        <bootstrap:genericInput field="beneficiaryAddress" bean="${exchangeInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="exchange.beneficiaryAddress.label"/>
    </bootstrap:formSection>
</bootstrap:div>
