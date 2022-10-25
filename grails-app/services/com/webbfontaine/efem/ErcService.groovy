package com.webbfontaine.efem

import grails.plugins.rest.client.RestBuilder
import groovy.util.logging.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletResponse
import java.nio.charset.Charset

@Slf4j(value = "LOGGER")
class ErcService {

    def getAvdDetails(String avdNum) {

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory())
        restTemplate.setMessageConverters([new StringHttpMessageConverter(Charset.forName("UTF-8"))])
        def rest = new RestBuilder(restTemplate)
        def response = rest.get(AppConfig.resolveRetrieveAvdURL()+"/${avdNum.toString()}") {
            header(HttpHeaders.COOKIE, "JOSSO_SESSIONID=${AppConfig.getJossoId()}")
            accept(MediaType.APPLICATION_JSON_VALUE)
            contentType(MediaType.APPLICATION_JSON_VALUE)

        }

        LOGGER.debug("eRC response status: $response.status, response value: $response.json")
        if (response.status != HttpServletResponse.SC_OK) {
            throw new IllegalArgumentException("Status for Web Service is not OK")
        }

        return response.json

    }

}
