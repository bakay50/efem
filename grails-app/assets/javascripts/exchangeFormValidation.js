/**
 * Copyrights 2002-2020 Webb Fontaine
 * Developer: Gobou Appia
 * Date: 8/14/20
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

function showAjaxSuccessDlg(operationName) {
    var defaultMessage = ' operation successful';
    $("#successAjaxDialog").css({ padding: "10px" }); 
    replaceSuccessdlg();
    $("#successAjaxDialog #successAjaxDialogHeader").html(operationName + defaultMessage);
    
    $("#successAjaxDialog").modal({backdrop: "static"});
}


function replaceSuccessdlg() {
    var successOldBody = $("#successAjaxDialog .modal-body").clone();
    var successOldHeader = $("#successAjaxDialog .modal-header").clone();
    var successNewBody = $("#js_successAjaxDialog .modal-body").clone();
    var successNewHeader = $("#js_successAjaxDialog .modal-header").clone();
    $("#successAjaxDialog .modal-body").replaceWith(successNewBody);
    $("#successAjaxDialog .modal-header").replaceWith(successNewHeader);
}