<%@ page import="com.webbfontaine.efem.constant.SearchTab" %>
<bootstrap:form>
    <bootstrap:tab class="bg-gray">
        <bootstrap:tabItem name="quickSearch" active="${searchCommand.searchTab == SearchTab.QUICK}">
            <g:render template="search/quickSearchCriteria" model="[searchCommand: searchCommand]"/>
        </bootstrap:tabItem>
        <bootstrap:tabItem name="advancedSearch" active="${searchCommand.searchTab == SearchTab.ADVANCED}">
            <g:render template="search/advancedSearchCriteria" model="[searchCommand: searchCommand]"/>
        </bootstrap:tabItem>
    </bootstrap:tab>
</bootstrap:form>