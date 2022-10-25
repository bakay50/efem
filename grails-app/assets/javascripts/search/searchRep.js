$(document).ready(function(){
    initSearchForm();
    transformRequestLink();
    listenAjaxCall();
});

function listenAjaxCall() {
    let open = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function() {
        this.addEventListener("loadend", event => transformRequestLink());
        open.apply(this, arguments);
    };
}

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
    toggleRangeFields('op_receptionDate', 'receptionDate', 'receptionDateTo');
}

function loadDataFromUserProperty() {
    makeAjaxAutocomplete('repatriationBankCode','HT_BNK','code,description','code,description','code,description',labelSetter,'70');

    if (hasUserProperties("#tinUserProps")) {
        var autocompleteSource = constructModelForAutocomplete(getLocalRef("REF_CMP"), "code,description,address1");
        makeAutocompleteByModel("code", autocompleteSource, setImporterAutocompleteDetails, "100");
    } else {
        makeAjaxAutocomplete('code',
            'HT_CMP',
            "code,description",
            'code,description,address1,address2,address3,address4',
            "code,description", setImporterAutocompleteDetails,'110');
    }

}

function transformRequestLink(){
    var resultList = document.getElementById("searchResBody");
    var links = resultList ? resultList.getElementsByTagName("a"):"";
    var requestCurrencyTransferUrl = $("#requestCurrencyTransferUrl").val();
    for (var i = 0; i<links.length; i++)
    {
        if(links[i].href.includes('REQUEST_CURRENCY_TRANSFER')){
            links[i].href = requestCurrencyTransferUrl + "/" + links[i].closest("tr").id;
        }
    }
}
