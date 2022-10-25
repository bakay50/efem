<g:hasErrors bean="${attDoc}">
    <bootstrap:div class="alert alert-danger errorContainer" style="clear: both">
        <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>x</button>

    <wf:beanErrors bean="${attDoc}" class="attDocInnerErrorMessages"/>
    </bootstrap:div>
</g:hasErrors>

<bootstrap:div id="attachedDocErrorAlertInfoTemplate" style="display: none; clear: both">
    <wf:alert class="alert-danger">${message(code: 'attachedDoc.error.message', default: 'File exceeds the allowable size (2 MB).')}</wf:alert>
</bootstrap:div>