<%@ page import="grails.util.Holders" %>
<bootstrap:modal name="uploadDialog" id="uploadDialog">
    <bootstrap:modalHeader>
        <h3><g:message code="attDoc.Upload_File.label" locale="${org.springframework.context.i18n.LocaleContextHolder.locale}"/></h3>
    </bootstrap:modalHeader>
    <bootstrap:modalBody>
        <wf:hiddenField name="uploadAndAddMessage"
                        value="${message(code: 'attDoc.Upload_Plus_Add.label', default: 'Upload + Add')}"
                        disabled="true"/>
        <wf:hiddenField name="uploadAndSaveMessage"
                        value="${message(code: 'attDoc.Upload_Plus_Save.label', default: 'Upload + Save')}"
                        disabled="true"/>
        <g:form url="[controller: 'attachedDoc', action: 'uploadAttDoc']" name="fileUpload"
                enctype='multipart/form-data'>
            <div class="modal-body modal-attdoc-body">
                <wf:fileInput id="attDoc" name="attDoc" accept="text/xml"/>
                <g:hiddenField name="fileExtension"/>
                <g:hiddenField name="preAttNumber"/>
            </div>
            <div class="modal-footer">
                <wf:submitButton id="startUpload" name="startUpload" class="btn btn-success" />
            </div>
        </g:form>

        <div id="attUploadProgress">
            <div class="uploadPercent" id="uploadPercent"></div>
            <div class="progressBar" id="progressBar"></div>
            <div class="ieProgressBar" id="ieProgressBar"></div>
        </div>

        <p style="color: green;">
            <b style="margin-left: 5px"><g:message code="utils.upload.accepted.formats"/>:</b>
            ${grailsApplication.config.attachmentAcceptedFormats.join(', ')}

        </p>

        <p style="color: green;">
            <b style="margin-left: 5px"><g:message
                    code="utils.upload.acceptedPDF.maxSize"/>:</b>  ${grailsApplication.config.attachmentMaxSizeBytes / (1024 * 1024)} MB
        </p>
    </bootstrap:modalBody>
</bootstrap:modal>

<g:render template="/attachedDoc/hiddenLinks" model="[domainType:domainType]" />