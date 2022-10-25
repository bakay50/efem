package com.webbfontaine.efem.http

import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class DefaultTrustManager implements X509TrustManager{

    @Override
    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return null
    }
}
