<bootstrap:div class="modal hide" id="js_warnMsgDlg" aria-hidden="false">
    <bootstrap:div class="modal-body">
        <dl class="dl-horizontal">
            <p id="js_warningMsg"></p>
        </dl>
    </bootstrap:div>
    <bootstrap:div class="modal-footer" id="js_warnMsgDlgFooter">
        <center><a id="js_warningActionOK" title="${message(code:"default.button.ok.label", default: "Close")}" class="btn btn-warning" data-dismiss="modal">
            <g:message code="exec.button.close.label" default="Close"/>
        </a></center>
    </bootstrap:div>
</bootstrap:div>