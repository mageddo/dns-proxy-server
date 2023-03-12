package com.mageddo.commons.io;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigProps;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class IoUtils {

  private IoUtils() {
  }

  public static void silentClose(Closeable c) {
    try {
      if (c != null) {
        c.close();
      }
    } catch (IOException e) {
    }
  }

  public static Properties loadPropertiesFromResources(String path) {
    final var in = getResourceAsStream(path);
    final var properties = new Properties();
    try {
      properties.load(in);
      return properties;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static InputStream getResourceAsStream(String path) {
    return ConfigProps.class.getResourceAsStream(path);
  }
}
