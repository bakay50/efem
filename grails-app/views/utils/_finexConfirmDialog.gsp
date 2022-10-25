<bootstrap:div id="finexConfirmDialog" class="modal fade" tabindex="-1" role="dialog">
    <bootstrap:div class="modal-dialog" role="document">
        <bootstrap:div class="modal-content">
            <bootstrap:div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><g:message code="utils.confirm.title"/></h4>
            </bootstrap:div>
            <bootstrap:div class="modal-body">
                <p>
                    <span id="confirmMessage"><g:message code="client.finexConfirmation"/></span>
                    <span><g:message code="client.finex"/></span> ?
                </p>
            </bootstrap:div>
            <bootstrap:div class="modal-footer">
                <bootstrap:div class="pull-left">
                    <a id="confirmOperYes" title="${message(code: 'utils.confirm.yes', default: 'Yes')}"
                       class="btn btn-success">
                        ${message(code: 'utils.confirm.yes', default: 'Yes')}
                    </a>
                    <a id="confirmOperNo" title="${message(code: 'utils.confirm.no', default: 'No')}"
                       class="btn btn-default">
                        ${message(code: 'utils.confirm.no', default: 'No')}
                    </a>
                </bootstrap:div>
                <a id="cancelOper"
                   title="${message(code: 'utils.confirm.cancel', default: 'Cancel')}" class="btn btn-default">
                    ${message(code: 'utils.confirm.cancel', default: 'Cancel')}
                </a>
            </bootstrap:div>
        </bootstrap:div>
    </bootstrap:div>>
</bootstrap:div>