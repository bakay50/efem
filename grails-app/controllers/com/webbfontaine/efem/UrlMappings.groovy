package com.webbfontaine.efem

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        group "/api", {
            get "/eaDocuments/$tvfNumber?/$tvfDate?"(controller: 'rest', action: 'retrieveAllEADocuments')
        }

        "/"(controller:"exchange",action:'index')
        "/access-denied"(view:'/access-denied')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
