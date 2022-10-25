<div id="js_successAjaxDialog" class="modal fade hide"  style="width:300px; left: 67%;top: 65%" >
    <div class="modal-header">
        <a class="close" data-dismiss="modal">x</a> 
         <h3><g:message code="successOperation.${flash.endOperation}.label" default="Operation successful"/></h3>
    </div>

    <div class="modal-body">  
        <dl class="dl-horizontal">
            <dt>${message(code: 'repatriation.requestNo.label', default: 'Request No ')}:</dt>
            <dd>${repatriationInstance?.requestNo}</dd>
            <dt>${message(code: 'repatriation.requestDate.label', default: 'Request Date ')}:</dt>
            <dd> <joda:format value="${repatriationInstance?.requestDate}" locale="" pattern="dd/MM/yyyy" style="" /></dd>
            <dt>${message(code: 'repatriation.exporterCode.label', default: 'Exporter Code ')}:</dt>
            <dd>${repatriationInstance?.code}</dd>
        </dl>
    </div>

    <div class="modal-footer">
        <a id="okAjaxOper" class="btn btn-sm btn-default" data-dismiss="modal" > ${g.message(code: 'button.operation.ok', default: 'OK')}</a>
    </div>
</div>