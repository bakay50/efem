<bootstrap:div id="searchResults">
    <g:render template="/utils/search/searchResult"
              model="[searchCommand: searchCommand,
                      domain       : domain,
                      max          : max,
                      resultList   : resultList,
                      totalCount   : totalCount]"/>
</bootstrap:div>
