$(document).ready(function () {
    initShowTvfBtn();
    disabledFields(TVFFieldsToEnable);
});

var TVFFieldsToEnable = ["bankCode",
    "nationalityCode",
    "resident",
    "operType",
    "amountMentionedCurrency",
    "countryProvenanceDestinationCode",
    "provenanceDestinationBank",
    "bankAccountNocreditedDebited",
    "docType",
    "docRef",
    "docDate",
    "addUploadFile",
    "commentHeader",
    "amountMentionedCurrency",
];

var basedOn = $("#basedOn").val();

function loadTvf() {
    var tvfNumberField = $("#tvfNumber").val();
    var tvfDateField = $("#tvfDate").val();
    var requestType = $("#requestType").val();

    $.ajax(
        $("#loadTvfUrl").val(),
        {
            method: 'POST',
            data: {
                tvfNumber: tvfNumberField,
                tvfDate: tvfDateField,
                basedOn: basedOn,
                requestType: requestType
            },
            success: function (data) {
                $(document.body).html(data);
                enableFields();
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {

            },
            complete: function () {

            }
        }
    );
    return false;
}

function restartTvf() {
    $.ajax(
        $("#restartTvfUrl").val(),
        {
            method: 'GET',
            success: function (data) {
                $(document.body).html(data);
            }
        }
    );
}

function initShowTvfBtn() {
    $("#showTvfBtn").off().on('click', function () {
        var trNumber = $("#tvfNumber").val()
        var trYear = $("#tvfDate").val().split("/")
        var win = window.open($("#showTvfUrl").val() + "?trNumber=" + trNumber + "&trYear=" + trYear[2], '_blank')
        win.focus();
        return false;
    });
}

function configureShowTvfBtn() {
    var oldTvfNo = $("#tvfNoFromExchange").val()
    var tvfNumber = $("#tvfNumber").val()
    var oldTvfDate = $("#tvfDateFromExchange").val()
    var tvfDate = $("#tvfDate").val()

    if ((oldTvfNo == tvfNumber) && (oldTvfDate == tvfDate)) {
        initShowTvfBtn();
        $("#showTvfBtn").removeClass("icon-disabled").addClass("icon-enabled")
    } else {
        $("#showTvfBtn").unbind('click').removeClass("icon-enabled").addClass("icon-disabled")
    }
}


function hideLoadTvfConfirmDialog() {
    $('#loadTvf' + 'ConfirmDialog').modal('hide');
}

function showLoadTvfConfirmDialog() {
    $('#loadTvf' + 'ConfirmDialog').modal({
        backdrop: 'static'
    });
}

function validateCertificateAndTvfFields() {
    var tvfNumberField = $("#tvfNumber");
    var tvfDateField = $("#tvfDate");
    return tvfNumberField.valid() && tvfDateField.valid() && !(tvfNumberField.val() === "" || tvfDateField === "")
}

function loadTvfConfirmButton() {
    if (validateCertificateAndTvfFields()) {
        $('#loadTvfConfirmDialog #loadTvfConfirmOper').unbind('click').bind('click', function () {
            loadTvf();
            hideLoadTvfConfirmDialog()
        });
        showLoadTvfConfirmDialog()
    }
}

function enableFields() {
    if (basedOn === "TVF") {
        enableListOfFields(TVFFieldsToEnable)
    } else if (basedOn === "SAD") {
        enableListOfFields(SADFieldsToEnable)
    }
}

function enableListOfFields(listOfFields) {
    $(":submit").attr("disabled", false);
    var sadInstanceId = $("#sadInstanceId").val();
    var tvfInstanceId = $("#tvfInstanceId").val()
    $.each(listOfFields, function () {
        if (typeof $("#" + this).val() != "undefined") {
            $("#" + this).removeAttr("disabled", "disabled");
            $("#" + this).removeAttr("readonly", "readonly");
        }
    });

    if (basedOn === "SAD" && sadInstanceId) {
        var declarationDetails = ["clearanceOfficeCode", "declarationSerial", "declarationNumber", "declarationDate"]
        $("#upload_sad").addClass("load-disabled");
        $.each(declarationDetails, function () {
            $("#" + this).attr("readonly", "readonly");
        });
    } else if (basedOn === "TVF" && tvfInstanceId) {
        var tvfDetails = ["tvfNumber", "tvfDate"]
        $(".upload_tvf").addClass("load-disabled");
        $.each(tvfDetails, function () {
            $("#" + this).attr("readonly", "readonly");
        });
    }
}

function disabledFields(fieldsToDisabled) {
    const requestType = $('#requestType').val()
    const TVF_LABEL = "TVF"
    const SAD_LABEL = "SAD"
    const EA_LABEL = "EA"
    if (requestType === EA_LABEL) {
        if (basedOn === TVF_LABEL) {
            disableFieldFromBaseOnTvf(fieldsToDisabled)
        }
        if (basedOn === SAD_LABEL) {
            disableFieldFromBaseOnSad(fieldsToDisabled)
        }
    }
}

function disableFieldFromBaseOnSad(fieldsToDisabled) {
    let clearanceOfficeCode = $("#clearanceOfficeCode").val()
    let declarationSerial = $("#declarationSerial").val()
    let declarationNumber = $("#declarationNumber").val()
    let declarationDate = $("#declarationDate").val()

    if (!clearanceOfficeCode || !declarationSerial ||
        !declarationNumber || !declarationDate) {
        $("#upload_sad").removeClass("load-disabled");
        $("#verifyOp").addClass("load-disabled");
        disableListOfFields(fieldsToDisabled)
    } else {
        let declarationDetails = ["clearanceOfficeCode", "declarationSerial", "declarationNumber", "declarationDate"];
        $("#upload_sad").addClass("load-disabled");
        $("#verifyOp").removeClass("load-disabled");
        $.each(declarationDetails, function () {
            $("#" + this).attr("readonly", "readonly");
        });
        $("#exportationTitleNo").attr("readonly", "readonly")
    }
}

function disableFieldFromBaseOnTvf(fieldsToDisabled) {
    let tvfNumber = $("#tvfNumber").val()
    let tvfDate = $("#tvfDate").val()

    if (!tvfNumber && !tvfDate) {
        $(".upload_tvf").removeClass("load-disabled");
        $("#verifyOp").addClass("load-disabled");
        disableListOfFields(fieldsToDisabled)
    } else {
        $("#exportationTitleNo").attr("readonly", "readonly")
    }
}

function disableListOfFields(fieldsToDisabled) {
    $(":submit").attr("disabled", true);
    $.each(fieldsToDisabled, function () {
        if (typeof $("#" + this).val() != "undefined") {
            $("#" + this).attr("disabled", "disabled");
            $("#" + this).attr("readonly", "readonly");
            $("#" + this).removeAttr("required", "true");
        }
    });
}


