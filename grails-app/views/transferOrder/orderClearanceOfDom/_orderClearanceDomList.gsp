<g:each in="${transferInstance.getAllOrderClearanceOfDoms()}" var="clearDom">
    <g:render template="/transferOrder/orderClearanceOfDom/orderClearanceDomRecord" model="[clearanceDom:clearDom, transferInstance:transferInstance]"/>
</g:each>