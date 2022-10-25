var isXofExceed = false

function executeOperation(commitOperation, operationValue) {
    var form = $("#appMainForm");

    $('<input />').attr('type', 'hidden')
        .attr('name', commitOperation)
        .attr('value', operationValue)
        .attr('id', commitOperation)
        .appendTo(form)

}

function createConfirmationMessage(opName) {
    if (opName == "Transfer" || opName == "Transférer"){
        var message = "";
        if(getNumericValue($("#transferRate").val()) < 80)
            message = msg("currencyTransfer.warningTransferRate",$("#transferRate").val()) + "<br\>";
        return message + $("#_confirmMessage").val() + ' ' + msg('transferCurrency.utilsConfirmSubmit');
    }
    var confirmMessage = msg('exchange.utilsConfirmThisAppMessage');
    return $("#_confirmMessage").val() + ' ' + opName + ' ' + confirmMessage;
}

function initEditCurTrsferFormValidation() {
    var $form = $("#appMainForm");
    var $btnContainer = $form.find("#operationButtonsSection")
    var operationButtons = $btnContainer.find("input")
    operationButtons.mousedown(function (e) {
        var commitOperation = $(this).attr('name');
        var operationValue = $(this).attr('value');
        executeOperation(commitOperation, operationValue);
        openConfirmDialog(createConfirmationMessage(operationValue), function () {
            $('#submitCurrentOperation').click()
        }, function () {
        }, function () {
        });
    });
}

function openConfirmDialog(confirmMsg, yesFunc, noFunc, closeFunc) {
    unbindEvents();

    $("#confirmOper").click(function () {
        callFunctionAndCloseDialog(yesFunc);
    });

    $("#cancelOper").click(function () {
        callFunctionAndCloseDialog(noFunc);
    });

    $("#confirmDialog").find(".close").click(function () {
        callFunctionAndCloseDialog(closeFunc);
    });

    $("#confirmDialog").bind("keydown", function (event) {
        if (event.which === 13) {
            callFunctionAndCloseDialog(yesFunc);
        }
    });

    storeOriginalMessage();
    $("#confirmMessage").html(confirmMsg);
    $("#operationName").text("");
    $("#thisAppMessage").text("");

    $("#confirmDialog").modal({
        backdrop: "static",
        keyboard: false
    });

    $("#confirmDialog").focus();
}

function loadRepatriationFields(state = true) {
    const fieldRepatriation = ["repatriationNo", "repatriationDate"]
    $.each(fieldRepatriation, function () {
        if (typeof $("#" + this).val() != "undefined") {
        if (state == true) {
                $("#" + this).removeAttr("readonly", "readonly");
                $("#" + this).prop("required", true);
                $("#" + this).addClass("mandatory");
        } else {
            $("#" + this).attr("readonly", "readonly");
            $("#" + this).removeProp("required", true);
            $("#" + this).removeClass("mandatory");
        }
    }
    });
}

function manageFieldCurrency(state = false) {
    let fieldToManage = ["currencyTransferDate", "addItemCategory", "docType", "docRef", "docDate"]
        $.each(fieldToManage, function () {
            if (typeof $("#" + this).val() != "undefined") {
                if(state == false){
                    $("#" + this).attr("readonly", "readonly");
                    $("#" + this).attr("disabled", "disabled");
                }else{
                    $("#" + this).removeAttr("readonly", "readonly");
                    $("#" + this).removeAttr("disabled", "disabled");
                }
            }
        });
    const operationList = ["CANCEL_TRANSFERRED", "UPDATE_TRANSFERRED"]
    if(operationList.indexOf($("#op").val()) > -1){
        $("#currencyTransferDate").attr("readonly", "readonly")
    }
}

function validateRepatriationFields(){
    let repatriationNo = $("#repatriationNo");
    let repatriationDate = $("#repatriationDate");
    return repatriationNo.valid() && repatriationDate.valid() && !(repatriationNo.val() === "" || repatriationDate === "")
}

function loadRepatriation(){
    if (validateRepatriationFields()) {
        $('#loadRepatriationConfirmDialog #loadRepatriationConfirmOper').unbind('click').bind('click', function () {
            loadRepatriationDetails();
            hideLoadRepatriationConfirmDialog()
        });
        showLoadRepatriationConfirmDialog()
    }
}

function showLoadRepatriationConfirmDialog() {
    $('#loadRepatriation' + 'ConfirmDialog').modal({
        backdrop: 'static'
    });
}

function hideLoadRepatriationConfirmDialog() {
    $('#loadRepatriation' + 'ConfirmDialog').modal('hide');
}

function loadRepatriationDetails() {
    let repatriationNo = $("#repatriationNo").val().trim();
    let repatriationDate = $("#repatriationDate").val();
    $.ajax(
        $("#loadRepatriationUrl").val(),
        {
            method: 'POST',
            data: {
                repatriationNo: repatriationNo,
                repatriationDate: repatriationDate
            },
            success: function(data) {
                $(document.body).html(data);
            }
        }
    );
    return false;
}

function restartRepatriation() {
    $.ajax(
        $("#restartRepatriationUrl").val(),
        {
            method: 'GET',
            success: function(data) {
                $(document.body).html(data);
            }
        }
    );
}


function initFields(){
    if($("#idRepatriation").val() || $("#loadedRepatriation").val()){
        manageFieldCurrency(true);
        loadRepatriationFields(false)
    }else{
        manageFieldCurrency(false);
        loadRepatriationFields(true)
    }
}

$(document).ready(function () {
    initEditCurTrsferFormValidation();
    initFields()
    setupAutocomplete()
});





