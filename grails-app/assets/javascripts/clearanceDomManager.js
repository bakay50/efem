/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 21/05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
$(document).ready(function () {
    viewEc()
})

$("#flagdom").val(" ");

function addClearenceOfDom() {
    var amountReceived = getNumericValues($("#receivedAmount").val())
    var ecReference = $("#ecReference").val().trim();
    var ecDate = $("#ecDate").val().trim();
    var domiciliaryBank = $("#domiciliaryBank").val().trim();
    var domiciliationNo = $("#domiciliationNo").val().trim();
    var domiciliationDate = $("#domiciliationDate").val().trim();
    var domAmtInCurr = $("#domAmtInCurr").val().trim();
    var invFinalAmtInCurr = $("#invFinalAmtInCurr").val().trim();
    var repatriatedAmtInCurr = $("#repatriatedAmtInCurr").val().trim();
    var currencyCodeEC = $('#currencyCodeEC').val().trim()
    var currencyCode = $('#currencyCode').val().trim()
    var conversationId = null // $("#conversationId").val();
    var dateOfBoarding = $("#dateOfBoarding").val().trim();
    var bankCode = $("#bankCodeDom").val();
    var fieldsToValidate = ["ecReference", "repatriatedAmtInCurr"];
    var allFieldsAreFilled = ecReference.length > 0;
    if (allFieldsAreFilled) {
        $.ajax({
            type: "POST",
            url: $("#addClearanceDocUrl").val(),
            beforeSend: function () {
                initAfterAdding();
            },
            data: {
                ecReference: ecReference,
                ecDate: ecDate,
                domiciliaryBank: domiciliaryBank,
                domiciliationNo: domiciliationNo,
                domiciliationDate: domiciliationDate,
                domAmtInCurr: domAmtInCurr,
                invFinalAmtInCurr: invFinalAmtInCurr,
                repatriatedAmtInCurr: repatriatedAmtInCurr,
                currencyCodeEC: currencyCodeEC,
                currencyCode: currencyCode,
                dateOfBoarding: dateOfBoarding,
                bankCode: bankCode,
                amountReceived: amountReceived
            },
            success: function (data, textStatus) {
                if (data.error == 'error') {
                    removeAllClearanceErrors();
                    $("#clearanceListError").html(data.responseData);
                    removevalidateDocType(fieldsToValidate);
                    setFocusOnClearanceDomErrorField($("#clearanceListError .errorContainer .clearOfDomInnerErrorMessages"));
                } else {
                    removeClearanceErrors();
                    $("#listOfClearanceDomsBody").html(data.template);
                    cleanAfterValidation();
                    removevalidateDocType(fieldsToValidate);
                    if (!data.finalInv && data.finalInv !== null) {
                        $("#alertInfo").html($("#clearanceOfDomInfoTemplate").clone(true));
                        $("#alertInfo").find("#clearanceOfDomInfoTemplate").css("display", "block");
                    } else {
                        $("#alertInfo").find("#clearanceOfDomInfoTemplate").css("display", "none");
                    }
                    removeAllClearanceErrors();
                }
                resetAddClearanceDom();
            }
        });
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
    var clearancefields = ["ecReference", "ecDate", "domiciliaryBank", "domiciliationNo", "domiciliationDate", "domAmtInCurr", "invFinalAmtInCurr", "repatriatedAmtInCurr", "dateOfBoarding"]
    var code_field
    $.each(errorElements, function (i) {
        $(this).click(function () {
            var fieldId = $(this).attr('errorElementId').split("_")[0];
            setTimeout(function () {
                if ($.inArray(fieldId, clearancefields) > -1) {
                    switch (fieldId) {
                        case "ecReference":
                            code_field = "ecReference"
                            break;
                        case "ecDate":
                            code_field = "ecDate"
                            break;
                        case "domiciliaryBank":
                            code_field = "domiciliaryBank"
                            break;
                        case "domiciliationNo":
                            code_field = "domiciliationNo"
                            break;
                        case "domiciliationDate":
                            code_field = "domiciliationDate"
                            break;
                        case "domAmtInCurr":
                            code_field = "domAmtInCurr"
                            break;
                        case "invFinalAmtInCurr":
                            code_field = "invFinalAmtInCurr"
                            break;
                        case "repatriatedAmtInCurr":
                            code_field = "repatriatedAmtInCurr"
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

function editClearenceOfDom(elemId) {
    if ($("#flagdom").val().trim().length == 0) {
        var fieldsDates = ["ecDate", "domiciliationDate"];
        initClearanceDom();
        var rank = elemId // params.rank;

        $("#flagdom").val(rank);
        var editingRow = $("#editCleaTHeader").clone(true);
        var ecReference = $("#ecReference_" + rank).text().trim();
        var ecDate = $("#ecDate_" + rank).text().trim();
        var domiciliaryBank = $("#domiciliaryBank_" + rank).text().trim();
        var domiciliationNo = $("#domiciliationNo_" + rank).text().trim();
        var domiciliationDate = $("#domiciliationDate_" + rank).text().trim();
        var domAmtInCurr = $("#domAmtInCurr_" + rank).text().trim();
        var invFinalAmtInCurr = $("#invFinalAmtInCurr_" + rank).text().trim();
        var repatriatedAmtInCurr = $("#repatriatedAmtInCurr_" + rank).text().trim();
        var dateOfBoarding = $("#dateOfBoarding_" + rank).text().trim();

        $(".clearenceRow_" + rank).replaceWith(editingRow);
        $("#edecReference").val(ecReference);
        $("#edecDate").val(ecDate);
        $("#eddomiciliaryBank").val(domiciliaryBank);
        $("#eddomiciliationNo").val(domiciliationNo);
        $("#eddomiciliationDate").val(domiciliationDate);
        $("#eddomAmtInCurr").val(domAmtInCurr);
        $("#eddomAmtInCurr").val(domAmtInCurr);
        $("#edinvFinalAmtInCurr").val(invFinalAmtInCurr);
        $("#edrepatriatedAmtInCurr").val(repatriatedAmtInCurr);
        $("#eddateOfBoarding").val(dateOfBoarding);
        $.each(fieldsDates, function () {
            makeDatepicker($("#" + this))
        });
        $("#rankTr").text(rank);
        removeAllClearanceErrors();
        editCallingExchangeReference();

        $("#saveClearanceDom").click(function () {
            var ecReference = $("#edecReference").val().trim();
            var ecDate = $("#edecDate").val().trim();
            var domiciliaryBank = $("#eddomiciliaryBank").val().trim();
            var domiciliationNo = $("#eddomiciliationNo").val().trim();
            var domiciliationDate = $("#eddomiciliationDate").val().trim();
            var domAmtInCurr = $("#eddomAmtInCurr").val().trim();
            var invFinalAmtInCurr = $("#edinvFinalAmtInCurr").val().trim();
            var repatriatedAmtInCurr = $("#edrepatriatedAmtInCurr").val().trim();
            var dateOfBoarding = $("#eddateOfBoarding").val().trim();
            var conversationId = $("#conversationId").val();
            var bankCode = $("#bankCodeDom").val();

            var allFieldsAreFilled = ecReference.length > 0 && repatriatedAmtInCurr.length > 0;
            if (allFieldsAreFilled) {
                $.ajax({
                    type: "POST",
                    url: $("#editClearanceDocUrl").val(),
                    data: {
                        rank: rank,
                        ecReference: ecReference,
                        ecDate: ecDate,
                        domiciliaryBank: domiciliaryBank,
                        domiciliationNo: domiciliationNo,
                        domiciliationDate: domiciliationDate,
                        domAmtInCurr: domAmtInCurr,
                        invFinalAmtInCurr: invFinalAmtInCurr,
                        repatriatedAmtInCurr: repatriatedAmtInCurr,
                        dateOfBoarding: dateOfBoarding,
                        bankCode: bankCode
                    },
                    success: function (data, textStatus) {
                        if (data.error == 'error') {
                            removeAllClearanceErrors();
                            $("#clearanceListError").html(data.responseData);
                            setFocusOnClearanceDomErrorField($("#clearanceListError .errorContainer .clearOfDomInnerErrorMessages"));
                        } else {
                            removeClearanceErrors();
                            resetClearanceDom();
                            removeAllClearanceErrors();
                            $("#listOfClearanceDomsBody").html(data.template);
                        }
                    }
                });
            }
        });
        $("#cancelClearanceDom").click(cancelClearanceDom)
    }
}

function loadExchange() {
    var url = $("#loadExchangeCommitmentUrl").val();
    event.preventDefault()
    const redirect = window.open(url, '_blank')
    redirect.focus()
    return false
}

function removeClearenceOfDom(elemId) {
    var clearanceRank = elemId
    var ecReference = $("#ecReference_" + clearanceRank).text().trim();
    var conversationId = $("#conversationId").val()

    $("#delClearance_" + clearanceRank).attr("disabled");
    $.ajax({
        type: "POST",
        data: {
            rank: clearanceRank,
            ecReference: ecReference.trim(),
        },
        url: $("#deleteClearanceDocUrl").val(),
        success: function (data, textStatus) {
            if (data.error == 'error') {
                removeAllClearanceErrors();
                $("#clearanceListError").html(data.responseData);
                resetClearanceDom();
            } else {
                removeClearanceErrors();
                resetClearanceDom();
                removeAllClearanceErrors();
                $("#listOfClearanceDomsBody").html(data.template);
            }
        }
    });
}

function cancelClearanceDom() {
    var conversationId = $("#conversationId").val()
    $.ajax({
        type: "POST",
        url: $("#cancelEditClearanceDocUrl").val(),
        data: {
            conversationId: conversationId
        },
        success: function (data, textStatus) {
            if (data.error == 'error') {
                removeAllClearanceErrors();
                $("#clearanceListError").html(data.responseData);
            } else {
                removeClearanceErrors();
                resetClearanceDom();
                removeAllClearanceErrors();
                $("#clearanceDomListBody").html(data.template);
            }
        }
    });
}

function viewEc() {
    $(document).on("click", 'a.js_viewEc', function (event) {
        event.preventDefault()
        const { reference } = event.target.parentNode.dataset
        const url = $('#retrieveClearanceEcIdUrl').val()
        const conversationId = $('#conversationId').val()
        if (reference) {
            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    conversationId : conversationId,
                    ecReference: reference
                },
                success: function (data) {
                    if (data && data.ecReference_id) {
                        const exchangeUrl = $('#retrieveExchangeUrl').val()
                        const redirect = window.open(exchangeUrl + "/" + data.ecReference_id, "_blank")
                        redirect.focus()
                        return false
                    } else {
                        alert(msg("repatriation.retrieve.exchange.message"))
                    }
                }
            })
        } else {
            alert(msg("view.button.repatriation.confirm.message"))
        }

    })
}

function updateDateDom(value, element) {
    $("#clearanceDomListBody" + "#" + element).val(value);
    $("#" + element).val(value);
}

function validateFieldOnKeyPressDecl(element) {
    var id = $(element).attr("id");
    var value = $("#" + id).val().trim();
    var fieldHasValue = value.length > 0;
    if (fieldHasValue) {
        $("#" + id).removeClass("error-border");
    }
}

var clearanceMainFields = ["cleranceOfficeDeclCode", "declarationDeclSerial", "declarationDeclNumber", "declarationDeclDate", "dateOfBoarding"];
var clearanceMainMainBtns = ["addClearanceLink", "ecReference", "repatriatedAmtInCurr"];


function initAfterAdding() {
    var clearanceAddMainMainBtns = ["addClearanceLink"];
    $.each(clearanceAddMainMainBtns, function () {
        var elementBtn = $("#" + this);
        elementBtn.attr('disabled', 'disabled');
        elementBtn.addClass("disabled");
        elementBtn.attr("onclick", "return false");
    })
}

function resetAddClearanceDom() {
    var clearanceAddMainMainBtns = ["addClearanceLink"];
    $.each(clearanceAddMainMainBtns, function () {
        var element = $("#" + this);
        element.removeAttr('disabled', 'disabled');
        element.removeClass("disabled");
        element.attr("onclick", "addClearenceOfDom()");
    })
}


function initClearanceDom() {
    $.each(clearanceMainFields, function () {
        var elent = $("#" + this);
        elent.val("");
        elent.attr('disabled', 'disabled');
    });

    $.each(clearanceMainMainBtns, function () {
        var elentbts = $("#" + this);
        elentbts.attr('disabled', 'disabled');
        elentbts.addClass("disabled");
        elentbts.attr("onclick", "return false");
    })
}

function resetClearanceDom() {
    $.each(clearanceMainFields, function () {
        var elent = $("#" + this);
        elent.val("");
        elent.removeAttr('disabled', 'disabled');
    });

    $.each(clearanceMainMainBtns, function () {
        var element = $("#" + this);
        element.removeAttr('disabled', 'disabled');
        element.removeClass("disabled");
        element.attr("onclick", "addClearenceOfDom()");
    })
    $("#flagdom").val(" ");
}

function callExchangeReference() {
    var clearancefields = ["ecDate","domiciliaryBank", "domiciliationNo", "domiciliationDate", "domAmtInCurr", "invFinalAmtInCurr", "repatriatedAmtInCurr", "dateOfBoarding"]
    cleanFieldsBeforeCalling(clearancefields);
    removeAllClearanceErrors();
    var ecReference = $('#ecReference').val();
    var exporterCode = $("#code").val().trim();
    if (ecReference) {
        commonAjax(ecReference, exporterCode, "add");
    } else {
        removeAllClearanceErrors();
    }
}

function commonAjax(referenceValue, exporterCode, option) {
    var conversationId = $("#conversationId").val();
    $.ajax({
        url: $("#retrieveEcUrl").val(),
        type: "GET",
        cache: false,
        headers: {
            Accept: "application/json; text/javascript",
            "Content-Type": "application/json; text/javascript"
        },
        data: {
            ecReference: referenceValue,
            exporterCode: exporterCode,
            conversationId: conversationId
        },
        success: function (resp) {
            if (resp.error == 'error') {
                $("#clearanceListError").html(resp.responseData);
                $("#addItemCategory").attr("onclick","");
                $("#addItemCategory").attr("disabled",true);
            } else {
                if (resp) {
                    removeAllClearanceErrors();
                    $("#addItemCategory").attr("onclick","addClearenceOfDom()");
                    $("#addItemCategory").attr("disabled",false);
                    if (option == "edit") {
                        $("#edecDate").val(updateDateFormat(resp[0]));
                        $("#eddomiciliaryBank").val(resp[1]);
                        $("#eddomiciliationNo").val(resp[2]);
                        $("#eddomiciliationDate").val(updateDateFormat(resp[3]));
                        $("#eddomAmtInCurr").val(resp[4]);
                        if (resp[5]) {
                            $("#edinvFinalAmtInCurr").val(resp[5]);
                        }
                        $("#currencyCodeEC").val(resp[6]);
                        if (resp[7]) {
                            $("#dateOfBoarding").val(updateDateFormat(resp[7]));
                        }
                        if (resp[8]) {
                            $("#bankCodeDom").val(resp[8]);
                        }
                        updateFormatOfAmountClearanceFields('edit');
                    } else {
                        $("#ecDate").val(updateDateFormat(resp[0]));
                        $("#domiciliaryBank").val(resp[1]);
                        $("#domiciliationNo").val(resp[2]);
                        $("#domiciliationDate").val(updateDateFormat(resp[3]));
                        $("#domAmtInCurr").val(resp[4]);
                        if (resp[5]) {
                            $("#invFinalAmtInCurr").val(resp[5]);
                        }
                        $("#currencyCodeEC").val(resp[6]);
                        if (resp[7]) {
                            $("#dateOfBoarding").val(updateDateFormat(resp[7]));
                        }
                        if (resp[8]) {
                            $("#bankCodeDom").val(resp[8]);
                        }
                        updateFormatOfAmountClearanceFields('add');
                    }
                }
            }
        }
    });
}

function editCallingExchangeReference() {
    $(document).on('focusout', '#edecReference', function () {
        var clearancefields = ["edecDate", "eddomiciliaryBank", "eddomiciliationNo", "eddomiciliationDate", "eddomAmtInCurr", "edinvFinalAmtInCurr", "edrepatriatedAmtInCurr"]
        cleanFieldsBeforeCalling(clearancefields);
        removeAllClearanceErrors();
        var ecReference = $('#edecReference').val();
        var exporterCode = $("#code").val().trim();
        if (ecReference) {
            commonAjax(ecReference, exporterCode, "edit");
        } else {
            removeAllClearanceErrors();
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

function updateDateFormat(ecDate) {
    if (ecDate) {
        var date_dec = ecDate
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
            var data = new Array("invFinalAmtInCurr", "domAmtInCurr");
        } else {
            var data = new Array("edinvFinalAmtInCurr", "eddomAmtInCurr");
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
