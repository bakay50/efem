var lngLnk
var requestType = $("#requestType").val();

$(document).ready(function () {
    addValidatorMethods()
    disableBankFields()
    customBeanLoadBankData('domiciliationBankCode')
    customBeanLoadBankData('bankCode')
    customBeanLoadCountryData('countryOfDestinationCode')
    customBeanLoadCountryData('countryPartyCode')
    geoAreaName()
    if (typeof requestType != "undefined" && requestType.trim() === 'EC') {
        handleSetAreaParty()
        handleClearEcCurrencyOnAreaPartySelected()
        handleSetCountryParty()
        convertAreaPartyName()
        setEcCurrencyCodePayment()
    }
});

$(".lang_link").bind('click', function (element) {
    if (isCreate() || isEdit() || isUpdate() || isVerify()) {
        event.preventDefault()
        lngLnk = element.currentTarget.href

        var chgLngConfirmDialog = $("#chgLngconfirmDialog");
        showChgLngConfirmDialog(chgLngConfirmDialog)

        var chgLngConfirmOper = chgLngConfirmDialog.find("#chgLngConfirmOper");
        chgLngConfirmOper.unbind('click').bind('click', proceedChgLng)

        var chgLngCancelOper = chgLngConfirmDialog.find("#chgLngCancelOper");
        chgLngCancelOper.unbind('click').bind('click', closeChgLng)
    }
});

function isFormChanged() {
    var formChanged = false

    $(":input:text").each(function (idx, name) {
        if (name.id !== "xof" && $("#" + name.id).val() !== '') {
            formChanged = true
        }
    });

    if ($("#attDocTable #attDocListBody tr").length > 0) {
        formChanged = true
    }

    return formChanged
}

function isCreate() {
    return location.href.includes('create')
}

function isEdit() {
    return location.href.includes('edit')
}

function isUpdate() {
    return location.href.includes('update')
}

function isVerify() {
    return location.href.includes('verify')
}


function showChgLngConfirmDialog(chgLngConfirmDialog) {
    chgLngConfirmDialog.removeClass('hide');
    chgLngConfirmDialog.modal({backdrop: 'static'});
}

function proceedChgLng() {
    location.href = initChangeLanguageLink(lngLnk)
}

function initChangeLanguageLink(link) {
    var newLink;
    var previousUrl = window.location.href
    if (link.toString().indexOf("verify") >= 0) {
        var translatedUrl = previousUrl.substring(0, previousUrl.indexOf("&lang"))
        newLink = (translatedUrl ? translatedUrl : previousUrl) + '&' + link.substr(link.indexOf("lang") + 0)
    } else if (link.toString().indexOf("update") >= 0) {
        newLink = link.toString().replace("update?", 'edit/' + $("#instanceId").val() + "?op=" + $("#op").val() + '&');
    } else {
        newLink = link;
    }
    return newLink;
}

function closeChgLng() {
    var chgLngConfirmDialog = $("#chgLngconfirmDialog");

    chgLngConfirmDialog.modal('hide');
}

function setBankCodeDropdown() {
    if (!hasUserProperties("#BankCodeListHasValue")) {
        $("#bankCode").removeAttr('onfocusout')
    }
    var ref_bank = hasUserProperties("#adbUserProps") ? "REF_ADB" : hasUserProperties("#jossoAvailable") ? "REF_BNK_FLG" : "HT_BNK";
    makeAjaxAutocomplete('bankCode', ref_bank, 'code,description', 'code,description', 'code,description', labelSetter, '70');
}

function disableBankFields() {
    const bankFields = ['bankCode', 'countryProvenanceDestinationCode', 'accountNumberBeneficiary', 'provenanceDestinationBank', 'bankAccountNocreditedDebited'];
    const geoArea = $("#geoArea").val();
    const countryDest = $('#countryOfDestinationCode')
    if (geoArea === "002") {
        clearDisable(bankFields, true)
    } else {
        clearDisable(bankFields, false)
    }

    if (countryDest.val() && geoArea !== "002") {
        manageEcCurrencyCodePayment(countryDest)

    }
}

function clearDisable(selector, disable) {
    let el = "#" + selector
    if ($.isArray(selector)) {
        $.each(selector, function (idx, sel) {
            clearDisable(sel, disable)
        })
    } else if (disable) {
        let elName = el.replace("Code", "Name")
        $(el).val('')
        $(el).attr('disabled', 'disabled')
        $(elName).text('')
        $(el + "-error").text('')
    } else if (!disable) {
        $(el).removeAttr('disabled')
    }
}

function manageEcCurrencyCodePayment(element) {
    const geoArea = $("#geoArea");
    const countryDest = $(element).val();
    const listCountryDest = $("#js_ListCountryOfDest").val();
    if (requestType === "EC") {
        if (countryDest !== "" && countryDest) {
            const idx = listCountryDest.indexOf(countryDest)
            checkIdxlistCountryDestAndArea(idx, geoArea.val())
            addRuleForCountryDestination("countryOfDestinationCode")
        } else {
            clearEcCurrencyCodePayment();
        }
    }
}

function updateEcCurrencyCodePayment() {
    var ecStatus = $('#ecStatus').val();
    if (!ecStatus) {
        $("#currencyCode").val("XOF");
        $("#currencyCode").attr("readonly", "readonly");
        $("#currencyName").text("Franc CFA - BCEAO");
        $("#labelcurrencyoperation").html($("#currencyCode").val())
        retrieveCurrencyRate()
        $("#amountMentionedCurrency").val("");
        $("#amountNationalCurrency").val("");
    }
}

function clearEcCurrencyCodePayment() {
    var ecStatus = $('#ecStatus').val();
    if (!ecStatus) {
        $("#currencyCode").val("");
        $("#currencyRate").val("");
        $("#currencyCode").removeAttr("readonly", "readonly");
        $("#currencyName").html("");
        $("#currencyCode-error").html("");
        $("#labelcurrencyoperation").html("");
        $("#amountMentionedCurrency").val("");
        $("#digitoperation").html("");
        $("#amountNationalCurrency").val("");
    }
}

function retrieveCurrencyRate(currencyCode = $("#currencyCode").val(), currencyRateField = 'currencyRate') {
    var url = $("#retrieveCurrencyRateUrl").val();
    const result = addRuleForCurrencyCode('currencyCode')
    if (result) {
        $('#currencyName').html('')
    } else {
        $.ajax({
            type: "GET",
            url: url,
            async: false,
            data: {
                currencyCode: currencyCode,
                locale: $("#locale").val()
            },
            success: function (data) {
                if (data.rateValue) {
                    $(`#${currencyRateField}`).val(data.rateValue);
                    $('#labelCurrencyOperation').val(currencyCode)
                } else {
                    $(`#${currencyRateField}`).val("");
//                    $("#currencyRate").val("");
                }
            }
        })
    }
}

function retrieveCurrencyRateRapatriation() {
    var currencyCode = $("#currencyCode").val();
    var url = $("#retrieveCurrencyRateUrl").val();
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        data: {
            currencyCode: currencyCode,
            locale: $("#locale").val()
        },
        success: function (data) {
            if (data.rateValue) {
                $("#currencyRate").val(data.rateValue);
                $('#labelCurrencyOperation').val(currencyCode)
                updateReceivedAmount()
            } else {
                $("#currencyRate").val("");
            }
        }
    })
}

function geoAreaName() {
    var geoArea = $("#geoArea").val();
    var url = $("#convertGeoAreaNameUrl").val();
    var locale = $("#locale").val()
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        data: {
            geoArea: geoArea,
            locale: locale
        },
        success: function (data) {
            var geoAreaData = data.convert_result
            if (geoArea) {
                (locale === 'fr') ? $("#geoAreaName").text(geoAreaData.description_fr) : $("#geoAreaName").text(geoAreaData.description)
            } else {
                $("#geoAreaName").val("");
            }
        }
    })
}



function addValidatorMethods() {
    $.validator.addMethod("checkCurrencyCode", function (value, element) {
    }, msg("exchange.errors.currencyPayCode_XOF"))

    $.validator.addMethod("checkCountryDest", function (value, element) {

    }, msg("exchange.errors.CountryDest"))

    $.validator.addMethod("checkCountryDestination", function (value, element) {
    }, msg('exchange.errors.countryProvenanceDestinationCode.invalid'))

    $.validator.addMethod("checkCountryProvenanceDestinationCode", function (value, element) {
    }, msg("exchange.countryProvenanceDestinationCode.error"))

    $.validator.addMethod("checkCountryParty", function (value, element) {
    }, msg("exchange.errors.CountryDest"))
}

function addRuleForCountryDestination(element) {
    let mainForm = $('#appMainForm').validate();
    const countryDest = $('#countryOfDestinationCode').val().trim();
    var counbtryProhibited = $("#js_CI_value").val().trim()
    $(`#${element}`).rules('add', {
        checkCountryDestination: {
            depends: function () {
                if (requestType.trim() == "EC") {
                    if (countryDest == counbtryProhibited) {
                        $('#countryOfDestinationName').html('')
                        return true
                    } else {
                        return false
                    }

                }
            }
        }
    })
    mainForm.element("[id=" + element + "]")
}

function addRuleForCountryDest(element) {
    let mainForm = $('#appMainForm').validate();
    const geoArea = $("#geoArea").val();
    const area1 = $("#js_GeoArea1").val();
    const area3 = $("#js_GeoArea3").val();
    const countryDest = $('#countryOfDestinationCode').val().trim();
    const listCountryDest = $("#js_ListCountryOfDest").val();
    $(`#${element}`).rules('add', {
        checkCountryDest: {
            depends: function () {
                if (requestType.trim() === "EC") {
                    if (geoArea === area1 && listCountryDest.indexOf(countryDest) !== -1) {
                        return false
                    } else if (geoArea === area3 && listCountryDest.indexOf(countryDest) === -1) {
                        return false
                    } else {
                        $('#countryOfDestinationName').html('')
                        return true
                    }

                }
            }
        }
    })
    mainForm.element("[id=" + element + "]")
}

function addRuleForCountryProvenanceDestinationCode(element) {
    let mainForm = $('#appMainForm').validate();
    const geoArea = $('#geoArea').val()
    const geoArea1 = $('#js_GeoArea1').val()
    const geoArea3 = $('#js_GeoArea3').val()
    const CI_value = $('#js_CI_value').val()
    $(element).rules('add', {
        checkCountryProvenanceDestinationCode: {
            depends: function () {
                if (requestType === 'EC') {
                    if ((geoArea === geoArea1 || geoArea === geoArea3) && $(element).val().toString().toUpperCase() === CI_value) {
                        $('#countryProvenanceDestinationName').html('')
                        return true
                    } else {
                        return false
                    }
                }
            }
        }
    })
    mainForm.element("[id=" + $(element).attr('name') + "]")
}


function addRuleForCurrencyCode(element) {
    let appMainForm = $('#appMainForm').validate();
    const geoArea = $("#geoArea").val();
    const areaPartyCode = $('#areaPartyCode').val()
    const currencyCode = $('#currencyCode').val();
    const countryDest = typeof $('#countryOfDestinationCode').val() == "undefined" ? "" : $('#countryOfDestinationCode').val().trim();
    const listCountryDest = $("#js_ListCountryOfDest").val();
    let result = false
    const geoArea1 = $("#js_GeoArea1").val();
    const geoArea2 = $("#js_GeoArea2").val();
    const geoArea3 = $("#js_GeoArea3").val();
    const DEVICE_XOF = "XOF"
    $(`#${element}`).rules('add', {
        checkCurrencyCode: {
            depends: function () {
                if (requestType.trim() === "EC") {
                    if (countryDest !== "" && geoArea !== "") {
                        if (listCountryDest.indexOf(countryDest) > -1) {
                            if ((geoArea === geoArea2)) {
                                return result = false
                            }
                            if (geoArea == geoArea1 && areaPartyCode == geoArea3 && currencyCode == DEVICE_XOF) {
                                return result = true
                            }
                        } else if (listCountryDest.indexOf(countryDest) === -1) {
                            if (geoArea === geoArea3) {
                                if (currencyCode === DEVICE_XOF && areaPartyCode === geoArea3) {
                                    return result = true
                                }
                            }
                            if (geoArea === geoArea2 && currencyCode === DEVICE_XOF) {
                                return result = true
                            }
                        }
                    } else {
                        return result = false
                    }
                }
            }
        }
    });
    appMainForm.element("[id=" + element + "]");
    return result
}

function checkIdxlistCountryDestAndArea(idx, area) {
    if (area === "001") {
        if (idx !== -1) {
            addRuleForCountryDest('countryOfDestinationCode')
        } else {
            addRuleForCountryDest('countryOfDestinationCode')
            clearEcCurrencyCodePayment()
        }
    }
    if (area === "002") {
        if (idx !== -1) {
            updateEcCurrencyCodePayment()
        } else {
            clearEcCurrencyCodePayment()
        }
    }
    if (area === "003") {
        if (idx === -1) {
            addRuleForCountryDest('countryOfDestinationCode')
        } else {
            addRuleForCountryDest('countryOfDestinationCode')
            clearEcCurrencyCodePayment()
        }
    }
}

function manageOperationType() {
    const requestTypeEC = $('#requestTypeEC').val()
    const operType = $('#operType').val()
    const operTypWithExportTitle = $('#operTypWithExportTitle').val()
    if (requestType === requestTypeEC) {
        if (operTypWithExportTitle === operType) {
            $('#exportationTitleNo').attr('required', true).addClass('mandatory')
        } else {
            $('#exportationTitleNo').removeAttr('required').removeClass('mandatory')
        }
    }
}

function handleSetAreaParty() {
    const geoArea = $('#geoArea')
    const area = $('#areaPartyCode')
    const areaName = $('#areaPartyName')
    geoArea.on('autocompleteselect', function (_, ui) {
        area.val(ui.item.itemModel.code)
        areaName.html(ui.item.itemModel.description)
        if (ui.item.itemModel.code == '002') {
            area.attr('readonly', 'readonly')
            area.removeClass('mandatory')
        } else {
            area.removeAttr('readonly')
            area.addClass('mandatory')
        }
    })
}

function handleSetCountryParty() {
    const geoArea = $('#geoArea')
    const countryOfDest = $('#countryOfDestinationCode')
    const countryOfParty = $('#countryPartyCode')
    const countryOfPartyName = $('#countryPartyName')
    countryOfDest.on('autocompleteselect', function (_, ui) {
        countryOfParty.val(ui.item.itemModel.code)
        countryOfPartyName.html(ui.item.itemModel.description)
        if (geoArea.val() == '002') {
            countryOfParty.attr("readonly", 'readonly')
            countryOfParty.removeClass('mandatory')
        } else {
            countryOfParty.removeAttr('readonly')
            countryOfParty.addClass('mandatory')
        }
        if (geoArea.val() !== '002') {
            handleSetEcCurrencyCodePaymentOnCountrySelected(countryOfParty)
        }
    })

}

function handleSetEcCurrencyCodePaymentOnCountrySelected(element) {
    const geoArea = $('#geoArea')
    const areaParty = $('#areaPartyCode')
    const listCountryDest = $('#js_ListCountryOfDest').val()
    const country = $(element)
    const idx = listCountryDest.indexOf(country.val())
    if (geoArea.val() === '001' && areaParty.val() === '001' && idx !== -1) {
        setEcCurrencyCodePayment()
    } else {
        addRuleForCountryParty('countryPartyCode')
        cleanEcCurrencyCodePayment()
    }
    if (geoArea.val() === '003' && areaParty.val() === '001' && idx !== -1) {
        setEcCurrencyCodePayment()
    } else {
        addRuleForCountryParty('countryPartyCode')
    }
}

function handleClearEcCurrencyOnAreaPartySelected() {
    const areaParty = $('#areaPartyCode')
    areaParty.on('autocompleteselect', function () {
        cleanEcCurrencyCodePayment()
        $('#countryPartyCode').val("")
        $('#countryPartyName').html("")
    })
}

function addRuleForCountryParty(element) {
    let mainForm = $('#appMainForm').validate()
    const areaParty = $("#areaPartyCode")
    const area1 = $("#js_GeoArea1")
    const area2 = $("#js_GeoArea2")
    const area3 = $("#js_GeoArea3")
    const listCountries = $("#js_ListCountryOfDest").val()
    const country = $('#countryPartyCode')
    $(`#${element}`).rules('add', {
        checkCountryParty: {
            depends: function () {
                if (areaParty.val() === area1.val() && listCountries.indexOf(country.val()) !== -1) {
                    return false
                } else if (areaParty.val() === area3.val() && listCountries.indexOf(country.val()) === -1) {
                    return false
                } else if (areaParty.val() == area2.val) {
                    return false
                } else {
                    $('#countryPartyName').html('')
                    return true
                }
            }
        }
    })
    mainForm.element("[id=" + element + "]")
}

function convertAreaPartyName() {
    const areaParty = $("#areaPartyCode").val();
    const url = $("#convertGeoAreaNameUrl").val();
    const locale = $("#locale").val()
    if (areaParty.length > 0) {
        $.ajax({
            type: "GET",
            url: url,
            async: false,
            data: {
                geoArea: areaParty,
                locale: locale
            },
            success: function (data) {
                const geoAreaData = data.convert_result
                if (areaParty) {
                    (locale === 'fr') ? $("#areaPartyName").text(geoAreaData.description_fr) : $("#areaPartyName").text(geoAreaData.description)
                } else {
                    $("#areaPartyName").val("");
                }
            }
        })
    }
}

function setEcCurrencyCodePayment() {
    const waemuCountries = $("#js_ListCountryOfDest").val()
    const country = $('#countryPartyCode')
    if (waemuCountries.indexOf(country.val()) !== -1) {
        $("#currencyCode").val("XOF");
        $("#currencyCode").attr("readonly", "readonly");
        $("#currencyName").text("Franc CFA - BCEAO");
        $("#labelcurrencyoperation").html($("#currencyCode").val())
        retrieveCurrencyRate()
    }
}

function cleanEcCurrencyCodePayment() {
    $("#currencyCode").val("");
    $("#currencyRate").val("");
    $("#currencyCode").removeAttr("readonly", "readonly");
    $("#currencyName").html("");
    $("#currencyCode-error").html("");
    $("#labelcurrencyoperation").html("");
    $("#amountMentionedCurrency").val("");
    $("#digitoperation").html("");
    $("#amountNationalCurrency").val("");
}
