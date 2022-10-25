<%@ page import="com.webbfontaine.efem.ReferenceUtils; com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.constants.ReferenceConstants; com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils; org.joda.time.LocalDate; org.springframework.context.i18n.LocaleContextHolder" %>
<g:set var="loc" value="${(LocaleContextHolder.locale as String == "en") ? "" :"_fr"}"/>
<wf:hiddenField name="loc_js" value="${loc}"/>
