package com.webbfontaine.efem

import grails.converters.JSON

class NotificationController {

    def toastNotificationService

    def index() {
        Integer numberOfPrefinancing = toastNotificationService.buildQueryNotification()
        Map result = [prefinancingCount: numberOfPrefinancing]
        render result as JSON
    }
}
