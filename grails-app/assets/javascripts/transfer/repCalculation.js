
$(document).ready(function(){
  
});

String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

function getNumericValue(fieldValue) {
    var numeric = parseFloat(reformatNumber(fieldValue));
    console.log("numeric ="+numeric)
    if (isNaN(numeric)) {
        numeric = 0;
        console.log("isNaN ="+numeric)

    }
    return numeric;
}

function returnNumberAfterDelimiter(number) {
    var transformNumber  = reformatNumber(number);
    if(transformNumber && transformNumber.toString().length <= 16){
        if(transformNumber.toString().split('.')[1] != undefined && transformNumber.toString().split('.')[1] != "undefined"){
            return (transformNumber.toString().split('.')[1]).length;
        }else{
            return 0
        }
    }else{
        return 0
    }
}

function updateRealNumber(realNumber){
    var numberAfterDeciaml = returnNumberAfterDelimiter(realNumber);
    if(numberAfterDeciaml == 1){
        return realNumber + '0';
    }else{
        return realNumber;
    }
}

function getExchangeRateOperation() {
    var basedOn = $("#basedOn").val().trim();
    var request_Type = $("#requestType").val().trim()
    if ((request_Type=="EA" && basedOn == "TVF") ||(request_Type=="EC")){
        return getNumericValue($("#currencyRate").val());
    }else if(request_Type=="EA" && basedOn == "SAD"){
        return getNumericValue($("#currencyPayRate").val());
    }
}

function checkAmount(basedOn,request_Type,option_rate,isCurrencyPayRate){
    if(option_rate === "Y"){
        if(isCurrencyPayRate === "Y"){
            if(request_Type === "EC"){
                return (parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(3);
            }else{
                if(basedOn === "SAD"){
                    return parseFloat(parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(0);
                }else{
                    return (parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(3);
                }
            }
        }else{
            if(request_Type === "EC"){
                return (parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(3);
            }else{
                if(basedOn === "SAD"){
                    return parseFloat(parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(0);
                }else{
                    return (parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue($("#amountMentionedCurrency").val()))).toFixed(3);
                }
            }
        }

    }else{
        if(request_Type == "EC"){
            return getNumericValue($("#amountMentionedCurrency").val())
        }else{
            if(basedOn == "SAD"){
                return parseFloat(getNumericValue($("#amountMentionedCurrency").val())).toFixed(0);
            }else{
                return getNumericValue($("#amountMentionedCurrency").val())
            }
        }
    }
}


function updateAmount(realNumber,name){
    var decimalPos = 2;
    var amountField = $("#"+name)
    if(realNumber){
        var numberAfterDeciaml = returnNumberAfterDelimiter(realNumber);
        if(name === "amountMentionedCurrency" || name === "finalAmountInDevise"){
            if(numberAfterDeciaml >= 3 && (numberAfterDeciaml != "undefined" && numberAfterDeciaml != undefined)){
                amountField.val(amountField.val().slice(0, -numberAfterDeciaml + decimalPos) + '0');
            }
        }else if(name === "amountNationalCurrency" || name === "finalAmount"){
            if(numberAfterDeciaml >= 3 && (numberAfterDeciaml != "undefined")){
                amountField.val(realNumber.toString().slice(0, -numberAfterDeciaml + decimalPos) + '0');
            }else{
                amountField.val(realNumber);
            }
        }
    }

}

function calculNationalAmount(basedOn,request_Type) {
    var amount
    var currencyCodes="currencyCode";
    var error_code = $('label.form_error[for="' + currencyCodes + '"]').text().trim();
    $("#currencyCode").valid();
    if(error_code.length === 0 && $("#currencyCode").val().length > 0 && $("#currencyRate").val().length > 0){
        if ($("#currencyCode").val().length > 0 && $("#currencyRate").val().length > 0) {
            if ($("#amountMentionedCurrency").val().length > 0) {
                var realNumber = $("#amountMentionedCurrency").val();
                updateAmount(realNumber,"amountMentionedCurrency");
                amount = checkAmount(basedOn,request_Type,"Y","N");
            } else {
                $("#digitoperation").html("")
                $("#amountInLetter").val("")
                $("#amountNationalCurrency").val("")
            }
        } else {
            if ($("#amountMentionedCurrency").val().length > 0) {
                var realNumber = $("#amountMentionedCurrency").val();
                updateAmount(realNumber,"amountMentionedCurrency");
                amount = checkAmount(basedOn,request_Type,"N","N");
            } else {
                $("#digitoperation").html("")
                $("#amountNationalCurrency").val("")
            }
        }
        updateAmount(amount,"amountNationalCurrency");
        formatElementsToString($("#amountNationalCurrency"))

        if ($("#amountMentionedCurrency").val().length > 0) {
            if(amount){
                var url = $("#convertDigitUrl").val();
                $.ajax({
                    type: "GET",
                    url: url,
                    async:false,
                    data: {
                        conversationId: $("#conversationId").val(),
                        amount: updateRealNumber(amount),
                        locale: $("#locale").val()
                    },
                    success: function (data, textStatus) {
                        var amountInLetter = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result.capitalize() + ' Francs CFA'
                        $("#digitoperation").html(amountInLetter)
                        $("#amountInLetter").val(amountInLetter)
                        $("#amountMentionedCurrency").valid()
                        if ($("#amountMentionedCurrency").valid() == false) {
                            $("#digitoperation").html("")
                            $("#amountInLetter").val("")
                        }
                    }
                })
            }

        } else {
            $("#digitoperation").html("")
            $("#amountInLetter").val("")
            $("#amountNationalCurrency").val("")
        }
    }else{
        $("#amountMentionedCurrency").val("");
        $("#amountMentionedCurrency").valid();
        $("#labelcurrencyoperation").html("");
    }
}


function calculNewNationalAmount(basedOn,request_Type) {
    var amount
    var currencyCodes="currencyPayCode";
    var error_code = $('label.form_error[for="' + currencyCodes + '"]').text().trim();

    $("#currencyPayCode").valid();
    if(error_code.length === 0 && $("#currencyPayCode").val().length > 0 && $("#currencyPayRate").val().length > 0){
        if ($("#currencyPayCode").val().length > 0 && $("#currencyPayRate").val().length > 0) {
            if ($("#amountMentionedCurrency").val().length > 0) {
                var realNumber = $("#amountMentionedCurrency").val();
                updateAmount(realNumber,'amountMentionedCurrency');
                amount  = checkAmount(basedOn,request_Type,"Y","Y");
            } else {
                $("#digitoperation").html("")
                $("#amountInLetter").val("")
                $("#amountNationalCurrency").val("")
            }
        } else {
            if ($("#amountMentionedCurrency").val().length > 0) {
                var realNumber = $("#amountMentionedCurrency").val();
                updateAmount(realNumber,'amountMentionedCurrency');
                amount = checkAmount(basedOn,request_Type,"N","N");

            } else {
                $("#digitoperation").html("")
                $("#amountInLetter").val("")
                $("#amountNationalCurrency").val("")
            }
        }

        updateAmount(amount,"amountNationalCurrency");
        formatElementsToString($("#amountNationalCurrency"))
        if ($("#amountMentionedCurrency").val().length > 0) {
            if(amount){
                var url = $("#convertDigitUrl").val();
                $.ajax({
                    type: "GET",
                    url: url,
                    async:false,
                    data: {
                        conversationId: $("#conversationId").val(),
                        amount: updateRealNumber(amount),
                        locale: $("#locale").val()
                    },
                    success: function (data, textStatus) {
                        var amountInLetter = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result + ' Francs CFA'
                        $("#digitoperation").html(amountInLetter)
                        $("#amountInLetter").val(amountInLetter)
                        $("#amountMentionedCurrency").valid()
                        if ($("#amountMentionedCurrency").valid() === false) {
                            $("#digitoperation").html("")
                            $("#amountInLetter").val("")
                        }
                    }
                })
            }
        } else {
            $("#digitoperation").html("")
            $("#amountInLetter").val("")
            $("#amountNationalCurrency").val("")
        }
    }else{
        $("#amountMentionedCurrency").val("");
        $("#amountMentionedCurrency").valid();
        $("#labelcurrencyoperation").html("");
    }
}

function doCalculateNationalAmountRepatriation(){
    var js_EaDisabledFields = $("#js_EaDisabledFields").val();
    var basedOn = $("#basedOn").val().trim();
    var request_Type = $("#requestType").val().trim()

    if ((request_Type=="EA" && basedOn == "TVF") ||(request_Type=="EC")){
        calculNationalAmount(basedOn,request_Type)
    }else if(request_Type=="EA" && basedOn == "SAD"){
        var officeCode = $("#clearanceOfficeCode").val();
        if(js_EaDisabledFields && js_EaDisabledFields.indexOf(officeCode) > -1){
            calculNewNationalAmount(basedOn,request_Type)
        }else{
            calculNationalAmount(basedOn,request_Type)
        }
    }
}

function calculateFinalAmount(){
    var amount
    var basedOn = $("#basedOn").val().trim();
    var request_Type = $("#requestType").val().trim()
    var finalAmountInDevise=$("#finalAmountInDevise");
    var finalAmount=$("#finalAmount");
    var finalAmountInWords=$("#finalAmountInWords");
    var currencyRate=$("#currencyRate");
    var currencyCode=$("#currencyCode");
    var currencyCodes="currencyCode";
    var error_code = $('label.form_error[for="' + currencyCodes + '"]').text().trim();

    currencyCode.valid();
    if(error_code.length === 0 && currencyCode.val().length > 0 && currencyRate.val().length > 0){
        if (currencyCode.val().length > 0 && currencyRate.val().length > 0) {
            if (finalAmountInDevise.val().length > 0) {
                var realNumber = finalAmountInDevise.val();
                updateAmount(realNumber,"finalAmountInDevise");
                amount = parseFloat(parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue(finalAmountInDevise.val()))).toFixed(0);
            } else {
                finalAmountInWords.html("")
                $("#amountFinalInLetter").val("")
                finalAmount.val("")
            }
        } else {
            if (finalAmountInDevise.val().length > 0) {
                var realNumber = finalAmountInDevise.val();
                updateAmount(realNumber,"finalAmountInDevise");
                amount = parseFloat(parseFloat(getExchangeRateOperation()) * parseFloat(getNumericValue(finalAmountInDevise.val()))).toFixed(0);
            } else {
                finalAmountInWords.html("")
                finalAmount.val("")
            }
        }
        updateAmount(amount,"finalAmount");
        formatElementsToString($("#finalAmount"))

        if (finalAmount.val().length > 0) {
            if(amount){
                var url = $("#convertDigitUrl").val();
                $.ajax({
                    type: "GET",
                    url: url,
                    async:false,
                    data: {
                        conversationId: $("#conversationId").val(),
                        amount: updateRealNumber(amount),
                        locale: $("#locale").val()
                    },
                    success: function (data, textStatus) {
                        var amountFinalInLetter = data.convert_result == '' || data.convert_result == null ? '' : data.convert_result.capitalize() + ' Francs CFA'
                        finalAmountInWords.html(amountFinalInLetter)
                        $("#amountFinalInLetter").val(amountFinalInLetter)
                        finalAmountInDevise.valid()
                        if (finalAmountInDevise.valid() == false) {
                            finalAmountInWords.html("")
                            $("#amountFinalInLetter").val("")
                        }
                    }
                })
            }

        } else {
            $("#amountFinalInLetter").val("")
            finalAmountInWords.html("")
            finalAmount.val("")
        }
    }else{
        finalAmountInDevise.val("");
        finalAmountInDevise.valid();
        $("#labelcurrencyoperation").html("");
    }
}

function retrieveCurrencyRate(){
    var currencyCode = $("#currencyCode").val();
    var url = $("#retrieveCurrencyRateUrl").val();

    $.ajax({
        type: "GET",
        url: url,
        async:false,
        data: {
            currencyCode: currencyCode,
            locale: $("#locale").val()
        },
        success: function (data) {
            if(data.rateValue){
                $("#currencyRate").val(data.rateValue);
                $("#labelCurrencyOperation").val(currencyCode);
                $("#labelBalanceAs").val(currencyCode);
                $("#labelfinalAmount").val(currencyCode);
            }else{
                $("#currencyRate").val("");
                $("#labelCurrencyOperation").val("");
                $("#labelBalanceAs").val("");
                $("#labelfinalAmount").val("");
            }
        }
    })
}