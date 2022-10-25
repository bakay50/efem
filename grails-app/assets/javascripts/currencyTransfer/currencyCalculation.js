var lastGoodValue = 0;
function retrieveCurrencyTransferRate() {
    var currencyCode = $('#currencyCode').val()
    var url = $('#retrieveCurrencyRateUrl').val()
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        data: {
            currencyCode: currencyCode,
            locale: $('#locale').val()
        },
        success: function (data) {
            if (data.rateValue) {
                $('#currencyRate').val(data.rateValue)
            } else {
                $('#currencyRate').val("")
            }
        }
    })

    if($("#amountTransferred").val().trim().length > 0 && getNumericValue($("#currencyRate").val()) > 0 ){
        calculateAmountTransferredNat()
    }
}

function calculateAmountTransferredNat(){
    if($("#amountTransferred").val().trim().length > 0 && getNumericValue($("#amountTransferred").val()) > 0 && getNumericValue($("#currencyRate").val()) > 0 ){
        var amount = getNumericValue($("#currencyRate").val()) * getNumericValue($("#amountTransferred").val())
        $("#amountTransferredNat").val(amount)
        $("#amountTransferredNat").formatNumber({format:$("#decimalNumberFormat").val(), locale: $('#locale').val()});
    }
}

function calculateAmountTransferredRate() {
    if ($("#isPrefinancingWithoutEC").val() == "true" && $("#amountTransferred").val().trim().length > 0 && getNumericValue($("#amountTransferred").val()) > 0 && $("#amountRepatriated").val().trim().length > 0 && getNumericValue($("#amountRepatriated").val()) > 0) {
        var rate = (getNumericValue($("#amountTransferred").val()) / getNumericValue($("#amountRepatriated").val())) * 100
        if(rate > 100) {
            openWarningPopup("currencyTransfer.badAmountTransferred","");
            $("#amountTransferred").val(lastGoodValue);
        } else {
            (rate < 80) ?openWarningPopup("currencyTransfer.warningTransferRate",rate):"";
            lastGoodValue = $("#amountTransferred").val();
            $("#transferRate").val(rate)
            $("#transferRate").formatNumber({format: $("#decimalNumberFormat").val(), locale: $('#locale').val()});
        }
    }
    if(getNumericValue($("#amountTransferred").val()) <= 0){
        $("#transferRate").val(0)
    }
}

function getNumericValue(fieldValue) {
    var numeric = parseFloat(reformatNumber(fieldValue));
    if (isNaN(numeric)) {
        numeric = 0;
    }
    return numeric;
}

function openWarningPopup(popupMessage, value) {

    var warningDlg = $("#js_warnMsgDlg");
    var warnConfirmationMsg = warningDlg.find("#js_warningMsg");
    var warnConfirmationOk = warningDlg.find("#js_warningActionOK");

    warningDlg.removeClass("hide")
    warningDlg.modal({backdrop: 'static'});
    warnConfirmationMsg.empty();
    warnConfirmationMsg.append(msg(popupMessage, value));
    warnConfirmationOk.off("click").on("click", function () {
        warningDlg.modal('hide');
    });

}