<%@ page import="com.webbfontaine.efem.AppConfig; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.constants.Statuses" %>
<layout:pageResources dependsOn="application">
    <layout:resource name="exchange.js"/>
    <layout:resource name="xml.js"/>
</layout:pageResources>
<g:set var="canDisplayImportExport" value="${AppConfig.displayImportExport() as Boolean}"/>
<g:if test="${repatriationInstance?.id == null && canDisplayImportExport}">
    <bootstrap:linkButton id="xmlImportButton" labelCode="xml.import.label" class="btn xmlImportButton">
        <i class="glyphicon glyphicon-circle-arrow-down pull-left"></i>
    </bootstrap:linkButton>
</g:if>
<g:if test="${repatriationInstance?.id && canDisplayImportExport}">
    <bootstrap:linkButton id="xmlExportButton" labelCode="xml.export.label" class="btn xmlExportButton" >
        <i class="glyphicon glyphicon-circle-arrow-up pull-left"></i>
    </bootstrap:linkButton>
</g:if>
<wf:hiddenField name="domainInstanceName" value="Repatriation"/>
<bootstrap:div id="operationButtonsSection">
    <g:if test="${BusinessLogicUtils.isVerifyAvailable(repatriationInstance) && params.isDocumentEditable}">
        <a id="verifyOp" onclick="verifyDocument()" class="btn btn-default">
            <g:message code="default.button.verify.label"/>
        </a>
    </g:if>
    <g:render plugin="wf-workflow" template="/operationButtons" model="[operations: repatriationInstance.operations]"/>
    <bootstrap:linkButton id="close" action="list" controller="exchange" class="btn btn-default" name="close"
                      labelCode='exec.button.close.label'/>
    <wf:ajaxProgress/>
</bootstrap:div>
<wf:hiddenField name="_confirmMessage" value="${wf.message(code: 'utils.confirm.confirmMessage')}"/>