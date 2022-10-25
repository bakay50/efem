<%@ page import="com.webbfontaine.efem.AppConfig" %>
<g:set var="canDisplayImportExport" value="${AppConfig.displayImportExport() as Boolean}"/>
<g:if test="${currencyTransferInstance.id == null && canDisplayImportExport}">
    <bootstrap:linkButton id="importXML" action="importXML" class="btn btn-default importXML"
                          name="importXML" labelCode='currencyTransfer.importXML.operation.label'
                          onclick="importXMLCurrencyTransfer(event)" />
</g:if>
<g:if test="${currencyTransferInstance.id && canDisplayImportExport}">
    <bootstrap:linkButton id="exportXML" class="btn btn-default"
                          name="exportXML" labelCode='currencyTransfer.exportXML.operation.label'
                          href="javascript:void(0)"
                          onclick="exportXMLCurrencyTransfer('appMainForm')" />
</g:if>

<bootstrap:div id="operationButtonsSection">
    <g:render plugin="wf-workflow" template="/operationButtons" model="[operations: currencyTransferInstance.operations]"/>
    <bootstrap:linkButton id="close" action="list" controller="currencyTransfer" class="btn btn-default" name="close"
                      labelCode='exec.button.close.label'/>
    <wf:ajaxProgress/>
</bootstrap:div>
<wf:hiddenField name="_confirmMessage" value="${wf.message(code: 'utils.confirm.confirmMessage')}"/>