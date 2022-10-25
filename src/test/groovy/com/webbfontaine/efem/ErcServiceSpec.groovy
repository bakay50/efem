package com.webbfontaine.efem

import com.webbfontaine.efem.builder.MockRestBuilder
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugins.rest.client.RequestCustomizer
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class ErcServiceSpec extends Specification implements ServiceUnitTest<ErcService>, DataTest{

   def setup() {
       mockDomain(Exchange)
       grailsApplication.config.com.webbfontaine.efem.erc.retrieveAvd.url = "http://sample.com"
       RestBuilder.metaClass.constructor = { RestTemplate restTemplate ->
           return new MockRestBuilder()
       }
       AppConfig.metaClass.static.getJossoId = {
           "JOSSOID000"
       }
   }

    def cleanupSpec() {
        RestBuilder.metaClass.constructor = null
        AppConfig.metaClass.static.getJossoId = null
        SpringSecurityService.metaClass = null
    }

    void "test request in getAvdDetails"(){
        given:
        String initUrl = null
        def mockRequestCustomizer = Mock(RequestCustomizer)
        MockRestBuilder.metaClass.get = { String link, Closure closure ->
            initUrl = link
            closure.delegate = mockRequestCustomizer
            closure.call()
            return new RestResponse(new ResponseEntity(HttpStatus.OK))
        }

        when:
        service.getAvdDetails("BEN001")

        then:
        initUrl == "http://sample.com/BEN001"
        1 * mockRequestCustomizer.contentType(MediaType.APPLICATION_JSON_VALUE)

    }
}
