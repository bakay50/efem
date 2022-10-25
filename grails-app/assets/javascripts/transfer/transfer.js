
$(document).ready(function () {
    customBeanLoadBankData('bankCode');
    viewEa();
});
var lngLnk

$(".lang_link").bind('click', function (element) {
    if (isCreate() || isEdit() || isUpdate() || isVerify()) {
        event.preventDefault()
        lngLnk = element.currentTarget.href

        var chgLngConfirmDialog = $("#chgLngconfirmDialog");
        showChgLngConfirmDialog(chgLngConfirmDialog)

        var chgLngConfirmOper = chgLngConfirmDialog.find("#chgLngConfirmOper");
        chgLngConfirmOper.unbind('click').bind('click', proceedChgLng)

        var chgLngCancelOper = chgLngConfirmDialog.find("#chgLngCancelOper");
        chgLngCancelOper.unbind('click').bind('click', closeChgLng)
    }
});

function isFormChanged() {
    var formChanged = false

    $(":input:text").each(function(idx, name){
        if (name.id !== "xof" && $("#" + name.id).val() !== ''){
            formChanged =  true
        }
    });

    if ($("#attDocTable #attDocListBody tr").length > 0) {
        formChanged = true
    }

    return formChanged
}

function isCreate() {
    return location.href.includes('create')
}

function isEdit(){
    return location.href.includes('edit')
}

function isUpdate(){
    return location.href.includes('update')
}

function isVerify(){
    return location.href.includes('verify')
}


function showChgLngConfirmDialog(chgLngConfirmDialog) {
    chgLngConfirmDialog.removeClass('hide');
    chgLngConfirmDialog.modal({backdrop: 'static'});
}

function proceedChgLng() {
    location.href =  initChangeLanguageLink(lngLnk)
}

function initChangeLanguageLink(link) {
    var newLink;
    var previousUrl = window.location.href
    if (link.toString().indexOf("verify") >= 0) {
        var translatedUrl = previousUrl.substring(0, previousUrl.indexOf("&lang"))
        newLink =  (translatedUrl? translatedUrl  : previousUrl) +  '&' + link.substr(link.indexOf("lang") + 0)
    } else if (link.toString().indexOf("update") >= 0) {
        newLink = link.toString().replace("update?", 'edit/' + $("#instanceId").val() + "?op=" + $("#op").val() + '&');
    } else {
        newLink = link;
    }
    return newLink;
}
function closeChgLng() {
    var chgLngConfirmDialog = $("#chgLngconfirmDialog");
    chgLngConfirmDialog.modal('hide');
}

function importXMLTransferOrder(e) {
    e.preventDefault();
    document.querySelector('#upload-form-file').click();
    document.getElementById("upload-form-file").onchange = function () {
        if (this.files.length !== 0) {
            uploadXMLFile(document.getElementById("upload-form"))
            $("#upload-form-file").replaceWith($("#upload-form-file").clone());
        }
    }
}

function exportXMLTransferOrder(form) {
    let appForm = $('#' + form);
    window.location = $('#exportXmlURL').val() + "?" +appForm.serialize();
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


function viewEa() {
    $(document).on("click", 'a.js_viewEa', function (event) {
        event.preventDefault()
        const {reference} = event.target.parentNode.dataset
        const url = $('#retrieveClearanceEaIdUrl').val()
        const conversationId = $('#conversationId').val()
        if (reference) {
            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    conversationId: conversationId,
                    eaReference: reference
                },
                success: function (data) {
                    if (data && data.eaReference_id) {
                        const exchangeUrl = $('#retrieveExchangeUrl').val()
                        const redirect = window.open(exchangeUrl + "/" + data.eaReference_id, "_blank")
                        redirect.focus()
                        return false
                    } else {
                        alert(msg("transfer.retrieve.exchange.message"))
                    }
                }
            })
        } else {
            alert(msg("client.view.button.transfer.confirm.message"))
        }

    })
}
