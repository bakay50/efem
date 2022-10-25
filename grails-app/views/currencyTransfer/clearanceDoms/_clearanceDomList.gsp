<g:each in="${currencyTransferInstance?.clearanceDomiciliations}" var="clearDom">
    <g:render template="/currencyTransfer/clearanceDoms/clearanceDomRecord" model="[clearanceDom:clearDom, currencyTransferInstance:currencyTransferInstance]"/>
</g:each>