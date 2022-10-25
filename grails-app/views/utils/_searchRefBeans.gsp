<%@ page import="org.springframework.context.i18n.LocaleContextHolder; com.webbfontaine.efem.constants.ReferenceConstants; com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.ReferenceUtils; org.joda.time.LocalDate; com.webbfontaine.efem.constants.ExchangeRequestType" %>
<g:set var="htDate" value="${LocalDate.now().toDate().clearTime()}"/>
<g:set var="loc" value="${(LocaleContextHolder.locale as String == "en") ? "" :"_fr"}"/>
<g:set var="geoAreaCode" value="${UserUtils.isBankAgent()?ExchangeRequestType.AREA_002:"null"}"/>
<wf:hiddenField name="loc_js" value="${loc}"/>
<wf:localRefCustom refName="REF_GEOARE" selectFields="code,description${loc}" from="${ReferenceUtils.getGeoAreaNotEqual(geoAreaCode)?: [""]}" enableRefCache="false"/>

<g:set var="decProperties" value="${UserUtils.hasUserProperties(UserProperties.DEC)}" scope="request"/>
<g:if test="${decProperties.hasUserProperties}">
    <wf:hiddenField name="decUserProps" value="true"/>
    <g:if test="${decProperties.hasDefaultProperty}">
        <g:set var="defDecProperty" value="${decProperties.hasDefaultProperty ? decProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_DEC" selectFields="${ReferenceConstants.COMMON_SELECT_FIELDS}"
                           from="${ReferenceUtils.getDeclarantFields(decProperties?.userPropValues)}"/>
    </g:else>
</g:if>

<g:set var="tinProperties" value="${UserUtils.hasUserProperties(UserProperties.TIN)}"/>
<g:if test="${tinProperties.hasUserProperties}">
    <wf:hiddenField name="tinUserProps" value="true"/>
    <g:if test="${tinProperties.hasDefaultProperty}">
        <g:set var="defTinProperty" value="${tinProperties.hasDefaultProperty ? tinProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_CMP" selectFields="${ReferenceConstants.COMMON_SELECT_FIELDS}"
                           from="${ReferenceUtils.getCompanyFields(tinProperties.userPropValues)}"/>
    </g:else>
</g:if>

<g:set var="adbProperties" value="${UserUtils.hasUserProperties(UserProperties.ADB)}"/>
<g:if test="${adbProperties.hasUserProperties}">
    <wf:hiddenField name="adbUserProps" value="true"/>
    <g:if test="${adbProperties.hasDefaultProperty}">
        <g:set var="defAdbProperty" value="${adbProperties.hasDefaultProperty ? adbProperties.userPropValues[0] : null}" scope="request"/>
    </g:if>
    <g:else>
        <wf:localRefCustom refName="REF_ADB" selectFields="${ReferenceConstants.COMMON_SELECT_FIELDS}"
                           from="${ReferenceUtils.getBankFields(adbProperties.userPropValues)}"/>
    </g:else>
</g:if>

<layout:javascript defer="true">
    makeAjaxAutocomplete('domiciliationBankCode','HT_BNK','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('currencyCode','REF_CUR','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('countryProvenanceDestinationCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
    makeAjaxAutocomplete('executingBankCode','HT_BNK','code,description','code,description','code,description',labelSetter,'70,150');
    makeAutocomplete('operType','REF_OPT','code,description',labelSetter,'50');
    if ($("#loc_js").val() === "") {
        makeAutocomplete('geoArea','REF_GEOARE','code,description',labelSetter,'100');
    } else {
        makeAutocomplete('geoArea','REF_GEOARE','code,description_fr',labelSetter,'100');
    }
    makeAutocomplete("codeBankSearch", "REF_BNK", "code", labelSetter, "50");

</layout:javascript>