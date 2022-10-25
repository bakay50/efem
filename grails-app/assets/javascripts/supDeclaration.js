function validateDeclarationDetails() {
    var isDeclarationValid = true;
    $("#declarationListContainer").find(".wfinput").each(function () {
        if (!$(this).is('readonly')) {
            isDeclarationValid = $(this).valid() && isDeclarationValid;
        }
    });

    return isDeclarationValid;
}

function showSupDeclarationError(data) {
    $("#declarationListContainer").find("#supDeclarationListError").html(data.responseData);
}

function removeDeclarationErrors() {
    $("#supDeclarationListError .errorContainer").remove();
}


function loadSupDeclarationListTemplate(data) {
    removeDeclarationErrors()
    var declarationListTableBody = $("#declarationListTableBody")
    declarationListTableBody.html(data.template)
    setRemainingBalanceEADeclaredAmount(data.exchangeInstance)
    var displayRemainingBalanceTransferField = ($("#canDisplayRemainingBalanceTransfer").val() === 'true')
    if (displayRemainingBalanceTransferField) {
        setRemainingBalanceTransferDoneAmount(data.exchangeInstance)
    }
    cleanSupDeclaration()
}

function onSuccessAddRequest(data) {
    if (data.error == 'error') {
        showSupDeclarationError(data)
    } else {
        loadSupDeclarationListTemplate(data)
    }
}

function addSupDeclaration(e) {
    e.preventDefault()
    const url = $("#addSupDeclarationUrl").val()
    const validDeclarationDetails = validateDeclarationDetails()
    const clearanceOfficeCode = $("#supDeclarationAddRow #sdClearanceOfficeCode").val()
    const clearanceOfficeName = $("#supDeclarationAddRow #sdClearanceOfficeName").html()
    const declarationSerial = $("#supDeclarationAddRow #sdDeclarationSerial").val()
    const declarationNumber = $("#supDeclarationAddRow #sdDeclarationNumber").val()
    const declarationDate = $("#supDeclarationAddRow #sdDeclarationDate").val()
    const declarationAmountWriteOff = $("#supDeclarationAddRow #declarationAmountWriteOff").val()

    if (validDeclarationDetails && declarationAmountWriteOff.length > 0) {

        $.ajax({
            type: "POST",
            url: url,
            data: {
                clearanceOfficeCode: clearanceOfficeCode,
                clearanceOfficeName: clearanceOfficeName,
                declarationSerial: declarationSerial,
                declarationNumber: declarationNumber,
                declarationDate: declarationDate,
                declarationAmountWriteOff: declarationAmountWriteOff
            },
            success: function (data) {
                onSuccessAddRequest(data)
            }
        });
    }
}

function editSupDeclaration(elementId) {

    if ($("#flagdom").val().trim().length == 0) {
        const rank = elementId
        const editingRow = $('#editItemsHeader').clone(true)
        const clearanceOfficeCode = $('#sdClearanceOfficeCode_' + rank).text().trim()
        const clearanceOfficeName = $('#sdClearanceOfficeName_' + rank).text().trim()
        const declarationSerial = $('#sdDeclarationSerial_' + rank).text().trim()
        const declarationNumber = $('#sdDeclarationNumber_' + rank).text().trim()
        const declarationDate = $('#sdDeclarationDate_' + rank).text().trim()
        const declarationCifAmount = $('#declarationCifAmount_' + rank).text().trim()
        const declarationRemainingBalance = $('#declarationRemainingBalance_' + rank).text().trim()
        const declarationAmountWriteOff = $('#declarationAmountWriteOff_' + rank).text().trim()
        $(".supDecRow_" + rank).replaceWith(editingRow)

        $('#edSdClearanceOfficeCode').val(clearanceOfficeCode)
        $('#edSdClearanceOfficeName').html(clearanceOfficeName)
        $('#edSdDeclarationSerial').val(declarationSerial)
        $('#edSdDeclarationNumber').val(declarationNumber)
        $('#edSdDeclarationDate').val(declarationDate)
        $('#eddeclarationCifAmount').val(declarationCifAmount)
        $('#eddeclarationRemainingBalance').val(declarationRemainingBalance)
        $('#eddeclarationAmountWriteOff').val(declarationAmountWriteOff)
        makeDatepicker($('#edSdDeclarationDate'))
        $("#rankTr").text(rank)
        removeDeclarationErrors()
        loadRimBeanData()
        handleSaveSupDecalaration(rank)
        $("#cancelSupDeclaration").click(cancelSupDeclaration)
    }
}

function handleSaveSupDecalaration(rank) {
    $('#saveSupDeclaration').click(function () {
        const clearanceOfficeCode = $('#edSdClearanceOfficeCode').val().trim()
        const clearanceOfficeName = $('#edSdClearanceOfficeName').html()
        const declarationSerial = $('#edSdDeclarationSerial').val().trim()
        const declarationNumber = $('#edSdDeclarationNumber').val().trim()
        const declarationDate = $('#edSdDeclarationDate').val().trim()
        const declarationCifAmount = $('#eddeclarationCifAmount').val().trim()
        const declarationRemainingBalance = $('#eddeclarationRemainingBalance').val().trim()
        const declarationAmountWriteOff = $('#eddeclarationAmountWriteOff').val().trim()
        const allFieldsAreFilled = clearanceOfficeCode.length
        if (allFieldsAreFilled > 0) {
            $.ajax({
                type: "POST",
                url: $("#editSupDeclarationUrl").val(),
                data: {
                    rank: rank,
                    clearanceOfficeCode: clearanceOfficeCode,
                    clearanceOfficeName: clearanceOfficeName,
                    declarationSerial: declarationSerial,
                    declarationNumber: declarationNumber,
                    declarationDate: declarationDate,
                    declarationCifAmount: declarationCifAmount,
                    declarationRemainingBalance: declarationRemainingBalance,
                    declarationAmountWriteOff: declarationAmountWriteOff
                },
                success: function (data) {
                    onSuccessAddRequest(data)
                }
            })
        }
    })
}

function removeSupDeclaration(elementId) {
    $.ajax({
        type: "POST",
        url: $('#deleteSupDeclarationUrl').val(),
        data: {
            rank: elementId,
            clearanceOfficeCode: $('#sdClearanceOfficeCode_' + elementId).text().trim(),
            declarationSerial: $('#sdDeclarationSerial_' + elementId).text().trim(),
            declarationNumber: $('#sdDeclarationNumber_' + elementId).text().trim(),
            declarationDate: $('#sdDeclarationDate_' + elementId).text().trim(),
        },
        success: function (data) {
            onSuccessAddRequest(data)
        }
    })
}

function cancelSupDeclaration() {
    const conversationId = $("#conversationId").val()
    $.ajax({
        type: "POST",
        url: $("#cancelEditSupDeclarationUrl").val(),
        data: {
            conversationId: conversationId
        },
        success: function (data) {
            onSuccessAddRequest(data)
        }
    })
}

function updateDateSupDec(value, element) {
    $("#declarationListTableBody" + "#" + element).val(value)
    $("#" + element).val(value)
}

function loadRimBeanData() {
    makeAjaxAutocomplete('edSdClearanceOfficeCode', 'HT_CUO', 'code,description', 'code,description', 'code,description', labelSetter, '70')
}

function setRemainingBalanceEADeclaredAmount(props) {
    $('#remainingBalanceDeclaredAmount').val(props.remainingBalanceDeclaredAmount)
    $('#remainingBalanceDeclaredNatAmount').val(props.remainingBalanceDeclaredNatAmount)
}

function setRemainingBalanceTransferDoneAmount(props) {
    $('#remainingBalanceTransferDoneAmount').val(props.remainingBalanceTransferDoneAmount)
    $('#remainingBalanceTransferDoneNatAmount').val(props.remainingBalanceTransferDoneNatAmount)
}

function cleanSupDeclaration() {
    const fielsToClean = ["sdClearanceOfficeCode", "sdDeclarationSerial", "sdDeclarationNumber", "sdDeclarationDate", "declarationAmountWriteOff"]
    $.each(fielsToClean, function () {
        $('#' + this).val("")
    })
    $('#sdClearanceOfficeName').html("")
}

