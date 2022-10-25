<bootstrap:div id="chgLngconfirmDialog" class="modal fade" tabindex="-1" role="dialog">
    <bootstrap:div class="modal-dialog" role="document">
        <bootstrap:div class="modal-content">
            <bootstrap:div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><g:message code="utils.confirm.title"/></h4>
            </bootstrap:div>
            <bootstrap:div class="modal-body">
                <p>
                    <span id="confirmMessage"><g:message code="utils.change.language.confirm.label"/></span>
                </p>
            </bootstrap:div>
            <bootstrap:div class="modal-footer">
                <a id="chgLngConfirmOper" title="${message(code: 'utils.confirm.yes', default: 'Yes')}"
                   class="btn btn-success">
                    ${message(code: 'utils.confirm.yes', default: 'Yes')}
                </a>
                <a id="chgLngCancelOper"
                   title="${message(code: 'utils.confirm.no', default: 'No')}" class="btn btn-default">
                    ${message(code: 'utils.confirm.no', default: 'No')}
                </a>
            </bootstrap:div>
        </bootstrap:div>
    </bootstrap:div>>
</bootstrap:div>