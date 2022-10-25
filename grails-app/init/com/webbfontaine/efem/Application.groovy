package com.webbfontaine.efem

import com.webbfontaine.grails.application.ConfigurationLoaderEnvironmentAware
import com.webbfontaine.grails.banner.GrailsBannerApp
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration implements ConfigurationLoaderEnvironmentAware {
    static void main(String[] args) {
        GrailsApp app = new GrailsBannerApp(Application)
        app.run(args)
    }
}