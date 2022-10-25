package com.webbfontaine.efem.http

import grails.util.Holders
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy

import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import java.security.SecureRandom

class HttpClientFactory {
    static CloseableHttpClient createClientWithDefaultSSL(){
        SSLContext sslContext = SSLContext.getInstance("TLS")
        sslContext.init(new KeyManager[0], [new DefaultTrustManager()] as TrustManager[], new SecureRandom())

        BasicCookieStore cookieStore = new BasicCookieStore()

        return HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultCookieStore(cookieStore)
                .setSSLContext(sslContext)
                .build()
    }

    static CloseableHttpClient createClientWithBasicAuth(){
            return HttpClients.custom()
                .setDefaultCredentialsProvider(setBasicAuthentification())
                .build()
        }

    static CredentialsProvider setBasicAuthentification() {
        String username = Holders.config.rest.tvf.basic.username
        String password = Holders.config.rest.tvf.basic.password
        CredentialsProvider provider = new BasicCredentialsProvider()
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password)
        provider.setCredentials(AuthScope.ANY, credentials)
        return provider
    }
}
