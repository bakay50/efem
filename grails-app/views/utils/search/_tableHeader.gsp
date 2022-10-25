<g:set var="searchParams" value="${searchCommand.getSearchParams(searchCommand)}"/>

<g:hiddenField name="searchUrl" value="${createLink(action: actionName, params: searchParams)}" disabled="disabled"/>

<g:set var="resultFields" value="${searchCommand.resultFields}"/>

<g:each in="${resultFields}">
    <g:set var="fld" value="${results.get(it.name)}"/>
    <th style="${fld.width() > 1 ? "width:${fld.width()}" : ''}%"
        class="j_searchResHeader ${it.name}"
        title="${message(code: fld.fullName())}">
     <g:set var="displayNameArgs" value="${fld.displayNameArgs().collect { layout.message(code: it) }}"/>

        <g:if test="${fld.sortable()}">
            <%

                def currentSort = ''
                if (params.sort == it.name) {
                    if (params.order == 'asc') {
                        currentSort = " \u25B2"
                    } else if (params.order == 'desc') {
                        currentSort = " \u25BC"
                    }
                }
                def order = (params.sort == it.name && params.order == 'asc') ? 'desc' : 'asc'
            %>

            <g:link onclick="sortByField('${it.name}', '${order}'); return false;">
                ${raw(layout.message(code: fld.displayName(), args: displayNameArgs))}${currentSort}
            </g:link>
        </g:if><g:else>
            ${raw(layout.message(code: fld.displayName(), args: displayNameArgs))}
        </g:else>

    </th>
</g:each>
