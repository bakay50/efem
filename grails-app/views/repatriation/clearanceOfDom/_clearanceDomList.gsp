<g:each in="${repatriationInstance?.getAllClearanceOfDom()}" var="clearDom">
    <g:render template="/repatriation/clearanceOfDom/clearanceDomRecord" model="[clearanceDom:clearDom, repatriationInstance:repatriationInstance]"/>
</g:each>