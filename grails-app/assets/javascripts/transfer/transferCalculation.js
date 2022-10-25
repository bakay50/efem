
$(document).ready(function(){
    retrieveTransferCurrencyRate()
});

String.prototype.capitalize = function () {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

function retrieveTransferCurrencyRate() {
    const currencyCode = $("#currencyPayCode").val();
    if (currencyCode) {
        const url = $("#retrieveCurrencyRateUrl").val();
        $.ajax({
            type: "GET",
            url: url,
            async: false,
            data: {
                currencyCode: currencyCode,
                locale: $("#locale").val()
            },
            success: function (data) {
                if (data.rateValue) {
                    $("#ratePayment").val(data.rateValue);
                } else {
                    $("#ratePayment").val("");
                }
            }
        })
    }
}