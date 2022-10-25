function validateExecution() {
    var isExecutionValid = true;
    $("#executionContainer").find(".wfinput").each(function () {
        if (!$(this).is('readonly')) {
            isExecutionValid = $(this).valid() && isExecutionValid;
        }
    });

    return isExecutionValid;
}

function addExecution() {

    var url = $("#addExecutionUrl").val();
    $.ajax({
        method: "POST",
        url: url,
        success: function (data, textStatus) {
            $("#executionListContainer").html(data.template);
            makeAjaxAutocomplete('countryProvenanceDestinationExCode', 'REF_CTY', 'code,description', 'code,description', 'code,description', labelSetter, '50,100');
            makeAjaxAutocomplete('executingBankCode', 'HT_BNK', "code,description", 'code,description', "code,description", labelSetter, '100');
            makeAjaxAutocomplete('executionDomBankCode', 'HT_BNK', "code,description", 'code,description', "code,description", labelSetter, '100');
            removeDomClasses();
        }
    });
}

function loadExecutionListTemplate(data) {
    var executionListContainer = $("#executionListContainer");
    executionListContainer.find("#executionListError .errorContainer").remove();
    executionListContainer.html(data.template);
    makeAjaxAutocomplete('countryProvenanceDestinationExCode', 'REF_CTY', 'code,description', 'code,description', 'code,description', labelSetter, '50,100');
    makeAjaxAutocomplete('executingBankCode', 'HT_BNK', "code,description", 'code,description', "code,description", labelSetter, '100');
    makeAjaxAutocomplete('executionDomBankCode', 'HT_BNK', "code,description", 'code,description', "code,description", labelSetter, '100');
}

function showExecutionError(data) {
    $("#executionListContainer").find("#executionListError").html(data.responseData);
}

function onSuccessRequest(data) {
    if (data.error == 'error') {
        showExecutionError(data)
    } else {
        $("#balanceAs").val(data.updatedBalance);
        loadExecutionListTemplate(data)
    }
}

function saveExecution() {

    var validExecutionDetails = validateExecution();

    if (validExecutionDetails) {
        var url = $("#saveExecutionUrl").val()
        var executionContainer = $("#executionContainer :input").serialize()
        var data = executionContainer
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            success: function (data) {
                onSuccessRequest(data)
            }
        });
    }
}

function editExecution(executionRank) {
    var url = $("#editExecutionUrl").val();
    $.ajax({
        method: "POST",
        async: false,
        url: url,
        data: {
            rank: executionRank,
            id: $("#exchangeId").find("#id").val()
        },
        success: function (data) {
            onSuccessRequest(data)
            removeDomClasses();
        }
    });
}

function deleteExecution() {
    var delConfirmationDlg = $("#js_delCnfrmActiondlg");
    var delConfirmationMsg = delConfirmationDlg.find("#js_delCnfrmActionMsg");
    var delConfirmationOk = delConfirmationDlg.find("#js_delCnfrmActionOK");

    delConfirmationDlg.removeClass("hide")
    delConfirmationDlg.modal({backdrop: 'static'});
    delConfirmationMsg.empty();
    delConfirmationMsg.append(msg("deleteExecutionMessage"));


    delConfirmationOk.off("click").on("click", function () {
        var url = $("#deleteExecutionUrl").val();
        $.ajax({
            type: "POST",
            url: url,
            data: {
                rank: $("#rank").val()
            },
            success: function (data) {
                onSuccessRequest(data)
            }
        });
    });
}


function removeExecution(executionRank) {
    var url = $("#removeExecutionUrl").val();
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        data: {
            rank: executionRank
        },
        success: function (data) {
            onSuccessRequest(data);
            disableExecutionFields();
        }
    })
}

function disableExecutionFields() {
    var allItemElements = $("#executionContainer .wfinput")
    $.each(allItemElements, function () {
        $(this).attr("disabled", "disabled");
    });
}

function loadExecution() {
    var url = $("#loadExecutionTransferUrl").val();
    event.preventDefault()
    const redirect = window.open(url, '_blank')
    redirect.focus()
    return false
}

function cancelExecution(action) {
    var url = $("#cancelExecutionUrl").val();
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        data: {
            rank: $("#rank").val(),
            previousOperation: action
        },
        success: function (data) {
            onSuccessRequest(data)
        }
    })
}

function getExchangeRate() {
    return getNumericValue($("#currencyExRate").val());
}

function updateAmountExecution(realNumber, name) {
    var decimalPos = 2;

    if (realNumber) {
        var numberAfterDecimal = returnNumberAfterDelimiter(realNumber);
        var computedRealNumber = realNumber.toString().slice(0, -numberAfterDecimal + decimalPos) + '0'

        if (name === "amountMentionedExCurrency") {
            if (numberAfterDecimal >= 3 && (numberAfterDecimal != "undefined" && numberAfterDecimal != undefined)) {
                $("#amountMentionedExCurrency").val($("#amountMentionedExCurrency").val().slice(0, -numberAfterDecimal + decimalPos) + '0');
            }
        } else if (name === "amountSettledMentionedCurrency") {
            if (numberAfterDecimal >= 3 && (numberAfterDecimal != "undefined" && numberAfterDecimal != undefined)) {
                $("#amountSettledMentionedCurrency").val(computedRealNumber);
            } else {
                $("#amountSettledMentionedCurrency").val(realNumber);
            }
        } else if (name === "amountNationalExCurrency") {
            if (numberAfterDecimal >= 3 && (numberAfterDecimal != "undefined" && numberAfterDecimal != undefined)) {
                $("#amountNationalExCurrency").val(computedRealNumber);
            } else {
                $("#amountNationalExCurrency").val(realNumber);
            }
        } else if (name === "amountSettledNationalExCurrency") {
            if (numberAfterDecimal >= 3 && (numberAfterDecimal != "undefined" && numberAfterDecimal != undefined)) {
                $("#amountSettledNationalExCurrency").val(computedRealNumber);
            } else {
                $("#amountSettledNationalExCurrency").val(realNumber);
            }
        }
    }
}

function calculNationalAmountExecution() {

    if ($("#amountMentionedExCurrency").val().trim().length > 0) {

        var realNumber = $("#amountMentionedExCurrency").val();
        updateAmountExecution(realNumber, "amountMentionedExCurrency");

        $("#amountSettledMentionedCurrency").val(getNumericValue($("#amountMentionedExCurrency").val()))
        var amount = parseFloat(getExchangeRate()) * parseFloat(getNumericValue($("#amountMentionedExCurrency").val()))

        updateAmountExecution(amount, "amountNationalExCurrency");
        updateAmountExecution(amount, "amountSettledNationalExCurrency");

        formatElementsToString($("#amountNationalExCurrency"))
        formatElementsToString($("#amountMentionedExCurrency"))
        formatElementsToString($("#amountSettledMentionedCurrency"))
        formatElementsToString($("#amountSettledNationalExCurrency"))

        var url = $("#convertDigitExUrl").val();
        $.ajax({
            type: "GET",
            url: url,
            data: {
                conversationId: $("#conversationId").val(),
                amount: amount,
                locale: $("#locale").val()
            },
            success: function (data) {
                var digitLetter = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result + ' Francs CFA'
                var digitLetterSettled = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result + ' Francs CFA'

                $("#digitLetter").html(digitLetter)
                $("#digitLetterSettled").html(digitLetterSettled)
                $("#amountInExLetter").val(digitLetter)
                $("#amountInSettledLetter").val(digitLetterSettled)

                $("#amountMentionedExCurrency").valid()
                $("#amountSettledMentionedCurrency").valid()

                if ($("#amountMentionedExCurrency").valid() == false) {
                    $("#digitLetter").html("")
                    $("#amountInExLetter").val("")
                }
                if ($("#amountSettledMentionedCurrency").valid() == false) {
                    $("#digitLetterSettled").html("")
                    $("#amountInSettledLetter").val("")
                }
                removeDomClasses();
            }
        })

    } else {
        $("#digitLetter").html("")
        $("#digitLetterSettled").html("")
        $("#amountInExLetter").val("")
        $("#amountInSettledLetter").val("")
        $("#amountNationalExCurrency").val("")
        $("#amountSettledMentionedCurrency").val("")
        $("#amountSettledNationalExCurrency").val("");
    }
}

function calculateAmountSettledNationalExCurrency() {

    if ($("#amountSettledMentionedCurrency").val().trim().length > 0) {
        var realNumber = $("#amountSettledMentionedCurrency").val();
        updateAmountExecution(realNumber, "amountSettledNationalExCurrency");

        var amount = parseFloat(getExchangeRate()) * parseFloat(getNumericValue($("#amountSettledMentionedCurrency").val()))
        updateAmountExecution(amount, "amountSettledNationalExCurrency");

        formatElementsToString($("#amountSettledMentionedCurrency"))
        formatElementsToString($("#amountSettledNationalExCurrency"))

        var url = $("#convertDigitExUrl").val();
        $.ajax({
            type: "GET",
            url: url,
            data: {
                conversationId: $("#conversationId").val(),
                amount: amount,
                locale: $("#locale").val()
            },
            success: function (data) {
                var digitLetterSettled = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result + ' Francs CFA'

                $("#digitLetterSettled").html(digitLetterSettled)
                $("#amountInSettledLetter").val(digitLetterSettled)
                $("#amountSettledMentionedCurrency").valid()

                if ($("#amountSettledMentionedCurrency").valid() == false) {
                    $("#digitLetterSettled").html("")
                    $("#amountInSettledLetter").val("")
                }
                removeDomClasses();
            }
        })

    } else {
        $("#digitLetterSettled").html("")
        $("#amountInSettledLetter").val("")
        $("#amountSettledNationalCurrency").val("");
    }
}

function removeDomClasses() {
    var amountSettleNatExCur = $("#amountSettledNationalExCurrency").val()
    var lang = $("#locale").val()
    var parseGivenVal
    if (lang === 'en') {
        parseGivenVal = parseFloat(amountSettleNatExCur.replace(/,/g, ''))
        fieldClassAction(parseGivenVal, amountSettleNatExCur)
    } else {
        var replacedAmountVal = amountSettleNatExCur.replace(/\s/g, '')
        parseGivenVal = parseFloat(replacedAmountVal.replace(/,/g, '.'))
        fieldClassAction(parseGivenVal, amountSettleNatExCur)
    }
}

function fieldClassAction(parseValue, origValue) {
    var domiciliationFields = ["executionDomNumber", "executionDomDate", "executionDomBankCode"];
    if (parseValue < 10000000 || origValue == "") {
        $.each(domiciliationFields, function () {
            $("#" + this).removeAttr("required").removeClass("mandatory");
        });
    } else {
        $.each(domiciliationFields, function () {
            $("#" + this).attr("required", true).addClass("mandatory");
        })
    }
}
