package com.webbfontaine.efem

import com.webbfontaine.efem.constants.EmailConstants
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.HttpHostConnectException
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.http.HttpStatus

@Slf4j('LOGGER')

class CallApiNotifService {
    def mailService
    def EMAIL_FROM = AppConfig.resolveEmailFrom() ?: EmailConstants.DEFAULT_EMAIL_FROM
    boolean ISQUEUINGENABLED = AppConfig.resolveEnabledQueueing()
    def EMAIL_TOADMINS = AppConfig.resolveEmailToAdmin() ?: EmailConstants.DEFAULT_EMAIL_TOADMINS
    def EMAIL_TOADMINS_SUBJECT = AppConfig.resolveEmailToAdminSubject() ?: EmailConstants.DEFAULT_EMAIL_TOADMINS_SUBJECT
    def EMAIL_TOADMINS_BODY = AppConfig.resolveEmailToAdminBody() ?: EmailConstants.DEFAULT_EMAIL_TOADMINS_BODY

    CredentialsProvider setBasicAuthentification() {
        CredentialsProvider provider = new BasicCredentialsProvider()
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("wf", "12345678")
        provider.setCredentials(AuthScope.ANY, credentials)
        return provider
    }

    StringEntity prepareParameter(HttpPost request, def params) {
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json")
        String inputJson = new JsonBuilder(params).toPrettyString()
        return new StringEntity(inputJson)
    }

    def sendnotif(def params, String url) {
        HttpPost request
        def result = null
        CloseableHttpClient client
        CloseableHttpResponse response
        try {
            request = new HttpPost("${url}")
            client = HttpClients.custom()
                    .setDefaultCredentialsProvider(setBasicAuthentification())
                    .build()

            request.setEntity(prepareParameter(request, params))
            response = client.execute(request)
            if (response.original.code != HttpStatus.OK){
                setNotifQueueParams(params)
            }
            return response
        } catch (ConnectException| HttpHostConnectException|IllegalArgumentException e) {
            LOGGER.error("Error Encountered ", e)
            setNotifQueueParams(params)
            result = [error : true]
            return result
        }
    }

    def setNotifQueueParams(def params){
        NotifQueue notifQueue = new NotifQueue()
        notifQueue.modName= params.modName
        notifQueue.type= params.mailType
        notifQueue.corpsus = new JsonBuilder(params).toPrettyString()
        notifQueue.typeError= "405: NOTIFWBS API SERVER IS UNAVAILABLE"
        sendFallNotif()
        if (ISQUEUINGENABLED) {
            NotifQueue result = NotifQueue.withNewSession {
                notifQueue.save(modName: notifQueue.modName, type: notifQueue.type,
                        corpsus: notifQueue.corpsus, typeError: notifQueue.typeError, flush: true, failOnError: true)
            }
            return result
        }
    }

    void sendFallNotif() {
        try {
            mailService.sendMail {
                to EMAIL_TOADMINS
                from EMAIL_FROM
                subject EMAIL_TOADMINS_SUBJECT
                html EMAIL_TOADMINS_BODY
            }
            LOGGER.debug("Message has been sent to ${EMAIL_TOADMINS} because mail notif server is unavailable")
        } catch (e) {
            LOGGER.error("", e)
        }
    }
}
