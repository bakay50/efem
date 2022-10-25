<%@ page import="org.springframework.context.i18n.LocaleContextHolder; grails.util.Metadata" %>
<g:applyLayout name="sw-page">
    <html>
    <head>
        <layout:resource name="application"/>
        <asset:i18n name="messages" locale="${LocaleContextHolder.locale}"/>
    </head>
    <body>
    <content tag="breadcrumbs">
        <bootstrap:breadcrumb>
            <bootstrap:breadcrumbItem>
                <a href="${grailsApplication.config.trade.path}">
                    <wf:message code="layout.home"/>
                </a>
            </bootstrap:breadcrumbItem>
            <bootstrap:breadcrumbItem>
                <a href="${grailsApplication.config.trade.path}/finances" style="">
                    <wf:message code="layout.agencies.finances.name"/>
                </a>
            </bootstrap:breadcrumbItem>
            <bootstrap:breadcrumbItem last="true">
                <layout:appName/>
            </bootstrap:breadcrumbItem>
            <bootstrap:breadcrumbItem class="pull-right">
                <a href="#"><wf:message prefix="layout" code="version"/>: <g:meta name="${Metadata.APPLICATION_VERSION}"/></a>
            </bootstrap:breadcrumbItem>
        </bootstrap:breadcrumb>
        <wf:loadSystemMessages appName="efem"/>
    </content>

    <content tag="services"><g:render template="/services"/></content>

    <content tag="application">
        <g:pageProperty name="page.application"/>
    </content>
    </body>
    </html>
</g:applyLayout>
