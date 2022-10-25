
function labelSetter(sourceId, item) {
  var val = $('#' + sourceId).val();
    if (val && item || !val && !item) {
        var itemPropertyToSet = ['name', 'description', 'nameI18n', 'description_fr'];
        var value = '';
        var addressValue = '';

        if (item) {
            for (var it in itemPropertyToSet) {
                var prop = itemPropertyToSet[it];
                value = item[prop];
                if (value) {
                    break;
                }
            }

            if (item['address1']) {
                addressValue = item['address1']
            }
        }
        var targetId = sourceId + 'Name';
        var addressField = sourceId + 'Address';

        if(targetId.indexOf('Code') > -1){
            targetId = sourceId.toString().replace("Code","") + 'Name';
            addressField = sourceId.toString().replace("Code","") + 'Address';
        }

        if(sourceId === "declarantCode" || sourceId === "exporterCode") {
            targetId = sourceId.toString().replace("Code","") + 'NameAddress';
        }

        if(sourceId.indexOf('docType_') > -1){
            targetId = "docTypeName_" + sourceId.charAt(sourceId.length-1)
        }

        if (targetId) {
            $('#' + targetId).val(value);
            $('#' + targetId).html(value);
        }
        if(addressField){
            $('#' + addressField).val(addressValue);
        }

    }
}

function openWarningDialog(warningMessage){
    var messageDlg = $("#messageDialog");
    var dlgMsg = messageDlg.find("#infoMessage");
    var messageOk = messageDlg.find("#okButton");
    var messageTitle = messageDlg.find("#messageDialogTitle");

    messageDlg.modal({backdrop: 'static'});
    messageTitle.text(msg('warningTitle'))
    dlgMsg.text(msg(warningMessage));
    messageOk.off("click").on("click", function(){
        messageDlg.modal("hide");
    });
}

function removeErrorMessageOnChange(errorElement, targetField) {
    var removeFunc =  function() {
        var $errorContainer = $(".errorContainer");
        if ($(this).val().trim()) {
            errorElement.parent("li").remove();
        }
        if ($errorContainer.find("li").length === 0) {
            $errorContainer.remove();
        }
    };

    if(targetField.attr("data-class") == "BootstrapDatepicker"){
        targetField.on("dp.change", removeFunc)
    }else{
        targetField.change(removeFunc)
    }

}

function getRimmAddressDetails(item, itemProperty) {
    if (item != null && item !== undefined) {
        var itemPrefix = itemProperty.substr(0, itemProperty.indexOf('_') + 1);
        var addr1 = item[itemPrefix + 'address1'] ? '\n'+item[itemPrefix + 'address1'] : '';
        var addr2 = item[itemPrefix + 'address2'] ? '\n'+item[itemPrefix + 'address2'] : '';
        var addr3 = item[itemPrefix + 'address3'] ? '\n'+item[itemPrefix + 'address3'] : '';
        var addr4 = item[itemPrefix + 'address4'] ? '\n'+item[itemPrefix + 'address4'] : '';
        return addr1 + addr2 + addr3 + addr4;
    }
}

function customBeanLoadBankData(attachmentItem) {
    const actionUrl = $('#beanLoadBankUrl').val()
    makeAjaxAutocomplete(attachmentItem, 'HT_BNK', 'code', 'code,description,email', 'code,description', labelSetter, '70', null, actionUrl)
}

function customBeanLoadCountryData(attachmentItem) {
    const actionUrl = $('#beanLoadCountryUrl').val()
    makeAjaxAutocomplete(attachmentItem, 'REF_CTY', 'code', 'code,description', 'code,description', labelSetter, '70', null, actionUrl)
}

