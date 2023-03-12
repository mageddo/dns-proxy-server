package com.mageddo.commons.io;

import java.io.Closeable;
import java.io.IOException;

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
}
