
<bootstrap:div class="modal hide" id="js_delCnfrmActiondlg" aria-hidden="false">
    <bootstrap:div class="modal-header" id="js_delCnfrmActionHeader">
        <a class="close" data-dismiss="modal">×</a>
        <h3><g:message code="default.dialog.box.header" default="Confirm"/></h3>
    </bootstrap:div>
    <bootstrap:div class="modal-body" id="js_delCnfrmActionModalBody">
        <dl class="dl-horizontal">
            <p id="js_delCnfrmActionMsg"></p>
        </dl>
    </bootstrap:div>

    <bootstrap:div class="modal-footer">
        <a id="js_delCnfrmActionOK" title="${message(code:"dlg.button.ok.label", default: "OK")}" class="btn btn-success" data-dismiss="modal">
            <g:message code="dlg.button.ok.label" default="Ok"/>
        </a>
        <a id="js_delCnfrmActionCancel" title="${message(code:"default.button.cancel.label", default:"Cancel")}" class="btn" data-dismiss="modal">
            <g:message code="default.button.cancel.label" default="Cancel"/>
        </a>
    </bootstrap:div>
</bootstrap:div>