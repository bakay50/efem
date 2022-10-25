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

function initEditFormValidation() {
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
        rules: {
            countryOfExportCode: {
                required: true
            },
            dateOfBoarding: {
                required: false
            },
            registrationNumberBank: {
                maxlength: 35
            },
            domiciliationNumber: {
                maxlength: 35
            },
            provenanceDestinationBank: {
                maxlength: 100
            },
            bankAccountNocreditedDebited: {
                maxlength: 34
            },
            accountNumberBeneficiary: {
                maxlength: 34
            },
            comments: {
                maxlength: 1024
            },
            executionReference: {
                maxlength: 35
            },
            emailEC :{
                email:true
            },
            emailEA :{
                email:true
            },
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
        },
        errorPlacement: function (error, element) {
            var fieldName = $(element).attr("name")
            if (fieldName === "amountMentionedCurrency") {
                error.insertAfter("#xof")
            } else if (fieldName === "finalAmountInDevise") {
                error.insertAfter("#xof1")
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
        var isApproveRequested = $("#operationAPPROVE_REQUESTED").is(":visible")
        var basedOn = $("#basedOn").val()
        var maxAmountInXofCurrency = getNumericValue($("#maxAmountInXofCurrency").val());
        var amountNationalCurrency = getNumericValue($("#amountNationalCurrency").val());
        var currencyXOF = $("#currencyXOF").val();
        var currencyPayCode = $("#currencyPayCode").val();
        var isBankAgent = ($("#isBankAgent").val() === 'true')
        var requestType = $('#requestType').val()
        const allowFinexApprovalEA = ($("#js_allow_finex_approval").val() === "true")
        executeOperation(commitOperation, operationValue);
        if (commitOperation === 'operationAPPROVE_REQUESTED'
            && requestType === 'EA'
            && basedOn === 'SAD'
            && isBankAgent
            && amountNationalCurrency > maxAmountInXofCurrency
            && currencyXOF !== currencyPayCode
            && allowFinexApprovalEA
        ) {
            openFinexConfirmation()
        } else {
            openConfirmDialog(createConfirmationMessage(operationValue), function () {
                removeAttributDisabled()
                $('#submitCurrentOperation').click()
            }, function () {

            }, function () {
            });
        }
    });
}

function verifyDocument() {
    var $form = $("#appMainForm");

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

function isExceedMaxAmountInXofCurrency() {
    var url = $("#checkMaxXofUrl").val();
    $.ajax({
        type: "GET",
        async: false,
        url: url,
        data: {
            conversationId: $("#conversationId").val()
        },
        success: function (data) {
            handleCallBack(data)
        }
    });
    return isXofExceed
}

function handleCallBack(result) {
    isXofExceed = result.isExceed
}

function openFinexConfirmation() {
    const finexModal = $("#finexConfirmDialog");
    const oldDialogBody = $("#confirmDialog .modal-body").clone();
    const oldDialogFooter = $("#confirmDialog .modal-footer").clone(true);
    const finexDialogBody = $('#finexConfirmDialog .modal-body').clone();
    const finexDialogFooter = $('#finexConfirmDialog .modal-footer').clone(true);

    const confirmOperYes = finexModal.find('#confirmOperYes')
    const confirmOperNo = finexModal.find('#confirmOperNo')
    const confirmOperCancel = finexModal.find('#cancelOper')
    oldDialogBody.replaceWith(finexDialogBody)
    oldDialogFooter.replaceWith(finexDialogFooter)
    finexModal.modal({backdrop: 'static'})

    confirmOperYes.off('click').on('click', function () {
        finexModal.modal('hide')
        $("#finexApproval").val("true")
        $('#submitCurrentOperation').click()
    })

    confirmOperNo.off('click').on('click', function () {
        finexModal.modal('hide')
        $("#finexApproval").val("false")
        $('#submitCurrentOperation').click()
    })

    confirmOperCancel.off('click').on('click', function () {
        finexModal.modal('hide')
    })
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

function isFinalInvoice(element, value) {
    let checked = $(element).prop('checked')
    let finalAmountInDevise = $('#finalAmountInDevise')
    let finalAmount = $('#finalAmount')
    let amountFinalInLetter = $('#digitoperations')
    if (checked) {
        $(element).val(true)
        finalAmountInDevise.val('')
        finalAmount.val('')
        finalAmountInDevise.removeAttr('readOnly')
        finalAmount.removeAttr('readOnly')
        amountFinalInLetter.html('')
        RemoveRuleFor('finalAmountInDevise')
        AddRuleFor('finalAmountInDevise')
    } else {
        $(element).val(false)
        finalAmountInDevise.val($('#amountMentionedCurrency').val())
        finalAmount.val($('#amountNationalCurrency').val())
        finalAmountInDevise.attr('readOnly', 'readOnly')
        finalAmount.attr('readOnly', 'readOnly')
        amountFinalInLetter.html($('#digitoperation').html())
        RemoveRuleFor('finalAmountInDevise')
        $("#balanceAs").val(finalAmountInDevise.val())
        formatElementsToString($('#balanceAs'))
    }
}

function AddRuleFor(element) {
    $('#appMainForm').validate();
    let $element = $(`#${element}`)
    $element.rules('add', {required: true})
    $("#appMainForm").validate().element("[id=" + element + "]");
}

function RemoveRuleFor(element) {
    $('#appMainForm').validate();
    let $element = $(`#${element}`)
    $element.removeClass('form_error')
    $element.rules('remove')
    $("#appMainForm").validate().element("[id=" + element + "]");
}

function doCalculateFinalAmount() {
    $("#finalAmountInDevise").on("change", function () {
        const requestType = $('#requestType').val()
        if (requestType === 'EC') {
            const currencyRate = $('#currencyRate').val()
            const finalAmountInDevise = $('#finalAmountInDevise').val()
            const amount = getNumericValue(currencyRate) * getNumericValue(finalAmountInDevise)
            const finalAmount = $('#finalAmount')
            finalAmount.val(amount)
            formatElementsToString(finalAmount)
            if (finalAmountInDevise.length > 0) {
                if (amount) {
                    let url = $("#convertDigitUrl").val()
                    $.ajax({
                        type: "GET",
                        url: url,
                        data: {
                            conversationId: $("#conversationId").val(),
                            amount: amount,
                            locale: $("#locale").val()
                        },
                        success: function (data) {
                            let amountFinalInLetter
                            if (data) {
                                amountFinalInLetter = data.convert_result.capitalize() + ' Francs CFA'
                            } else {
                                amountFinalInLetter = ''
                            }
                            $('#digitoperations').html(amountFinalInLetter)
                            $('#amountFinalInLetter').val(amountFinalInLetter)
                            $("#balanceAs").val(finalAmountInDevise)
                        }
                    })
                }
            }
        }
    })
}

function handleCustomsEcFieldsMandatory() {
    const documentStatus = $('#ecStatus').val()
    const requestType = $('#requestType').val()
    const operationName = $('#op').val()
    if (requestType.toString().toUpperCase() ==='EC' && !operationName.toString().includes("CANCEL") && (!documentStatus || documentStatus.toString().toUpperCase() === 'Requested'.toUpperCase() || documentStatus.toString().toUpperCase() === 'Queried'.toUpperCase())) {
        const mandatoryFields = ["importerNameAddress", "exporterCode"]
        $.each(mandatoryFields, function () {
            $('#' +this).attr('required', true).addClass('mandatory')
        })
    }
}

function initDocument() {
    initEditFormValidation();
    editFormRecipientsFundsFields()
    focusOnErrorFields();
    loadAutoCompleteDataFromUserProperty();
    setBankCodeDropdown();
    handleCustomsEcFieldsMandatory();
    doCalculateFinalAmount();
    enableECDeclarationFields()
    setupAutocomplete()
    handleRegistrationNumberBankField()
}

function handleRegistrationNumberBankField(){
    const operationName = $('#op').val()
    const isBankAgent = ($("#isBankAgent").val() === 'true')
    if(isBankAgent && ((requestType === 'EC' && operationName == "DOMICILIATE") || (requestType === 'EA' && operationName == "APPROVE_REQUESTED"))){
        $("#registrationNumberBank").addClass("mandatory")
        $("#registrationNumberBank").attr("required", 'true')
        $("#registrationNumberBank").removeAttr("readonly")
    }
}

$(document).ready(function () {
    initDocument();
});

function enableECDeclarationFields() {
    if (requestType === 'EC') {
        const ecStatus = $('#ecStatus').val()
        const isBankAgent = ($("#isBankAgent").val() === 'true')
        const isTrader = ($("#isTrader").val() === 'true')
        const isDeclarant = ($("#isDeclarant").val() === 'true')
        const clearanceOfficeCode = $('#clearanceOfficeCode')
        const declarationSerial = $('#declarationSerial')
        const declarationNumber = $('#declarationNumber')
        const declarationDate = $('#declarationDate')
        const sadIdValue = $('#sadInstanceId').val()
        const hasError = $('.errorContainer').is(':visible')
        if (clearanceOfficeCode.val() !== undefined || declarationSerial.val() !== undefined || declarationNumber.val() !== undefined) {
            const enableDeclarationsFields = clearanceOfficeCode.val().length > 0 && declarationSerial.val().length > 0 && declarationNumber.val().length > 0 && declarationDate.val().length > 0
            if ((ecStatus === 'Approved' || ecStatus === 'Executed') && (isTrader || isDeclarant) && !enableDeclarationsFields) {
                clearanceOfficeCode.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationSerial.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationNumber.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationDate.removeAttr('disabled', 'disabled').removeAttr('readonly', 'readonly')
            } else if ((ecStatus === 'Approved' || ecStatus === 'Executed') && (isTrader || isDeclarant) && hasError && enableDeclarationsFields && sadIdValue.length === 0) {
                clearanceOfficeCode.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationSerial.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationNumber.removeAttr('readonly', 'readonly').removeAttr('disabled', 'disabled')
                declarationDate.removeAttr('disabled', 'disabled').removeAttr('readonly', 'readonly')
            }
            else if (isBankAgent) {
                clearanceOfficeCode.attr('readonly', 'readonly')
                declarationSerial.attr('readonly', 'readonly')
                declarationNumber.attr('readonly', 'readonly')
                declarationDate.attr('readonly', 'readonly')
            }
        }
    }
}

function editFormRecipientsFundsFields() {
    const operations = ["create", "request_stored", "request_queried", "update_queried"]
    const fields = {
        beneficiaryName: $("#beneficiaryName"),
        beneficiaryAddress: $("#beneficiaryAddress"),
        operation: $("#startedOperation").val()
    }
    if (requestType.toLowerCase() === 'ea' && operations.indexOf(fields.operation.toLowerCase()) > -1) {
        fields.beneficiaryName.addClass("mandatory")
        fields.beneficiaryName.attr("required", true)
        fields.beneficiaryAddress.addClass("mandatory")
        fields.beneficiaryAddress.attr("required", true)
    }
}

function removeAttributDisabled() {
    const fields = ["isFinalAmount"]
    $.each(fields, function () {
        const field = $('#' +this)
        if (field.attr("disabled")) {
            field.removeAttr("disabled")
        }
    })
}




