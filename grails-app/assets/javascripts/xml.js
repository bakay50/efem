$(document).ready(function(){
    const id = $("#id").val().trim()
    if(id){
        initExportXMLFunction();
    }else{
        initXmlUploadAjaxForm();
    }
});


function initXmlUploadAjaxForm() {
    var uploadDialogObject = {
        uploadDialogId: "importXmlDialog",
        fileInputId: "xmlFile",
        conversationId : $('#conversationId').val(),

        onUploadCompleteFunction: function (xhr) {
            onXMLUploadComplete(xhr.responseText);
        }
    };

    var submitButtonObject = {
        uploadDialogId: "importXmlDialog",
        submitButtonId: "startUpload",
        fileInputId: "xmlFile",
        errorMessage: xmlExtensionAlertMsg,
        acceptedFormatsArrayInLowerCase: ["xml"]
    };

    onStartUploadButtonClick(submitButtonObject);
    onUploadDialogFireButtonClick("importXmlDialog", "xmlImportButton");
    initUploadDialog(uploadDialogObject);
}

function onXMLUploadComplete(serverResponse) {

    if (serverResponse.indexOf("errorContainer xmlError") == -1) {
        $("#importXmlDialog").modal("hide");
        $(document.body).html(serverResponse)
        updateAction();
    } else {
        var xmlErrors = $("#xmlErrors");
        xmlErrors.html("<div></div>");
        $("#xmlErrors").html(serverResponse);
        $("#xmlErrors").addClass("wf-error-bold");

    }

    function updateAction(){
        $("#appMainForm").attr("action","save");
    }

}

function initExportXMLFunction() {
    $(".xmlExportButton").click(function (e) {
        e.preventDefault();
        var url = $("#exportXmlUrl").val();
        var type = $("#domainInstanceName").val();
        var id = $("#id").val();
        var dataToSend = $("#appMainForm").serializeArray();
        dataToSend.push({name: 'domainName', value: type});
        dataToSend.push({name: 'id', value: id});
        var hiddenForm = $("<form></form>")
            .attr("method", "POST")
            .attr("action", url).hide();
        $.each(dataToSend, function (idx, item) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", item["name"]);
            hiddenField.setAttribute("value", item["value"]);
            hiddenForm.append(hiddenField);
        });
        document.body.appendChild(hiddenForm[0]);
        hiddenForm[0].submit();
    });
}




