
$(document).ready(function(){
    disabledFields(SADFieldsToEnable);
});

var SADFieldsToEnable = ["dateOfBoarding", "bankCode", "commentHeader", "nationalityCode", "resident", "beneficiaryName",
    "beneficiaryAddress", "operType", "amountMentionedCurrency", "countryProvenanceDestinationCode", "provenanceDestinationBank",
    "bankAccountNocreditedDebited", "exportationTitleNo", "accountNumberBeneficiary", "docType", "docRef",
    "docDate", "addUploadFile"]

function loadSad(){
    var clearanceOfficeCode = $("#clearanceOfficeCode").val();
    var clearanceOfficeName = $("#clearanceOfficeName").text();
    var declarationSerial = $("#declarationSerial").val();
    var declarationNumber = $("#declarationNumber").val();
    var declarationDate = $("#declarationDate").val();
    var basedOn = $("#basedOn").val();
    var requestType = $("#requestType").val();

    $.ajax(
        $("#loadSadUrl").val(),
        {
            method: 'POST',
            data: {
                clearanceOfficeCode: clearanceOfficeCode,
                clearanceOfficeName: clearanceOfficeName,
                declarationSerial: declarationSerial,
                declarationNumber: declarationNumber,
                declarationDate: declarationDate,
                basedOn: basedOn,
                requestType: requestType
            },
            success: function(data) {
                $(document.body).html(data)
                enableFields();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {

            },
            complete: function(){

            }
        }
    );
}

function validateEsadFields(){
    var clearanceOfficeCode = $("#clearanceOfficeCode");
    var declarationSerial = $("#declarationSerial");
    var declarationNumber = $("#declarationNumber");
    var declarationDate = $("#declarationDate");

    var isValid = clearanceOfficeCode.valid() && declarationSerial.valid() && declarationNumber.valid() && declarationDate.valid()
    var isNotEmpty = !(clearanceOfficeCode.val() === "" || declarationSerial.val() === "" || declarationNumber.val() === "" || declarationDate.val() === "")

    return isValid && isNotEmpty
}

function hideLoadSadConfirmDialog() {
    $('#loadSadConfirmDialog').modal('hide');
}

function showLoadSadConfirmDialog() {
    $('#loadSadConfirmDialog').modal({
        backdrop: 'static'
    });
}

function loadEsadConfirmButton(){
    if(validateEsadFields()){
        $('#loadSadConfirmDialog #loadSadConfirmOper').unbind('click').bind('click', function () {
            loadSad();
            hideLoadSadConfirmDialog();
        })
        showLoadSadConfirmDialog();
    } else {
        $("#clearanceOfficeCode").valid();
        $("#declarationSerial").valid();
        $("#declarationNumber").valid();
        $("#declarationDate").valid();
    }
}

function showDeclaration(sadInstanceId){
    var win = window.open($("#showSadUrl").val() + "/" + sadInstanceId, '_blank')
    win.focus();
    return false;
}

function showSad(){
    var sadInstanceId = $("#sadInstanceId").val();
    if(sadInstanceId){
        showDeclaration(sadInstanceId);
    }else{
        loadOneDeclaration();
   }
}

function loadOneDeclaration(){
    var clearanceOfficeCode = $("#clearanceOfficeCode").val();
    var clearanceOfficeName = $("#clearanceOfficeName").text();
    var declarationSerial = $("#declarationSerial").val();
    var declarationNumber = $("#declarationNumber").val();
    var declarationDate = $("#declarationDate").val();

    $.ajax(
        $("#loadDeclarationUrl").val(),
        {
            method: 'POST',
            data: {
                clearanceOfficeCode: clearanceOfficeCode,
                declarationSerial: declarationSerial,
                declarationNumber: declarationNumber,
                declarationDate: declarationDate
            },
            success: function(response) {
                if(response.statusCode === 200){
                    showDeclaration(response.data);
                }else{
                    return false;
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {

            },
            complete: function(){

            }
        }
    );
}

function restartSad() {
    $.ajax(
        $("#restartSadUrl").val(),
        {
            method: 'GET',
            success: function(data) {
                $(document.body).html(data);
            }
        }
    );
}

