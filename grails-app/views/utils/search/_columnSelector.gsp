<%@ page import="com.webbfontaine.efem.constants.UtilConstants; com.webbfontaine.efem.Config.FieldsConfiguration" %>
<g:set var="defaultColumns" value="${FieldsConfiguration.getDefaultColumnsForSearchResultByDomain(domain)}"/>
<table id="table-column-selector" class="table-hover">
    <g:each in="${defaultColumns}" var="col">
        <tr>
            <td><wf:checkBox name="${col.getKey()}" disabled="${col.getValue()}" value="${col.getValue()}"/></td>
            <td><g:message code="searchResult.${col.getKey()}.label"/></td>
        </tr>
    </g:each>
</table>
