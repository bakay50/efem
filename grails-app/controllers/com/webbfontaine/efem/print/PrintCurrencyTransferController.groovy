package com.webbfontaine.efem.print

import com.webbfontaine.efem.PrintUtils
import com.webbfontaine.efem.command.CurrencyTransferSearchCommand
import groovy.util.logging.Slf4j

@Slf4j('LOGGER')
class PrintCurrencyTransferController {
    def printResultService
    def currencyTransferSearchService
    static searchResultData = []

    def exportResult(CurrencyTransferSearchCommand searchCommand){
        PrintUtils.setExportLimitDate(searchCommand)
        searchResultData = currencyTransferSearchService.getSearchResults(searchCommand, params)
        printResultService.exportPrintResult(params.exportFormat, searchResultData.resultList, params, params.name_jasper, params.print_name, response)
    }

}
