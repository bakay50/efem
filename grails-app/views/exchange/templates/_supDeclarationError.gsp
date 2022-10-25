<g:hasErrors bean="${supDeclaration}">
    <bootstrap:div class="alert alert-danger errorContainer" style="clear: both">
        <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>x</button>
        <wf:beanErrors bean="${supDeclaration}" class="executionErrorMessages"/>
    </bootstrap:div>
</g:hasErrors>
