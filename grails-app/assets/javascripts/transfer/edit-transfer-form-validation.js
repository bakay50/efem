var FIELDS_MANDATORY_REQUEST
let isBankAgent = $('#authAvailable').val();
if(isBankAgent === "false"){
    FIELDS_MANDATORY_REQUEST = ["charges", "byCreditOfAccntOfCorsp", "bankAccntNoCredit", "bankAccntNoDebited", "nameOfAccntHoldCredit", "bankCode", "currencyPayCode"]
}else{
    FIELDS_MANDATORY_REQUEST = ["charges", "byCreditOfAccntOfCorsp", "bankAccntNoCredit", "bankAccntNoDebited", "nameOfAccntHoldCredit", "bankCode", "currencyPayCode","executionRef", "executionDate"]
}
var chargesDefaultValue = 'All charges to be paid by the ordering party'
var queryNotificationButtons = ["operationCANCEL_VALIDATED","operationCANCEL_QUERIED","operationQUERY_REQUESTED"]
var queryNotificationFields =  ['comments']
const operationCancelQueried = 'operationCANCEL_QUERIED'

function executeOperation(commitOperation, operationValue) {
    var form = $("#appTrMainForm");
    $("#tempOper").val(operationValue)
    $('<input />').attr('type', 'hidden')
        .attr('name', commitOperation)
        .attr('value', operationValue)
        .attr('id', commitOperation)
        .appendTo(form)
}

function createConfirmationMessage() {
    return $("#_confirmMessage").val();
}

function initEditTransferFormValidation() {
    var $form = $("#appTrMainForm");

    $.validator.setDefaults({
        errorClass: 'form_error',
        errorElement: 'label'
    });

    $.extend(jQuery.validator.messages, {
        required: msg('exchange.required'),
        maxlength: msg('exchange.incorrectFormat'),
        number: msg('exchange.incorrectFormat')
    });

    $form.validate({
        onkeyup: false,
        onchange: true,
        submitHandler: function (form) {
            if ($form.valid()) {
                form.submit();
            }
        },
        ignore: [],
        rules: {
            importerCode: {
                maxlength: 17
            },
            countryBenefBankCode: {
                maxlength: 2
            },
            destinationBank: {
                maxlength: 100
            },
            bankAccntNoCredit: {
                maxlength: 34
            },
            nameOfAccntHoldCredit: {
                maxlength: 100
            },
            bankCode: {
                maxlength: 5
            },
            currencyPayCode: {
                maxlength: 3
            },
            charges: {
                maxlength: 65
            },
            executionRef: {
                maxlength: 35
            }
        },
        messages: {
            comments: {
                required: msg('transfer.queryMandatoryMessage')
            }
        },
        invalidHandler: function (form, validator) {
            var elements = validator.invalidElements();
            if (elements) {
                var firstElement$ = $(elements[0]);
                if (firstElement$.is(':hidden')) {
                    focusOnFieldTab(firstElement$.attr('id'), function (targetFields) {
                        setTimeout(function () {
                            targetFields.focus();
                        }, 200)
                    })
                } else {
                    setTimeout(function () {
                        firstElement$.focus()
                    }, 200)
                }
            }
        }
    });

    var operationButtons = $form.find("[id^='operation'][class~='btn']");
    operationButtons.mousedown(function (e) {
        var commitOperation = $(this).attr('name');
        var operationValue = $(this).attr('value');
        updateMandatoryFields($('#listOfRequiredFieldsOfTransfer').val(), commitOperation)
        $form.validate();
        executeOperation(commitOperation, operationValue);
        var confirmMessage = msg('transfer.utilsConfirmThisAppMessage');
        var title = msg('transfer.titleModal')
        openConfirmDialog(createConfirmationMessage(), operationValue, confirmMessage, title, function () {
            $('#submitCurrentOperation').click()
        }, function () {
        }, function () {
        });
    });
}

function openConfirmDialog(confirmMsg, operationName, thisAppMessage, title, yesFunc, noFunc, closeFunc) {
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
    $("#confirmMessage").text(confirmMsg);
    $("#operationName").text(operationName);
    $("#thisAppMessage").text(thisAppMessage);
    $(".modal-title").text(title)

    $("#confirmDialog").modal({
        backdrop: "static",
        keyboard: false
    });

    $("#confirmDialog").focus();
}

function focusOnFieldTab(fieldId, func) {

    var tabId = '#formContent a[href="#header"]';
    var attDocs = 'attachedDocs'

    if (fieldId) {
        var errorId = '#' + fieldId;
        var error = $(errorId);
        if (error.length === 0) {
            var crit = '.error [id^="' + fieldId + '"]';
            error = $(crit).first()
        }
        var list = $(error.parents('div.tab-pane').get().reverse());

        list.each(function () {
            var content = $(this).closest('div.tab-content');
            var ul = content.parent().find('ul.nav-tabs');
            tabId = 'ul.nav-tabs li' + ' a[href="#' + $(this).attr('id') + '"]';
            func(error);
        });

        if (fieldId === attDocs) {
            tabId = 'ul.nav-tabs li a[href="#attachements"]'
        }

        $(tabId).tab('show');
    }
}

function focusOnErrorFields() {

    var transferError = $(".errorContainer .exchangeErrorMessages");

    $.each(transferError, function (i) {
        var $error = $(this);
        var elementId = $error.attr('errorElementId');
        $error.click(function () {
            focusOnFieldTab(elementId, function (targetFields) {
                setTimeout(function () {
                    targetFields.focus();
                }, 200);
            });
        });
        removeErrorMessageOnChange($error, $("#" + elementId))
    });
}

function addMandatory() {
    var fieldsFromConfig = $('#listOfRequiredFieldsOfTransfer').val();
    var listOfRequiredFields = []
    if (!(fieldsFromConfig == '' || fieldsFromConfig == null)) {
        listOfRequiredFields = fieldsFromConfig.toString().replace('[', '').replace(']', '').split(',')
        $.each(listOfRequiredFields, function (index, value) {
            $('#' + value.trim()).addClass('mandatory')
        });
    }
}

function updateMandatoryFields(fieldsFromConfig, operation) {
    var listOfRequiredFields = []
    const mandatoryOperations = ["operationREQUEST", "operationREQUEST_STORED"]
    if (!(fieldsFromConfig == '' || fieldsFromConfig == null)) {
        listOfRequiredFields = fieldsFromConfig.toString().replace('[', '').replace(']', '').split(',')
        initMandatoryFieldsForTransfer();
        if (mandatoryOperations.indexOf(operation) !== -1) {
            var newListOfRequiredFields = listOfRequiredFields.concat(FIELDS_MANDATORY_REQUEST)
            addMandatoryOnFields(newListOfRequiredFields)
        } else if(jQuery.inArray(operation,queryNotificationButtons) >= 0 && operation != operationCancelQueried){
            addMandatoryOnFields(queryNotificationFields)
        }
        else {
            addMandatoryOnFields(listOfRequiredFields)
        }
    }
}

function initMandatoryFieldsForTransfer() {
    var form_transfer = $("#appTrMainForm");
    var fieldsTransferToUpdate = form_transfer.find(".wfinput");
    $.each(fieldsTransferToUpdate, function () {
        $(this).prop("required", false);
        $(this).removeClass('mandatory');
    });
}

function addMandatoryOnFields(listOfRequiredFields) {
    $.each(listOfRequiredFields, function (index, value) {
        $('#' + value.trim()).prop("required", true);
        $('#' + value.trim()).addClass('mandatory')
    });
}

function initByCreditOfAccntOfCorsp() {
    var byCreditOfAccntOfCorsp = $('select#byCreditOfAccntOfCorsp').val();
    if (!byCreditOfAccntOfCorsp) {
        $("#byCreditOfAccntOfCorsp option[value='FRANCE']").prop('selected', true);
    }
}

function initCharges() {
    var charges = $('select#charges').val();
    if (!charges) {
        $('#charges').val(chargesDefaultValue)
    }
}


$(document).ready(function () {
    initDocument();
});

function initDocument() {
    initByCreditOfAccntOfCorsp();
    initCharges();
    addMandatory();
    initEditTransferFormValidation();
    focusOnErrorFields();
    loadTransferAutoCompleteDataFromUserProperty();
    setupAutocomplete()
}
