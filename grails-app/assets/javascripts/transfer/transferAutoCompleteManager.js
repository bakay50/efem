function loadTransferAutoCompleteDataFromUserProperty() {

    if (hasUserProperties("#tinUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_CMP"), "code,description,address1,address2,address3,address4");
        makeAutocompleteByModel("importerCode", autocompleteSource, setImporterAutocompleteDetails, "100");
    } else {

        makeAjaxAutocomplete('importerCode',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setImporterAutocompleteDetails,'110');
    }
}

function hasUserProperties(userProps) {
    var hasUserProperty = $(userProps).val();
    return typeof hasUserProperty!="undefined" && hasUserProperty=="true"
}


function setImporterAutocompleteDetails(sourceId, item){
    if(item != null && item !== undefined){
        var itemProperty = "description";
        var name = item[itemProperty];
        var address = getRimmAddressDetails(item, itemProperty)
        $("#importerNameAddress").val(name + address);
    }else{
        $("#importerNameAddress").val("")
    }
}

function getNumericValues(fieldValue) {
    var numeric = parseFloat(reformatNumber(fieldValue));
    if (isNaN(numeric)) {
        numeric = 0;
    }
    console.log("numeric ="+numeric)
    return numeric;
}

function updateValue($element,value){
    $element.val(value);
    $element.formatNumber({format:$("#decimalNumberFormat").val(), locale: $('#locale').val()});
}
