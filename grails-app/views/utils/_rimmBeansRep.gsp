<%@ page import="com.webbfontaine.efem.ReferenceUtils; com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.constants.ReferenceConstants; com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils; org.joda.time.LocalDate; org.springframework.context.i18n.LocaleContextHolder" %>
<g:set var="htDate" value="${LocalDate.now().toDate().clearTime()}"/>
<g:set var="loc" value="${(LocaleContextHolder.locale as String == "en") ? "" :"_fr"}"/>
<wf:hiddenField name="loc_js" value="${loc}"/>
<g:render template="/utils/commonHiddenFields" />

<layout:javascript defer="true">
    makeAjaxAutocomplete('docType','HT_ATT','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('currencyCode','REF_CUR','code,description','code,description','code,description',labelSetter,'70');
    makeAjaxAutocomplete('countryOfOriginCode','REF_CTY','code,description','code,description','code,description',labelSetter,'50,100');
</layout:javascript>