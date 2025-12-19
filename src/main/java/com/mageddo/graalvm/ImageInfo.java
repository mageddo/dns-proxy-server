package com.mageddo.graalvm;

public class ImageInfo {

  public static boolean inImageRuntimeCode() {
    return "runtime".equals(System.getProperty("org.graalvm.nativeimage.imagecode"));
  }

}
