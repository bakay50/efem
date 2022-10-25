var deselectedCol = [];
var selectedCol = [];

$(document).ready(function () {
    initSearchFunctions();
});

$(window).load(function() {
    initDeselectedCol();
    initSelectedCol();
    manageColumnSelector();
});

function resetCriteriaFields() {
    searchClearForm();
    $("form").find(".rangeField, .hasOperatorFld").attr("disabled", "disabled");
}
function initSelectedCol(){
    $("#table-column-selector input:checkbox(:checked)").each(function () {
        var column = $(this).attr("name");
        selectedCol.push(column);
    });
    selectedCol = $(selectedCol).not(deselectedCol).get();
}

function initSearchFunctions(){
    handleCheckBox();

    $("#table-column-selector input:checkbox").on( "click", function(event) {
        var clazz = $(this).attr("name");
        if ($(this).is(":checked")) {
            isChecked(clazz);
        }else {
            isUnchecked(clazz)
        }
    });
    manageColumnSelector();
    handleColumnWidth();
}

function isChecked(item){
    selectedCol.push(item);
    var index = deselectedCol.indexOf(item);
    if (index !== -1) deselectedCol.splice(index, 1);
    $("." + item).css({"display": '', "width": '50px'});
    $('#searchResults').trigger('remotePaginateComplete');
}

function handleCheckBox(){
    $('#table-column-selector tr').click(function(event) {
        event.stopPropagation();
        if (event.target.type !== 'checkbox') {
            $(':checkbox', this).trigger('click');
        }
    });
}

function handleColumnWidth(){
    var widthSize = 100/(selectedCol.length + 1)

    $.each(selectedCol, function ( index, value){
        $("." + value).css({"display": '', "width": widthSize + 'px'});
    });
    $('.actions-th').css({"display": '', "width": widthSize + 'px'})
    $('.actions').css({"display": '', "width": widthSize + 'px'})

    $('#searchRes').css({'table-layout' : 'fixed'})
}

function isUnchecked(item){
    deselectedCol.push(item);
    var index = selectedCol.indexOf(item);
    if (index !== -1) selectedCol.splice(index, 1);
    $("." + item).css('display', 'none');
}

function initDeselectedCol(){
    $("#table-column-selector input:checkbox:not(:checked)").each(function () {
        var column = $(this).attr("name");;
        deselectedCol.push(column)
    });
}

function manageColumnSelector() {
    hideDeselected();
    showSelected();
    removeDisabledAttribute();
}

function hideDeselected() {
    $.each(deselectedCol, function (index, value) {
        var column = "#searchRes ." + value;
        $("#table-column-selector #" + value).prop('checked', false);
        $(column).css('display','none');
    });
}

function showSelected() {
    $.each(selectedCol, function (index, value) {
        $("#table-column-selector #" + value).prop('checked', true);
    });
}

function removeDisabledAttribute(){
    $("#search_exchange_column_selector").find("input:checked").each(function (index, value) {
        $(value).removeAttr("disabled");
    });
}