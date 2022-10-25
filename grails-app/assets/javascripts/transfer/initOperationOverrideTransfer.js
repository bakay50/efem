/**
 * Created by a.goya on 5/17/2016.
 */
/**
 *overrides function in wf-utils plugin because the original does not delete the texts before applying new one.
 * Delete if the bug is fixed.
 */

function initOperationDialog(){

    var allOperations = $("#operationButtonsContainer").find('input[type=submit]');
    var confirmDialog = $("#confirmDialog");
    var form = $("#" + confirmDialog.find("#formName").val());
    var buttonsToDisable = form.find(".btn");
    var noValidationOperations = form.find(".j_noValidation");
    var noValidationOperationIds = [];
    var confirmOperations = form.find(".j_confirm");
    var confirmOperationIds = [];
    var doubleConfirmOperations = form.find(".j_doubleConfirm");
    var doubleConfirmOperationIds = [];

    $.each(noValidationOperations, function () {
        noValidationOperationIds.push($(this).attr('id'));
    });
    $.each(confirmOperations, function () {
        confirmOperationIds.push($(this).attr('id'));
    });

    $.each(doubleConfirmOperations, function () {
        doubleConfirmOperationIds.push($(this).attr('id'));
    });

    allOperations.unbind("click");
    allOperations.click(function (e) {
        if ($.inArray($(this).attr('id'), noValidationOperationIds) == -1) {
            e.preventDefault();

            //fix for bug in plugins
            $("#confirmMessage").text(I18N['client.commitConfirmMessage']);
            $("#operationName").text("");
            $("#thisAppMessage").text("");
            $("#doubleConfirmMessage").text("");

            try {
                beforeValidateAction();
            } catch (err) {
            }

            if (form.valid()) {
                doOperation(buttonsToDisable, form, confirmOperationIds, doubleConfirmOperationIds, $(this));
            }
        } else {
            e.preventDefault();
            form.validate().cancelSubmit = true;
            doOperation(buttonsToDisable, form, confirmOperationIds, doubleConfirmOperationIds, $(this));
        }
    });
}


function doOperation(buttonsToDisable, form, confirmOperationIds, doubleConfirmOperationIds, thisElem) {
    unbindEvents();
    var confirmDialog = $("#confirmDialog");

    $("#confirmOper").click(function () {
        submitFormAndCloseDialog(form);
    });

    $("#cancelOper").click(function () {
        hideDialog(buttonsToDisable);
    });

    confirmDialog.find(".close").click(function () {
        hideDialog(buttonsToDisable);
    });

    confirmDialog.bind("keydown", function (event) {
        if (event.which === 13) {
            submitFormAndCloseDialog(form);
        }
    });

    var value = thisElem.val();
    var name = thisElem.attr('name');
    setOperationParams(value, name);
    disableButtons(buttonsToDisable);

    var isDoubleConfirmOperation = $.inArray(thisElem.attr('id'), doubleConfirmOperationIds) > -1;
    if ($.inArray(thisElem.attr('id'), confirmOperationIds) > -1 || isDoubleConfirmOperation) {
        addDoubleConfirmCheckbox(isDoubleConfirmOperation);         // add a checkbox to ask for confirmation if operation requires that

        var operationMessage = $("#confirm_message_" + thisElem.attr('id')).val();
        if (!operationMessage) {
            operationMessage = thisElem.val();
        }
        var thisAppMessage = $("#confirm_thisAppMessage_" + thisElem.attr('id')).val();

        confirmDialog.find("#operationName").text(operationMessage);

        if (!thisAppMessage) {
            confirmDialog.find("#thisAppMessage").text(I18N['client.utilsConfirmThisAppMessage']);
        }
        else if (thisAppMessage == "emptyMessage") {
            confirmDialog.find("#thisAppMessage").text("");
        }
        else {
            confirmDialog.find("#thisAppMessage").text(thisAppMessage);
        }

        if(hasConfirmDisclaimer(thisElem)) {
            addConfirmDisclaimer(confirmDialog);
        }

        confirmDialog.modal({
            backdrop: "static",
            keyboard: false
        });
    } else {
        form.submit();
    }

    $("#confirmCheckbox").change(function () {
        toggleYesButton(confirmDialog);
    });

    $("#confirmDialog").focus();
}
