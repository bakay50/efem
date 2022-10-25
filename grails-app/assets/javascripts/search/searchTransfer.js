

$(document).ready(function(){
    initSearchForm();
});

function initSearchForm() {
    initSearchRangeFields();
    loadDataFromUserProperty();
}

function toggleRangeFields(opField, fieldFrom, fieldTo) {
    var operatorField = $("#" + opField);
    var elementFrom = $("#" + fieldFrom);
    var elementTo = $("#" + fieldTo);
    if (operatorField.val() != "") {
        if (operatorField.val() == "between") {
            elementFrom.removeAttr("disabled");
            elementTo.removeAttr("disabled");
        } else {
            elementFrom.removeAttr("disabled");
            elementTo.attr("disabled", "disabled");
            elementTo.val("")
        }
    } else {
        elementFrom.attr("disabled", "disabled");
        elementTo.attr("disabled", "disabled");
        elementFrom.val("");
        elementTo.val("");
    }
    var searchForm = $("#searchForm");
    makeDatepickersForTheListOfInputs(searchForm.find(".generate-datepicker"));
}

function enableFieldByOperator(opField, valField) {
    var operatorField = $("#" + opField);
    var valueField = $("#" + valField);
    if (operatorField.val() != "") {
        valueField.removeAttr("disabled");
    } else {
        valueField.attr("disabled", "disabled");
        valueField.val("");
    }
}

function initSearchRangeFields() {
    toggleRangeFields('op_requestDate', 'requestDate', 'requestDateTo');
    toggleRangeFields('op_executionDate', 'executionDate', 'executionDateTo');
}

function loadDataFromUserProperty() {
    makeAjaxAutocomplete('bankCode','HT_BNK','code,description','code,description','code,description',labelSetter,'70');

    if (hasUserProperties("#tinUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_CMP"), "code,description,address1");
        makeAutocompleteByModel("code", autocompleteSource, setImporterAutocompleteDetails, "100");
    } else {
        makeAjaxAutocomplete('importerCode',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setImporterAutocompleteDetails,'110');
    }

}
