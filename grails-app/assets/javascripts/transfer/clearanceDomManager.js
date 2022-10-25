/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
$("#flagdom").val(" ");

var orderClearanceMainFields = ["eaReference", "amountRequestedMentionedCurrency", "amountSettledMentionedCurrency"];
var orderClearanceMainMainBtns = ["addClearanceLink", "eaReference", "amountRequestedMentionedCurrency", "amountSettledMentionedCurrency"];
var temp_lines = [];

function addOrderClearenceOfDom() {
    var eaReference = $("#addRow #eaReference").val().trim();
    var authorizationDate = $("#addRow #authorizationDate").val().trim();
    var registrationNoBank = $("#addRow #registrationNoBank").val().trim();
    var registrationDateBank = $("#addRow #registrationDateBank").val().trim();
    var amountToBeSettledMentionedCurrency = $("#addRow #amountToBeSettledMentionedCurrency").val().trim();
    var amountRequestedMentionedCurrency = $("#addRow #amountRequestedMentionedCurrency").val().trim();
    var amountSettledMentionedCurrency = $("#addRow #amountSettledMentionedCurrency").val().trim();
    var bankName = $("#addRow #bankName").val().trim();
    var ratePayment = $("#ratePayment").val().trim();

    var fieldsToValidate = ["eaReference", "amountRequestedMentionedCurrency"];
    var allFieldsAreFilled = eaReference.length > 0 && amountRequestedMentionedCurrency.length > 0;
    if (allFieldsAreFilled) {
        $.ajax({
            type: "POST",
            url: $("#addClearanceDocUrl").val(),
            beforeSend: function () {
                initAfterAdding();
            },
            data: {
                eaReference: eaReference,
                authorizationDate: authorizationDate,
                registrationNoBank: registrationNoBank,
                registrationDateBank: registrationDateBank,
                amountToBeSettledMentionedCurrency: amountToBeSettledMentionedCurrency,
                amountRequestedMentionedCurrency: amountRequestedMentionedCurrency,
                amountSettledMentionedCurrency: amountSettledMentionedCurrency,
                bankName: bankName,
                ratePayment: ratePayment,
                state: '0'

            },
            success: function (data, textStatus) {
                if (data.error == true) {
                    removeAllClearanceErrors();
                    $("#orderClearanceListError").html(data.template);
                    removevalidateDocType(fieldsToValidate);
                    setFocusOnClearanceDomErrorField($("#orderClearanceListError .errorContainer .clearOfDomInnerErrorMessages"));
                } else {
                    removeClearanceErrors();
                    $("#listOfOrderClearanceDomsBody").html(data.template);
                    setTransferAmountRequested(data.transferInstance)
                    setTransferAmountExpected(data.transferInstance)
                    cleanAfterValidation();
                    removevalidateDocType(fieldsToValidate);
                    removeAllClearanceErrors();
                }
                resetAddClearanceDom();
            }
        });
    }
}

function setTransferAmountRequested(props) {
    $('#transferAmntRequested').val(props.transferAmntRequested)
    $('#transferNatAmntRequest').val(props.transferNatAmntRequest)
    $('#digitoperation').html(props.amntRequestedInLetters + ' Francs CFA')

}

function setTransferAmountExpected(props) {
    $('#transferAmntExecuted').val(props.transferAmntExecuted)
    $('#transferNatAmntExecuted').val(props.transferNatAmntExecuted)
    if (props.amntExecutedInLetters) {
        $('#finalAmountInWords').html(props.amntExecutedInLetters + ' Francs CFA')
    }
}

function cleanAfterValidation() {
    var fieldsToClean = ["ecReference", "ecDate", "domiciliaryBank", "domiciliationNo", "domiciliationDate", "domAmtInCurr", "invFinalAmtInCurr", "repatriatedAmtInCurr", 'currencyCodeEC', 'dateOfBoarding'];
    $.each(fieldsToClean, function () {
        $("#" + this).val("");
    });
}

function validateClearanceDom(fieldsToValidate) {
    $.each(fieldsToValidate, function (index, value) {
        if ($("#" + value).val().length == 0) {
            $("#" + value).addClass("error-border");
        }
    });
}

function removevalidateDocType(fieldsToValidate) {
    $.each(fieldsToValidate, function (index, value) {
        if ($("#" + value).val().length > 0) {
            $("#" + value).removeClass("error-border");
        }
    });
}

function setFocusOnClearanceDomErrorField(errorElements) {
    var clearancefields = ["eaReference", "authorisationDate"]
    var code_field
    $.each(errorElements, function (i) {
        $(this).click(function () {
            var fieldId = $(this).attr('errorElementId').split("_")[0];
            setTimeout(function () {
                if ($.inArray(fieldId, clearancefields) > -1) {
                    switch (fieldId) {
                        case "ecReference":
                            code_field = "eaReference"
                            break;
                        case "authorisationDate":
                            code_field = "ecDate"
                            break;

                        default:
                            code_field = fieldId
                            break;
                    }
                    $("#" + code_field).focus()
                } else {
                    $("#" + fieldId).focus()
                }

            }, 200)

        });
    });
}

function removeClearanceErrors() {
    $("#clearanceListError .errorContainer").remove();
}

function editOrderClearenceOfDom(elemId) {
    if ($("#flagdom").val().trim().length == 0) {
        var fieldsDates = ["authorizationDate", "registrationDateBank"];
        var rank = elemId
        $("#flagdom").val(rank);
        var editingRow = $("#editCleaTHeader").clone(true);
        var eaReference = $("#eaReference_" + rank).text().trim();
        var authorizationDate = $("#authorizationDate_" + rank).text().trim();
        var registrationNoBank = $("#registrationNoBank_" + rank).text().trim();
        var registrationDateBank = $("#registrationDateBank_" + rank).text().trim();
        var amountToBeSettledMentionedCurrency = $("#amountToBeSettledMentionedCurrency_" + rank).text().trim();
        var amountRequestedMentionedCurrency = $("#amountRequestedMentionedCurrency_" + rank).text().trim();
        var amountSettledMentionedCurrency = $("#amountSettledMentionedCurrency_" + rank).text().trim();
        var bankName = $('#bankName_' + rank).text().trim();
        $(".clearenceRow_" + rank).replaceWith(editingRow);

        $("#edeaReference").val(eaReference);
        $("#edauthorizationDate").val(authorizationDate);
        $("#edregistrationNoBank").val(registrationNoBank);
        $("#edregistrationDateBank").val(registrationDateBank);
        $("#edamountToBeSettledMentionedCurrency").val(amountToBeSettledMentionedCurrency);
        $("#edamountRequestedMentionedCurrency").val(amountRequestedMentionedCurrency);
        $("#edamountSettledMentionedCurrency").val(amountSettledMentionedCurrency);
        $("#eddomiciliaryBank").val(bankName);
        $.each(fieldsDates, function () {
            makeDatepicker($("#" + this))
        });
        $("#rankTr").text(rank);

        if(isEmptyLine(rank)){
            temp_lines.push({rank : rank, amountRequestedMentionedCurrency : amountRequestedMentionedCurrency, amountSettledMentionedCurrency : amountSettledMentionedCurrency});
        }
        removeAllClearanceErrors();

        onSaveOrderClearenceItemBtnClick(rank);
        onCancelOrderClearanceDomItem(rank);
    }
}

function onCancelOrderClearanceDomItem(rank) {
    $("#cancelOrderClearanceDom").click(function(){
        var conversationId = $("#conversationId").val()
        var eaReference = $("#edeaReference").val().trim();
        var line = getline(rank);
        $.ajax({
            type: "POST",
            url: $("#cancelEditClearanceDocUrl").val(),
            data: {
                conversationId: conversationId,
                rank: rank,
                eaReference: eaReference,
                amountRequestedMentionedCurrency: line.amountRequestedMentionedCurrency,
            },
            success: function (data, textStatus) {
                if (data.error == 'error') {
                    removeAllClearanceErrors();
                    $("#clearanceListError").html(data.responseData);
                } else {
                    removeClearanceErrors();
                    removeAllClearanceErrors();
                    $("#orderClearanceDomListBody").html(data.template);
                }
                $("#amountRequestedMentionedCurrency_" + rank).text(line.amountRequestedMentionedCurrency);
                $("#amountSettledMentionedCurrency_" + rank).text(line.amountSettledMentionedCurrency);
                $("#flagdom").val("");
            }
        });
    });
}

function onSaveOrderClearenceItemBtnClick(rank) {
    $("#saveOrderClearanceDom").click(function () {
        var eaReference = $("#edeaReference").val().trim();
        var authorizationDate = $("#edauthorizationDate").val().trim();
        var registrationNoBank = $("#edregistrationNoBank").val().trim();
        var registrationDateBank = $("#edregistrationDateBank").val().trim();
        var amountToBeSettledMentionedCurrency = $("#edamountToBeSettledMentionedCurrency").val().trim();
        var amountRequestedMentionedCurrency = $("#edamountRequestedMentionedCurrency").val().trim();
        var amountSettledMentionedCurrency = $("#edamountSettledMentionedCurrency").val().trim();
        var ratePayment = $("#ratePayment").val().trim();
        var allFieldsAreFilled = eaReference.length;
        var fieldsToValidate = ["eaReference", "amountRequestedMentionedCurrency"];

        if (allFieldsAreFilled) {
            $.ajax({
                type: "POST",
                url: $("#editClearanceDocUrl").val(),
                data: {
                    rank: rank,
                    eaReference: eaReference,
                    authorizationDate: authorizationDate,
                    registrationNoBank: registrationNoBank,
                    registrationDateBank: registrationDateBank,
                    amountToBeSettledMentionedCurrency: amountToBeSettledMentionedCurrency,
                    amountRequestedMentionedCurrency: amountRequestedMentionedCurrency,
                    amountSettledMentionedCurrency: amountSettledMentionedCurrency,
                    ratePayment: ratePayment,
                    state: '0'
                },
                success: function (data, textStatus) {
                    if (data.error == true) {
                        removeAllClearanceErrors();
                        $("#orderClearanceListError").html(data.template);
                        removevalidateDocType(fieldsToValidate);
                        setFocusOnClearanceDomErrorField($("#orderClearanceListError .errorContainer .clearOfDomInnerErrorMessages"));
                    } else {
                        removeClearanceErrors();
                        removeAllClearanceErrors();
                        $("#listOfOrderClearanceDomsBody").html(data.template);
                        setTransferAmountRequested(data.transferInstance);
                        setTransferAmountExpected(data.transferInstance);
                        var line = getline(rank);
                        removeLine(temp_lines, line.rank);
                    }
                }
            });
        }


    });
}

function removeOrderClearenceOfDom(elemId) {
    var clearanceRank = elemId
    var eaReference = $("#eaReference_" + clearanceRank).text().trim();
    var conversationId = $("#conversationId").val()
    var ratePayment = $("#ratePayment").val().trim();

    $("#delClearance_" + clearanceRank).attr("disabled");
    $.ajax({
        type: "POST",
        data: {
            rank: clearanceRank,
            eaReference: eaReference.trim(),
            ratePayment: ratePayment
        },
        url: $("#deleteClearanceDocUrl").val(),
        success: function (data, textStatus) {
            if (data.error == 'error') {
                removeAllClearanceErrors();
                $("#clearanceListError").html(data.responseData);
                resetOrderClearanceDom();
            } else {
                removeClearanceErrors();
                resetOrderClearanceDom();
                removeAllClearanceErrors();
                $("#listOfOrderClearanceDomsBody").html(data.template);
                setTransferAmountRequested(data.transferInstance)
                setTransferAmountExpected(data.transferInstance)
                temp_lines = [];
            }
        }
    });
}

function resetOrderClearanceDom() {
    $.each(orderClearanceMainFields, function () {
        var element = $("#" + this);
        element.val("");
        element.removeAttr('disabled', 'disabled');
    });

    $.each(orderClearanceMainMainBtns, function () {
        var element = $("#" + this);
        element.removeAttr('disabled', 'disabled');
        element.removeClass("disabled");
        element.attr("onclick", "addClearenceOfDom()");
    })
    $("#flagdom").val(" ");
}

function initAfterAdding() {
    var clearanceAddMainMainBtns = ["addClearenceOfDom","addClearanceLink"];
    $.each(clearanceAddMainMainBtns, function () {
        var elementBtn = $("#" + this);
        elementBtn.attr('disabled', 'disabled');
        elementBtn.addClass("disabled");
        elementBtn.attr("onclick", "return false");
    })
}

function resetAddClearanceDom() {
    var clearanceAddMainMainBtns = ["addClearanceLink","addClearenceOfDom"];
    $.each(clearanceAddMainMainBtns, function () {
        var element = $("#" + this);
        element.removeAttr('disabled', 'disabled');
        element.removeClass("disabled");
        element.attr("onclick", "addOrderClearenceOfDom()");
    })
}

function callExchangeEaReference() {
    var clearancefields = ["authorizationDate", "registrationNoBank", "registrationDateBank", "amountToBeSettledMentionedCurrency", "amountRequestedMentionedCurrency", "amountSettledMentionedCurrency"]

    cleanFieldsBeforeCalling(clearancefields);
    removeAllOrderClearanceErrors();
    var eaReference = $('#eaReference').val().trim();
    var importerCode = $("#importerCode").val().trim();
    var currencyCode = $("#currencyPayCode").val().trim();
    var bankCode = $("#bankCode").val().trim();

    if (eaReference) {
        commonAjax(eaReference, importerCode, currencyCode, bankCode, "add");
    } else {
        removeAllOrderClearanceErrors();
    }

}

function commonAjax(referenceValue, importerCode, currencyCode, bankCode, option) {
    var conversationId = $("#conversationId").val();
    $.ajax({
        url: $("#retrieveEAUrl").val(),
        type: "GET",
        cache: false,
        headers: {
            Accept: "application/json; text/javascript",
            "Content-Type": "application/json; text/javascript"
        },
        data: {
            eaReference: referenceValue,
            importerCode: importerCode,
            currencyCode: currencyCode,
            bankCode: bankCode,
            conversationId: conversationId
        },
        success: function (resp) {
            if (resp.error == 'error') {
                $("#orderClearanceListError").html(resp.responseData);
            } else {
                if (resp) {
                    removeAllClearanceErrors();

                    if (option == "edit") {
                        $("#addRow #authorizationDate").val(updateDateFormat(resp[0]));
                        $("#addRow #bankName").val(resp[1]);
                        $("#addRow #registrationNoBank").val(resp[2]);
                        $("#addRow #registrationDateBank").val(updateDateFormat(resp[3]));
                        $("#addRow #amountToBeSettledMentionedCurrency").val(resp[4]);

                        updateFormatOfAmountClearanceFields('edit');
                    } else {
                        $("#addRow #authorizationDate").val(updateDateFormat(resp.authorizationDate));
                        $("#addRow #bankName").val(resp.bankName);
                        $("#addRow #registrationNoBank").val(resp.registrationNumberBank);
                        $("#addRow #registrationDateBank").val(updateDateFormat(resp.registrationDateBank));
                        $("#addRow #amountToBeSettledMentionedCurrency").val(resp.balance);

                        updateFormatOfAmountClearanceFields('add');

                    }
                }
            }
        }
    });
}

function cleanFieldsBeforeCalling(fieldsToClean) {
    $.each(fieldsToClean, function () {
        $("#" + this).val("");
    });
}

function removeAllClearanceErrors() {
    $("#clearanceListError .errorContainer").remove();
}

function removeAllOrderClearanceErrors() {
    $("#orderClearanceListError .errorContainer").remove();
}

function updateDateFormat(vDate) {
    if (vDate) {
        var date_dec = vDate
        var splitdec = date_dec.split("-")
        if (splitdec.length != 0) {
            var newdate_dec = splitdec[2] + "/" + splitdec[1] + "/" + splitdec[0]
            return newdate_dec
        } else {
            return date_dec
        }
    }
}

function updateFormatOfAmountClearanceFields(action) {
    if (action) {
        if (action == "add") {
            var data = new Array("amountToBeSettledMentionedCurrency", "amountRequestedMentionedCurrency", "amountSettledMentionedCurrency");
        } else {
            var data = new Array("edamountToBeSettledMentionedCurrency", "edamountRequestedMentionedCurrency", "edamountSettledMentionedCurrency");
        }
        $.each(data, function () {
            if (typeof $("#" + this).val() !== "undefined" && typeof $("#" + this).val() !== undefined && $("#" + this).val() !== null && $("#" + this).val() !== "") {
                if ($("#" + this).val() === '0' && action == "add") {
                    $("#" + this).val(isLocale());
                } else if ($("#" + this).val() === '0' && action == "edit") {
                    $("#" + this).html(isLocale());
                } else {
                    $("#" + this).formatNumber({format: $("#decimalNumberFormat").val(), locale: $('#locale').val()});
                }

            }
        });
    }

}

function updateFormatAmount(fields) {
    if (fields) {
        if (typeof $("#" + fields).val() !== "undefined" && typeof $("#" + fields).val() !== undefined && $("#" + fields).val() !== null && $("#" + fields).val() !== "") {
            if ($("#" + fields).val() === '0') {
                $("#" + fields).val(isLocale());
            } else {
                $("#" + fields).formatNumber({format: $("#decimalNumberFormat").val(), locale: $('#locale').val()});
            }
            removeAllClearanceErrors();
        } else {
            removeAllClearanceErrors();
        }
    }
}

function isLocale() {
    if ($('#locale').val() == "en" || $('#locale').val() == "EN" || $('#locale').val() == "us" || $('#locale').val() == "US") {
        return "0.00"
    } else {
        return "0,00"
    }
}

function getline(rank) {
    return temp_lines.find(line => line.rank == rank)
}

function removeLine (lines, rank) {
    for (var i in lines) {
        if (lines[i].rank == rank) {
            lines.splice(i, 1);
        }
    }
}

function isEmptyLine(rank) {
    return temp_lines.find(line => line.rank == rank) == null
}