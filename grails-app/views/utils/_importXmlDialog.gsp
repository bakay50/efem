<%@ page import="com.webbfontaine.efem.constants.UtilConstants" %>
<bootstrap:div id="importXmlDialog" name="importXmlDialog" title="${message(code: 'utils.importXML.label')}"
               class="modal" style="display:none;width: 600px; height:600px; left: 30%; top: 30%;">
    <bootstrap:div class="modal-dialog">
        <bootstrap:div class="modal-content">
            <bootstrap:div class="modal-header" style="padding: 4px 4px !important;">
                <a class="close" id="closeButton">×</a>

                <h3><g:message code="utils.importXML.label"/></h3>
            </bootstrap:div>
            <bootstrap:form url="[controller: controller, action: action]" name="fileUpload"
                            enctype='multipart/form-data'>
                <bootstrap:div class="modal-body" style="height:50px !important;">
                    <wf:fileInput id="xmlFile" name="xmlFile"/>
                    <g:hiddenField name="domainName" id="domainName" value="${controllerName == domainName.toLowerCase() ? domainName : controllerName}"/>
                    <g:hiddenField name="conversationId" id="conversationId" value="${params?.conversationId}"/>
                    <g:hiddenField name="requestType" id="requestType" value="${requestType}"/>
                    <g:hiddenField name="basedOn" id="basedOn" value="${basedOn}"/>
                </bootstrap:div>

                <bootstrap:div class="modal-footer">
                    <wf:submitButton name="startUpload" class="btn btn-success xml"
                                     value="${message(code: 'utils.importFromXML', default: 'Import from  XML')}"/>
                </bootstrap:div>
            </bootstrap:form>

            <bootstrap:div id="xmlUploadProgress">
                <bootstrap:div class="uploadPercent" id="uploadPercent"></bootstrap:div>
                <bootstrap:div class="progressBar" id="progressBar"></bootstrap:div>
                <bootstrap:div class="ieProgressBar" id="ieProgressBar"></bootstrap:div>
            </bootstrap:div>

            <p style="color: green;">
                <b style="margin-left: 5px"><g:message
                        code="utils.upload.document.maxSize"/>:</b> ${UtilConstants.XML_MAX_SIZE_BYTES} MB
            </p>
        </bootstrap:div>
    </bootstrap:div>
</bootstrap:div>