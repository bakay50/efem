$(document).ready(function () {
    initSearchForm();

});
makeAjaxAutocomplete('currencyCode', 'REF_CUR', 'code,description', 'code,description', 'code,description', labelSetter, '70');
makeAjaxAutocomplete('bankCode', 'HT_BNK', 'code,description', 'code,description', 'code,description', labelSetter, '70');

function initSearchForm() {
    initSearchRangeFields();
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
    toggleRangeFields('op_currencyTransferDate', 'currencyTransferDate', 'currencyTransferDateTo');
    toggleRangeFields('op_ecDate', 'ecDate', 'ecDateTo');
}

