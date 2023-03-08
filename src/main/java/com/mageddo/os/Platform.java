package com.mageddo.os;

public class Platform {

  private Platform() {
  }

  public static boolean isWindows() {
    return com.sun.jna.Platform.isWindows();
  }

  public static boolean isMac() {
    return com.sun.jna.Platform.isMac();
  }

  public static boolean isLinux() {
    return com.sun.jna.Platform.isLinux();
  }

}
