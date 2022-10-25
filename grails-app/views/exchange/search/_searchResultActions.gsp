<%@ page import="com.webbfontaine.efem.workflow.Operations; grails.util.Holders" %>
<g:each in="${domainInstance.operations.findAll { it instanceof com.webbfontaine.grails.plugins.workflow.operations.ViewOperation }}"
        var="viewOperation">
    <%
        def opLinksParams = [:]
        if (viewOperation.linksParams) {
            opLinksParams.putAll(viewOperation.linksParams)
            opLinksParams.remove('mapping')
        }
    %>
    <g:link id="${domainInstance.id}"
            title="${message(code: 'default.button.' + viewOperation.name + '.label', default: viewOperation.name)}"
            action="show" mapping="${viewOperation.linksParams['mapping']}" params="${opLinksParams}">
        <bootstrap:icon name="eye-open"/>
    </g:link>
</g:each>

<g:set var="editOperations"
       value="${domainInstance.operations.findAll {
           it instanceof com.webbfontaine.grails.plugins.workflow.operations.UpdateOperation && !it.properties.containsKey(com.webbfontaine.grails.plugins.workflow.operations.OperationConstants.HIDDEN_OPERATION)
       }}"/>

<g:set var="otherOperations"
       value="${domainInstance.operations.findAll {
           it instanceof com.webbfontaine.grails.plugins.workflow.operations.OperationClass && !it.properties.containsKey(com.webbfontaine.grails.plugins.workflow.operations.OperationConstants.HIDDEN_OPERATION)
       }}"/>

<g:set var="newWindow"
       value="${Holders.grailsApplication.config.com.webbfontaine.grails.plugins.workflow.searchResultActions.newWindow}"/>

<g:if test="${otherOperations.size() == 1}">
    <%
        def operationClass = otherOperations.iterator().next()
        def opClassParams = [id: domainInstance.id]
        opClassParams.put('op', operationClass.id)
        if (operationClass.linksParams) {
            opClassParams.putAll(operationClass.linksParams)
            opClassParams.remove('mapping')
        }
    %>
    <g:link params="${opClassParams}" mapping="${operationClass.linksParams['mapping']}"
            title="${message(code: 'default.button.' + operationClass.name + '.label', default: operationClass.name)}"
            action="edit" target="${newWindow ? '_blank' : ''}">
        <g:each in="${otherOperations}" var="operation">
            <bootstrap:icon name="${operation.properties['icon']}"/>
        </g:each>
    </g:link>
</g:if>

<g:elseif test="${otherOperations.size() > 1}">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown"
       title="${message(code: 'default.workflow.edit.label', default: 'Edit')}">
        <bootstrap:icon name="pencil"/>
    </a>
    <ul class="dropdown-menu" style="position: absolute; left: auto; top: auto; margin: 0 0 0 30px;">
        <g:each in="${otherOperations}" var="operation">
            <%
                opLinksParams = [id: domainInstance.id, op: operation.id]
                if (operation.linksParams) {
                    opLinksParams.putAll(operation.linksParams)
                    opLinksParams.remove('mapping')
                }
            %>
            <li style="text-align: left">
                <g:link params="${opLinksParams}" mapping="${operation.linksParams['mapping']}" action="edit"
                        target="${newWindow ? '_blank' : ''}">
                    ${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}
                </g:link>
            </li>

        </g:each>
    </ul>

</g:elseif>

<g:if test="${editOperations.size() == 1}">
    <%
        def operation = editOperations.iterator().next()
        def opParams = [id: domainInstance.id]
        opParams.put('op', operation.id)
        if (operation.linksParams) {
            opParams.putAll(operation.linksParams)
            opParams.remove('mapping')
        }
    %>
    <g:if test="${operation.name == Operations.OP_REQUEST_TRANSFER_ORDER}">
        <g:link params="${opParams}" mapping="${operation.linksParams['mapping']}" controller="transferOrder"
                title="${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}"
                action="create" target="${newWindow ? '_blank' : ''}">
            <bootstrap:icon name="pencil"/>
        </g:link>
    </g:if>
    <g:else>
        <g:link params="${opParams}" mapping="${operation.linksParams['mapping']}"
                title="${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}"
                action="edit" target="${newWindow ? '_blank' : ''}">
            <bootstrap:icon name="pencil"/>
        </g:link>
    </g:else>
</g:if>
<g:elseif test="${editOperations.size() > 1}">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown"
       title="${message(code: 'default.workflow.edit.label', default: 'Edit')}">
        <bootstrap:icon name="pencil"/>
    </a>
    <ul class="dropdown-menu" style="position: absolute; left: auto; top: auto; margin: 0 0 0 30px;">
        <g:each in="${editOperations}" var="operation">
            <%
                opLinksParams = [id: domainInstance.id, op: operation.id]
                if (operation.linksParams) {
                    opLinksParams.putAll(operation.linksParams)
                    opLinksParams.remove('mapping')
                }
            %>
            <li style="text-align: left">
                <g:if test="${operation.name == Operations.OP_REQUEST_TRANSFER_ORDER}">
                    <a href="javascript:void(0)" class="js_requestTransferOrderEA"
                       data-url="${createLink(controller: 'transferOrder', action: 'create', params: [id: opLinksParams.id])}"
                    >
                        ${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}
                    </a>
                </g:if>
                <g:else>
                    <g:link params="${opLinksParams}" mapping="${operation.linksParams['mapping']}" action="edit"
                            target="${newWindow ? '_blank' : ''}">
                        ${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}
                    </g:link>
                </g:else>
            </li>

        </g:each>
    </ul>

</g:elseif>

<g:each in="${domainInstance.operations}" var="operation">
    <g:set var="doConfirm"
           value="${operation.properties.containsKey("j_confirm") ? "if(!confirm('" + message(code: 'default.operationConfirm.message', args: [message(code: "default.button.${operation.name}.label", default: operation.name)], default: "Are you sure you want to ${operation.name} this application ?") + "')) return false" : ""}"/>
    <g:if test="${operation.isDirect()}">
        <%
            opLinksParams = [id: domainInstance.id]
            if (operation.linksParams) {
                opLinksParams.putAll(operation.linksParams)
                opLinksParams.remove('mapping')
            }
        %>
        <g:if test="${operation.isRemote()}">
            <%
                def url = createLink([mapping: operation.linksParams['mapping'], params: opLinksParams, contextPath: ''])
            %>
            <g:remoteLink update="searchResults" id="${domainInstance.id}"
                          title="${message(code: 'default.button.' + operation.name + '.label', default: operation.name)}"
                          action="${operation.action}"
                          params="${searchCommand.getSearchParams(searchCommand)}"
                          uri="${url}"
                          before="${doConfirm}"
                          onComplete="\$('#searchResults').trigger('directOperationComplete');">
                <g:if test="${operation.icon}">
                    <bootstrap:icon name="${operation.icon}"/>
                </g:if>
            </g:remoteLink>
        </g:if>
        <g:else>
            <g:link action="${operation.action}" mapping="${operation.linksParams['mapping']}" params="${opLinksParams}"
                    title="${message(code: 'default.button.' + operation.name + '.label', default: '')}">
                <g:if test="${operation.icon}">
                    <bootstrap:icon name="${operation.icon}"/>
                </g:if>
            </g:link>
        </g:else>
    </g:if>
</g:each>