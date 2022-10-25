package com.webbfontaine.efem.print

import com.print.PrintResult
import grails.plugins.jasper.JasperReportDef
import groovy.util.logging.Slf4j
import net.sf.jasperreports.engine.JRParameter
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.MessageSourceResourceBundle
import javax.servlet.http.HttpServletResponse
import java.text.DecimalFormat
import java.text.NumberFormat

import static com.webbfontaine.efem.constants.PrintConstants.*
import static com.webbfontaine.efem.signature.SignatureUtils.enableDigitalSignature
import static grails.plugins.jasper.JasperExportFormat.*

@Slf4j('LOGGER')
class PrintResultService {

    def grailsApplication
    def jasperService
    def reportDef
    def printResult
    Integer responseCode = null
    def messageSource
    def gwsSignatureRestService

    def exportPrintResult(String format, data = [], parameter = [:], String name_jasper, String name, HttpServletResponse response) {
        if (format == 'PDF' && data) {
            reportDef = new JasperReportDef(name: name_jasper,
                    fileFormat: PDF_FORMAT,
                    reportData: data,
                    parameters: parameter
            )
        } else if (format == 'XLS' && data) {
            reportDef = new JasperReportDef(name: name_jasper,
                    fileFormat: XLS_FORMAT,
                    reportData: data,
                    parameters: parameter
            )
            reportDef.addParameter(IS_IGNORE_CELL_BORDER, true)
            reportDef.addParameter(IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true)
            reportDef.addParameter(IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true)
            reportDef.addParameter(IS_COLLAPSE_ROW_SPAN, false)
            reportDef.addParameter(IGNORE_PAGE_MARGINS, true)
            reportDef.addParameter(IS_DETECT_CELL_TYPE, true)
            reportDef.addParameter(IS_IGNORE_PAGINATION, true)
        } else if (format == 'CSV' && data) {
            reportDef = new JasperReportDef(name: name_jasper,
                    fileFormat: CSV_FORMAT,
                    reportData: data,
                    parameters: parameter
            )
            reportDef.addParameter(IS_IGNORE_CELL_BORDER, true)
            reportDef.addParameter(IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true)
            reportDef.addParameter(IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true)
            reportDef.addParameter(IS_COLLAPSE_ROW_SPAN, false)
            reportDef.addParameter(IGNORE_PAGE_MARGINS, true)
            reportDef.addParameter(IS_DETECT_CELL_TYPE, true)
            reportDef.addParameter(IS_IGNORE_PAGINATION, true)
        }

        def sd = new MessageSourceResourceBundle(messageSource,LocaleContextHolder.locale)
        reportDef.addParameter(JRParameter.REPORT_RESOURCE_BUNDLE,sd)

        NumberFormat nf = NumberFormat.getNumberInstance(LocaleContextHolder.locale);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#,##0.00")

        reportDef.addParameter('df', df)
        reportDef.addParameter('language', LocaleContextHolder.locale.toString())
        byte[] reportBytes = jasperService.generateReport(reportDef).toByteArray()

        if(enableDigitalSignature && PDF_FORMAT.extension.toUpperCase() == format){
            LOGGER.debug("Signing PDF document with name: ${name_jasper}")
            def gwsSignature = gwsSignatureRestService.sign(reportBytes, "${name_jasper}.${PDF_FORMAT.extension}")
            if(gwsSignature){
                reportBytes = gwsSignature.body
            }
        }

        printResult = new PrintResult(name:name, fileFormat: reportDef.fileFormat, content: reportBytes)
        response.setHeader 'Content-disposition', "attachment; filename=\"${name}.${printResult.fileFormat.extension}\""
        response.setStatus(responseCode = HttpServletResponse.SC_OK)
        response.setContentLength(reportBytes.length)
        String curFormat=format.toLowerCase()
        if (curFormat=="xls"){
            curFormat="excel"
        }
       
        response.contentType = grailsApplication.config.grails.mime.types[curFormat]

        if(format == 'CSV'){ // for excel microsoft
            response.outputStream.write(0xEF)
            response.outputStream.write(0xBB)
            response.outputStream.write(0xBF)
        }
        response.outputStream << printResult.content
        response.outputStream.flush()
        response.outputStream.close()
    }
}