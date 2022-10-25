function setCurrencyCode() {
    var ref_currency_code = "REF_CURR"
    var autocompleteCurrSource = constructModelForAutocomplete(getLocalRef(ref_currency_code), "code,description")
    makeAutocompleteByModel('currencyCode', autocompleteCurrSource, labelSetter, '70')
}

function initDocument() {
    setCurrencyCode()
}

function importXMLCurrencyTransfer(e) {
    e.preventDefault();
    document.querySelector('#upload-form-file').click();
    document.getElementById("upload-form-file").onchange = function () {
        if (this.files.length !== 0) {
            uploadXMLFile(document.getElementById("upload-form"))
            $("#upload-form-file").replaceWith($("#upload-form-file").clone());
        }
    }
}

function exportXMLCurrencyTransfer(form) {
    let appForm = $('#' + form);
    window.location = $('#exportXmlURL').val() + "?" + appForm.serialize();
}

function uploadXMLFile() {
    let $form = $('#upload-form');
    let formData = new FormData();
    let inputFiles = $("#upload-form-file").get(0);
    const conversation = $('#conversationId').val();
    formData.append('userFile', inputFiles.files[0]);
    formData.append('conversationId', conversation);

    $.ajax(
        $('#importXmlURL').val(),
        {
            type: 'POST',
            processData: false,
            contentType: false,
            data: formData,
            success: function (data) {
                $(document.body).html(data)
                $("#ajax_progress_show").hide()
            }

        }
    );
    return false;
}

$(document).ready(function () {
    initDocument();
    customBeanLoadBankData('bankCode')
});
