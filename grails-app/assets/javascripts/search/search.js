

$(document).ready(function(){
    initSearchForm();
    redirectEADocumentToTransferOrder();
});

function initSearchForm() {
    initSearchRangeFields();
    loadDataFromUserProperty();
    loadAutoCompleteDataFromUserProperty();
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
    toggleRangeFields("op_amountNationalCurrency", "amountNationalCurrency", "amountNationalCurrencyTo");
    toggleRangeFields('op_declarationDate', 'declarationDate', 'declarationDateTo');
    toggleRangeFields('op_registrationDateBank', 'registrationDateBank', 'registrationDateBankTo');
    toggleRangeFields('op_domiciliationDate', 'domiciliationDate', 'domiciliationDateTo');
    toggleRangeFields('op_tvfDate', 'tvfDate', 'tvfDateTo');
    toggleRangeFields('op_authorizationDate', 'authorizationDate', 'authorizationDateTo');
    toggleRangeFields('op_bankApprovalDate', 'bankApprovalDate', 'bankApprovalDateTo');
    toggleRangeFields('op_requestDate', 'requestDate', 'requestDateTo');
    toggleRangeFields('op_dov', 'dateOfValidity', 'dovTo');
}

function loadDataFromUserProperty() {
    makeAjaxAutocomplete('clearanceOfficeCode', 'HT_CUO', 'code,description', 'code,description', 'code,description', labelSetter, '70');
    if (hasUserProperties("#decUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_DEC"), "code,description");
        makeAutocompleteByModel("declarantCodeAdvanced", autocompleteSource, labelSetter, "110");
        makeAutocompleteByModel("declarantCodeQuick", autocompleteSource, labelSetter, "110");
    } else {
        makeAjaxAutocomplete('declarantCodeAdvanced',
            'HT_DEC',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", labelSetter,'110');
        makeAjaxAutocomplete('declarantCodeQuick',
            'HT_DEC',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", labelSetter,'110');
    }

    if (hasUserProperties("#tinUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_CMP"), "code,description,address1");
        makeAutocompleteByModel("importerCode", autocompleteSource, setImporterAutocompleteDetails, "100");
        makeAutocompleteByModel("importerCodeQuick", autocompleteSource, labelSetter, "100");
    } else {
        makeAjaxAutocomplete('importerCode',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setImporterAutocompleteDetails,'110');
        makeAjaxAutocomplete('importerCodeQuick',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", labelSetter,'110');
    }

    if (hasUserProperties("#adbUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_ADB"), "code,description");
        makeAutocompleteByModel("bankCode", autocompleteSource, labelSetter, "100");
        makeAutocompleteByModel("bankCodeQuick", autocompleteSource, labelSetter, "100");
    } else {
        makeAjaxAutocomplete('bankCode',
            'HT_BNK',
            "code,description",
            'code,description',
            "code,description", labelSetter,'110');
        makeAjaxAutocomplete('bankCodeQuick',
            'HT_BNK',
            "code,description",
            'code,description',
            "code,description", labelSetter,'110');
    }

}

function redirectEADocumentToTransferOrder() {
    $(document).on("click", "a.js_requestTransferOrderEA", function(event) {
        event.preventDefault()
        const url = event.target.dataset.url
        const redirect = window.open(url, '_blank')
        redirect.focus()
        return false
    })
}
