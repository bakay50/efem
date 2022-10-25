function resetCriteriaFields() {
    deleteValueOfActivatedInput();
    $("form").find(".rangeField").attr("disabled", "disabled");
    $("form").find("#commodityCode").attr("disabled", "disabled");
    $("#max").val($("#max option:first").val());
}

function deleteValueOfActivatedInput() {
    $("form .form-group .wfinput").each(function (index, value) {
            if (!(value.hasAttribute("disabled") || value.hasAttribute("readonly"))){
                $(this).val("");
            }
        }
    );
    $("form").find(".hasDatepicker").attr("disabled", "disabled");
}
