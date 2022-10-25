<header id="overview" class="jumbotron subhead">
        <bootstrap:div id="operationButtonsSection" name="operationButtonsSection"
                       style="float: right !important;" class="pull-right">
            <g:render plugin="wf-workflow" template="/operationButtons" model="[operations: bankInstance?.operations]"/>
            <bootstrap:linkButton class="btn Close" controller="bank" action="list"><g:message code="exec.button.close.label"
                                                                                                     default="Close"/></bootstrap:linkButton>
            <wf:ajaxProgress/>
        </bootstrap:div>
</header>