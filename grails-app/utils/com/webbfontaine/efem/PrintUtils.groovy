package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UtilConstants
import org.joda.time.Days
import org.joda.time.LocalDate

class PrintUtils {

    private static boolean EXPORT_CURRENT_MONTH = AppConfig.exportCurrentMonthOnly()
    private static int MAX_DAYS_TO_EXPORT = AppConfig.maxDaysToExport()

    static setExportLimitDate(searchCommand){
        LocalDate firstDateOfMonth = new LocalDate(getFirstDateOfMonth())
        if(EXPORT_CURRENT_MONTH && periodIsTooLarge(searchCommand)){
            searchCommand.requestDate = firstDateOfMonth
            searchCommand.op_requestDate = UtilConstants.OP_GREATER_THAN
        }
    }

    static boolean periodIsTooLarge(searchCommand){
        (!searchCommand?.requestDateTo && searchCommand?.op_requestDate != UtilConstants.OP_EQUAL) || (searchCommand?.requestDateTo && Days.daysBetween(new LocalDate(searchCommand?.requestDateTo), new LocalDate(searchCommand?.requestDate)).days.abs() > MAX_DAYS_TO_EXPORT)
    }

    static Date getFirstDateOfMonth(){
        Calendar cal = Calendar.getInstance()
        cal.setTime(new Date())
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        return cal.getTime()
    }

}
