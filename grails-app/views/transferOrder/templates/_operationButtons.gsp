<%@ page import="com.webbfontaine.grails.plugins.workflow.WorkflowService; com.webbfontaine.grails.plugins.workflow.operations.OperationClass; com.webbfontaine.grails.plugins.workflow.containers.Buttons; com.webbfontaine.grails.plugins.workflow.operations.CustomActionOperation; com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation;com.webbfontaine.grails.plugins.workflow.operations.OperationConstants" %>
<g:set var="groups" value="${operations.findAll(
        {
            it instanceof UpdateOperation && it.group && !it.properties.containsKey(OperationConstants.HIDDEN_OPERATION)
        })
        .groupBy({ it.group }
)}"/>
<g:each in="${operations}" var="entry">
    <g:set var="group" value="${entry instanceof UpdateOperation ? entry.group : null}"/>
    <g:set var="list" value="${group ? groups.remove(group) : [entry]}"/>
    <g:if test="${list}">
        <g:set var="grouped" value="${group && list?.size() > 1}"/>
        <g:if test="${grouped}">
            <div class="btn-group btn-operations">
        </g:if>
        <g:each in="${list}" var="operation" status="current">
            <g:set var="javaScriptClasses"
                   value="${operation.properties.keySet().findAll {
                       it.startsWith(OperationConstants.JAVA_SCRIPT_PREFIX)
                   }.join(' ')}"/>
            <g:set var="buttonClass"
                   value="${operation.properties.keySet().find {
                       it.startsWith(Buttons.BUTTON_PREFIX)
                   } ?: Buttons.BUTTON_SUCCESS}"/>
            <g:set var="disabled"
                   value="${operation.properties.containsKey(OperationConstants.DISABLED)}"/>
            <g:if test="${grouped}">
                <g:if test="${current == 1}">
                    <a aria-expanded="false" role="button" data-toggle="dropdown"
                       class="btn btn-operation ${buttonClass} dropdown-toggle" href="#">
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu pull-right">
                </g:if>
                <g:if test="${current}">
                    <li class="pull-right">
                </g:if>
            </g:if>
            <g:if test="${(operation instanceof UpdateOperation) && !operation.properties.containsKey(OperationConstants.HIDDEN_OPERATION)}">
                <g:submitButton class="btn btn-operation ${buttonClass}"
                                name="${WorkflowService.OPERATION_PREFIX}${operation.id}"
                                disabled="${disabled}"
                                value="${message(code: "default.button.${operation.name}.label", default: operation.name)}"/>
            </g:if>
            <g:if test="${operation instanceof CustomActionOperation}">
                <g:set var="action" value="${operation.action}"/>
                <g:submitButton class="btn btn-operation ${buttonClass}"
                                name="${WorkflowService.OPERATION_PREFIX}${operation.id}"
                                disabled="${disabled}"
                                value="${message(code: "default.button.${operation.name}.label", default: operation.name)}"/>
            </g:if>
            <g:if test="${operation instanceof OperationClass}">
                <g:render template="/operationButtons" model="[operations: operation.operations]"/>
            </g:if>
            <g:if test="${grouped && current}">
                </li>
            </g:if>
        </g:each>
        <g:if test="${grouped}">
            </ul>
        </div>
        </g:if>
    </g:if>

</g:each>