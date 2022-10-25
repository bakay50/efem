const descriptionFields = ["bankName", "nationalityName", "countryProvenanceDestinationName", "clearanceOfficeName", "countryOfDestinationName",
    "exporterNameAddress", "importerNameAddress", "countryBenefBankName", "currencyPayName", "nameAndAddress", "currencyName"];
const fieldsWithOtherFormat = ["exporterCode", "importerCode", "code"];
function loadAutoCompleteDataFromUserProperty() {

    if (hasUserProperties("#decUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_DEC"), "code,description,address1,address2,address3,address4");
        makeAutocompleteByModel("declarantCode", autocompleteSource, setDeclarantAutocompleteDetails, "110");
    } else {
        makeAjaxAutocomplete('declarantCode',
            'HT_DEC',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setDeclarantAutocompleteDetails,'110');
    }

    if (hasUserProperties("#tinUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_CMP"), "code,description,address1,address2,address3,address4");
        makeAutocompleteByModel("exporterCode", autocompleteSource, setExporterAutocompleteDetails, "100");
        makeAutocompleteByModel("importerCode", autocompleteSource, setImporterAutocompleteDetails, "100");
        makeAutocompleteByModel("code", autocompleteSource, setCompanyAutocompleteDetails, "100");
    } else {
        makeAjaxAutocomplete('exporterCode',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setExporterAutocompleteDetails,'110');
        makeAjaxAutocomplete('importerCode',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setImporterAutocompleteDetails,'110');
        makeAjaxAutocomplete('code',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setCompanyAutocompleteDetails,'110');
    }
}

function hasUserProperties(userProps) {
    var hasUserProperty = $(userProps).val();
    return typeof hasUserProperty!="undefined" && hasUserProperty=="true"
}

function setExporterAutocompleteDetails(sourceId, item){
    if(item != null && item !== undefined){
        var itemProperty = "description";
        var name = item[itemProperty];
        var address = getRimmAddressDetails(item, itemProperty)
        $("#exporterNameAddress").val(name + address);
    }
}

function setImporterAutocompleteDetails(sourceId, item){
    if(item != null && item !== undefined){
        var itemProperty = "description";
        var name = item[itemProperty];
        var address = getRimmAddressDetails(item, itemProperty)
        $("#importerNameAddress").val(name + address);
        $("#importerNameAddress").html(name + address);
    }
}

function setCompanyAutocompleteDetails(sourceId, item){
    if(item != null && item !== undefined){
        var itemProperty = "description";
        var name = item[itemProperty];
        var address = getRimmAddressDetails(item, itemProperty)
        $("#nameAndAddress").val(name + address);
    }
}

function setDeclarantAutocompleteDetails(sourceId, item){
    if(item != null && item !== undefined){
        var itemProperty = "description";
        var name = item[itemProperty];
        var address = getRimmAddressDetails(item, itemProperty)
        $("#declarantNameAddress").val(name + address);
        $("#declarantNameAddress").html(name + address);
    }
}

function limitFieldsBehavior(thisId,descriptionId) {
    var elementId = $("#" + thisId);
    var elementDescription = $(descriptionId);
    if (elementDescription.val() === '') {
        elementId.val('');
    }
    elementId.keydown(function (event) {
        if (event.keyCode !== 9) {
            elementDescription.val('')
        }
    });
}

function updateCurrencyRate(){
    var elts = "currencyRate";
    var elts_val = $("#" + elts).val()
    console.log("elts_val="+elts_val)
    if(typeof $("#" + elts).val() !== "undefined" && typeof $("#" + elts).val() !== undefined && $("#" + elts).val() !== null && $("#" + elts).val() !== ""){
        getNumericValues(elts_val);
        formatElementsToString($("#" + elts));
        updateReceivedAmount();
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

function updateReceivedAmount(){
    var receivedAmountInNationalCurrency = getNumericValues($("#receivedAmount").val()) * getNumericValues($("#currencyRate").val());
    console.log("receivedAmountInNationalCurrency ="+receivedAmountInNationalCurrency)
    updateValue($("#receivedAmountNat"),receivedAmountInNationalCurrency);
    updateValue($("#j_currencyRate"),receivedAmountInNationalCurrency);
    formatElementsToString($("#receivedAmountNat"))
}
function updateValue($element,value){
    $element.val(value);
    $element.formatNumber({format:$("#decimalNumberFormat").val(), locale: $('#locale').val()});
}

function setupAutocomplete() {
    let prevVal;

    let setter = {
        setTextOrValue :
            function($element,value){
                $.each($element,function() {
                    updateTextOrValue(this,value);
                });
            },
        elementNotEmpty :
            function($element){
                $.each($element,function(){
                    updateElementNotEmpty(this)
                });
            }
    };

    $(document.body).on("autocompleteselect",".ui-autocomplete-input",function(event, ui){
        if (ui) {
            prevVal = ui.item.value;
        }
    });

    $(document.body).on("autocompletechange",".ui-autocomplete-input",function(event, ui){
        var $descriptionElements = [];
        if ($(this).length > 0) {
            $descriptionElements.push($(this));
        }

        if($descriptionElements.length > 0 && setter.elementNotEmpty($descriptionElements)){
            clearValues($(this));
        } else if(prevVal && prevVal != $(this).val()) {
            clearValues($(this));
            if($descriptionElements) setter.setTextOrValue($descriptionElements,"");
        } else if (prevVal == undefined || prevVal == 'undefined') {
            clearValues($(this));
            if($descriptionElements) setter.setTextOrValue($descriptionElements,"");
        }
    });
}

function clearValues(value) {
    let targetId;
    let sourceId = value.attr("id");
    targetId = jQuery.inArray(sourceId, fieldsWithOtherFormat) >= 0 ? sourceId == "code" ? "nameAndAddress" : sourceId.replace("Code", "NameAddress") : sourceId.replace("Code", "Name");
    value.val("");
    $.trim(value.val());
    if(jQuery.inArray(targetId, descriptionFields) > -1) {
        $("#" + targetId).text('');
        $("#" + targetId).val('');
    }
}

function updateTextOrValue(element,value){
    if($(element)[0].value === undefined || typeof $(element)[0].value === "undefined") {
        $(element).text(value);
    } else {
        $(element).val(value);
    }
}

function updateElementNotEmpty(element){
    if($(element)[0].value === undefined || typeof $(element)[0].value === "undefined"){
        return !$(element).text();
    }else{
        return false;
    }
}
