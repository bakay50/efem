<g:if test="${totalCount > 0}">
    <div id="${divResults ?: 'searchResults'}" class="well">
        <g:render template="/utils/search/searchResultBody" model="[domain: domain]"/>
    </div>
</g:if>
<g:else>
    <g:if test="${flash.searchResultMessage}">
        <bootstrap:alert type="info">
            <%=flash.searchResultMessage%>
        </bootstrap:alert>
    </g:if>
</g:else>
