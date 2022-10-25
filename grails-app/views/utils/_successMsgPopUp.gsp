<g:if test="${flash.successOperation == true}">
    <layout:javascript defer="true">
        showAjaxSuccessDlg('${flash.endOperation}');
    </layout:javascript>
</g:if>