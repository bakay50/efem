<bootstrap:div id="'attDocListContainer">
    <bootstrap:div id="showwaybills" style="clear:both">
        <h3 id="titleListItems" style="float:left">
            <g:message code="attDoc.list_of_Attachments.label" default="List of Attachments"/>
        </h3>
        <g:render template="/attachedDoc/attachedDocAlertInfo"/>
        <g:render template="/attachedDoc/attachedDocError"/>
    </bootstrap:div>
    <bootstrap:div id="attDocListErrors" style="clear:both"/>
    <bootstrap:div id="attDocListAlertInfo" style="clear:both"/>
    <div id="alertInfo"></div>
    <table class="table table-bordered table-striped tableNoWrap" id="attDocTable">
        <thead>
        <tr>
            <th class="actions-th"></th>
            <th class="span1 listRank">#</th>
            <th><g:message code="attacheddoc.docType.label" default="Code"/></th>
            <th><g:message code="attacheddoc.docTypeName.label" default="Type"/></th>
            <th><g:message code="attacheddoc.docRef.label" default="Reference Number"/></th>
            <th><g:message code="attacheddoc.docDate.label" default="Date"/></th>
            <th><g:message code="attacheddoc.file.label" default="File"/></th>
        </tr>
        <g:if test="${exchangeInstance?.isAttachmentEditable()}">
            <tr id="addRow">
                <td></td>
                <td></td>
                <td><wf:textInput id="docType" name="docType" value="" class="span1" maxlength="5" /></td>
                <td><wf:textInput id="docTypeName" name="docTypeName" value="" class="span2" maxlength="100" disabled="disabled" /></td>
                <td><wf:textInput id="docRef" name="docRef" value="" class="span5" maxlength="35"/></td>
                <td><wf:datepicker name="docDate" maxDate="0" dateformat="${grails.util.Holders.config.bootstrapDate}" regexp="${'^[\\d /]+$'}" onkeydown="return false;"/></td>
                <td class="centered">
                    <a id="addUploadFile" title="${message(code: 'attDoc.upload.label', default: 'Upload file')}" class="btn btn-default disabled">
                        ${message(code: 'attDoc.upload.label', default: 'Upload file')}
                    </a>
                </td>
            </tr>
        </g:if>
        </thead>
        <tbody id="attDocListBody">
            <g:render template="/attachedDoc/attachedDocList" model="[domainInstance:exchangeInstance]"/>
        </tbody>
    </table>
    <g:if test="${exchangeInstance?.isAttachmentEditable()}">
        <table id="editAttDocTable" style="display: none">
            <tr id="editAttDocRowContent">
                <td class="centered">
                    <div class="js_actions">
                        <bootstrap:icon name="ok" id="updateAttDoc" />
                        <bootstrap:icon name="remove" onclick="cancelEditAttDoc()" />
                    </div>
                </td>
                <td><wf:hiddenField name="edRank" /> </td>
                <td><wf:textInput name="edDocType" maxlength="5" value="" placeholder="SUGGEST" class="span1"/></td>
                <td><wf:textInput name="edDocTypeName" maxlength="100" value="" disabled="disabled"  class="span2"/></td>
                <td><wf:textInput name="edDocRef" maxlength="35" value="" class="span5"/></td>
                <td><wf:datepicker name="edDocDate" regexp="${'^[\\d/]+$'}" /></td>
                <td class="centered">
                    <a id="editUploadFile"
                       title="${message(code: 'default.button.update.label', default: 'Upload file')}"
                       class="btn btn-default">
                        ${message(code: 'default.button.update.label', default: 'Upload file')}
                    </a>
                </td>
            </tr>
        </table>
    </g:if>
</bootstrap:div>
