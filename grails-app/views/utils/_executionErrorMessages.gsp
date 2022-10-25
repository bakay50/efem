<bootstrap:alert type="error" class="wf-alert-scrolable error alert-block alert-error errorContainer">
    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>x</button>
    <g:hasErrors bean="${executionInstance}">
        <wf:beanErrors bean="${executionInstance}" class="executionErrorMessages"/>
    </g:hasErrors>
</bootstrap:alert>