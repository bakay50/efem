package com.print

import grails.plugins.jasper.JasperExportFormat


/**
 * Created by DEV on 20/05/14.
 */
class PrintResult {
    String name
    JasperExportFormat fileFormat
    byte[] content
}
