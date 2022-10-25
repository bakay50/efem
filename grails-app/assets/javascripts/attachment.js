var isEnable = true;
var onEdit = false
var addFields = ["docType", "docTypeName", "docRef", "docDate"]
var editFields = ["edDocType", "edDocTypeName", "edDocRef", "edDocDate"]

$(document).ready(function() {
    initAttDoc()
})

function addAttDoc() {

    var docTyp = $("#docType").val();
    var docTypeName = $("#docTypeName").val()
    var docRef = $("#docRef").val();
    var docDat = $("#docDate").val();
    var attNum = Number($("#attachments").find(".attRow").length + 1);
    var fileExt = $("#fileExtension").val();
    var conversationId = $("#conversationId").val();
    $("#addAttDoc").attr('disabled', 'disabled');

    if (isEnable == true) {
        isEnable = false;
        $.ajax({
            type: 'POST',
            data: {
                docType: docTyp,
                docTypeName: docTypeName,
                docRef: docRef,
                docDate: docDat,
                attNumber: attNum,
                fileExtension: fileExt,
                conversationId: conversationId
            },
            url: $("#addAttDocUrl").val(),
            success: function (data, textStatus) {
                if (data.error) {
                    $("#attDocListAlertInfo").empty();
                    $("#attDocListErrors").html(data.responseData);
                } else {
                    $("#attDocListBody").html(data.attachmentData);
                    displayAlertMessageBy($("#attachedDocAddedAlertInfoTemplate"))
                    clearErrors()
                    resetAll()
                }
                getTotalAttachmentCount();
            }
        });
    }
}

function editAttDoc(id) {
    onEdit = true;

    var docType = $("#docType_" + id).text();
    var docTypeName = $("#docTypeName_" + id).text();
    var docRef = $("#docRef_" + id).text();
    var docDate = $("#docDate_" + id).text();

    var editAttDocRowHtml = $("#editAttDocRowContent").clone(true);
    $("#attDocRow_" + id).replaceWith(editAttDocRowHtml);

    $("#edRank").val(id)
    $("#edDocType").val(docType);
    $("#edDocTypeName").val(docTypeName);
    $("#edDocRef").val(docRef);
    $("#edDocDate").val(docDate);

    $("#edDocDate").datepicker({
            dateFormat: "dd/mm/yy",
            maxDate: 0,
            changeMonth: true,
            changeYear: true
        });

    makeAutocomplete("edDocType", "HT_ATT", "code,description", editAttDocCodeSetter, "250");
    disableAddFields()
    setButtonStatus(editFields, "editUploadFile")
    initDocFieldsOnChange(editFields, "editUploadFile")
    onUploadDialogFireButtonClick("uploadDialog", "editUploadFile", openEditUploadDialog);

    $("#attDocListBody #updateAttDoc").on('click', function() {
        if (isEnable) {
            isEnable = false;

            $.ajax({
                type: 'POST',
                url: $("#updateAttDocURL").val(),
                data: {
                    docType: $("#edDocType").val(),
                    docTypeName: $("#edDocTypeName").val(),
                    docRef: $("#edDocRef").val(),
                    docDate: $("#edDocDate").val(),
                    rank: $("#edRank").val(),
                    conversationId: $("#conversationId").val(),
                },
                success: function (data, textStatus) {
                    if (data.error) {
                        $("#attDocListAlertInfo").empty();
                        $("#attDocListErrors").html(data.responseData);
                    } else {
                        $("#attDocListBody").html(data);
                        displayAlertMessageBy($("#attachedDocSaveAlertInfoTemplate"))
                        clearErrors()
                        resetAll()
                        enableAddFields()
                    }
                    isEnable = true;
                }
            });
        }
    })
}

function cancelEditAttDoc() {
    if (isEnable == true) {
        isEnable = false;
        $.ajax({
            type: 'POST',
            url: $("#cancelEditAttDocUrl").val(),
            success: function (data, textStatus) {
                $("#alertInfo").empty();
                $("#attDocListBody").html(data);
                resetAll();
                clearErrors()
                enableAddFields()
                isEnable = true;
            }
        });
    }
}

function removeAttDoc(id) {
    function doRemove() {
        if (isEnable == true) {
            isEnable = false;
            $.ajax({
                type: "POST",
                data: {
                    rank: id
                },
                url: $("#deleteAttDocUrl").val(),
                success: function (data, textStatus) {
                    if  (data.error) {
                        $("#alertInfo").empty();
                        $("#attDocListErrors").append(data.responseData)
                    } else {
                        resetAll()
                        displayAlertMessageBy($("#attachedDocRemovedAlertInfoTemplate"))
                        $("#attDocListBody").html(data);
                    }

                    isEnable = true;
                    getTotalAttachmentCount();
                }
            });
        }
    }

    openConfirmDialog(msg("attachments.delete.message"), doRemove);
}

function initAttUploadAjaxForm() {
    var uploadDialogObject = {
        uploadDialogId: "uploadDialog",
        fileInputId: "attDoc",

        onUploadCompleteFunction: function (xhr, data) {
            if(xhr.status === 601){
                displayAlertMessageBy($("#attachedDocErrorAlertInfoTemplate"));
            }
            onUploadComplete(xhr);
        },

        hideUploadDialogFunction: function () {
            isEnable = true
        }
    };

    var submitButtonObject = {
        uploadDialogId: "uploadDialog",
        submitButtonId: "startUpload",
        fileInputId: "attDoc",
        errorMessage: msg('default.file.extension.incorrect'),
        acceptedFormatsArrayInLowerCase: $("#attachmentAcceptedFormats").val().split(','),

        onClickFunction: function (extension) {
            $("#fileExtension").val(extension);
            $("#preAttNumber").val(Number($("#attachments .attRow").length + 1));
        }
    };

    onStartUploadButtonClick(submitButtonObject);

    if(document.querySelector('#addUploadFile')){
        onUploadDialogFireButtonClick("uploadDialog", "addUploadFile", openAddUploadDialog);
    }
    if(document.querySelector('#editUploadFile')){
        onUploadDialogFireButtonClick("uploadDialog", "editUploadFile", openEditUploadDialog);
    }

    initUploadDialog(uploadDialogObject);

    $("#addUploadFile").click(function() {
        $("#uploadDialog").modal({backdrop: 'static'})

    })
}

function openAddUploadDialog() {
    onEdit = false
    openUploadDialog("addUploadFile")
}

function openEditUploadDialog() {
    openUploadDialog("editUploadFile")
}

function openUploadDialog(elementUploadFile) {
    if (isEnable) {
        isEnable = false;
    }

    if (!$("#" + elementUploadFile).hasClass('disabled')) {
        onOpenDiaglog($("#uploadAndSaveMessage"))
    }
}

function onOpenDiaglog(element) {
    $("#alertInfo").empty();
    $("#uploadDialog #startUpload").val(element.val());
    $("#uploadDialog").modal({ backdrop: "static" });
}

function displayAlertMessageBy(elementTemplate) {
    $("#attDocListAlertInfo").empty();
    $("#attDocListAlertInfo").append(elementTemplate.html());
    $("#attDocListAlertInfo").css("display", "block")
    isEnable = true;
}

function onUploadComplete(serverResponse) {
    isEnable = true;
    $("#uploadDialog").modal('hide');
    if (serverResponse.responseText === "OK") {
        fireAttDocSaveOrAddActions();
    } else if (serverResponse.statusText === 'error') {
        $("#alertInfo").empty();

    }
}

function fireAttDocSaveOrAddActions() {
    if (onEdit) {
        $("#attDocListBody #updateAttDoc").click();
    } else {
        addAttDoc()
    }
}

function editAttDocCodeSetter(sourceId, item) {
    $("#edDocTypeName").val(item ? item['description'] : '');
}

function clearErrors() {
    $("#attDocListErrors").html("")
}

function initAttDoc() {
    initDocFieldsOnChange(addFields, "addUploadFile")
    initAttUploadAjaxForm()
}

function initDocFieldsOnChange(attDocFields, uploadDialogBtn) {
    $.each(attDocFields, function(idx, field) {
        setFieldFocusEvent(field, attDocFields, uploadDialogBtn)
    })
}

function setFieldFocusEvent(field, attDocFields, uploadDialogBtn) {
    $("#" + field).focusout(function() {
        setButtonStatus(attDocFields, uploadDialogBtn)
    })
}

function applyUploadButtonStatus(uploadDialogBtn) {
    if (onEdit) {
        setButtonStatus(editFields, uploadDialogBtn)
    } else {
        setButtonStatus(addFields, uploadDialogBtn)
    }
}

function setButtonStatus(fields, uploadDialogBtn) {
    setTimeout(function(){
        var state = true
        $.each(fields, function(idx, field) {
            if ($("#" + field).val().trim() === '') {
                state = false
            }
        })

        if (state) {
            $("#" + uploadDialogBtn).removeClass('disabled')
        } else {
            $("#" + uploadDialogBtn).addClass('disabled')
        }
    }, 100)
}

function resetAll() {
    resetFields()
    resetButtons()
}

function resetButtons() {
    applyUploadButtonStatus("addUploadFile")
    applyUploadButtonStatus("editUploadFile")
}

function resetFields() {
    resetAddFields()
    resetEditFields()
}

function resetAddFields() {
    $("#docType").val("");
    $("#docTypeName").val("");
    $("#docRef").val("");
    $("#docDate").val("");
}

function resetEditFields() {
    $("#edDocType").val("");
    $("#edDocTypeName").val("");
    $("#edDocRef").val("");
    $("#edDocDate").val("");
}

function disableAddFields() {
    $("#docType").attr('disabled','disabled');
    $("#docRef").attr('disabled','disabled');
    $("#docDate").attr('disabled','disabled');
    $("#addUploadFile").addClass('disabled');
}

function enableAddFields() {
    $("#docType").removeAttr('disabled','disabled');
    $("#docRef").removeAttr('disabled','disabled');
    $("#docDate").removeAttr('disabled','disabled');
    $("#addUploadFile").removeClass('disabled');
}

function getTotalAttachmentCount() {
    $("#totalAttachmentBadge").text($("#attDocListBody .attDocRow").length)
}