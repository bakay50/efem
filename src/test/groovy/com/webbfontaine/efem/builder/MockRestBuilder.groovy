package com.webbfontaine.efem.builder

import grails.plugins.rest.client.RequestCustomizer
import grails.plugins.rest.client.RestResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class MockRestBuilder {

    //source: https://gist.github.com/ctoestreich/efb527f4b492c64bf378/1eb951334a79db7e005b5e7565c3743bd5dd8965

    MockRestBuilder(Map settings) {
        println 'Not calling super constructor due to NPE'
    }

    MockRestBuilder(RestTemplate restTemplate) {
        println 'Not calling super constructor due to NPE'
    }

    MockRestBuilder() {
        println 'Not calling super constructor due to NPE'
    }

    RestResponse post(String url, @DelegatesTo(RequestCustomizer.class) Closure customizer) {
        return new RestResponse(new ResponseEntity(HttpStatus.OK))
    }

    RestResponse get(String url, @DelegatesTo(RequestCustomizer.class) Closure customizer) {
        return new RestResponse(new ResponseEntity(HttpStatus.OK))
    }

    RestTemplate getRestTemplate() {
        return null
    }


    void setRestTemplate(RestTemplate restTemplate) {
        null
    }
}
