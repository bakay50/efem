$(document).ready(function () {
    viewEc()
})

var clearanceMainFields = ["cleranceOfficeDeclCode", "declarationDeclSerial", "declarationDeclNumber", "declarationDeclDate", "dateOfBoarding"];
var clearanceMainMainBtns = ["addClearanceLink", "ecReference", "amountTransferredInCurr"];
var temp_lines = [];
$("#flagdom").val(" ");

function callExchangeReference() {
    $(document).on('focusout', '#ecReference', function () {
        var clearancefields = ["ecDate","ecExporterName", "domiciliationCodeBank", "domiciliationNo", "domiciliationDate", "domiciliatedAmounttInCurr", "invoiceFinalAmountInCurr", "amountTransferredInCurr"]
        cleanFieldsBeforeCalling(clearancefields)
        removeAllClearanceErrors()
        var ecReference = $('#ecReference').val()
        var currencyCodeTransfer = $('#currencyCode').val()
        if (ecReference) {
            commonAjax(ecReference, currencyCodeTransfer, "add")
        } else {
            removeAllClearanceErrors()
        }
    })
}

function commonAjax(referenceValue, currencyCodeTransfer, option) {
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
            currencyCodeTransfer: currencyCodeTransfer,
            conversationId: conversationId
        },
        success: function (resp) {
            if (resp.error == 'error') {
                $("#clearanceListError").html(resp.respErrorData);
            } else {
                if (resp) {
                    removeAllClearanceErrors()
                    if (option == 'edit') {
                        $("#edecDate").val(updateDateFormat(resp.exchange.requestDate));
                        $("#eddomiciliationCodeBank").val(resp.exchange.bankCode);
                        $("#edecExporterName").val(resp.exchange.exporterNameAddress);
                        $("#eddomiciliationNo").val(resp.exchange.registrationNumberBank);
                        $("#eddomiciliationDate").val(updateDateFormat(resp.exchange.registrationDateBank));
                        $("#eddomiciliatedAmounttInCurr").val(resp.exchange.amountMentionedCurrency);
                        $('#edrepatriatedAmountToBank').val(resp.repatriatedAmount)
                        if (resp.exchange.finalAmountInDevise) {
                            $("#edinvoiceFinalAmountInCurr").val(resp.exchange.finalAmountInDevise);
                        }
                        updateFormatOfAmountClearanceFields('edit')
                    } else {
                        $("#ecDate").val(updateDateFormat(resp.exchange.requestDate));
                        $("#ecExporterName").val(resp.exchange.exporterNameAddress);
                        $("#domiciliationCodeBank").val(resp.exchange.bankCode);
                        $("#domiciliationNo").val(resp.exchange.registrationNumberBank);
                        $("#domiciliationDate").val(updateDateFormat(resp.exchange.registrationDateBank));
                        $("#domiciliatedAmounttInCurr").val(resp.exchange.amountMentionedCurrency);
                        $("#repatriatedAmountToBank").val(resp.repatriatedAmount);
                        if (resp.exchange.finalAmountInDevise) {
                            $("#invoiceFinalAmountInCurr").val(resp.exchange.finalAmountInDevise);
                        }
                        updateFormatOfAmountClearanceFields('add')
                    }
                }
            }
        }
    })
}

function cleanFieldsBeforeCalling(fieldsToClean) {
    $.each(fieldsToClean, function () {
        $("#" + this).val("");
    });
}

function removeAllClearanceErrors() {
    $("#clearanceListError .errorContainer").remove();
}

function updateFormatOfAmountClearanceFields(action) {
    if (action) {
        if (action == "add") {
            var data = new Array("invoiceFinalAmountInCurr", "domiciliatedAmounttInCurr");
        } else {
            var data = new Array("edinvFinalAmtInCurr", "eddomAmtInCurr");
        }
        $.each(data, function () {
            if (typeof $("#" + this).val() !== "undefined" && typeof $("#" + this).val() !== undefined && $("#" + this).val() !== null && $("#" + this).val() !== "") {
                if ($("#" + this).val() === '0' && action == "add") {
                    $("#" + this).val(isLocale());
                } else if ($("#" + this).val() === '0' && action == "edit") {
                    $("#" + this).html(isLocale());
                }
                else {
                    $("#" + this).formatNumber({format: $("#decimalNumberFormat").val(), locale: $('#locale').val()});
                }

            }
        });
    }

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

function addClearenceDom() {
    var conversationId = null
    var ecReference = $("#ecReference").val().trim();
    var ecDate = $("#ecDate").val().trim();
    var ecExporterName = $("#ecExporterName").val().trim();
    var domiciliationCodeBank = $("#domiciliationCodeBank").val().trim();
    var domiciliationNo = $("#domiciliationNo").val().trim();
    var domiciliationDate = $("#domiciliationDate").val().trim();
    var domiciliatedAmounttInCurr = $("#domiciliatedAmounttInCurr").val().trim();
    var invoiceFinalAmountInCurr = $("#invoiceFinalAmountInCurr").val().trim();
    var repatriatedAmountToBank = $("#repatriatedAmountToBank").val().trim();
    var amountTransferredInCurr = $("#amountTransferredInCurr").val().trim();
    var amountTransferred = $('#amountTransferred').val().trim().replace(/\s/g, '')
    var fieldsToValidate = ["ecReference", "amountTransferredInCurr"];
    var allFieldsAreFilled = ecReference.length > 0;
    if (allFieldsAreFilled) {
        $.ajax({
            type: 'POST',
            url: $('#addClearanceDomDocUrl').val(),
            beforeSend: function () {
                initAfterAdding();
            },
            data: {
                ecReference: ecReference,
                ecDate: ecDate,
                ecExporterName:ecExporterName,
                domiciliationCodeBank: domiciliationCodeBank,
                domiciliationNo: domiciliationNo,
                domiciliationDate: domiciliationDate,
                domiciliatedAmounttInCurr: domiciliatedAmounttInCurr,
                invoiceFinalAmountInCurr: invoiceFinalAmountInCurr,
                repatriatedAmountToBank: repatriatedAmountToBank,
                amountTransferredInCurr: amountTransferredInCurr,
                currencyCodeTransfer: $('#currencyCode').val(),
                amountTransferred: amountTransferred,
            },
            success: function (data) {
                if (data.error == true) {
                    removeAllClearanceErrors()
                    $("#clearanceListError").html(data.template)
                    removevalidateDocType(fieldsToValidate)
                    setFocusOnClearanceDomErrorField($("#clearanceListError .errorContainer .clearOfDomInnerErrorMessages"))
                } else {
                    removeClearanceErrors()
                    $("#listOfClearanceDomsBody").html(data.template)
                    cleanAfterValidation()
                    removevalidateDocType(fieldsToValidate)
                    if (!data.finalInv) {
                        $("#alertInfo").html($("#clearanceOfDomInfoTemplate").clone(true))
                        $("#alertInfo").find("#clearanceOfDomInfoTemplate").css("display", "block")
                    } else {
                        $('#alertInfo').find('#clearanceOfDomInfoTemplate').css("display", "none")
                    }
                    removeAllClearanceErrors()
                }
                resetAddClearanceDom()

            }
        })
    }
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
        var domiciliationCodeBank = $("#domiciliationCodeBank_" + rank).text().trim();
        var domiciliationNo = $("#domiciliationNo_" + rank).text().trim();
        var domiciliationDate = $("#domiciliationDate_" + rank).text().trim();
        var domiciliatedAmounttInCurr = $("#domiciliatedAmounttInCurr_" + rank).text().trim();
        var invoiceFinalAmountInCurr = $("#invoiceFinalAmountInCurr_" + rank).text().trim();
        var repatriatedAmountToBank = $("#repatriatedAmountToBank_" + rank).text().trim();
        var amountTransferredInCurr = $("#amountTransferredInCurr_" + rank).text().trim();
        var exporterName = $("#ecExporterName_" + rank).text().trim()

        $(".clearenceRow_" + rank).replaceWith(editingRow);
        $("#edecReference").val(ecReference);
        $("#edecDate").val(ecDate);
        $("#eddomiciliationCodeBank").val(domiciliationCodeBank);
        $("#eddomiciliationNo").val(domiciliationNo);
        $("#eddomiciliationDate").val(domiciliationDate);
        $("#eddomiciliatedAmounttInCurr").val(domiciliatedAmounttInCurr);
        $("#edinvoiceFinalAmountInCurr").val(invoiceFinalAmountInCurr);
        $("#edrepatriatedAmountToBank").val(repatriatedAmountToBank);
        $("#edamountTransferredInCurr").val(amountTransferredInCurr);
        $("#edecExporterName").val(exporterName)
        $.each(fieldsDates, function () {
            makeDatepicker($("#" + this))
        });
        $("#rankTr").text(rank);
        if(isEmptyLine(rank)){
            temp_lines.push({rank : rank, amountTransferredInCurr : amountTransferredInCurr, ecReference: ecReference});
        }
        removeAllClearanceErrors();
        editCallingExchangeReference();

        onSaveClearanceDomItemBtnClick(rank);
        onCancelClearanceDomItemBtnClick(rank);

    }
}

function onCancelClearanceDomItemBtnClick(rank){
    $("#cancelClearanceDom").click(function cancelClearanceDom() {
        var conversationId = $("#conversationId").val()
        var line = getline(rank);
        $.ajax({
            type: "POST",
            url: $("#cancelEditClearanceDomDocUrl").val(),
            data: {
                conversationId: conversationId,
                rank: rank,
                ecReference: line.ecReference,
                amountTransferredInCurr: line.amountTransferredInCurr.replace(/\s/g, ""),
            },
            success: function (data) {
                if (data.error == 'error') {
                    removeAllClearanceErrors();
                    $("#clearanceListError").html(data.responseData);
                } else {
                    $("#listOfClearanceDomsBody").html(data.template);
                    removeClearanceErrors();
                    removeAllClearanceErrors();
                }
                $("#amountTransferredInCurr_" + rank).text(line.amountTransferredInCurr);
                $("#flagdom").val("");

            }
        });
    })
}

function onSaveClearanceDomItemBtnClick(rank){
    $("#saveClearanceDom").click(function () {
        var ecReference = $("#edecReference").val().trim();
        var ecDate = $("#edecDate").val().trim();
        var domiciliationCodeBank = $("#eddomiciliationCodeBank").val().trim();
        var domiciliationNo = $("#eddomiciliationNo").val().trim();
        var domiciliationDate = $("#eddomiciliationDate").val().trim();
        var domiciliatedAmounttInCurr = $("#eddomiciliatedAmounttInCurr").val().trim();
        var invoiceFinalAmountInCurr = $("#edinvoiceFinalAmountInCurr").val().trim();
        var repatriatedAmountToBank = $("#edrepatriatedAmountToBank").val().trim();
        var amountTransferredInCurr = $("#edamountTransferredInCurr").val().trim();
        var conversationId = $("#conversationId").val();
        var exporterName = $("#edecExporterName").val().trim()
        var allFieldsAreFilled = ecReference.length > 0 && amountTransferredInCurr.length > 0;
        if (allFieldsAreFilled) {
            $.ajax({
                type: "POST",
                url: $("#editClearanceDomDocDocUrl").val(),
                data: {
                    rank: rank,
                    ecReference: ecReference,
                    ecDate: ecDate,
                    domiciliationCodeBank: domiciliationCodeBank,
                    domiciliationNo: domiciliationNo,
                    domiciliationDate: domiciliationDate,
                    domiciliatedAmounttInCurr: domiciliatedAmounttInCurr,
                    invoiceFinalAmountInCurr: invoiceFinalAmountInCurr,
                    repatriatedAmountToBank: repatriatedAmountToBank,
                    amountTransferredInCurr: amountTransferredInCurr,
                    currencyCodeTransfer: $('#currencyCode').val(),
                    ecExporterName: exporterName
                },
                success: function (data) {
                    if (data.error == true) {
                        removeAllClearanceErrors();
                        $("#clearanceListError").html(data.template);
                        setFocusOnClearanceDomErrorField($("#clearanceListError .errorContainer .clearOfDomInnerErrorMessages"));
                    } else {
                        $("#listOfClearanceDomsBody").html(data.template);
                        $('#amountTransferred').val(data.currencyTransferInstance.amountTransferred)
                        $('#transferRate').val(data.currencyTransferInstance.transferRate)
                        removeClearanceErrors();
                        removeAllClearanceErrors();
                        calculateAmountTransferredNat();
                        var line = getline(rank);
                        removeLine(temp_lines, line.rank);
                    }
                }
            });
        }
    });
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
        url: $("#deleteClearanceDomDocUrl").val(),
        success: function (data) {
            if (data.error == 'error') {
                removeAllClearanceErrors();
                $("#clearanceListError").html(data.responseData);
                resetClearanceDom();
            } else {
                removeClearanceErrors();
                removeAllClearanceErrors();
                $("#listOfClearanceDomsBody").html(data.template);
                temp_lines = [];
            }
        }
    });
}

function editCallingExchangeReference() {
    $(document).on('focusout', '#edecReference', function () {
        var clearancefields = ["edecDate", "eddomiciliationCodeBank", "eddomiciliationNo", "eddomiciliationDate", "eddomiciliatedAmounttInCurr", "edrepatriatedAmountToBank","edinvoiceFinalAmountInCurr", "edamountTransferredInCurr"]
        cleanFieldsBeforeCalling(clearancefields);
        removeAllClearanceErrors();
        var ecReference = $('#edecReference').val();
        var currencyCodeTransfer = $('#currencyCode').val()
        if (ecReference) {
            commonAjax(ecReference, currencyCodeTransfer, "edit");
        } else {
            removeAllClearanceErrors();
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

function resetClearanceDom() {
    $.each(clearanceMainFields, function () {
        var element = $("#" + this);
        element.val("");
        element.removeAttr('disabled', 'disabled');
    });

    $.each(clearanceMainMainBtns, function () {
        var element = $("#" + this);
        element.removeAttr('disabled', 'disabled');
        element.removeClass("disabled");
        element.attr("onclick", "addClearenceOfDom()");
    })
    $("#flagdom").val(" ");
}

function initClearanceDom() {
    $.each(clearanceMainFields, function () {
        var element = $("#" + this);
        element.val("");
        element.attr('disabled', 'disabled');
    });

    $.each(clearanceMainMainBtns, function () {
        var elentbts = $("#" + this);
        elentbts.attr('disabled', 'disabled');
        elentbts.addClass("disabled");
        elentbts.attr("onclick", "return false");
    })
}

function setFocusOnClearanceDomErrorField(errorElements) {
    var clearancefields = ["ecReference", "ecDate", "domiciliationCodeBank", "domiciliationNo", "domiciliationDate", "domiciliatedAmounttInCurr", "invoiceFinalAmountInCurr", "amountTransferredInCurr"]
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
                        case "domiciliationCodeBank":
                            code_field = "domiciliationCodeBank"
                            break;
                        case "domiciliationNo":
                            code_field = "domiciliationNo"
                            break;
                        case "domiciliationDate":
                            code_field = "domiciliationDate"
                            break;
                        case "domiciliatedAmounttInCurr":
                            code_field = "domiciliatedAmounttInCurr"
                            break;
                        case "invoiceFinalAmountInCurr":
                            code_field = "invoiceFinalAmountInCurr"
                            break;
                        case "amountTransferredInCurr":
                            code_field = "amountTransferredInCurr"
                            break;
                        default:
                            code_field = fieldId
                            break;
                    }
                    $("#" + code_field).focus()
                }
                else {
                    $("#" + fieldId).focus()
                }

            }, 200)

        });
    });
}
function removeClearanceErrors() {
    $("#clearanceListError .errorContainer").remove();
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

function cleanAfterValidation() {
    var fieldsToClean = ["ecReference", "ecDate", "domiciliationCodeBank", "domiciliationNo", "domiciliationDate", "domiciliatedAmounttInCurr", "invoiceFinalAmountInCurr", "amountTransferredInCurr"];
    $.each(fieldsToClean, function () {
        $("#" + this).val("");
    });
}

function initAfterAdding() {
    var clearanceAddMainMainBtns = ["addClearanceLink"];
    $.each(clearanceAddMainMainBtns, function () {
        var elementBtn = $("#" + this);
        elementBtn.attr('disabled', 'disabled');
        elementBtn.addClass("disabled");
        elementBtn.attr("onclick", "return false");
    })
}

function updateDateDom(value, element) {
    $("#clearanceDomListBody" + "#" + element).val(value);
    $("#" + element).val(value);
}

function isLocale() {
    if ($('#locale').val() === "en" || $('#locale').val() === "EN" || $('#locale').val() === "us" || $('#locale').val() === "US") {
        return "0.00"
    } else {
        return "0,00"
    }
}

function viewEc() {
    $(document).on("click", 'a.js_viewEc', function (event) {
        event.preventDefault()
        const {reference} = event.target.parentNode.dataset
        const url = $('#retrieveClearanceEcIdUrl').val()
        const conversationId = $('#conversationId').val()
        if (reference) {
            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    conversationId: conversationId,
                    ecReference: reference
                },
                success: function (data) {
                    if (data && data.ecReference_id) {
                        const exchangeUrl = $('#retrieveExchangeUrl').val()
                        const redirect = window.open(exchangeUrl + "/" + data.ecReference_id, "_blank")
                        redirect.focus()
                        return false
                    } else {
                        alert(msg("currencyTransfer.retrieve.exchange.message"))
                    }
                }
            })
        } else {
            alert(msg("view.button.currencyTransfer.confirm.message"))
        }

    })
}

function showRepatiation(e) {
    let url = $("#showRepatriationUrl").val()
    e.preventDefault()
    const redirect = window.open(url, '_blank')
    redirect.focus()
    return false
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