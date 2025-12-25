package com.mageddo.dnsserver.doh;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class MockSSLUtils {

  public static SSLContext setupFakeSSLContext() {
    try {
      final SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, new TrustManager[]{createFakeTrustManager()}, new SecureRandom());
      return sslcontext;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException(e);
    }
  }

  public static X509TrustManager createFakeTrustManager() {
    return new X509TrustManager() {
      public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
      }

      public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
      }

      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }
    };
  }

  public static HostnameVerifier createFakeHostnameVerifier() {
    return (a, b) -> true;
  }
}
