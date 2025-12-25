package com.mageddo.dnsserver.doh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.mageddo.commons.io.IoUtils;

public class SslContextMapper {

  public static SSLContext of(String pkcs12ResourcePath, char[] password) {
    try {
      return of0(pkcs12ResourcePath, password);
    } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException |
             CertificateException | UnrecoverableKeyException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static SSLContext of0(String pkcs12ResourcePath, char[] password)
      throws NoSuchAlgorithmException,
      KeyManagementException,
      KeyStoreException,
      CertificateException,
      UnrecoverableKeyException,
      IOException {

    final var ks = KeyStore.getInstance("PKCS12");
    try (final var is = IoUtils.getResourceAsStream(pkcs12ResourcePath)) {
      if (is != null) {
        ks.load(is, password);
      } else {
        try (final var fis = Files.newInputStream(Path.of(pkcs12ResourcePath))) {
          ks.load(fis, password);
        }
      }
    }

    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, password);
    final var ctx = SSLContext.getInstance("TLS");
    ctx.init(kmf.getKeyManagers(), null, null);
    return ctx;
  }
}
