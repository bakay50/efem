<%@ page import="com.webbfontaine.efem.constants.ExchangeRequestType" %>
<div id="js_successAjaxDialog" class="modal fade hide"  style="width:300px; left: 67%;top: 65%" >
    <div class="modal-header">
        <a class="close" data-dismiss="modal">x</a> 
         <h3><g:message code="successOperation.${flash.endOperation}.label" default="Operation successful"/></h3>
    </div>

    <div class="modal-body">  
        <dl class="dl-horizontal">
            <dt>${message(code: 'exchange.requestNo.label', default: 'Request No ')}:</dt>
            <dd>${exchangeInstance?.requestNo}</dd>
            <dt>${message(code: 'exchange.requestDate.label', default: 'Request Date ')}:</dt>
            <dd> <joda:format value="${exchangeInstance?.requestDate}" locale="" pattern="dd/MM/yyyy" style="" /></dd>
            <dt>${message(code: 'exchange.requestType.label', default: 'Request Type ')}:</dt>
            <dd>${exchangeInstance?.requestType}</dd>
            <dt>${message(code: 'exchange.operType.label', default: 'Operation Type')}:</dt>
            <dd>${exchangeInstance?.operType}</dd>
            <g:if test="${exchangeInstance?.declarantCode}">
                <dt>${message(code: 'exchange.declarantCode.label', default: 'Declarant Code ')}:</dt>
            </g:if>
            <dd>${exchangeInstance?.declarantCode}</dd>
            <g:if test="${exchangeInstance?.basedOn?.equals("TVF") && exchangeInstance?.requestType == ExchangeRequestType.EA_FROM_SAD}">
                <dt>${message(code: 'exchange.tvfNumber.label', default: 'TVF No')}:</dt>
                <dd>${exchangeInstance?.tvfNumber}</dd>
                <dt>${message(code: 'exchange.tvfDate.label', default: 'TVF Date')}:</dt>
                <dd><joda:format value="${exchangeInstance?.tvfDate}" locale="" pattern="dd/MM/yyyy" style="" /></dd>
            </g:if>
            <g:elseif test="${exchangeInstance?.basedOn?.equals("SAD") && exchangeInstance?.requestType == ExchangeRequestType.EA_FROM_SAD}">
                <dt>${message(code: 'exchange.clearanceOfficeCode.label', default: 'Clearance Office Code')}:</dt>
                <dd>${exchangeInstance?.clearanceOfficeCode}</dd>
                <dt>${message(code: 'exchange.declarationNumber.label', default: 'Declaration No')}:</dt>
                <dd>${exchangeInstance?.declarationSerial}  ${exchangeInstance?.declarationNumber}</dd>
                <dt>${message(code: 'exchange.declarationDate.label', default: 'Declaration Date')}:</dt>
                <dd><joda:format value="${exchangeInstance?.declarationDate}" locale="" pattern="dd/MM/yyyy" style="" /></dd>
            </g:elseif>

        </dl>
       
    </div>

    <div class="modal-footer">
        <a id="okAjaxOper" class="btn btn-sm btn-default" data-dismiss="modal" > ${g.message(code: 'button.operation.ok', default: 'OK')}</a>
    </div>
</div>