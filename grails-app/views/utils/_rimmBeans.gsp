<%@ page import="com.webbfontaine.efem.ReferenceUtils; com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.constants.ReferenceConstants; com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils; org.joda.time.LocalDate; org.springframework.context.i18n.LocaleContextHolder" %>
<g:set var="htDate" value="${LocalDate.now().toDate().clearTime()}"/>
<g:set var="loc" value="${(LocaleContextHolder.locale as String == "en") ? "" :"_fr"}"/>
<g:set var="operationCodes" value="${exchangeInstance?.requestType == ExchangeRequestType.EA ? ExchangeRequestType.operationCodes_EA :ExchangeRequestType.operationCodes_EC}"/>
<g:set var="geoAreaCode" value="${UserUtils.isBankAgent()?ExchangeRequestType.AREA_002:"null"}"/>
<wf:hiddenField name="loc_js" value="${loc}"/>
<g:render template="/utils/commonHiddenFields" />

<wf:localRefCustom refName="REF_CTY" renderOnView="true" selectFields="code,description" from="${ReferenceUtils.getCountryFieldsWithNotIn(ExchangeRequestType.CI_CURRENCY)?: [""]}"/>
<wf:localRefCustom refName="REF_GEOARE" selectFields="code,description" from="${ReferenceUtils.getGeoAreaNotEqual(geoAreaCode)?: [""]}" enableRefCache="false"/>
<wf:localRefCustom refName="REF_OPT" selectFields="code,description" from="${ReferenceUtils.getOperationTypeInList(operationCodes)?: [""]}" enableRefCache="false"/>
<g:if test="${exchangeInstance.requestType == ExchangeRequestType.EC}">
    <wf:localRefCustom refName="REF_AREA_PARTY" selectFields="code,description" from="${ReferenceUtils.getNameAndPartiesArea()}" enableRefCache="false"/>
</g:if>

<g:if test="${AppConfig.isRimmEnabled() && ReferenceUtils.isProduction() && ExchangeRequestType.EA.equals(exchangeInstance?.requestType)}">
    <wf:hiddenField name="jossoAvailable" value="true"/>
    <g:set var="BankCodeList" value="${ReferenceUtils.getBankFields(ReferenceUtils.getBankCodes())}"/>
    <g:if test="${BankCodeList}">
        <wf:hiddenField name="BankCodeListHasValue" value="true"/>
        <wf:localRefCustom enableRefCache="false" refName="REF_BNK_FLG" selectFields="${ReferenceConstants.COMMON_SELECT_FIELDS}"
                           from="${BankCodeList}"/>
    </g:if>
</g:if>

<g:set var="decProperties" value="${UserUtils.isDeclarant()?UserUtils.hasUserProperties(UserProperties.DEC):null}" scope="request"/>
<g:if test="${decProperties?.hasUserProperties}">
    <wf:hiddenField name="decUserProps" value="true"/>
    <g:if test="${decProperties?.hasDefaultProperty}">
        <g:set var="defDecProperty" value="${decProperties.hasDefaultProperty ? decProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_DEC" selectFields="${ReferenceConstants.DECLARANT_SHOW_FIELDS}"
                           from="${ReferenceUtils.getDeclarantFields(decProperties?.userPropValues)}"/>
    </g:else>
</g:if>

<g:set var="tinProperties" value="${UserUtils.isTrader()?UserUtils.hasUserProperties(UserProperties.TIN):null}"/>
<g:if test="${tinProperties?.hasUserProperties}">
    <g:if test="${UserUtils.isTrader() && ExchangeRequestType.EA.equals(exchangeInstance?.requestType) && ExchangeRequestType.BASE_ON_SAD.equals(exchangeInstance?.basedOn)}">
        <wf:hiddenField name="tinUserProps" value="false"/>
    </g:if>
    <g:else>
        <wf:hiddenField name="tinUserProps" value="true"/>
    </g:else>
    <g:if test="${tinProperties?.hasDefaultProperty}">
        <g:set var="defTinProperty" value="${tinProperties.hasDefaultProperty ? tinProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_CMP" selectFields="${ReferenceConstants.COMPANY_SHOW_FIELDS}"
                           from="${ReferenceUtils.getCompanyFields(tinProperties.userPropValues)}"/>
    </g:else>
</g:if>

<g:set var="adbProperties" value="${UserUtils.isBankAgent()?UserUtils.hasUserProperties(UserProperties.ADB):null}"/>
<g:if test="${adbProperties?.hasUserProperties}">
    <wf:hiddenField name="adbUserProps" value="true"/>
    <g:if test="${adbProperties?.hasDefaultProperty}">
        <g:set var="defAdbProperty" value="${adbProperties.hasDefaultProperty ? adbProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_ADB" selectFields="${ReferenceConstants.COMMON_SELECT_FIELDS}"
                           from="${ReferenceUtils.getBankFields(adbProperties.userPropValues)}"/>
    </g:else>
</g:if>

<layout:javascript defer="true">
    makeAjaxAutocomplete('clearanceOfficeCode', 'HT_CUO', 'code,description', 'code,description', 'code,description', labelSetter, '70');
    makeAjaxAutocomplete('sdClearanceOfficeCode', 'HT_CUO', 'code,description', 'code,description', 'code,description', labelSetter, '70');
    makeAjaxAutocomplete('nationalityCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('countryProvenanceDestinationCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('countryProvenanceDestinationExCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('countryOfExportCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('countryOfDestinationCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('currencyCode','REF_CUR','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('currencyPayCode','REF_CUR','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('docType','HT_ATT','code,description','code,description','code,description',labelSetter,'70');
    makeAutocomplete('operType','REF_OPT','code,description',labelSetter,'50');
    makeAutocomplete('geoArea','REF_GEOARE','code,description',labelSetter,'100');
    makeAutocomplete('areaPartyCode','REF_AREA_PARTY','code,description',labelSetter,'100');
    makeAjaxAutocomplete('countryPartyCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
</layout:javascript>
