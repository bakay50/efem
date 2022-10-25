<g:if test="${flash.message}">
    <bootstrap:alert class="alert-info">
        ${flash.message}
    </bootstrap:alert>
</g:if>

<g:if test="${hasDocErrors}">
    <bootstrap:alert type="error" class="wf-alert-scrolable error alert-block alert-danger errorContainer">
        <g:hasErrors bean="${domainInstance}">
            <wf:beanErrors bean="${domainInstance}" class="exchangeErrorMessages"/>
        </g:hasErrors>

        <g:if test="${flash.errorMessage}">
            ${flash.errorMessage}
        </g:if>
    </bootstrap:alert>
</g:if>
