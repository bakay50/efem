<%@ page import="com.webbfontaine.efem.AppConfig" %>
<g:set var="canDisplayImportExport" value="${AppConfig.displayImportExport() as Boolean}"/>
<g:if test="${transferInstance.id == null && canDisplayImportExport}">
    <bootstrap:linkButton id="importXML" action="importXML" class="btn btn-default importXML"
                          name="importXML" labelCode='transferOrder.importXML.operation.label'
                          onclick="importXMLTransferOrder(event)" />
</g:if>
<g:if test="${transferInstance.id && canDisplayImportExport}">
    <bootstrap:linkButton id="exportXML" class="btn btn-default"
                          name="exportXML" labelCode='transferOrder.exportXML.operation.label'
                          href="javascript:void(0)"
                          onclick="exportXMLTransferOrder('appTrMainForm')" />
</g:if>
<bootstrap:div id="operationButtonsSection">
    <g:render template="templates/operationButtons" model="${[operations: transferInstance?.operations]}"/>
    <bootstrap:linkButton id="close" action="list" controller="transferOrder" class="btn btn-default" name="close"
                      labelCode='exec.button.close.label'/>
    <wf:ajaxProgress/>
</bootstrap:div>
<wf:hiddenField name="_confirmMessage" value="${wf.message(code: 'utils.confirm.confirmMessage')}"/>