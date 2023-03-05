package com.mageddo.os.cross;

import com.sun.jna.Platform;

public class Networks {
  public static Network getInstance() {
    if (Platform.isMac()) {
      return new NetworkOSX();
    } else if (Platform.isWindows()) {
      return new NetworkWindows();
    }
    throw new UnsupportedOperationException("Unsupported platform: " + System.getProperty("os.name"));
  }
}
