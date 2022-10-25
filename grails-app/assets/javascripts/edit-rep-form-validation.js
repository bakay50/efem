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
    var confirmMessage = msg('exchange.utilsConfirmThisAppMessage');
    return $("#_confirmMessage").val() + ' ' + opName + ' ' + confirmMessage;
}

function initEditRepFormValidation() {
    var $form = $("#appMainForm");

    $.validator.setDefaults({
        errorClass: 'form_error',
        errorElement: 'label'
    });

    $.extend(jQuery.validator.messages, {
        required: msg("exchange.required"),
        maxlength: msg("exchange.incorrectFormat"),
        number: xmlExtensionAlertMsg
    });

    $form.validate({
        onkeyup: false,
        onchange: true,
        submitHandler: function (form) {
            if ($('#appMainForm').valid()) {
                form.submit();
            }
        },
        ignore: [],
        rules: {},
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
        },
        errorPlacement: function (error, element) {
            var fieldName = $(element).attr("name")
            if (fieldName === "amountMentionedCurrency") {
                error.insertAfter("#xof")
            } else if (fieldName === "currencyCode") {
                error.insertAfter("#currencyRate")
            } else {
                error.insertAfter(element);
            }
        }
    });

    var operationButtons = $form.find("[id^='operation'][class~='btn']");
    operationButtons.mousedown(function (e) {
        $form.validate();
        var commitOperation = $(this).attr('name');
        var operationValue = $(this).attr('value');
        updateMandatoryFieldsExecution(commitOperation)
        executeOperation(commitOperation, operationValue);

        openConfirmDialog(createConfirmationMessage(operationValue), function () {
            $('#submitCurrentOperation').click()
        }, function () {
        }, function () {
        });
    });
}

function updateMandatoryFieldsExecution(operation) {
    if (operation == "operationCONFIRM" || operation == "operationCONFIRM_DECLARED" || operation == "operationDECLARE") {
        let isBankAgent = $('#isBankAgent').val();
        if (isBankAgent == "true") {
            var fieldMandatoryBankAgent = ["executionRef", "executionDate"]
            $.each(fieldMandatoryBankAgent, function (index, value) {
                $('#' + value.trim()).prop("required", true);
                $('#' + value.trim()).addClass('mandatory')
            });
        }
    }
}

function verifyDocument() {
    var $form = $("#appMainForm");
    console.log($("#verifyURL").val())
    var dataToSend = $("#appMainForm :input").serialize()
    if ($form.valid()) {
        $.ajax({
            type: "POST",
            data: dataToSend,
            url: $("#verifyURL").val(),
            success: function (data, textStatus) {
                $(document.body).html(data);
                initDocument();
            },
            complete: function (jqXHR, textStatus) {

            }
        });
    }
}

function handleCallBack(result) {
    isXofExceed = result.isExceed
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

    var exchangeError = $(".errorContainer .exchangeErrorMessages");

    $.each(exchangeError, function (i) {
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


function initDocument() {
    initEditRepFormValidation();
    focusOnErrorFields();
    loadAutoCompleteDataFromUserProperty();
    updateNatureFunds();
    setupAutocomplete()
}


function updateClearancePanel() {
    var conversationId = $("#conversationId").val();
    $.ajax({
        url: $("#removeEcUrl").val(),
        type: "GET",
        cache: false,
        headers: {
            Accept: "application/json; text/javascript",
            "Content-Type": "application/json; text/javascript"
        },
        data: {
            conversationId: conversationId
        },
        success: function (data) {
            if (data.error == 'error') {
                removeAllClearanceErrors();
                $("#clearanceListError").html(data.responseData);
                updateNatureFunds();
            } else {
                removeClearanceErrors();
                removeAllClearanceErrors();
                $("#clearanceDomListBody").html(data.template);
                updateNatureFunds();
            }
        }
    });
}

function updateNatureFunds() {
    var selectedvalue = $('select#natureOfFund').val();
    var repatStatus = $('#repatStatus').val();
    if (selectedvalue) {
        if ((selectedvalue == "Pre-financing" || selectedvalue == "Préfinancement") && repatStatus.toUpperCase() != "CONFIRMED" && repatStatus.toUpperCase() != "CEDED") {
            initClearanceDomPerNature();
        } else {
            resetClearanceDomPerNature();
        }
    }
}

var clearancesOfDomBtn = ["addItemCategory"]
var clearancesOfDomFields = ["ecReference", "repatriatedAmtInCurr"]

function initClearanceDomPerNature() {
    $.each(clearancesOfDomFields, function () {
        var elentbts = $("#" + this);
        elentbts.attr('readonly', 'readonly');
    })

    $.each(clearancesOfDomBtn, function () {
        var elentbts = $("#" + this);
        elentbts.addClass("disabled");
        elentbts.attr("onclick", "return false");
    })
}

function resetClearanceDomPerNature() {
    $.each(clearancesOfDomFields, function () {
        var element = $("#" + this);
        element.val("");
        element.removeAttr('readonly', 'readonly');
    });

    $.each(clearancesOfDomBtn, function () {
        var element = $("#" + this);
        element.removeClass("disabled");
        element.attr("onclick", "addClearenceOfDom()");
    })
}

$(document).ready(function () {
    initDocument();
});



