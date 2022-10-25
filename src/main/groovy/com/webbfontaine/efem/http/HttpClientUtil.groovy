package com.webbfontaine.efem.http

import grails.util.Environment
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.util.EntityUtils
import org.grails.web.util.WebUtils

import javax.servlet.http.HttpServletResponse

@Slf4j('log')
class HttpClientUtil {

    def static setRequestHeaders(HttpGet request, def sessionId, def jossoSessionId){
        if (Environment.isDevelopmentMode()) {
           request.addHeader("Cookie", "JOSSO_SESSIONID=${jossoSessionId}; JSESSIONID=${sessionId};");
         //   request.addHeader("Cookie", "JOSSO_SESSIONID=07C4326764A1C2E819319AE58E3CD4B0; JSESSIONID=BAF84AA34B53E630B1E69359C3385DB0;");
        } else {
            request.addHeader("Cookie", "JOSSO_SESSIONID=${getJossoSessionId()}");
        }
    }

    protected static getJossoSessionId(){
        def request = WebUtils.retrieveGrailsWebRequest().currentRequest

        return request.cookies.find { it.name.toString().startsWith("JOSSO_SESSIONID") && it?.value != "-" }?.value
    }

    def <T extends HttpUriRequest> Map getDataAsJson(T httpRequest) {
        getData(httpRequest, "json")
    }

    def static <T extends HttpUriRequest> Map getDataAsXml(T httpRequest) {
        getData(httpRequest, "xml")
    }

    def <T extends HttpUriRequest> Map getDataAsPdf(T httpRequest) {
        getData(httpRequest, "pdf")
    }

    public static <T extends HttpUriRequest> Map getData(HttpResponse response, String dataType) {

        try {
            int statusCode = response.getStatusLine().statusCode;
            def data = null
            if (response) {
                if (dataType == "json") {
                    data = jsonFromHttpResponse(response)
                }

                if (dataType == "xml") {
                    data = xmlFromHttpResponse(response)
                }

                if (dataType == "pdf") {
                    data = pdfFromHttpResponse(response)
                }
            }
            log.debug("statusCode = {}.", statusCode)
            return ["statusCode": statusCode, data: data]
        } catch (IllegalArgumentException e) {
            log.error("", e)
            return ["statusCode": HttpServletResponse.SC_UNAUTHORIZED]
        }
        finally {
            response?.close()
        }

    }

    def static jsonFromHttpResponse(HttpResponse response) {
        def jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(EntityUtils.toString(response.getEntity()))
    }

    def static xmlFromHttpResponse(HttpResponse response) {
        return EntityUtils.toString(response.getEntity())
    }

    def static pdfFromHttpResponse(HttpResponse response) {
        EntityUtils.toByteArray(response.getEntity())
    }

    def static boolean isSuccess(int statusCode) { (200..299).contains(statusCode) }

}
