<g:set var="searchResultMax" value="${grailsApplication.config.com.webbfontaine.efem.exchange.search.maxLimit}"/>
<bootstrap:div style="width: 100%; margin: auto">
    <bootstrap:div class="export">
        <bootstrap:div class="menuContainer">
            <span class="menuButton">
                <g:set var="exportPdfParams" value="${params << [exportFormat: 'PDF', max : searchResultMax, extension: 'pdf', name_jasper: 'SearchExchangeReport.jasper', print_name: 'print-searchResult']}"/>
                <g:link class="pdf" controller="print" action="exportResult" params="${exportPdfParams}" >
                    <g:message code="exchange.pdf.export.label" default="PDF"/>
                </g:link>
            </span>
            <span class="menuButton">
                <g:set var="exportXlsParams" value="${params << [exportFormat: 'XLS', max : searchResultMax, extension: 'xlsx', name_jasper: 'SearchExchangeReport.jasper', print_name: 'print-searchResult']}"/>
                <g:link class="excel" controller="print" action="exportResult" params="${exportXlsParams}">
                    <g:message code="exchange.excel.export.label" default="EXCEL"/>
                </g:link>
            </span>
            <span class="menuButton">
                <g:set var="exportCsvParams" value="${params << [exportFormat: 'CSV', max : searchResultMax, extension: 'csv', name_jasper: 'SearchExchangeReport.jasper', print_name: 'print-searchResult']}"/>
                <g:link class="csv" controller="print" action="exportResult" params="${exportCsvParams}">
                    <g:message code="exchange.csv.export.label" default="CSV"/>
                </g:link>
            </span>
        </bootstrap:div>
    </bootstrap:div>
</bootstrap:div>